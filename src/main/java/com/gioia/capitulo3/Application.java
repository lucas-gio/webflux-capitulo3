package com.gioia.capitulo3;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.filter.reactive.HiddenHttpMethodFilter;

import java.nio.file.FileSystems;

@SpringBootApplication
public class Application {
	private final static String TEMP_DIR = System.getProperty("java.io.tmpdir");
	public final static String DIRECTORY = TEMP_DIR + /*FileSystems.getDefault().getSeparator() +*/ "cap3" + FileSystems.getDefault().getSeparator();

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@Bean
	public HiddenHttpMethodFilter hiddenHttpMethodFilter(){
		return new HiddenHttpMethodFilter();
	}
}
