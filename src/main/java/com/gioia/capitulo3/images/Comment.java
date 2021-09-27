package com.gioia.capitulo3.images;

import org.springframework.data.annotation.Id;

/**
 * Representa un comentario de la aplicación.
 */
public record Comment(
	@Id String id,
	String imageId,
	String comment
) {
}