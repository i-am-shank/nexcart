package com.springProjects.onlineStore.infrastructure.controller;

import com.springProjects.onlineStore.infrastructure.service.HealthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/health")
public class HealthController {
    private static final Logger logger = LoggerFactory.getLogger(HealthController.class);

    @Autowired
    private HealthService healthService;

    @GetMapping("/hello")
    public String sayHello() {
        logger.info("Testing connection : Hello API");
        return healthService.getHelloMessage();
    }
}
