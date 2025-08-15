package ru.origami.testit_allure.test_it.testit.services;

import ru.origami.testit_allure.test_it.testit.clients.ClientConfiguration;
import ru.origami.testit_allure.test_it.testit.properties.AdapterConfig;

import java.util.Properties;

public class ConfigManager {
    private final Properties properties;

    public ConfigManager(Properties properties){
        this.properties = properties;
    }

    public AdapterConfig getAdapterConfig(){
        return new AdapterConfig(properties);
    }

    public ClientConfiguration getClientConfiguration(){
        return new ClientConfiguration(properties);
    }
}
