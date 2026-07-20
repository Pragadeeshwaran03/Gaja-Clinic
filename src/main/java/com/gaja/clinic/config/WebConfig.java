package com.gaja.clinic.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${app.pdf.filesystem-path:src/main/resources/static/pdfs}")
    private String filesystemPath;

    @Value("${app.static.filesystem-path:src/main/resources/static}")
    private String staticFilesystemPath;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        Path pdfRoot = Path.of(System.getProperty("user.dir"), filesystemPath).toAbsolutePath().normalize();
        String pdfLocation = pdfRoot.toUri().toString();
        if (!pdfLocation.endsWith("/")) {
            pdfLocation += "/";
        }
        registry.addResourceHandler("/pdfs/**")
                .addResourceLocations(pdfLocation);

        Path staticRoot = Path.of(System.getProperty("user.dir"), staticFilesystemPath).toAbsolutePath().normalize();
        String staticLocation = staticRoot.toUri().toString();
        if (!staticLocation.endsWith("/")) {
            staticLocation += "/";
        }
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(staticLocation + "uploads/");
    }
}
