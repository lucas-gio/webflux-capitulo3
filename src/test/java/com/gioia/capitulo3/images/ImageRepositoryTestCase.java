package com.gioia.capitulo3.images;

import com.gioia.capitulo3.initializers.DatabaseInitializer;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.ArrayList;

@ExtendWith(SpringExtension.class) // junit 5
@DataMongoTest
public class ImageRepositoryTestCase {
    @Autowired
    ImageRepository imageRepository;

    @Autowired
    MongoOperations mongoOperations;

    @BeforeEach
    void init(){
        new DatabaseInitializer().initialize(mongoOperations);
    }

    @Test
    @DisplayName("ImageRepository.findAll() debe recuperar todos los registros.  ")
    public void findAllGetAllDocuments(){
        Flux<Image> images = imageRepository.findAll();

        StepVerifier // Clase para hacer pruebas con publicadores
                .create(images)
                .recordWith(ArrayList::new) // Llevando los registros a una lista
                .expectNextCount(4) // Se esperan que son 4 unidades
                .consumeRecordedWith(results-> { // Y se consumen los registros verificando que...
                    Assertions.assertThat(results).hasSize(4); // Son 4q unidades
                    Assertions.assertThat(results) // Que al extraer sus nombres todos contienen esos valores
                            .extracting(Image::getName)
                            .containsExactlyInAnyOrder( // Se contiene en cualquier orden esto
                                    "primer libro",
                                    "segundo libro",
                                    "tercer libro",
                                    "cuarto libro"
                            );
                    Assertions.assertThat(results)  // Y que al recolectar sus id
                            .extracting(Image::getId)
                        .doesNotContainNull();      // Ninguno es null
                })
                .expectComplete()       // Además, el flujo terminó con onComplete (sin errores).
                .verify();
    }

    @Test
    public void findByNameOnlyReturnDocumentsByName(){
        Mono<Image> monoToSecondBook = imageRepository.findByName("segundo libro");
        StepVerifier.create(monoToSecondBook)
                .expectNextMatches(result->{
                    Assertions.assertThat(result.getName()).isEqualTo("segundo libro");
                    Assertions.assertThat(result.getId()).isNotBlank();
                    return true;
                })
        .expectComplete()
        .verify();
    }
}
