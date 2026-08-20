package dev.interviewos.api.config;

import java.io.IOException;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.resource.PathResourceResolver;

/**
 * Serves the Angular bundle that the Docker build copies into
 * classpath:/static/. Deep links such as /dashboard are client-side routes with
 * no file behind them, so anything that is not a real asset falls back to
 * index.html and lets the Angular router take over.
 */
@Configuration
public class SpaResourceConfig implements WebMvcConfigurer {

    private static final Resource INDEX =
            new ClassPathResource("static/index.html");

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/static/")
                .resourceChain(true)
                .addResolver(new PathResourceResolver() {

                    @Override
                    protected Resource getResource(
                            String resourcePath,
                            Resource location)
                            throws IOException {

                        Resource requested =
                                location.createRelative(resourcePath);

                        if (requested.exists()
                                && requested.isReadable()) {

                            return requested;
                        }

                        /*
                         * An unmatched /api/** path is a genuine 404, not a
                         * page the SPA can render.
                         */
                        if (resourcePath.startsWith("api/")) {
                            return null;
                        }

                        return INDEX.exists() ? INDEX : null;
                    }
                });
    }
}
