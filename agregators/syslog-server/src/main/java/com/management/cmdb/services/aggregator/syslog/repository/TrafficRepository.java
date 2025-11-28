package com.management.cmdb.services.aggregator.syslog.repository;

import com.management.cmdb.services.aggregator.syslog.model.Traffic;
import com.management.cmdb.services.aggregator.syslog.server.PaloAltoSyslogMessage;
import org.jspecify.annotations.NonNull;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentMap;

@Component
public class TrafficRepository {

    private final CacheManager cacheManager;

    public TrafficRepository(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    public void save(String sourceHost, String destinationHost, Traffic traffic) {
        Cache cachedTraffics = cacheManager.getCache("traffics");
        if (cachedTraffics != null) {
            Map<String, Traffic> listOfTraffic = cachedTraffics.get(sourceHost, Map.class);
            if (listOfTraffic == null) {
                listOfTraffic = new HashMap<>();
            }
            listOfTraffic.put(destinationHost, traffic);
            cachedTraffics.put(sourceHost, listOfTraffic);
        }
    }

    public Optional<Traffic> getOneTraffic(String sourceHost, String destinationHost) {
        Cache cachedTraffics = cacheManager.getCache("traffics");
        if (cachedTraffics != null) {
            Map<String, Traffic> listOfTraffic = cachedTraffics.get(sourceHost, Map.class);
            if (listOfTraffic != null) {
                Traffic traffic = listOfTraffic.get(destinationHost);
                if (traffic != null) {
                    return Optional.of(traffic);
                }
            }
        }
        return Optional.empty();
    }

    public Map<String, Map<String, Traffic>> getTraffics() {
        Cache traffics = cacheManager.getCache("traffics");
        Map<String, Map<String, Traffic>> result = new java.util.HashMap<>();
        if (traffics instanceof CaffeineCache caffeineCache) {
            ConcurrentMap<Object, @NonNull Object> map = caffeineCache.getNativeCache().asMap();
            map.forEach((key, value) -> result.put(key.toString(), (Map<String, Traffic>) value));
        }
        return result;
    }

}
