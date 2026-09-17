package com.example.taco_cloud.actuator;

import java.util.HashMap;
import java.util.Map;

import com.example.taco_cloud.repositories.TacoRepository;
import org.springframework.boot.actuate.info.Info.Builder;
import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.stereotype.Component;


@Component
public class TacoCountInfoContributor implements InfoContributor {

    private final TacoRepository tacoRepo;

    public TacoCountInfoContributor(TacoRepository tacoRepo) {
        this.tacoRepo = tacoRepo;
    }

    @Override
    public void contribute(Builder builder) {
        long tacoCount = tacoRepo.count();

        Map<String, Object> tacoMap = new HashMap<>();
        tacoMap.put("count", tacoCount);

        builder.withDetail("taco-stats", tacoMap);
    }
}