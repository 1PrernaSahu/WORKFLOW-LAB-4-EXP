package edu.pbj.loanapproval.worker;

import io.camunda.zeebe.spring.client.annotation.Deployment;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class LoanWorkerApplication {

    public static void main(String[] args) {
        SpringApplication.run(LoanWorkerApplication.class, args);
    }
}

