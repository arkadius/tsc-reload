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

public class ReloadableNode<P, C> extends Reloadable<C> implements Listener<P> {

    private final Function<P, C> transform;

    private volatile C current;

    public ReloadableNode(P currentParentValue, Function<P, C> transform) {
        this.transform = transform;
        this.current = transform.apply(currentParentValue);
    }

    @Override
    public void notifyChanged(P changedParentValue) {
        C newValue = transform.apply(changedParentValue);
        current = newValue;
        notifyListeners(newValue);
    }

    @Override
    public <U> ReloadableNode<C, U> map(Function<C, U> f) {
        ReloadableNode<C, U> child = new ReloadableNode<>(current, f);
        addWeakListener(child);
        return child;
    }

    @Override
    public C currentValue() {
        return current;
    }

}