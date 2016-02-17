package com.schibsted.ratpack.eureka;

import com.google.inject.Inject;
import ratpack.exec.Promise;
import ratpack.health.HealthCheck;
import ratpack.registry.Registry;

public class EurekaHealthCheck implements HealthCheck {

    private final EurekaService eurekaService;

    @Inject
    public EurekaHealthCheck(EurekaService eurekaService) {
        this.eurekaService = eurekaService;
    }

    @Override
    public String getName() {
        return "eureka-health-check";
    }

    @Override
    public Promise<Result> check(Registry registry) throws Exception {
        return eurekaService.isAvailable().map(available -> {
            if (available) {
                return Result.healthy();
            }
            return Result.unhealthy("EurekaService: Not available");
        });
    }
}
