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

import pl.touk.tscreload.Reloadable;

import java.util.function.Function;

public abstract class AbstractReloadableNode<C> extends Reloadable<C> {

    private volatile C current;

    protected AbstractReloadableNode(C current) {
        this.current = current;
    }

    protected void updateCurrentValue(C newValue) {
        current = newValue;
        notifyListeners(newValue);
    }

    @Override
    public <U> Reloadable<U> map(Function<C, U> f) {
        ReloadableNode1<C, U> child = new ReloadableNode1<>(current, f);
        addWeakListener(child);
        return child;
    }

    @Override
    public C currentValue() {
        return current;
    }

}