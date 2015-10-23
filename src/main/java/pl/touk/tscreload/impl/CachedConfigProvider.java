package pl.touk.tscreload.impl;

import com.typesafe.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

public class CachedConfigProvider implements ConfigProvider {

    private Logger logger = LoggerFactory.getLogger(getClass());

    private List<File> scannedFiles;

    private ConfigProvider targetProvider;

    private Duration checkInterval;

    private volatile ConfigWithTimestamps configWithTimestamps;

    public CachedConfigProvider(List<File> scannedFiles, ConfigProvider targetProvider, Duration checkInterval) {
        this.scannedFiles = scannedFiles;
        this.targetProvider = targetProvider;
        this.checkInterval = checkInterval;
        this.configWithTimestamps = new ConfigWithTimestamps(targetProvider.getConfig(), optionalLastModified().get(), Instant.now());
    }

    @Override
    public Config getConfig() {
        return configWithTimestamps.getConfig();
    }

    public void invalidateCacheIfNeed(Instant now) {
        ConfigWithTimestamps current = configWithTimestamps;
        if (now.isAfter(current.getLastCheck().plus(checkInterval))) {
            configWithTimestamps = optionalLastModified()
                    .filter(lastModified -> lastModified.isAfter(current.getLastModified()))
                    .map(lastModifiedAfterCurrent -> {
                        logger.debug("Found changes. Reloading configuration.");
                        return invalidateCache(lastModifiedAfterCurrent, now);
                    })
                    .orElseGet(() -> current.withLastCheck(now));
        }
    }

    public Optional<Instant> optionalLastModified() {
        return scannedFiles.stream()
                .map(File::lastModified)
                .max(Long::compare)
                .map(Instant::ofEpochMilli);
    }

    private ConfigWithTimestamps invalidateCache(Instant lastModified, Instant lastCheck) {
        Config newValue = targetProvider.getConfig();
        return new ConfigWithTimestamps(newValue, lastModified, lastCheck);
    }

}