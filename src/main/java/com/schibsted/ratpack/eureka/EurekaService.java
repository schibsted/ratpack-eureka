package com.schibsted.ratpack.eureka;

import com.google.inject.Inject;
import com.netflix.appinfo.ApplicationInfoManager;
import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ratpack.exec.Blocking;
import ratpack.exec.Promise;
import ratpack.server.Service;
import ratpack.server.StartEvent;
import ratpack.server.StopEvent;

public class EurekaService implements Service {

    private static final Logger log = LoggerFactory.getLogger(EurekaService.class);

    private final ApplicationInfoManager applicationInfoManager;
    private final EurekaClient eurekaClient;

    @Inject
    public EurekaService(ApplicationInfoManager applicationInfoManager, EurekaClient eurekaClient) {
        this.applicationInfoManager = applicationInfoManager;
        this.eurekaClient = eurekaClient;
    }

    @Override
    public void onStart(StartEvent event) throws Exception {
        log.info("Service starting...");
        applicationInfoManager.setInstanceStatus(InstanceInfo.InstanceStatus.UP);
        log.info("Service started.");
    }

    @Override
    public void onStop(StopEvent event) throws Exception {
        log.info("Service stopping...");
        applicationInfoManager.setInstanceStatus(InstanceInfo.InstanceStatus.DOWN);
        if (eurekaClient != null) {
            eurekaClient.shutdown();
        }
        log.info("Service stopped.");
    }

    public Promise<Boolean> isAvailable() {
        return Blocking.get(() -> {
            // Eureka client caches all data. This means that everything still looks ok even when we lose connection
            // with Eureka server. This check only validates that we were able to connect to Eureka server at some time.
            String vipAddress = applicationInfoManager.getInfo().getVIPAddress();
            InstanceInfo instance = eurekaClient.getNextServerFromEureka(vipAddress, false);
            return instance.getVIPAddress().equals(vipAddress);
        });
    }

}
