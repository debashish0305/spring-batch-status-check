package com.example.batch.config;

import com.example.batch.tasklet.StatusCheckTasklet;
import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.job.builder.StepBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class NightlyJobConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final StatusCheckTasklet statusCheckTasklet;

    public NightlyJobConfig(JobRepository jobRepository,
                            PlatformTransactionManager transactionManager,
                            StatusCheckTasklet statusCheckTasklet) {
        this.jobRepository = jobRepository;
        this.transactionManager = transactionManager;
        this.statusCheckTasklet = statusCheckTasklet;
    }

    @Bean
    public Step waitForStatusStep() {
        return new StepBuilder("waitForStatusStep", jobRepository)
                .tasklet(statusCheckTasklet, transactionManager)
                .build();
    }

    @Bean
    public Job nightlyAvroJob() {
        return new JobBuilder("nightlyAvroJob", jobRepository)
                .start(waitForStatusStep())
                .build();
    }
}