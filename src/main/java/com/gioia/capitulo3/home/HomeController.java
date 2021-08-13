package com.gioia.capitulo3.home;


import com.gioia.capitulo3.images.ImageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.lang.invoke.MethodHandles;
import java.net.URI;

@Controller
public class HomeController {
    private final ImageService imageService;
    final static Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public HomeController(ImageService imageService){
        this.imageService = imageService;
    }

    @GetMapping(value = "/images/{filename:.+}/raw", produces = MediaType.IMAGE_PNG_VALUE)
    public Mono<ResponseEntity<?>> oneRawImage(@PathVariable String filename){
        return imageService.findOneImage(filename)
            .map(resource->{
                try{
                    return ResponseEntity
                        .ok()
                        .contentLength(resource.contentLength())
                        .body(new InputStreamResource(resource.getInputStream()));
                }
                catch(Exception e){
                    return ResponseEntity
                        .internalServerError()
                        .body("No se pudo encontrar la imágen ${filename?:''}. ${e.getMessage()}");
                }
            });
    }

    @PostMapping(value="/images")
    public Mono<String> createFile(@RequestPart(name="file") Flux<FilePart> files){
        return imageService.createImage(files)
            .onErrorResume(e -> Mono.just(logger.error(e.toString())))
            .then(Mono.just("redirect:/"));
    }

    @DeleteMapping("/images/{filename:.+}/raw")
    public Mono<String> deleteFile(@PathVariable String filename){
        return imageService.deleteImage(filename)
            .then(Mono.just("redirect:/"));
    }

    @GetMapping("/")
    public Mono<String> index(Model model){
        model.addAttribute("images", imageService.findAllImages());
        return Mono.just("index");
    }
}
