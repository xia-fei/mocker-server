package org.wing.mocker.http;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;
import org.springframework.http.MediaType;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.HttpRequestHandler;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@SpringBootApplication
public class MockerHttpApplication extends WebMvcConfigurerAdapter {

    public static void main(String[] args) {
        SpringApplication.run(MockerHttpApplication.class, args);
    }


    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**/*.css", "/**/*.js", "/**/*.ttf", "/**/*.woff", "/**/*.ico").addResourceLocations("classpath:/public/dist/");
    }

    @Bean
    HandlerMapping vueHandlerMapping() {
        return new VueHandlerMapping();
    }

    class VueHandlerMapping implements HandlerMapping, Ordered {
        @Override
        public HandlerExecutionChain getHandler(HttpServletRequest request) throws Exception {
            return new HandlerExecutionChain(new HttpRequestHandler() {
                @Override
                public void handleRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
                    response.setContentType(MediaType.TEXT_HTML_VALUE);
                    FileCopyUtils.copy(Thread.currentThread().getContextClassLoader().getResourceAsStream("public/dist/index.html"),
                            response.getOutputStream());
                }
            });
        }

        @Override
        public int getOrder() {
            return Ordered.LOWEST_PRECEDENCE;
        }
    }
}

