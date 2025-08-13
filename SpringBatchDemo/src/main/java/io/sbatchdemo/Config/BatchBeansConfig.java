package io.sbatchdemo.Config;

import io.sbatchdemo.DAO.BankTransaction;
import io.sbatchdemo.Repositories.BankTransactionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;

import java.text.SimpleDateFormat;

@Configuration
@Slf4j
public class BatchBeansConfig {

    @Bean
    public ItemReader<BankTransaction> bankTransactionItemReader(@Value("${app.inputFile}") String inputFilePath) {
        return new FlatFileItemReaderBuilder<BankTransaction>()
                .name("bankTransactionCsvReader")
                .resource(new ClassPathResource(inputFilePath))
                .linesToSkip(1) // <-- Skips the header line
                .delimited()
                .names("id", "accountId", "strTransactionDate", "transactionType", "amount")
                .fieldSetMapper(new BeanWrapperFieldSetMapper<>() {{
                    setTargetType(BankTransaction.class);
                }})
                .build();
    }


    @Bean
    public ItemProcessor<BankTransaction, BankTransaction> bankTransactionItemProcessor() {
        return transaction -> {
            // Convert the String date to java.util.Date here if needed
            transaction.setTransactionDate(new SimpleDateFormat("dd/MM/yyyy-H:mm").parse(transaction.getStrTransactionDate()));
            log.info("Processed item: {}", transaction);
            return transaction;
        };
    }

    /*
    @Bean
    public ItemWriter<BankTransaction> bankTransactionItemWriter(BankTransactionRepository repository) {
        return items -> repository.saveAll(items);
    }

     */


    @Bean
    public FlatFileItemWriter<BankTransaction> bankTransactionTxtWriter() {
        // Use FlatFileItemWriter to write output to a .txt file
        log.info("WRITING INTO TEXT FILE !");
        return new FlatFileItemWriterBuilder<BankTransaction>()
                .name("bankTransactionTxtWriter")
                .resource(new FileSystemResource("output/transactions_output.txt")) // output path
                .lineAggregator(new DelimitedLineAggregator<BankTransaction>() {{
                    setDelimiter(","); // can also use "\t" for tab-separated
                    setFieldExtractor(transaction -> new Object[]{
                            transaction.getId(),
                            transaction.getAccountId(),
                            transaction.getTransactionDate(),
                            transaction.getTransactionType(),
                            transaction.getAmount()
                    });
                }})
                .build();
    }


}
