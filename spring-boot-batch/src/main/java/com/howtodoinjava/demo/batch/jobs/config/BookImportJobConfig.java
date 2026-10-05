package com.howtodoinjava.demo.batch.jobs.config;

import com.howtodoinjava.demo.batch.jobs.listener.BookImportJobListener;
import com.howtodoinjava.demo.batch.jobs.listener.BookSkipListener;
import com.howtodoinjava.demo.batch.jobs.model.Book;
import com.howtodoinjava.demo.batch.jobs.processor.BookItemProcessor;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.infrastructure.item.database.JdbcBatchItemWriter;
import org.springframework.batch.infrastructure.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.infrastructure.item.file.FlatFileItemReader;
import org.springframework.batch.infrastructure.item.file.FlatFileParseException;
import org.springframework.batch.infrastructure.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
public class BookImportJobConfig {

  @Bean
  @StepScope
  public FlatFileItemReader<Book> bookReader(
      @Value("#{jobParameters['inputFile'] ?: 'books.csv'}") String inputFile) {
    return new FlatFileItemReaderBuilder<Book>()
        .name("bookReader")
        .resource(new ClassPathResource(inputFile))
        .linesToSkip(1)                                   // header line
        .delimited()
        .names("title", "author", "pages", "price")
        .targetType(Book.class)                           // Book is a record
        .build();
  }

  @Bean
  public BookItemProcessor bookProcessor() {
    return new BookItemProcessor();
  }

  @Bean
  public JdbcBatchItemWriter<Book> bookWriter(DataSource dataSource) {
    return new JdbcBatchItemWriterBuilder<Book>()
        .dataSource(dataSource)
        .sql("insert into book (title, author, pages, price) "
            + "values (:title, :author, :pages, :price)")
        .beanMapped()
        .build();
  }

  @Bean
  public Step importBooksStep(JobRepository jobRepository,
                              PlatformTransactionManager transactionManager,
                              FlatFileItemReader<Book> bookReader,
                              BookItemProcessor bookProcessor,
                              JdbcBatchItemWriter<Book> bookWriter) {
    return new StepBuilder("importBooksStep", jobRepository)
        .<Book, Book>chunk(3)
        .transactionManager(transactionManager)
        .reader(bookReader)
        .processor(bookProcessor)
        .writer(bookWriter)
        .faultTolerant()
        .skip(FlatFileParseException.class)
        .skipLimit(5)
        .skipListener(new BookSkipListener())
        .build();
  }

  @Bean
  public Job importBooksJob(JobRepository jobRepository, Step importBooksStep,
                            JdbcTemplate jdbcTemplate) {
    return new JobBuilder("importBooksJob", jobRepository)
        .start(importBooksStep)
        .listener(new BookImportJobListener(jdbcTemplate))
        .build();
  }
}
