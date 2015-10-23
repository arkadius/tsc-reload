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

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import pl.touk.tscreload.impl.CachedConfigProvider;
import pl.touk.tscreload.impl.ConfigProviderImpl;
import pl.touk.tscreload.impl.ConfigsReloader;

import java.io.File;
import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

public class ReloadableConfigFactory {

    private static ConfigsReloader reloader = new ConfigsReloader(1); // tick: 1 second

    public static Reloadable<Config> parseFile(File file, Duration checkInterval) {
        ConfigProviderImpl configProvider = new ConfigProviderImpl(() -> ConfigFactory.parseFile(file));
        CachedConfigProvider cached = new CachedConfigProvider(Collections.singletonList(file), configProvider, checkInterval);
        reloader.add(cached);
        return new Reloadable<>(cached, Function.identity());
    }

    public static Reloadable<Config> load(List<File> scannedFiles, Supplier<Config> loadConfig, Duration checkInterval) {
        ConfigProviderImpl configProvider = new ConfigProviderImpl(loadConfig);
        CachedConfigProvider cached = new CachedConfigProvider(scannedFiles, configProvider, checkInterval);
        reloader.add(cached);
        return new Reloadable<>(cached, Function.identity());
    }

}