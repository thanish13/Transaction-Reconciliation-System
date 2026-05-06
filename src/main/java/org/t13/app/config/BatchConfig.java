package org.t13.app.config;

import jakarta.annotation.Priority;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.EnableJdbcJobRepository;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.configuration.support.MapJobRegistry;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.JobExecution;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.job.parameters.JobParameters;
import org.springframework.batch.core.job.parameters.RunIdIncrementer;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.batch.core.launch.support.TaskExecutorJobOperator;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.dao.ExecutionContextDao;
import org.springframework.batch.core.repository.dao.JobExecutionDao;
import org.springframework.batch.core.repository.dao.JobInstanceDao;
import org.springframework.batch.core.repository.dao.StepExecutionDao;
import org.springframework.batch.core.repository.support.ResourcelessJobRepository;
import org.springframework.batch.core.repository.support.SimpleJobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.core.task.SyncTaskExecutor;
import org.springframework.web.context.annotation.RequestScope;
import org.t13.app.batch.CsvStreamItemProcessor;
import org.t13.app.batch.CsvStreamItemReader;
import org.t13.app.batch.CsvStreamItemWriter;
import org.t13.app.exception.DuplicateSettlementException;
import org.t13.app.model.SettlementReport;

@Slf4j
@Configuration
@EnableJdbcJobRepository
public class BatchConfig {

    private final JobRepository jobRepository;

    public BatchConfig(JobRepository jobRepository) {
        this.jobRepository = jobRepository;
    }

    // --- Step definition ---
    @Bean
    public Step updateSettlementStep(CsvStreamItemReader reader,
                                     CsvStreamItemProcessor processor,
                                     CsvStreamItemWriter writer) {

        log.info("Updating settlement step");
        return new StepBuilder(jobRepository)
                .<SettlementReport, SettlementReport>chunk(50)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .faultTolerant()
                .skip(DuplicateSettlementException.class)   // skip invalid records
                .skipLimit(100)
                .build();
    }

    @Bean
    public Job settlementJob(Step updateTransactionsStep) {
        log.info("Creating settlement job");
        return new JobBuilder(jobRepository)
                .flow(updateTransactionsStep)
                .end()
                .build();
    }


}

