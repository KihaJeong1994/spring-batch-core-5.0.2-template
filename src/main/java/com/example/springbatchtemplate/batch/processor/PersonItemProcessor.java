package com.example.springbatchtemplate.batch.processor;

import com.example.springbatchtemplate.entity.Person;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;


/**
 * A common paradigm in batch processing is to ingest data, transform it, and then pipe it out somewhere else.
 * ItemProcessor : transformer . makes it easy to wire the code into a batch job
* */
public class PersonItemProcessor implements ItemProcessor<Person,Person> {
    private static final Logger log = LoggerFactory.getLogger(PersonItemProcessor.class);

    @Override
    public Person process(final Person person) throws Exception {
        final String firstName = person.getFirstName().toUpperCase();
        final String lastName = person.getLastName().toUpperCase();

        final Person transformedPerson = new Person(firstName,lastName);

        log.info("Converting (" + person + ") into (" + transformedPerson + ")");

        return transformedPerson;
    }
}
