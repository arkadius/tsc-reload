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

import lombok.extern.slf4j.Slf4j;
import pl.touk.tscreload.Reloadable;

import java.io.File;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.function.Supplier;

@Slf4j
public class ReloadableConfig<T> extends Reloadable<T> implements Observer<Instant> {

    private final List<File> scannedFiles;

    private final Duration checkInterval;

    private final Supplier<T> configSupplier;

    private CheckInfo checkInfo;

    public ReloadableConfig(List<File> scannedFiles, Duration checkInterval, Supplier<T> configSupplier) {
        super(configSupplier.get());
        this.scannedFiles = scannedFiles;
        this.checkInterval = checkInterval;
        this.configSupplier = configSupplier;
        this.checkInfo = new CheckInfo(checkLastModified(), Instant.now());
    }

    @Override
    public synchronized void notifyChanged(Instant now) {
        CheckInfo lastCheckInfo = checkInfo;
        if (now.isAfter(lastCheckInfo.getLastCheck().plus(checkInterval))) {
            Instant lastModified = checkLastModified();
            boolean invalidated = false;
            if (lastModified.isAfter(lastCheckInfo.getLastModified())) {
                invalidated = invalidateCache();
            }
            if (invalidated) {
                checkInfo = new CheckInfo(lastModified, now);
            } else {
                checkInfo = lastCheckInfo.withLastCheck(now);
            }
        }
    }

    private Instant checkLastModified() {
        return scannedFiles.stream()
                .map(File::lastModified)
                .max(Long::compare)
                .map(Instant::ofEpochMilli)
                .orElseThrow(() -> new IllegalArgumentException("None files to scan specified."));
    }

    private boolean invalidateCache() {
        log.debug("Found changes. Reloading configuration.");
        return updateCurrentValue(prev -> configSupplier.get());
    }

}