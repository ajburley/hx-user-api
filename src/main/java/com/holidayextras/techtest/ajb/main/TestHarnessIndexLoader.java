package com.holidayextras.techtest.ajb.main;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Spring Boot does not automatically load index.html from subdirectories by default.
 * This class is so that index.html will be loaded for the test harness, without having to specify it in the URI.
 */
@Configuration
public class TestHarnessIndexLoader implements WebMvcConfigurer {
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/test-harness").setViewName("redirect:/test-harness/");
        registry.addViewController("/test-harness/").setViewName("forward:/test-harness/index.html");
    }
}
