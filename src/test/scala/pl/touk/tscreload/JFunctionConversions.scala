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
package pl.touk.tscreload

import io.vavr.{Function1 => JFunction1}
import io.vavr.{Function2 => JFunction2}
import io.vavr.{Function3 => JFunction3}
import io.vavr.{Function4 => JFunction4}
import io.vavr.{Function5 => JFunction5}
import io.vavr.{Function6 => JFunction6}

import scala.language.implicitConversions

object JFunctionConversions {

  implicit def toJavaFunction1[P1, R](f: Function1[P1, R]): JFunction1[P1, R] = new JFunction1[P1, R] {
    override def apply(p1: P1): R = f(p1)
  }

  implicit def toJavaFunction2[P1, P2, R](f: Function2[P1, P2, R]): JFunction2[P1, P2, R] = new JFunction2[P1, P2, R] {
    override def apply(p1: P1, p2: P2): R = f(p1, p2)
  }

  implicit def toJavaFunction3[P1, P2, P3, R](f: Function3[P1, P2, P3, R]): JFunction3[P1, P2, P3, R] =
    new JFunction3[P1, P2, P3, R] {
      override def apply(p1: P1, p2: P2, p3: P3): R = f(p1, p2, p3)
    }

  implicit def toJavaFunction4[P1, P2, P3, P4, R](f: Function4[P1, P2, P3, P4, R]): JFunction4[P1, P2, P3, P4, R] =
    new JFunction4[P1, P2, P3, P4, R] {
      override def apply(p1: P1, p2: P2, p3: P3, p4: P4): R = f(p1, p2, p3, p4)
    }

  implicit def toJavaFunction5[P1, P2, P3, P4, P5, R](f: Function5[P1, P2, P3, P4, P5, R]): JFunction5[P1, P2, P3, P4, P5, R] =
    new JFunction5[P1, P2, P3, P4, P5, R] {
      override def apply(p1: P1, p2: P2, p3: P3, p4: P4, p5: P5): R = f(p1, p2, p3, p4, p5)
    }

  implicit def toJavaFunction6[P1, P2, P3, P4, P5, P6, R](f: Function6[P1, P2, P3, P4, P5, P6, R]): JFunction6[P1, P2, P3, P4, P5, P6, R] =
    new JFunction6[P1, P2, P3, P4, P5, P6, R] {
      override def apply(p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6): R = f(p1, p2, p3, p4, p5, p6)
    }

}