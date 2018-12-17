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

import io.vavr.Function2;
import pl.touk.tscreload.Reloadable;
import pl.touk.tscreload.TransformationResult;

import java.util.Optional;

public class Reloadable1<P, C> extends Reloadable<C> implements Observer<P> {

    private final Function2<P, Optional<C>, TransformationResult<C>> transform;

    public Reloadable1(P currentParentValue, Function2<P, Optional<C>, TransformationResult<C>> transform) {
        super(transform.apply(
                currentParentValue,
                Optional.empty()).getValue());
        this.transform = transform;
    }

    @Override
    public void notifyChanged(P parentValue) {
        updateCurrentValue(transform.apply(parentValue));
    }

}