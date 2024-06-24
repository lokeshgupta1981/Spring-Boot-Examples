package com.howtodoinjava.demo.config;

import com.howtodoinjava.demo.model.Person;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.extensions.excel.poi.PoiItemReader;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class ExcelToDatabaseJobConfiguration {

  public static final Logger logger = LoggerFactory.getLogger(ExcelToDatabaseJobConfiguration.class);
  private static final String PROPERTY_EXCEL_SOURCE_FILE_PATH = "excel.to.database.job.source.file.path";

  private final JobRepository jobRepository;
  private final Environment environment;
  private final PlatformTransactionManager transactionManager;

  public ExcelToDatabaseJobConfiguration(JobRepository jobRepository,
                                         Environment environment,
                                         PlatformTransactionManager transactionManager) {
    this.jobRepository = jobRepository;
    this.environment = environment;
    this.transactionManager = transactionManager;
  }

  @Bean
  ItemReader<Person> reader() {
    PoiItemReader<Person> reader = new PoiItemReader<>();
    reader.setLinesToSkip(1);
    reader.setResource(new ClassPathResource(environment.getRequiredProperty(PROPERTY_EXCEL_SOURCE_FILE_PATH)));
    reader.setRowMapper(new PersonRowMapper());
    //reader.setRowMapper(excelRowMapper());
    return reader;
  }

  /*private RowMapper<Person> excelRowMapper() {
    BeanWrapperRowMapper<Person> rowMapper = new BeanWrapperRowMapper<>();
    rowMapper.setTargetType(Person.class);
    return rowMapper;
  }*/

  @Bean
  ItemProcessor<Person, Person> processor() {
    return new LoggingPersonProcessor();
  }

  @Bean
  ItemWriter<Person> writer() {
    //return new LoggingPersonWriter();
    return new DatabasePersonWriter();
  }

  /*@Bean
  ItemListenerSupport<Person, Person> errorListener() {
    return new PersonListenerSupport();
  }*/

  @Bean
  Step step1() {

    return new StepBuilder("stepBuilder", jobRepository)
        .<Person, Person>chunk(1, transactionManager)
        .reader(reader())
        .processor(processor())
        .writer(writer())
        .build();
  }

  @Bean
  Job excelFileToDatabaseJob(Step step1) {

    var builder = new JobBuilder("jobBuilder", jobRepository);
    return builder
        .incrementer(new RunIdIncrementer())
        .start(step1)
        .build();
  }
}
