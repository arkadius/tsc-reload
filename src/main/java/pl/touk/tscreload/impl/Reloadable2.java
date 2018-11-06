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

import io.vavr.Function3;
import pl.touk.tscreload.Reloadable;

import java.util.Optional;

public class Reloadable2<P1, P2, C> extends Reloadable<C> {

    private final Function3<P1, P2, Optional<C>, C> transform;

    private P1 currentParentValue1;

    private P2 currentParentValue2;

    public Reloadable2(P1 currentParentValue1,
                       P2 currentParentValue2,
                       Function3<P1, P2, Optional<C>, C> transform,
                       boolean propagateOnlyIfChanged) {
        super(transform.apply(currentParentValue1, currentParentValue2, Optional.empty()), propagateOnlyIfChanged);
        this.transform = transform;
        this.currentParentValue1 = currentParentValue1;
        this.currentParentValue2 = currentParentValue2;
    }

    public Observer<P1> observer1 = new Observer<P1>() {
        @Override
        public void notifyChanged(P1 changedValue1) {
            updateCurrentValue(prev -> {
                C newValue = transform.apply(changedValue1, currentParentValue2, prev);
                currentParentValue1 = changedValue1;
                return newValue;
            });
        }
    };

    public Observer<P2> observer2 = new Observer<P2>() {
        @Override
        public void notifyChanged(P2 changedValue2) {
            updateCurrentValue(prev -> {
                C newValue = transform.apply(currentParentValue1, changedValue2, prev);
                currentParentValue2 = changedValue2;
                return newValue;
            });
        }
    };

}