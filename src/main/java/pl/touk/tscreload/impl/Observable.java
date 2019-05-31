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

import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.Set;
import java.util.HashMap;

@Slf4j
public abstract class Observable<T> {

    private final Set<Observer<T>> observers = Collections.newSetFromMap(new HashMap<>());

    public void addWeakObserver(Observer<T> observer) {
        synchronized (observers) {
            // We are adding hard reference to ensure that chain won't be breaked
            observer.addParent(this);
            observers.add(observer);
            if (!observers.isEmpty()) {
                log.trace("{} Added observer {}. Current observer count: {}", this, observer, observers.size());
            }
        }
    }

    protected void notifyObservers(T changedValue) {
        synchronized (observers) {
            if (!observers.isEmpty()) {
                log.trace("{} Notifying {} observers...", this, observers.size());
            }
            observers.forEach(o -> {
                try {
                    o.notifyChanged(changedValue);
                } catch (Exception ex) {
                    log.error("Exception while notify about changed value. Propagation of changes for child nodes will be discarded.", ex);
                }
            });
        }
    }

}