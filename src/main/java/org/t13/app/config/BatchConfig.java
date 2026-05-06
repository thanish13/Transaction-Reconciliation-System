package org.t13.app.config;

import jakarta.annotation.Priority;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.job.parameters.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.web.context.annotation.RequestScope;
import org.t13.app.batch.CsvStreamItemProcessor;
import org.t13.app.batch.CsvStreamItemReader;
import org.t13.app.batch.CsvStreamItemWriter;
import org.t13.app.exception.DuplicateSettlementException;
import org.t13.app.model.SettlementReport;

@Slf4j
@Configuration
@EnableBatchProcessing
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
    @Scope("prototype")
    public Job settlementJob(Step updateTransactionsStep) {
        log.info("Creating settlement job");
        return new JobBuilder("settlementJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .flow(updateTransactionsStep)
                .end()
                .build();
    }


}

