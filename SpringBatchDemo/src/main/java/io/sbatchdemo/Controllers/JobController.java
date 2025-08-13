package io.sbatchdemo.Controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class JobController {

    private final JobLauncher jobLauncher;
    private final Job job;

    @GetMapping("/startJob")
    public BatchStatus load() throws Exception{

        // Spring Batch 5.x approach
        Map<String, JobParameter<?>> parameters = new HashMap<>();
        parameters.put("time", new JobParameter<>(System.currentTimeMillis(), Long.class));

        JobParameters jobParameters = new JobParameters(parameters);
        JobExecution jobExecution = jobLauncher.run(job, jobParameters);

        while (jobExecution.isRunning()) {
            System.out.println("Job is Running ...");
        }

        return jobExecution.getStatus();
    }
}