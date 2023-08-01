package com.example.springbatchtemplate.batch.listener;

import com.example.springbatchtemplate.entity.Person;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * get notified when the job completes
* */
@Component
public class JobCompletionNotificationListener implements JobExecutionListener {

    private static final Logger log = LoggerFactory.getLogger(JobCompletionNotificationListener.class);
    private final JdbcTemplate jdbcTemplate;

    public JobCompletionNotificationListener(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        if(BatchStatus.COMPLETED.equals(jobExecution.getStatus())){
            log.info("!!! JOB FINISHED! Time to verify the results");

            jdbcTemplate.query("SELECT first_name, last_name FROM people"
            , (rs,row)-> new Person(rs.getString(1),rs.getString(2))
            ).forEach(person -> log.info("Found <{{}}> in the database", person));
        }
    }
}
