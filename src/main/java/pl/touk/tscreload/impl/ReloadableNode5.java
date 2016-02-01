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

public class ReloadableNode5<P1, P2, P3, P4, P5, C> extends AbstractReloadableNode<C> {

    private final Function5<P1, P2, P3, P4, P5, C> transform;

    private P1 currentParentValue1;

    private P2 currentParentValue2;

    private P3 currentParentValue3;

    private P4 currentParentValue4;

    private P5 currentParentValue5;

    public ReloadableNode5(P1 currentParentValue1,
                           P2 currentParentValue2,
                           P3 currentParentValue3,
                           P4 currentParentValue4,
                           P5 currentParentValue5,
                           Function5<P1, P2, P3, P4, P5, C> transform) {
        super(transform.apply(
                currentParentValue1,
                currentParentValue2,
                currentParentValue3,
                currentParentValue4,
                currentParentValue5));
        this.transform = transform;
        this.currentParentValue1 = currentParentValue1;
        this.currentParentValue2 = currentParentValue2;
        this.currentParentValue3 = currentParentValue3;
        this.currentParentValue4 = currentParentValue4;
        this.currentParentValue5 = currentParentValue5;
    }

    public Observer<P1> observer1 = new Observer<P1>() {
        @Override
        public void notifyChanged(P1 changedValue1) {
            synchronized (ReloadableNode5.this) {
                currentParentValue1 = changedValue1;
                updateCurrentValue(transform.apply(
                        currentParentValue1,
                        currentParentValue2,
                        currentParentValue3,
                        currentParentValue4,
                        currentParentValue5));
            }
        }
    };

    public Observer<P2> observer2 = new Observer<P2>() {
        @Override
        public void notifyChanged(P2 changedValue2) {
            synchronized (ReloadableNode5.this) {
                currentParentValue2 = changedValue2;
                updateCurrentValue(transform.apply(
                        currentParentValue1,
                        currentParentValue2,
                        currentParentValue3,
                        currentParentValue4,
                        currentParentValue5));
            }
        }
    };

    public Observer<P3> observer3 = new Observer<P3>() {
        @Override
        public void notifyChanged(P3 changedValue3) {
            synchronized (ReloadableNode5.this) {
                currentParentValue3 = changedValue3;
                updateCurrentValue(transform.apply(
                        currentParentValue1,
                        currentParentValue2,
                        currentParentValue3,
                        currentParentValue4,
                        currentParentValue5));
            }
        }
    };

    public Observer<P4> observer4 = new Observer<P4>() {
        @Override
        public void notifyChanged(P4 changedValue4) {
            synchronized (ReloadableNode5.this) {
                currentParentValue4 = changedValue4;
                updateCurrentValue(transform.apply(
                        currentParentValue1,
                        currentParentValue2,
                        currentParentValue3,
                        currentParentValue4,
                        currentParentValue5));
            }
        }
    };

    public Observer<P5> observer5 = new Observer<P5>() {
        @Override
        public void notifyChanged(P5 changedValue5) {
            synchronized (ReloadableNode5.this) {
                currentParentValue5 = changedValue5;
                updateCurrentValue(transform.apply(
                        currentParentValue1,
                        currentParentValue2,
                        currentParentValue3,
                        currentParentValue4,
                        currentParentValue5));
            }
        }
    };

}