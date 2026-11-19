package edu.pbj.orderprocessing.worker;

import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.spring.client.annotation.JobWorker;
import io.camunda.zeebe.spring.client.annotation.Variable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class OrderValidationAndStockWorker {

    private static final Logger log = LoggerFactory.getLogger(OrderValidationAndStockWorker.class);

    private final WorkerConfigService configService;

    // Simulated in-memory inventory store
    private static final Map<String, ProductItem> INVENTORY_CATALOG = new ConcurrentHashMap<>();

    static {
        INVENTORY_CATALOG.put("PROD-001", new ProductItem("PROD-001", "Developer Laptop", 1200.0, 15));
        INVENTORY_CATALOG.put("PROD-002", new ProductItem("PROD-002", "Smartphone", 800.0, 25));
        INVENTORY_CATALOG.put("PROD-003", new ProductItem("PROD-003", "Wireless Headphones", 150.0, 50));
        INVENTORY_CATALOG.put("PROD-004", new ProductItem("PROD-004", "4K Ultra-Wide Monitor", 450.0, 10));
        INVENTORY_CATALOG.put("PROD-OUT-OF-STOCK", new ProductItem("PROD-OUT-OF-STOCK", "Discontinued GPU", 900.0, 0));
        INVENTORY_CATALOG.put("PROD-999", new ProductItem("PROD-999", "Sold Out Console", 500.0, 0));
    }

    public OrderValidationAndStockWorker(WorkerConfigService configService) {
        this.configService = configService;
    }

    @JobWorker(type = "validate-order-and-check-stock", autoComplete = false)
    public void validateOrderAndCheckStock(
            final JobClient client,
            final ActivatedJob job,
            @Variable(name = "customerRef") String customerRef,
            @Variable(name = "customerCategory") String customerCategory,
            @Variable(name = "productId") String productId,
            @Variable(name = "quantity") Integer quantity,
            @Variable(name = "deliveryPreference") String deliveryPreference,
            @Variable(name = "paymentToken") String paymentToken
    ) {
        log.info("Processing job {} (validate-order-and-check-stock) for customerRef={}, productId={}, quantity={}",
                job.getKey(), customerRef, productId, quantity);

        // 1. Deliberate failure mode simulation
        if (configService.isDeliberateFailureEnabled()) {
            String errorMsg = "DELIBERATE_INVENTORY_WORKER_FAILURE: Simulated inventory database lock in Lab 2. " +
                    "To recover, set failure-mode to false via /worker-config/failure-mode and retry the job in Camunda Operate.";
            log.error("Job {}: {}", job.getKey(), errorMsg);
            client.newFailCommand(job.getKey())
                    .retries(0)
                    .errorMessage(errorMsg)
                    .send()
                    .join();
            return;
        }

        // 2. Validate inputs
        if (quantity == null || quantity <= 0) {
            quantity = 1;
        }

        // 3. Check stock in simulated catalog
        ProductItem product = INVENTORY_CATALOG.get(productId);
        boolean stockAvailable = false;
        String stockStatus = "OUT_OF_STOCK";
        double unitPrice = 0.0;
        double orderTotal = 0.0;

        if (product != null && product.getStock() >= quantity) {
            stockAvailable = true;
            stockStatus = "IN_STOCK";
            unitPrice = product.getPrice();
            orderTotal = BigDecimal.valueOf(unitPrice * quantity)
                    .setScale(2, RoundingMode.HALF_UP)
                    .doubleValue();
        } else if (product != null) {
            unitPrice = product.getPrice();
            log.warn("Insufficient stock for productId={}: requested={}, available={}",
                    productId, quantity, product.getStock());
        } else {
            log.warn("Product not found in inventory: productId={}", productId);
        }

        Map<String, Object> output = new HashMap<>();
        output.put("stockAvailable", stockAvailable);
        output.put("stockStatus", stockStatus);
        output.put("unitPrice", unitPrice);
        output.put("orderTotal", orderTotal);

        log.info("Stock check complete for job {}: stockAvailable={}, stockStatus={}, orderTotal={}",
                job.getKey(), stockAvailable, stockStatus, orderTotal);

        client.newCompleteCommand(job.getKey())
                .variables(output)
                .send()
                .join();
    }

    public static class ProductItem {
        private final String id;
        private final String name;
        private final double price;
        private final int stock;

        public ProductItem(String id, String name, double price, int stock) {
            this.id = id;
            this.name = name;
            this.price = price;
            this.stock = stock;
        }

        public String getId() { return id; }
        public String getName() { return name; }
        public double getPrice() { return price; }
        public int getStock() { return stock; }
    }
}

