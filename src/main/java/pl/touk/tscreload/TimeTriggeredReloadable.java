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

import io.vavr.Function2;
import lombok.extern.slf4j.Slf4j;
import pl.touk.tscreload.impl.Observer;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

@Slf4j
public class TimeTriggeredReloadable<T> extends Reloadable<T> implements Observer<Instant> {

    private final Duration checkInterval;

    private final Function2<Instant, Optional<T>, TransformationResult<T>> transform;

    // protected by synchronized block
    private Instant lastCheck;


    public TimeTriggeredReloadable(T currentValue, Duration checkInterval,
                                   Function2<Instant, Optional<T>, TransformationResult<T>> transform) {
        this(currentValue, Instant.now(), checkInterval, transform);
    }

    public TimeTriggeredReloadable(T currentValue, Instant currentTickValue, Duration checkInterval,
                                   Function2<Instant, Optional<T>, TransformationResult<T>> transform) {
        super(currentValue);
        this.lastCheck = currentTickValue;
        this.checkInterval = checkInterval;
        this.transform = transform;
    }

    @Override
    public synchronized void notifyChanged(Instant now) {
        if (now.isAfter(lastCheck.plus(checkInterval))) {
            try {
                handleTimeTrigger(now);
            } catch (Exception e) {
                log.error("Error while handling tick, will check next time in " + checkInterval, e);
                throw e;
            } finally {
                lastCheck = now;
            }
        }
    }

    protected void handleTimeTrigger(Instant now) {
        updateCurrentValueWithTransformed(now);
    }

    protected void updateCurrentValueWithTransformed(Instant now) {
        updateCurrentValue(transform.apply(now));
    }

    public static TimeTriggeredReloadable<Instant> propagatingTicks(Instant currentTickValue, Duration checkInterval) {
        return new TimeTriggeredReloadable<>(currentTickValue, currentTickValue, checkInterval,
                (tick, prev) -> new TransformationResult<>(tick, true));
    }

}