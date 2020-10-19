package com.kousenit.shopping.config;

import com.kousenit.shopping.controllers.ProductHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.web.servlet.function.RequestPredicates.accept;
import static org.springframework.web.servlet.function.RouterFunctions.route;

@Configuration  // JavaConfig approach to adding beans to the app ctx
public class FunctionalBeans {

    @Bean
    public RouterFunction<ServerResponse> routerFunction(ProductHandler handler) {
        return route().path("/function",
                builder -> builder
                        .GET("", accept(APPLICATION_JSON), handler::getAllProducts)
                        .GET("/{id}", accept(APPLICATION_JSON), handler::getProductById)
                        .POST("", handler::createProduct))
                .build();
    }
}
