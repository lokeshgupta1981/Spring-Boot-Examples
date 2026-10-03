package com.howtodoinjava.demo.batch.jobs.csvToDb.job;

import com.howtodoinjava.demo.batch.jobs.csvToDb.listener.JobCompletionNotificationListener;
import com.howtodoinjava.demo.batch.jobs.csvToDb.listener.PersonItemReadListener;
import com.howtodoinjava.demo.batch.jobs.csvToDb.model.Person;
import com.howtodoinjava.demo.batch.jobs.csvToDb.processor.PersonItemProcessor;
import com.howtodoinjava.demo.batch.jobs.csvToDb.tasklets.DeleteInputCsvTasklet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.file.transform.LineTokenizer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
public class CsvToDatabaseJob {

  public static final Logger logger = LoggerFactory.getLogger(CsvToDatabaseJob.class);

  private static final String INSERT_QUERY = """
      insert into person (first_name, last_name, age, is_active)
      values (:firstName,:lastName,:age,:active)""";

  private final JobRepository jobRepository;

  public CsvToDatabaseJob(JobRepository jobRepository) {
    this.jobRepository = jobRepository;
  }

  @Value("classpath:csv/inputData.csv")
  private Resource inputFeed;

  @Bean(name="insertIntoDbFromCsvJob")
  public Job insertIntoDbFromCsvJob(Step step1, Step step2) {

    var name = "Persons Import Job";
    var builder = new JobBuilder(name, jobRepository);

    return builder.start(step1)
        //.next(step2)
        .listener(new JobCompletionNotificationListener(new Resource[]{inputFeed}))
        .build();
  }

  @Bean
  public Step step1(ItemReader<Person> reader,
                    ItemWriter<Person> writer,
                    ItemProcessor<Person, Person> processor,
                    PlatformTransactionManager txManager) {
    var name = "INSERT CSV RECORDS To DB Step";
    var builder = new StepBuilder(name, jobRepository);
    return builder
        .<Person, Person>chunk(5, txManager)
        /*.faultTolerant()
        .retryLimit(3).retry(DeadlockLoserDataAccessException.class)*/
        .reader(reader)
        .listener(new PersonItemReadListener())
        //.processor(processor)
        //.listener(new PersonItemProcessor())
        .writer(writer)
        //.listener(new PersonItemWriteListener())
        .build();
  }

  @Bean
  public Step step2(PlatformTransactionManager txManager) {
    DeleteInputCsvTasklet task = new DeleteInputCsvTasklet();
    task.setResources(new Resource[]{inputFeed});
    var name = "DELETE CSV FILE";
    var builder = new StepBuilder(name, jobRepository);
    return builder
        .tasklet(task, txManager)
        .build();
  }

  @Bean
  public FlatFileItemReader<Person> csvFileReader(
      LineMapper<Person> lineMapper) {
    var itemReader = new FlatFileItemReader<Person>();
    itemReader.setLineMapper(lineMapper);
    itemReader.setResource(inputFeed);
    return itemReader;
  }

  @Bean
  public DefaultLineMapper<Person> lineMapper(LineTokenizer tokenizer,
                                              FieldSetMapper<Person> mapper) {
    var lineMapper = new DefaultLineMapper<Person>();
    lineMapper.setLineTokenizer(tokenizer);
    lineMapper.setFieldSetMapper(mapper);
    return lineMapper;
  }

  @Bean
  public BeanWrapperFieldSetMapper<Person> fieldSetMapper() {
    var fieldSetMapper = new BeanWrapperFieldSetMapper<Person>();
    fieldSetMapper.setTargetType(Person.class);
    return fieldSetMapper;
  }

  @Bean
  public DelimitedLineTokenizer tokenizer() {
    var tokenizer = new DelimitedLineTokenizer();
    tokenizer.setDelimiter(",");
    tokenizer.setNames("firstName", "lastName", "age", "active");
    return tokenizer;
  }

  @Bean
  public JdbcBatchItemWriter<Person> jdbcItemWriter(DataSource dataSource) {
    var provider = new BeanPropertyItemSqlParameterSourceProvider<Person>();
    var itemWriter = new JdbcBatchItemWriter<Person>();
    itemWriter.setDataSource(dataSource);
    itemWriter.setSql(INSERT_QUERY);
    itemWriter.setItemSqlParameterSourceProvider(provider);
    return itemWriter;
  }

  @Bean
  public PersonItemProcessor personItemProcessor() {
    return new PersonItemProcessor();
  }
}
