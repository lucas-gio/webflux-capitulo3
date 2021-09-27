package com.gioia.capitulo3.images;


import com.gioia.capitulo3.comments.CommentReaderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.lang.invoke.MethodHandles;
import java.util.HashMap;
import java.util.Map;

@Controller
public class ImageController {
	final static Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
	private final ImageService imageService;
	private final CommentReaderRepository commentReaderRepository;

	public ImageController(ImageService imageService, CommentReaderRepository commentReaderRepository) {
		this.imageService = imageService;
		this.commentReaderRepository = commentReaderRepository;
	}

	@GetMapping(value = "/images/{filename:.+}/raw", produces = MediaType.IMAGE_PNG_VALUE)
	public Mono<ResponseEntity<?>> oneRawImage(@PathVariable String filename) {
		return imageService.findOneImage(filename)
			.map(resource -> {
				try {
					return ResponseEntity
						.ok()
						.contentLength(resource.contentLength())
						.body(new InputStreamResource(resource.getInputStream()));
				} catch (Exception e) {
					return ResponseEntity
						.internalServerError()
						.body("No se pudo encontrar la imágen " + filename + " . " + e.getMessage());
				}
			});
	}

	@PostMapping(value = "/images")
	public Mono<String> createFile(@RequestPart(name = "file") Flux<FilePart> files) {
		return imageService.createImage(files)
			.onErrorResume(e -> Mono.fromRunnable(() -> logger.error(e.getMessage())))
			.then(Mono.just("redirect:/"));
	}

	@DeleteMapping("/images/{filename:.+}/raw")
	public Mono<String> deleteFile(@PathVariable String filename) {
		return imageService.deleteImage(filename)
			.then(Mono.just("redirect:/"));
	}

	@GetMapping("/")
	public Mono<String> index(Model model) {
		Flux<Map<String, Object>> imagesAndComments = imageService
			.findAllImages()
			// Por cada imágen, hacer una tupla ( una imágen, n comentarios)
			.flatMap(image ->
				Mono.just(image)
					.zipWith(commentReaderRepository
						.findByImageId(image.getId())
						.collectList()))
			// Y para que lo interprete thymeleaf por Map, hacer un mapa con los valores que necesito.
			.map(imageAndComments ->
				new HashMap<String, Object>() {{
					put("id", imageAndComments.getT1().getId());
					put("name", imageAndComments.getT1().getName());
					put("comments", imageAndComments.getT2());
				}}
			);

		model.addAttribute("images", imagesAndComments);
		return Mono.just("index");
	}
}
