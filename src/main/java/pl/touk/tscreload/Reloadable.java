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
import pl.touk.tscreload.impl.ConfigProvider;

import java.util.function.Function;

public class Reloadable<T> {

    private ConfigProvider configProvider;

    private Function<Config, T> transformConfig;

    public Reloadable(ConfigProvider configProvider, Function<Config, T> transformConfig) {
        this.configProvider = configProvider;
        this.transformConfig = transformConfig;
    }

    public <U> Reloadable<U> map(Function<T, U> f) {
        return new Reloadable<U>(configProvider, transformConfig.andThen(f));
    }

    public T currentValue() {
        return transformConfig.apply(configProvider.getConfig());
    }

}