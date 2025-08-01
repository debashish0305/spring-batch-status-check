package com.example.batch.tasklet;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class StatusCheckTasklet implements Tasklet {

    private static final int MAX_RETRIES = 30;
    private static final int POLL_INTERVAL_MS = 10000;

    private final JdbcTemplate jdbcTemplate;

    public StatusCheckTasklet(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        int attempts = 0;
        while (attempts < MAX_RETRIES) {
            String status = jdbcTemplate.queryForObject(
                "SELECT status FROM status_table WHERE batch_name = ?",
                new Object[]{"my-nightly-batch"},
                String.class
            );

            if ("C".equalsIgnoreCase(status)) {
                return RepeatStatus.FINISHED;
            }

            attempts++;
            Thread.sleep(POLL_INTERVAL_MS);
        }

        throw new IllegalStateException("Status table did not reach 'C' within timeout.");
    }
}