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

import javaslang.Function2;
import javaslang.Function3;
import javaslang.Function4;
import javaslang.Function5;
import pl.touk.tscreload.impl.*;

import java.util.function.Function;

public abstract class Reloadable<T> extends Observable<T> {

    private T current;

    protected Reloadable(T current) {
        this.current = current;
    }

    public <U> Reloadable<U> map(Function<T, U> f) {
        Reloadable1<T, U> child = new Reloadable1<>(currentValue(), f);
        addWeakObserver(child);
        return child;
    }

    protected synchronized void updateCurrentValue(T newValue) {
        if (!current.equals(newValue)) {
            current = newValue;
            notifyObservers(newValue);
        }
    }

    public synchronized T currentValue() {
        return current;
    }

    public static <R1, R2, U> Reloadable<U> compose(Reloadable<R1> r1,
                                                    Reloadable<R2> r2,
                                                    Function2<R1, R2, U> f) {
        Reloadable2<R1, R2, U> reloadable = new Reloadable2<>(
                r1.currentValue(),
                r2.currentValue(),
                f);
        r1.addWeakObserver(reloadable.observer1);
        r2.addWeakObserver(reloadable.observer2);
        return reloadable;
    }

    public static <R1, R2, R3, U> Reloadable<U> compose(Reloadable<R1> r1,
                                                        Reloadable<R2> r2,
                                                        Reloadable<R3> r3,
                                                        Function3<R1, R2, R3, U> f) {
        Reloadable3<R1, R2, R3, U> reloadable = new Reloadable3<>(
                r1.currentValue(),
                r2.currentValue(),
                r3.currentValue(),
                f);
        r1.addWeakObserver(reloadable.observer1);
        r2.addWeakObserver(reloadable.observer2);
        r3.addWeakObserver(reloadable.observer3);
        return reloadable;
    }

    public static <R1, R2, R3, R4, U> Reloadable<U> compose(Reloadable<R1> r1,
                                                            Reloadable<R2> r2,
                                                            Reloadable<R3> r3,
                                                            Reloadable<R4> r4,
                                                            Function4<R1, R2, R3, R4, U> f) {
        Reloadable4<R1, R2, R3, R4, U> reloadable = new Reloadable4<>(
                r1.currentValue(),
                r2.currentValue(),
                r3.currentValue(),
                r4.currentValue(),
                f);
        r1.addWeakObserver(reloadable.observer1);
        r2.addWeakObserver(reloadable.observer2);
        r3.addWeakObserver(reloadable.observer3);
        r4.addWeakObserver(reloadable.observer4);
        return reloadable;
    }

    public static <R1, R2, R3, R4, R5, U> Reloadable<U> compose(Reloadable<R1> r1,
                                                                Reloadable<R2> r2,
                                                                Reloadable<R3> r3,
                                                                Reloadable<R4> r4,
                                                                Reloadable<R5> r5,
                                                                Function5<R1, R2, R3, R4, R5, U> f) {
        Reloadable5<R1, R2, R3, R4, R5, U> reloadable = new Reloadable5<>(
                r1.currentValue(),
                r2.currentValue(),
                r3.currentValue(),
                r4.currentValue(),
                r5.currentValue(),
                f);
        r1.addWeakObserver(reloadable.observer1);
        r2.addWeakObserver(reloadable.observer2);
        r3.addWeakObserver(reloadable.observer3);
        r4.addWeakObserver(reloadable.observer4);
        r5.addWeakObserver(reloadable.observer5);
        return reloadable;
    }

}