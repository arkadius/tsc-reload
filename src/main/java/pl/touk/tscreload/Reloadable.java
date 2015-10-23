package pl.touk.tscreload;

import com.typesafe.config.Config;

import java.io.File;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

public class Reloadable<T> {

    private List<File> scannedFiles;

    private Supplier<Config> loadConfig;

    private Function<Config, T> transformConfig;

    Reloadable(List<File> scannedFiles, Supplier<Config> loadConfig, Function<Config, T> transformConfig) {
        this.scannedFiles = scannedFiles;
        this.loadConfig = loadConfig;
        this.transformConfig = transformConfig;
    }

    public <U> Reloadable<U> map(Function<T, U> f) {
        return new Reloadable<U>(scannedFiles, loadConfig, transformConfig.andThen(f));
    }

    public T currentValue() {
        return transformConfig.apply(loadConfig.get());
    }

}
