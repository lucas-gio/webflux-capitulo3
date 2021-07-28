package com.gioia.capitulo3.initializers;

import org.springframework.stereotype.Component;

@Component
public class DatabaseInitializer {
    /*final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass())

    @Bean
    CommandLineRunner init(MongoOperations operations){
        Runnable insertions = (args)->{
            operations.dropCollection(Image.class)

            operations.insert([
                    new Image([name:"primer libro"]),
                    new Image([name:"segundo libro"]),
                    new Image([name:"tercer libro"]),
                    new Image([name:"cuarto libro"]),
            ])

            operations.findAll(Image.class).forEach((Image image)->{
                logger.debug("Imágen {} ya se encuentra en la base de datos", image.name?:'')
            })
        }
    }*/
}
