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
import pl.touk.tscreload.TimeTriggeredReloadable;
import pl.touk.tscreload.TransformationResult;

import java.io.File;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
public class ReloadableConfig<T> extends TimeTriggeredReloadable<T> {

    private final List<File> scannedFiles;

    // protected by synchronized block - see TimeTriggeredReloadable.notifyChanged
    private Instant savedLastModified;

    public ReloadableConfig(List<File> scannedFiles, Duration checkInterval,
                            Function1<Optional<T>, TransformationResult<T>> transformConfig) {
        super(transformConfig.apply(Optional.empty()).getValue(), Instant.now(), checkInterval,
                (now, prev) -> transformConfig.apply(prev));
        this.scannedFiles = scannedFiles;
        this.savedLastModified = checkLastModified();
    }

    @Override
    protected void handleTimeTrigger(Instant now) {
        Instant currentLastModified = checkLastModified();
        if (log.isTraceEnabled()) {
            String fileNames = scannedFiles.stream().map(File::getPath).collect(Collectors.joining(", "));
            log.trace("{} Last modified for files {}: {}. Previous saved is: {}", this, fileNames, currentLastModified, savedLastModified);
        }
        if (currentLastModified.isAfter(savedLastModified)) {
            if (log.isDebugEnabled()) {
                String fileNames = scannedFiles.stream().map(File::getPath).collect(Collectors.joining(", "));
                log.debug("Last modified time for files {}: {} is after previous saved: {}. Reloading configuration...",
                        fileNames, currentLastModified, savedLastModified);
            }
            updateCurrentValueWithTransformed(now);
            savedLastModified = currentLastModified;
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