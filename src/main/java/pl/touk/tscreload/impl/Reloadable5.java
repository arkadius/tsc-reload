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

import io.vavr.Function6;
import pl.touk.tscreload.Reloadable;
import pl.touk.tscreload.TransformationResult;

import java.util.Optional;

public class Reloadable5<P1, P2, P3, P4, P5, C> extends Reloadable<C> {

    private final Function6<P1, P2, P3, P4, P5, Optional<C>, TransformationResult<C>> transform;

    // access to currentParentValue* is synchronized because of synchronized updateCurrentValue
    private P1 currentParentValue1;

    private P2 currentParentValue2;

    private P3 currentParentValue3;

    private P4 currentParentValue4;

    private P5 currentParentValue5;

    public Reloadable5(P1 currentParentValue1,
                       P2 currentParentValue2,
                       P3 currentParentValue3,
                       P4 currentParentValue4,
                       P5 currentParentValue5,
                       Function6<P1, P2, P3, P4, P5, Optional<C>, TransformationResult<C>> transform) {
        super(transform.apply(
                currentParentValue1,
                currentParentValue2,
                currentParentValue3,
                currentParentValue4,
                currentParentValue5,
                Optional.empty()).getValue());
        this.transform = transform;
        this.currentParentValue1 = currentParentValue1;
        this.currentParentValue2 = currentParentValue2;
        this.currentParentValue3 = currentParentValue3;
        this.currentParentValue4 = currentParentValue4;
        this.currentParentValue5 = currentParentValue5;
    }

    public Observer<P1> observer1 = new Observer<P1>() {
        @Override
        public void notifyChanged(P1 parentValue1) {
            Reloadable5.this.updateCurrentValue(prev -> {
                TransformationResult<C> newValue = transform.apply(
                        parentValue1, currentParentValue2, currentParentValue3, currentParentValue4, currentParentValue5, prev);
                currentParentValue1 = parentValue1;
                return newValue;
            });
        }
    };


    public Observer<P2> observer2 = new Observer<P2>() {
        @Override
        public void notifyChanged(P2 parentValue2) {
            Reloadable5.this.updateCurrentValue(prev -> {
                TransformationResult<C> newValue = transform.apply(
                        currentParentValue1, parentValue2, currentParentValue3, currentParentValue4, currentParentValue5, prev);
                currentParentValue2 = parentValue2;
                return newValue;
            });
        }
    };

    public Observer<P3> observer3 = new Observer<P3>() {
        @Override
        public void notifyChanged(P3 parentValue3) {
            Reloadable5.this.updateCurrentValue(prev -> {
                TransformationResult<C> newValue = transform.apply(
                        currentParentValue1, currentParentValue2, parentValue3, currentParentValue4, currentParentValue5, prev);
                currentParentValue3 = parentValue3;
                return newValue;
            });
        }
    };

    public Observer<P4> observer4 = new Observer<P4>() {
        @Override
        public void notifyChanged(P4 parentValue4) {
            Reloadable5.this.updateCurrentValue(prev -> {
                TransformationResult<C> newValue = transform.apply(
                        currentParentValue1, currentParentValue2, currentParentValue3, parentValue4, currentParentValue5, prev);
                currentParentValue4 = parentValue4;
                return newValue;
            });
        }
    };

    public Observer<P5> observer5 = new Observer<P5>() {
        @Override
        public void notifyChanged(P5 parentValue5) {
            Reloadable5.this.updateCurrentValue(prev -> {
                TransformationResult<C> newValue = transform.apply(
                        currentParentValue1, currentParentValue2, currentParentValue3, currentParentValue4, parentValue5, prev);
                currentParentValue5 = parentValue5;
                return newValue;
            });
        }
    };

}