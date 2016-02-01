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

import javaslang.Function2;

public class ReloadableNode2<P1, P2, C> extends AbstractReloadableNode<C> {

    private final Function2<P1, P2, C> transform;

    private P1 currentParentValue1;

    private P2 currentParentValue2;

    public ReloadableNode2(P1 currentParentValue1,
                           P2 currentParentValue2,
                           Function2<P1, P2, C> transform) {
        super(transform.apply(currentParentValue1, currentParentValue2));
        this.transform = transform;
        this.currentParentValue1 = currentParentValue1;
        this.currentParentValue2 = currentParentValue2;
    }

    public Listener<P1> listener1 = new Listener<P1>() {
        @Override
        public void notifyChanged(P1 changedValue1) {
            synchronized (ReloadableNode2.this) {
                currentParentValue1 = changedValue1;
                updateCurrentValue(transform.apply(currentParentValue1, currentParentValue2));;
            }
        }
    };

    public Listener<P2> listener2 = new Listener<P2>() {
        @Override
        public void notifyChanged(P2 changedValue2) {
            synchronized (ReloadableNode2.this) {
                currentParentValue2 = changedValue2;
                updateCurrentValue(transform.apply(currentParentValue1, currentParentValue2));;
            }
        }
    };

}