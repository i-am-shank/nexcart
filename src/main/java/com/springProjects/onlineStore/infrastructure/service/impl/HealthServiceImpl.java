package com.springProjects.onlineStore.infrastructure.service.impl;

import com.springProjects.onlineStore.infrastructure.service.HealthService;
import org.springframework.stereotype.Service;

@Service
public class HealthServiceImpl implements HealthService {
    @Override
    public String getHelloMessage() {
        return "Hello from Online Store";
    }
}
