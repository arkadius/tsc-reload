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

import javaslang.Function5;
import pl.touk.tscreload.Reloadable;

import java.util.Optional;

public class Reloadable4<P1, P2, P3, P4, C> extends Reloadable<C> {

    private final Function5<P1, P2, P3, P4, Optional<C>, C> transform;

    private P1 currentParentValue1;

    private P2 currentParentValue2;

    private P3 currentParentValue3;

    private P4 currentParentValue4;

    public Reloadable4(P1 currentParentValue1,
                       P2 currentParentValue2,
                       P3 currentParentValue3,
                       P4 currentParentValue4,
                       Function5<P1, P2, P3, P4, Optional<C>, C> transform) {
        super(transform.apply(currentParentValue1, currentParentValue2, currentParentValue3, currentParentValue4, Optional.empty()));
        this.transform = transform;
        this.currentParentValue1 = currentParentValue1;
        this.currentParentValue2 = currentParentValue2;
        this.currentParentValue3 = currentParentValue3;
        this.currentParentValue4 = currentParentValue4;
    }

    public Observer<P1> observer1 = new Observer<P1>() {
        @Override
        public void notifyChanged(P1 changedValue1) {
            updateCurrentValue(prev -> {
                C newValue = transform.apply(changedValue1, currentParentValue2, currentParentValue3, currentParentValue4, prev);
                currentParentValue1 = changedValue1;
                return newValue;
            });
        }
    };


    public Observer<P2> observer2 = new Observer<P2>() {
        @Override
        public void notifyChanged(P2 changedValue2) {
            updateCurrentValue(prev -> {
                C newValue = transform.apply(currentParentValue1, changedValue2, currentParentValue3, currentParentValue4, prev);
                currentParentValue2 = changedValue2;
                return newValue;
            });
        }
    };

    public Observer<P3> observer3 = new Observer<P3>() {
        @Override
        public void notifyChanged(P3 changedValue3) {
            updateCurrentValue(prev -> {
                C newValue = transform.apply(currentParentValue1, currentParentValue2, changedValue3, currentParentValue4, prev);
                currentParentValue3 = changedValue3;
                return newValue;
            });
        }
    };

    public Observer<P4> observer4 = new Observer<P4>() {
        @Override
        public void notifyChanged(P4 changedValue4) {
            updateCurrentValue(prev -> {
                C newValue = transform.apply(currentParentValue1, currentParentValue2, currentParentValue3, changedValue4, prev);
                currentParentValue4 = changedValue4;
                return newValue;
            });
        }
    };

}