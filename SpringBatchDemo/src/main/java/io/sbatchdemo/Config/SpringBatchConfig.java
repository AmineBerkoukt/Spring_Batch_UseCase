package io.sbatchdemo.Config;

import io.sbatchdemo.DAO.BankTransaction;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@EnableBatchProcessing
@RequiredArgsConstructor
@Slf4j
public class SpringBatchConfig {

    private final ItemReader<BankTransaction> bankTransactionItemReader;
    private final ItemWriter<BankTransaction> bankTransactionItemWriter;
    private final ItemProcessor<BankTransaction, BankTransaction> bankTransactionItemProcessor;

    @Bean
    public Job bankTransactionJob(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        log.info("Creating Job: bankTransactionJob");
        return new JobBuilder("bankTransactionJob", jobRepository)
                .start(bankTransactionStep(jobRepository, transactionManager))
                .build();
    }



    @Bean
    public Step bankTransactionStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("bankTransactionStep", jobRepository)
                .<BankTransaction, BankTransaction>chunk(10, transactionManager)
                .reader(bankTransactionItemReader)
                .processor(bankTransactionItemProcessor)
                .writer(bankTransactionItemWriter)
                .build();
    }
}
