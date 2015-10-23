package pl.touk.tscreload.impl;

import com.typesafe.config.Config;

import java.time.Instant;

public class ConfigWithTimestamps {

    private Config config;

    private Instant lastModified;

    private Instant lastCheck;

    public ConfigWithTimestamps(Config config, Instant lastModified, Instant lastCheck) {
        this.config = config;
        this.lastModified = lastModified;
        this.lastCheck = lastCheck;
    }

    public ConfigWithTimestamps withLastCheck(Instant newLastCheck) {
        return new ConfigWithTimestamps(config, lastModified, newLastCheck);
    }

    public Config getConfig() {
        return config;
    }

    public Instant getLastModified() {
        return lastModified;
    }

    public Instant getLastCheck() {
        return lastCheck;
    }
}
