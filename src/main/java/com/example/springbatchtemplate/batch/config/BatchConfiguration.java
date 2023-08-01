package com.example.springbatchtemplate.batch.config;

import com.example.springbatchtemplate.batch.listener.JobCompletionNotificationListener;
import com.example.springbatchtemplate.batch.processor.PersonItemProcessor;
import com.example.springbatchtemplate.entity.Person;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

/**
 * define a reader, a processor, and a writer
 * define actual job configuration
* */
@Configuration
public class BatchConfiguration {

    /**
     * creates ItemReader that reads file and parses each line item to a Person
    * */
    @Bean
    public FlatFileItemReader<Person> reader(){
        return new FlatFileItemReaderBuilder<Person>()
                .name("personItemReader")
                .resource(new ClassPathResource("sample-data.csv"))
                .delimited()
                .names(new String[]{"firstName","lastName"})
                .fieldSetMapper(new BeanWrapperFieldSetMapper<>(){{
                    setTargetType(Person.class);
                }})
                .build();
    }

    @Bean
    public PersonItemProcessor processor(){
        return new PersonItemProcessor();
    }

    /**
     * creates ItemWriter
    * */
    @Bean
    public JdbcBatchItemWriter<Person> writer(DataSource dataSource){
        return new JdbcBatchItemWriterBuilder<Person>()
                .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
                .sql("INSERT INTO people (first_name, last_name) VALUES (:firstName, :lastName)")
                .dataSource(dataSource)
                .build();
    }

    /**
    * defines the job.
     * Jobs are built from steps, where each step can involve a reader, a processor, a writer.
     * Needs an incrementor because jobs use a database to maintain execution state.
    * */
    @Bean
    public Job importUserJob(JobRepository jobRepository, JobCompletionNotificationListener listener, Step step1){
        return new JobBuilder("importUserJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .listener(listener)
                .flow(step1)
//                .next() // you can list each step
                .end()
                .build();
    }

    /**
    * defines a single step
     * Define how much data to write at a time
    * */
    @Bean
    public Step step1(JobRepository jobRepository,
                      PlatformTransactionManager transactionManager, JdbcBatchItemWriter<Person> writer){
        return new StepBuilder("step1", jobRepository)
                .<Person,Person>chunk(10, transactionManager) //generic method <ItemReader Generic, ItemWriter Generic>
                .reader(reader())
                .processor(processor())
                .writer(writer)
                .build();
    }
}
