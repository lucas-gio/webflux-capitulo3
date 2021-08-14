package com.gioia.capitulo3.images;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ImageRepository extends ReactiveCrudRepository<Image, String>{
    Mono<Image> findByName(String name);
    Flux<Image> findAllByName(String name);
}
