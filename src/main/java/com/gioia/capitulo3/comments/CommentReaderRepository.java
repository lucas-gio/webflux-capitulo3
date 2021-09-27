package com.gioia.capitulo3.comments;

import org.springframework.data.repository.Repository;
import reactor.core.publisher.Flux;

/**
 * Repositorio de comentarios. Al extender de Repository no hay operaciones heredadas. Se usó para
 * tener acceso de solo lectura.
 */
public interface CommentReaderRepository extends Repository<Comment, String> {
    Flux<Comment> findByImageId(String imageId);
}
