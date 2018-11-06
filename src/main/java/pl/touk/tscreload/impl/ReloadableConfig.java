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

import io.vavr.Function1;
import lombok.extern.slf4j.Slf4j;
import pl.touk.tscreload.Reloadable;

import java.io.File;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Slf4j
public class ReloadableConfig<T> extends Reloadable<T> implements Observer<Instant> {

    private final List<File> scannedFiles;

    private final Duration checkInterval;

    private final Function1<Optional<T>, T> transformConfig;

    // protected by synchronized block
    private CheckInfo checkInfo;

    public ReloadableConfig(List<File> scannedFiles, Duration checkInterval,
                            Function1<Optional<T>, T> transformConfig, boolean propagateOnlyIfChanged) {
        super(transformConfig.apply(Optional.empty()), propagateOnlyIfChanged);
        this.scannedFiles = scannedFiles;
        this.checkInterval = checkInterval;
        this.transformConfig = transformConfig;
        this.checkInfo = new CheckInfo(checkLastModified(), Instant.now());
    }

    @Override
    public synchronized void notifyChanged(Instant now) {
        if (now.isAfter(checkInfo.getLastCheck().plus(checkInterval))) {
            try {
                Instant lastModified = checkLastModified();
                if (lastModified.isAfter(checkInfo.getLastModified())) {
                    log.debug("Last modified time: " + lastModified + " is after previous saved: " + checkInfo.getLastModified() +
                            ". Reloading configuration...");
                    updateCurrentValue(transformConfig);
                    checkInfo = checkInfo.withLastModified(lastModified);
                }
            } catch (Exception e) {
                log.error("Error while loading config, will check next time in " + checkInterval, e);
                throw e;
            } finally {
                checkInfo = checkInfo.withLastCheck(now);
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

}