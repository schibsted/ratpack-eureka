package com.schibsted.ratpack.eureka;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Scopes;
import com.google.inject.Singleton;
import com.netflix.appinfo.*;
import com.netflix.config.ConfigurationManager;
import com.netflix.config.DynamicProperty;
import com.netflix.discovery.EurekaClient;
import com.netflix.discovery.EurekaClientConfig;

import java.util.Properties;

public class EurekaModule extends AbstractModule {

    public EurekaModule(Properties properties) {
        ConfigurationManager.loadProperties(properties);
    }

    @Override
    protected void configure() {
        bind(ApplicationInfoManager.class).in(Scopes.SINGLETON);
        bind(EurekaClientConfig.class).in(Scopes.SINGLETON);
        bind(InstanceInfo.class).in(Scopes.SINGLETON);
        bind(EurekaClient.class).in(Scopes.SINGLETON);

        bind(EurekaHealthCheck.class).asEagerSingleton();
        bind(EurekaService.class).asEagerSingleton();
    }

    @Provides
    @Singleton
    public EurekaInstanceConfig provideEurekaInstanceConfig() {
        if (isCloudDataCenter()) {
            return new CloudInstanceConfig();
        }
        return new MyDataCenterInstanceConfig();
    }

    private boolean isCloudDataCenter() {
        return "cloud".equals(DynamicProperty.getInstance("eureka.datacenter").getString());
    }
}
