package br.edu.ifpb.file_service.config;

import jakarta.servlet.MultipartConfigElement;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("https://pdist-front.vercel.app/", "https://pdist-front-felipes-projects-ed3c083c.vercel.app/", "https://pdist-front-git-main-felipes-projects-ed3c083c.vercel.app/") // Substitua pela URL do seu frontend
                .allowedMethods("GET", "POST", "PUT", "DELETE")
                .allowedHeaders("*");
    }
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**").allowedOrigins("https://pdist-front.vercel.app/", "https://pdist-front-felipes-projects-ed3c083c.vercel.app/", "https://pdist-front-git-main-felipes-projects-ed3c083c.vercel.app/"); // Ajuste a URL conforme necessário
            }
        };
    }
    @Bean
    public MultipartConfigElement multipartConfigElement() {
        return new MultipartConfigElement("", 100 * 1024 * 1024, 100 * 1024 * 1024, 0);
    }
}
