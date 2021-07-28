package com.gioia.capitulo3.images;


import com.gioia.capitulo3.Application;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.File;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.nio.file.Files;
import java.nio.file.Paths;

@Service
public class ImageService {
    private final ResourceLoader resourceLoader;
    private final ImageRepository imageRepository;
    final static Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public ImageService(ResourceLoader resourceLoader, ImageRepository imageRepository){

        this.resourceLoader = resourceLoader;
        this.imageRepository = imageRepository;
    }

    public Flux<Image> findAllImages(){
        return imageRepository.findAll();
    }

    public Mono<Resource> findOneImage(String filename){
        // No se crea desde un iterable ya que no tendría sentido al tratarse de un único elemento en lugar de una
        // colección iterable.
        return Mono.fromSupplier(()->{
            return resourceLoader.getResource("file:${Application.DIRECTORY}${filename}");
        });
    }

    /**
     * Crea el archivo de imágen, obtenido del cliente, en la carpeta temporal.
     * Si no envía un archivo, retorna.
     * @param files
     * @return
     */
    public Mono<Void> createImage(Flux<FilePart> files){
        return files
                .filter((FilePart file)-> file.filename().length() > 0)
                .flatMap((FilePart file)-> Mono.when(
                        saveDatabaseImage(file),
                        copyFile(file)
                ))
                .then();
    }

    public Mono<Image> saveDatabaseImage(FilePart file){
        return imageRepository.save(Image.withName(file.filename()));
    }

    public Mono<Void> copyFile (FilePart file){
        return Mono.just(Paths.get(Application.DIRECTORY, file.filename()).toFile())
                .log("CreateImage - picktarget")
                .map((File destFile)->{
                    try {
                        destFile.createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return destFile;
                })
                .log("CreateImage - newFile")
                .flatMap(file::transferTo)
                .log("CreateImage - copy")
                .doOnError((Throwable e)->{
                    logger.error("error en copyFile" + e.toString());
                });
    }

    public Mono<Void> deleteImage(String filename){
        return Mono.when(
                deleteDatabaseImage(filename),
                deleteFile(filename)
        );
    }

    public Mono<Object> deleteFile(String filename){
        return Mono.fromRunnable(()-> {
            try {
                Files.deleteIfExists(
                        Paths.get(Application.DIRECTORY, filename)
                );
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public Mono<Void> deleteDatabaseImage(String filename) {
        return imageRepository
                .findByName(filename)
                .flatMap(imageRepository::delete);
    }
}
