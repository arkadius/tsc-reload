package pl.touk.tscreload;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import java.io.File;
import java.util.Collections;
import java.util.function.Function;

public class ReloadableConfigFactory {

    public static Reloadable<Config> parseFile(File file) {
        return new Reloadable<Config>(
                Collections.singletonList(file),
                () -> ConfigFactory.parseFile(file),
                Function.identity());
    }

}
