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
package pl.touk.tscreload;

import io.vavr.Function1;
import pl.touk.tscreload.impl.Observer;
import pl.touk.tscreload.impl.ReloadableConfig;
import pl.touk.tscreload.impl.Reloader;

import java.io.File;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

public class ReloadableConfigFactory {

    static final int TICK_SECONDS = 1;

    private final static Reloader reloader = new Reloader(TICK_SECONDS);

    public static <T> Reloadable<T> load(List<File> scannedFiles,
                                         Duration checkInterval,
                                         Supplier<T> loadConfig) {
        return load(scannedFiles, checkInterval,
                (prev) -> TransformationResult.withPropagateChangeWhenValueChanged(prev, loadConfig.get()));
    }

    public static <T> Reloadable<T> load(List<File> scannedFiles,
                                         Duration checkInterval,
                                         Function1<Optional<T>, TransformationResult<T>> transformConfig) {
        ReloadableConfig<T> reloadableConfig = new ReloadableConfig<>(scannedFiles, checkInterval, transformConfig);
        reloader.addWeakObserver(reloadableConfig);
        return reloadableConfig;
    }

    public static Reloadable<Instant> addTickPropagator(Duration checkInterval) {
        TimeTriggeredReloadable<Instant> propagator = TimeTriggeredReloadable.propagatingTicks(Instant.now(), checkInterval);
        addTickObserver(propagator);
        return propagator;
    }

    public static <T extends Observer<Instant>> T addTickObserver(T observer) {
        reloader.addWeakObserver(observer);
        return observer;
    }

}