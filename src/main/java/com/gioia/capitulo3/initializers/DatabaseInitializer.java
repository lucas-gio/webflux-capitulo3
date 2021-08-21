package com.gioia.capitulo3.initializers;

import com.gioia.capitulo3.images.Image;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;

@Component
public class DatabaseInitializer {
    @Autowired
    MongoOperations mongoOperations;
    final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @Bean
    CommandLineRunner init(MongoOperations operations){
        return (args)-> initialize(operations);
    }

    public void initialize(MongoOperations operations){
        operations.dropCollection(Image.class);

        operations.insert(Image.withName("primer libro"));
        operations.insert(Image.withName("segundo libro"));
        operations.insert(Image.withName("tercer libro"));
        operations.insert(Image.withName("cuarto libro"));

        operations.findAll(Image.class).forEach((Image image)->{
            logger.debug("Imágen {} ya se encuentra en la base de datos", image.getName() != null ? image.getName() :"");
        });
    }

}
