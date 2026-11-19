package edu.pbj.hospitaltriage.worker;

import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.spring.client.annotation.JobWorker;
import io.camunda.zeebe.spring.client.annotation.Variable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Component
public class BookAppointmentWorker {

    private static final Logger log = LoggerFactory.getLogger(BookAppointmentWorker.class);

    @JobWorker(type = "book-appointment", autoComplete = false)
    public void bookAppointment(
            final JobClient client,
            final ActivatedJob job,
            @Variable(name = "patientRef") String patientRef,
            @Variable(name = "slotId") String slotId
    ) {
        log.info("Processing job {} (book-appointment) for patientRef={}, slotId={}",
                job.getKey(), patientRef, slotId);

        String bookingReference = "SYN-BKG-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        Map<String, Object> output = new HashMap<>();
        output.put("bookingReference", bookingReference);
        output.put("bookingStatus", "CONFIRMED");
        output.put("confirmationTimestamp", Instant.now().toString());

        log.info("Appointment booked for job {}: bookingReference={}", job.getKey(), bookingReference);

        client.newCompleteCommand(job.getKey())
                .variables(output)
                .send()
                .join();
    }
}

