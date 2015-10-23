package pl.touk.tscreload.impl;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ConfigsReloader implements Runnable {

    public static final int TICK_SECONDS = 1;

    private final List<CachedConfigProvider> configProviders = new ArrayList<>();

    public ConfigsReloader(int tickDelaySeconds) {
        final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(TICK_SECONDS);
        scheduler.scheduleAtFixedRate(this, tickDelaySeconds, tickDelaySeconds, TimeUnit.SECONDS);
    }

    public void add(CachedConfigProvider configProvider) {
        synchronized (configProviders) {
            configProviders.add(configProvider);
        }
    }

    @Override
    public void run() {
        Instant now = Instant.now();
        synchronized (configProviders) {
            configProviders.forEach(c -> c.invalidateCacheIfNeed(now));
        }
    }

}