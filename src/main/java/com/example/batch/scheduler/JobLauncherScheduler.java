package com.example.batch.scheduler;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class JobLauncherScheduler {

    private final JobLauncher jobLauncher;
    private final Job nightlyAvroJob;

    public JobLauncherScheduler(JobLauncher jobLauncher, Job nightlyAvroJob) {
        this.jobLauncher = jobLauncher;
        this.nightlyAvroJob = nightlyAvroJob;
    }

    @Scheduled(cron = "0 0 1 * * *") // every day at 1 AM
    public void runNightlyJob() throws Exception {
        JobParameters params = new JobParametersBuilder()
                .addLong("timestamp", System.currentTimeMillis())
                .toJobParameters();
        jobLauncher.run(nightlyAvroJob, params);
    }
}