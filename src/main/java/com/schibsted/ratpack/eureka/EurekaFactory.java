package com.schibsted.ratpack.eureka;

import com.google.common.base.Preconditions;
import com.google.inject.Module;
import com.google.inject.util.Modules;
import ratpack.server.ServerConfig;

import java.net.URL;
import java.util.Properties;

public class EurekaFactory {

    public enum DataCenter {
        AMAZON,
        LOCAL
    }

    private String name;
    private String vipAddress;
    private DataCenter dataCenter;
    private URL defaultServiceUrl;
    private String healthCheckUrl;
    private Boolean enabled;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVipAddress() {
        return vipAddress;
    }

    public void setVipAddress(String vipAddress) {
        this.vipAddress = vipAddress;
    }

    public DataCenter getDataCenter() {
        return dataCenter;
    }

    public void setDataCenter(DataCenter dataCenter) {
        this.dataCenter = dataCenter;
    }

    public URL getDefaultServiceUrl() {
        return defaultServiceUrl;
    }

    public void setDefaultServiceUrl(URL defaultServiceUrl) {
        this.defaultServiceUrl = defaultServiceUrl;
    }

    public String getHealthCheckUrl() {
        return healthCheckUrl;
    }

    public void setHealthCheckUrl(String healthCheckUrl) {
        this.healthCheckUrl = healthCheckUrl;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public Module buildModule(ServerConfig serverConfig) {
        if (enabled) {

            Preconditions.checkNotNull(name);
            Preconditions.checkNotNull(defaultServiceUrl);
            Preconditions.checkNotNull(healthCheckUrl);
            Preconditions.checkNotNull(dataCenter);

            Properties properties = new Properties();

            properties.setProperty("eureka.name", name);
            properties.setProperty("eureka.serviceUrl.default", defaultServiceUrl.toString());
            properties.setProperty("eureka.healthCheckUrl", healthCheckUrl);
            properties.setProperty("eureka.datacenter", dataCenter == DataCenter.LOCAL ? "local" : "cloud");

            properties.setProperty("eureka.port", String.valueOf(serverConfig.getPort()));
            properties.setProperty("eureka.vipAddress", vipAddress != null ? vipAddress : name);
            properties.setProperty("eureka.statusPageUrl", healthCheckUrl);

            return new EurekaModule(properties);
        }
        return Modules.EMPTY_MODULE;
    }
}
