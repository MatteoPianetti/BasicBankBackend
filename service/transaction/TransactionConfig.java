package com.example.demo.service.transaction;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import com.example.demo.model.transaction.Type;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TransactionConfig {

    @Bean
    public Map<Type, Validator> validatorMap(List<Validator> validators) {
        Map<Type, Validator> map = new EnumMap<>(Type.class);
        for (Validator v : validators) {
            if(v instanceof MultiTypeValidator mtv) {
                for(Type t : mtv.getSupportedTypes()) {
                    map.put(t,v);
                }
            } else {
                map.put(v.getType(), v);
            }
        }
        return map;
    }

    @Bean
    public Map<Type, Logic> logicMap(List<Logic> logics) {
        Map<Type, Logic> map = new EnumMap<>(Type.class);
        for (Logic l : logics) {
            if(l instanceof MultiTypeLogic mtl) {
                for(Type t : mtl.getSupportedTypes()) {
                    map.put(t, l);
                }
            } else {
                map.put(l.getType(), l);
            }
        }
        return map;
    }
}

