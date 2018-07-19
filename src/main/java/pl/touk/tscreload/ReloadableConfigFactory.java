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
import pl.touk.tscreload.impl.ReloadableConfig;
import pl.touk.tscreload.impl.Reloader;

import java.io.File;
import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

public class ReloadableConfigFactory {

    static final int TICK_SECONDS = 1;

    private final static Reloader reloader = new Reloader(TICK_SECONDS);

    public static Reloadable<Config> parseFile(File file, Duration checkInterval) {
        return load(
                Collections.singletonList(file),
                checkInterval,
                () -> ConfigFactory.parseFile(file));
    }

    public static <T> Reloadable<T> load(List<File> scannedFiles,
                                         Duration checkInterval,
                                         Supplier<T> loadConfig) {
        ReloadableConfig<T> reloadableConfig = new ReloadableConfig<>(scannedFiles, checkInterval, loadConfig);
        reloader.addWeakObserver(reloadableConfig);
        return reloadableConfig;
    }

}