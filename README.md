# tsc-reload

[![Build Status](https://travis-ci.org/TouK/tsc-reload.svg)](https://travis-ci.org/TouK/tsc-reload)

*tsc-reload* is a TypeSafe config wrapper for automatic reloadable configuration

## Overview

When you use plain *TypeSafe* config, you probably load config on bootstrap phase of your project. When configuration content will change, you need to restart your application. Thanks to *tsc-reload*, you decide when you want to use current value from configuration.

Sample *TypeSafe* config usage code:
```java
import com.typesafe.config.*

Config cfg = ConfigFactory.parseFile("config.conf");
int configValue = cfg.getInt("foo.bar");
```

Then you can pass this value to any place in your application. But this value will change when you change content of *config.conf*.

When you use *tsc-config* the same code will look like:
```java
import pl.touk.tscreload.*;
import java.time.*;

Reloadable<Config> cfg = ReloadableConfigFactory.parseFile("config.conf", Duration.ofSeconds(30));
Reloadable<Integer> configValue = cfg.map(c -> c.getInt("foo.bar"));
```
Then you can also pass value to any place in your application. Value is wrapped in reloadable context. You decide when you want to read current value invoking `configValue.currentValue()`. You can add any transformations to `Reloadable<T>` using `map` method e.g. wrap values with own configuration or use other lib which covert `Config` to something else.

## Interoperability

This lib is just a thin wrapper for *TypeSafe* config wirtten in *Java*. You still can mix it with other libs like e.g. [Ficus](https://github.com/ceedubs/ficus).

## Usage

With maven:

```xml
<dependency>
    <groupId>pl.touk</groupId>
    <artifactId>tsc-reload</artifactId>
    <version>0.1.0</version>
</dependency>
```

With sbt:

```sbt
libraryDependencies += "pl.touk" % "tsc-reload" % "0.1.0"
```

## License

The tsc-reload is released under version 2.0 of the [Apache License](http://www.apache.org/licenses/LICENSE-2.0).
