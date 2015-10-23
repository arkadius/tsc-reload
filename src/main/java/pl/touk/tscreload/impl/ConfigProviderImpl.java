package pl.touk.tscreload.impl;

import com.typesafe.config.Config;

import java.util.function.Supplier;

public class ConfigProviderImpl implements ConfigProvider {

    private Supplier<Config> loadConfig;

    public ConfigProviderImpl(Supplier<Config> loadConfig) {
        this.loadConfig = loadConfig;
    }

    @Override
    public Config getConfig() {
        return loadConfig.get();
    }
}
