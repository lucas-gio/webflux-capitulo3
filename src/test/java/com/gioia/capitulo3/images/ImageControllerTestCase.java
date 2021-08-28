package com.gioia.capitulo3.images;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.thymeleaf.ThymeleafAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;

@ExtendWith(SpringExtension.class) // junit 5
@WebFluxTest(controllers = ImageController.class)
@Import({ThymeleafAutoConfiguration.class})
public class ImageControllerTestCase {
    @Autowired
    WebTestClient webTestClient;

    @MockBean
    ImageService imageService;

    @Test
    @DisplayName("Al entrar a la página inicial, debe mostrar las imágenes")
    public void whenInitPageThenReturnPageWithImages(){
        //Given: Llamando a findAll, retorno dos registros de pruebas.
        Image image1 = Image.withName("image1.png");
        Image image2 = Image.withName("image2.png");
        BDDMockito.given(imageService.findAllImages())
            .willReturn(Flux.just(image1,image2));

        // When: Se navega a /
        EntityExchangeResult<String> result = webTestClient
                .get().uri("/")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .returnResult();

        // Then: Se verifica que se haya llamado a los métodos correspondientes
        Mockito.verify(imageService).findAllImages();
        Mockito.verifyNoMoreInteractions(imageService);

        // And: La respuesta es html con los valores esperados.
        Assertions.assertThat(
            result.getResponseBody()
        ).contains("<a href=\"/images/image1.png/raw\">")
        .contains("<img src=\"/images/image1.png/raw\" class=\"thumbnail\">")
        .contains("<a href=\"/images/image2.png/raw\">")
        .contains("<img src=\"/images/image2.png/raw\" class=\"thumbnail\">")
        .doesNotContain("image3.png")
        .doesNotContain("image4.png");
    }

    @Test
    public void whenFetchImageThenImageIsObtained(){
        // Given: Al llamar a findOneImage se retorna un publicador a imágen de pruebas
        BDDMockito.given(imageService.findOneImage(Mockito.any(String.class)))
            .willReturn(
                Mono.just(
                    new ByteArrayResource("data".getBytes())
                )
            );

        //When: Se trata de descargar una imágen , Then: Se obtiene un 200, con la imágen.
        webTestClient
                .get().uri("/images/imageABC.png/raw")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class).isEqualTo("data");

        //And: Se verifica interacción con el servicio
        Mockito.verify(imageService).findOneImage("imageABC.png");
        Mockito.verifyNoMoreInteractions(imageService);
    }

    @Test
    public void whenImageNotInServerThenErrorIsObtained() throws IOException{
        // Given: Un resource que al obtenerlo da IOException, e imageService.findOneImage() que trata de obtenerlo
        Resource resource = Mockito.mock(Resource.class);
        BDDMockito.given(resource.getInputStream())
                .willThrow(new IOException("Not found image on server"));

        BDDMockito.given(imageService.findOneImage(Mockito.any()))
                .willReturn(Mono.just(resource));

        // When: Se trata de obtener la imágen; Then: Se obtiene un error y mensaje
        webTestClient
                .get().uri("/images/image1.png/raw")
                .exchange()
                .expectStatus().is5xxServerError()
                .expectBody(String.class)
                .isEqualTo("No se pudo encontrar la imágen image1.png . Not found image on server");

        //And: Se verifica interacción con el servicio
        Mockito.verify(imageService).findOneImage("image1.png");
        Mockito.verifyNoMoreInteractions(imageService);
    }

    @Test
    public void whenDeleteImageThenResponseIsRedirection(){

        //Given: Al llamar a deleteImage, se obtiene un publicador vacío (corresponde a imágen borrada con éxito)
        BDDMockito.given(imageService.deleteImage(Mockito.any()))
                .willReturn(Mono.empty());

        // When: Se elimine la imágen; Then: Se obtiene una redirección
        webTestClient
                .delete().uri("/images/image1.png/raw")
                .exchange()
                .expectStatus().isSeeOther() // Se redirecciona
                .expectHeader().valueEquals(HttpHeaders.LOCATION, "/"); // A la pantalla principal

        // And: Se verifica interacciones
        Mockito.verify(imageService).deleteImage("image1.png");
        Mockito.verifyNoMoreInteractions(imageService);
    }
}
