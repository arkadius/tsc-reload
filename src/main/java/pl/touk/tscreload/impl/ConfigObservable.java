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
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

@Slf4j
public class ConfigObservable extends AbstractReloadableNode<Config> implements Observer<Instant> {

    private final List<File> scannedFiles;

    private final Duration checkInterval;

    private final Supplier<Config> configSupplier;

    private volatile ConfigWithTimestamps configWithTimestamps;

    public ConfigObservable(List<File> scannedFiles, Duration checkInterval, Supplier<Config> configSupplier) {
        super(configSupplier.get());
        this.scannedFiles = scannedFiles;
        this.checkInterval = checkInterval;
        this.configSupplier = configSupplier;
        this.configWithTimestamps = new ConfigWithTimestamps(configSupplier.get(), optionalLastModified().get(), Instant.now());
    }

    @Override
    public void notifyChanged(Instant now) {
        ConfigWithTimestamps current = configWithTimestamps;
        if (now.isAfter(current.getLastCheck().plus(checkInterval))) {
            optionalLastModified()
                    .filter(lastModified -> lastModified.isAfter(current.getLastModified()))
                    .map(lastModifiedAfterCurrent -> invalidateCache(lastModifiedAfterCurrent, now))
                    .orElseGet(() -> {
                        configWithTimestamps = current.withLastCheck(now);
                        return null;
                    });
        }
    }

    private Optional<Instant> optionalLastModified() {
        return scannedFiles.stream()
                .map(File::lastModified)
                .max(Long::compare)
                .map(Instant::ofEpochMilli);
    }

    private Void invalidateCache(Instant lastModified, Instant lastCheck) {
        log.debug("Found changes. Reloading configuration.");
        Config newValue = configSupplier.get();
        configWithTimestamps = new ConfigWithTimestamps(newValue, lastModified, lastCheck);
        updateCurrentValue(newValue);
        return null;
    }

}