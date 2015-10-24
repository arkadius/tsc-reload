/*
 * Copyright 2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package pl.touk.tscreload.impl;

import com.typesafe.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

public class ConfigObservable extends Observable<Config> implements Listener<Instant>, ConfigProvider {

    private Logger logger = LoggerFactory.getLogger(getClass());

    private List<File> scannedFiles;

    private ConfigProvider targetProvider;

    private Duration checkInterval;

    private volatile ConfigWithTimestamps configWithTimestamps;

    public ConfigObservable(List<File> scannedFiles, ConfigProvider targetProvider, Duration checkInterval) {
        this.scannedFiles = scannedFiles;
        this.targetProvider = targetProvider;
        this.checkInterval = checkInterval;
        this.configWithTimestamps = new ConfigWithTimestamps(targetProvider.getConfig(), optionalLastModified().get(), Instant.now());
    }

    @Override
    public Config getConfig() {
        return configWithTimestamps.getConfig();
    }

    @Override
    public void notifyChanged(Instant now) {
        ConfigWithTimestamps current = configWithTimestamps;
        if (now.isAfter(current.getLastCheck().plus(checkInterval))) {
            configWithTimestamps = optionalLastModified()
                    .filter(lastModified -> lastModified.isAfter(current.getLastModified()))
                    .map(lastModifiedAfterCurrent -> invalidateCache(lastModifiedAfterCurrent, now))
                    .orElseGet(() -> current.withLastCheck(now));
        }
    }

    private Optional<Instant> optionalLastModified() {
        return scannedFiles.stream()
                .map(File::lastModified)
                .max(Long::compare)
                .map(Instant::ofEpochMilli);
    }

    private ConfigWithTimestamps invalidateCache(Instant lastModified, Instant lastCheck) {
        logger.debug("Found changes. Reloading configuration.");
        Config newValue = targetProvider.getConfig();
        notifyListeners(newValue);
        return new ConfigWithTimestamps(newValue, lastModified, lastCheck);
    }

}