package com.edoc.controllers.config;

import com.edoc.models.Config;
import com.edoc.repository.ConfigRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ConfigController {

    @Autowired
    private ConfigRepository configRepository;

    @GetMapping("/get-base-config/")
    public Config getBaseConfig() {
        return configRepository.findAll().stream().findFirst().orElse(null);
    }
}
