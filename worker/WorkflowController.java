package edu.pbj.hospitaltriage.worker;

import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.command.DeployResourceCommandStep1.DeployResourceCommandStep2;
import io.camunda.zeebe.client.api.response.DeploymentEvent;
import io.camunda.zeebe.client.api.response.ProcessInstanceEvent;
import io.camunda.zeebe.client.api.response.Topology;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

@RestController
@RequestMapping("/workflow")
public class WorkflowController {

    private static final Logger log = LoggerFactory.getLogger(WorkflowController.class);
    private final ZeebeClient zeebeClient;

    public WorkflowController(ZeebeClient zeebeClient) {
        this.zeebeClient = zeebeClient;
    }

    @GetMapping("/topology")
    public ResponseEntity<Map<String, Object>> getTopology() {
        try {
            Topology topology = zeebeClient.newTopologyRequest().send().join();
            return ResponseEntity.ok(Map.of(
                    "status", "CONNECTED",
                    "clusterSize", topology.getClusterSize(),
                    "partitionsCount", topology.getPartitionsCount(),
                    "gatewayVersion", topology.getGatewayVersion()
            ));
        } catch (Exception e) {
            log.error("Failed to query Zeebe topology", e);
            return ResponseEntity.status(503).body(Map.of(
                    "status", "DISCONNECTED",
                    "error", e.getMessage()
            ));
        }
    }

    // Absolute base path for the project - resolves resources reliably regardless of CWD
    private static final String PROJECT_BASE = "d:\\workflow\\camunda8-assignment";

    private Path resolveFile(String filename, String... candidateSubdirs) {
        // 1. Try absolute project-relative paths first
        for (String sub : candidateSubdirs) {
            Path p = Paths.get(PROJECT_BASE, sub, filename).normalize();
            if (Files.exists(p)) {
                log.debug("Resolved {} at {}", filename, p);
                return p;
            }
        }
        // 2. Fallback: resolve relative to CWD
        for (String sub : candidateSubdirs) {
            Path p = Paths.get(sub, filename).toAbsolutePath().normalize();
            if (Files.exists(p)) {
                log.debug("Resolved {} at {} (CWD-relative)", filename, p);
                return p;
            }
        }
        return Paths.get(PROJECT_BASE, candidateSubdirs[0], filename).normalize();
    }

    @PostMapping("/deploy")
    public ResponseEntity<Map<String, Object>> deployModels(@RequestParam(name = "bpmnPath", required = false) String customBpmn,
                                                            @RequestParam(name = "dmnPath", required = false) String customDmn) {
        try {
            Path bpmn = customBpmn != null ? Paths.get(customBpmn) : resolveFile("hospital-triage.bpmn", "lab3-hospital-triage/bpmn", "bpmn");
            Path dmn = customDmn != null ? Paths.get(customDmn) : resolveFile("triage-classification.dmn", "lab3-hospital-triage/dmn", "dmn");

            log.info("Deploying BPMN from {} and DMN from {}", bpmn, dmn);

            if (!Files.exists(bpmn)) {
                throw new IllegalArgumentException("BPMN file not found at " + bpmn);
            }

            DeployResourceCommandStep2 cmd = zeebeClient.newDeployResourceCommand()
                    .addResourceFile(bpmn.toString());

            if (Files.exists(dmn)) {
                cmd = cmd.addResourceFile(dmn.toString());
            }

            // Also deploy any .form files in the bpmn directory
            Path bpmnDir = bpmn.getParent();
            if (bpmnDir != null && Files.exists(bpmnDir)) {
                try (var stream = Files.list(bpmnDir)) {
                    for (Path f : stream.filter(p -> p.toString().endsWith(".form")).toList()) {
                        log.info("Adding form resource: {}", f);
                        cmd = cmd.addResourceFile(f.toString());
                    }
                }
            }

            DeploymentEvent event = cmd.send().join();

            return ResponseEntity.ok(Map.of(
                    "status", "SUCCESS",
                    "deploymentKey", event.getKey(),
                    "bpmnProcesses", event.getProcesses().stream().map(p -> Map.of(
                            "bpmnProcessId", p.getBpmnProcessId(),
                            "processDefinitionKey", p.getProcessDefinitionKey(),
                            "version", p.getVersion()
                    )).toList(),
                    "decisions", event.getDecisions().stream().map(d -> Map.of(
                            "decisionId", d.getDmnDecisionId(),
                            "decisionKey", d.getDecisionKey(),
                            "version", d.getVersion()
                    )).toList()
            ));
        } catch (Exception e) {
            log.error("Deployment failed", e);
            return ResponseEntity.status(500).body(Map.of(
                    "status", "FAILED",
                    "error", e.getMessage()
            ));
        }
    }

    @PostMapping("/start")
    public ResponseEntity<Map<String, Object>> startProcess(@RequestBody Map<String, Object> variables) {
        try {
            log.info("Starting process 'hospital-triage-process' with variables: {}", variables);
            ProcessInstanceEvent event = zeebeClient.newCreateInstanceCommand()
                    .bpmnProcessId("hospital-triage-process")
                    .latestVersion()
                    .variables(variables)
                    .send()
                    .join();

            return ResponseEntity.ok(Map.of(
                    "status", "STARTED",
                    "processInstanceKey", event.getProcessInstanceKey(),
                    "bpmnProcessId", event.getBpmnProcessId(),
                    "version", event.getVersion()
            ));
        } catch (Exception e) {
            log.error("Failed to start process", e);
            return ResponseEntity.status(500).body(Map.of(
                    "status", "FAILED",
                    "error", e.getMessage()
            ));
        }
    }
}
