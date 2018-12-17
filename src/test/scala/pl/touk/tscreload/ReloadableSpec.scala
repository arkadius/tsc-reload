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

import java.io.{File, PrintWriter}
import java.time.Duration
import java.util.Optional
import java.util.concurrent.atomic.AtomicInteger

import com.typesafe.config.{Config, ConfigFactory}
import net.ceedubs.ficus.Ficus
import net.ceedubs.ficus.readers.ArbitraryTypeReader
import org.scalatest._

class ReloadableSpec extends fixture.FlatSpec with Matchers with GivenWhenThen {
  import ArbitraryTypeReader._
  import Ficus._
  import JFunctionConversions._
  import collection.convert.decorateAsJava._

  it should "provide initial value" in { fixture =>
    import fixture._
    When("load reloadable initial value")
    val initialFooBarValue = 1
    val reloadableFooBar = loadReloadableValue(initialFooBarValue)

    Then("nested value should be same as initial")
    reloadableFooBar.currentValue() shouldEqual initialFooBarValue
  }

  it should "reload nested value after change" in { fixture =>
    import fixture._
    Given("reloadable initial value")
    val reloadableFooBar = loadReloadableValue(1)

    When("write new value to config file")
    val nextFooBarValue = 2
    Thread.sleep(1000) // for make sure that last modified was changed
    writeValueToConfigFile(nextFooBarValue)

    Then("after reload nested value should be same as new value")
    Thread.sleep(ReloadableConfigFactory.TICK_SECONDS * 1000 + 500)
    reloadableFooBar.currentValue() shouldEqual nextFooBarValue
  }

  it should "reload composed value after change" in { fixture =>
    import fixture._
    Given("reloadable initial values")
    val initialBase = 0
    val leftDelta = 1
    val rightDelta = 2
    val reloadable = loadReloadableConfig(initialBase)

    val left = reloadable.map((cfg: Config) => cfg.getInt("foo.bar") + leftDelta)
    val right = reloadable.map((cfg: Config) => cfg.getInt("foo.bar") + rightDelta)

    When("compose")
    val reloadableComposed = Reloadable.compose(left, right, (l: Int, r: Int) => l + r)

    Then("should compote initial sum")
    reloadableComposed.currentValue() shouldEqual (initialBase + leftDelta + initialBase + rightDelta)


    When("write new value to config file")
    val newBase = 2
    Thread.sleep(1000) // for make sure that last modified was changed
    writeValueToConfigFile(newBase)

    Then("after reload composed value should be recalculated")
    Thread.sleep(ReloadableConfigFactory.TICK_SECONDS * 1000 + 500)
    reloadableComposed.currentValue() shouldEqual (newBase + leftDelta + newBase + rightDelta)
  }

  it should "cache evaluation of nested values" in { fixture =>
    import fixture._
    Given("reloadable initial config")
    val initialFooBarValue = 1
    val reloadable = loadReloadableConfig(initialFooBarValue)

    When("transform reloadable config to return nested value")
    var evaluationCount = 0
    val reloadableFooBar = reloadable.map { cfg: Config =>
      evaluationCount += 1
      cfg.getInt("foo.bar")
    }

    And("double get current value")
    reloadableFooBar.currentValue() shouldEqual initialFooBarValue
    reloadableFooBar.currentValue() shouldEqual initialFooBarValue

    Then("value should be evaluated once")
    evaluationCount shouldEqual 1
  }

  it should "be able to handle previous value" in { fixture =>
    import fixture._
    Given("reloadable initial config")
    val initialFooBarValue = 1
    val reloadable = loadReloadableConfig(initialFooBarValue)

    When("transform reloadable config to return nested value")
    var savedPrev: Optional[Int] = null
    val reloadableFooBar = reloadable.map[Int] { (cfg: Config, prev: Optional[Int]) =>
      savedPrev = prev
      new TransformationResult(cfg.getInt("foo.bar"), true)
    }

    When("write new value to config file")
    val newValue = 2
    Thread.sleep(1000) // for make sure that last modified was changed
    writeValueToConfigFile(newValue)

    Then("after reload first value should be the initial one")
    Thread.sleep(ReloadableConfigFactory.TICK_SECONDS * 1000 + 500)
    reloadableFooBar.currentValue() shouldEqual newValue
    Then("second value should be the new one")
    savedPrev shouldEqual Optional.of(initialFooBarValue)
  }

  it should "not break reload process if some child will throw exception" in { fixture =>
    import fixture._
    Given("reloadable initial config")
    val initialFooBarValue = 1
    val newValue = 2
    val reloadable = loadReloadableConfig(initialFooBarValue)

    When("transform reloadable config to return nested value")
    val firstFooBar = reloadable.map { cfg: Config =>
      val v = cfg.getInt("foo.bar")
      if (v == newValue) throw new Exception("fail")
      v
    }
    var secReloaded= false
    val secFooBar = reloadable.map { cfg: Config =>
      val v = cfg.getInt("foo.bar")
      if (v == newValue) secReloaded = true
      v
    }

    When("write new value to config file")
    Thread.sleep(1000) // for make sure that last modified was changed
    writeValueToConfigFile(newValue)

    Then("after reload previous value should be the initial one")
    Thread.sleep(ReloadableConfigFactory.TICK_SECONDS * 1000 + 500)
    firstFooBar.currentValue() shouldEqual initialFooBarValue
    secFooBar.currentValue() shouldEqual newValue
  }

  it should "not reload nested value if was no changes in value" in { fixture =>
    import fixture._
    Given("reloadable initial value")
    val initialValue = 1
    val reloadable = loadReloadableConfig(initialValue)

    When("transform reloadable config to return nested value")
    var evaluationCount = 0
    val reloadableFooBar = reloadable.map { cfg: Config =>
      evaluationCount += 1
      cfg.getInt("foo.bar")
    }

    When("write the same value to config file")
    Thread.sleep(1000) // for make sure that last modified was changed
    writeValueToConfigFile(initialValue)

    Then("after reload nested value should be same as new value")
    Thread.sleep(ReloadableConfigFactory.TICK_SECONDS * 1000 + 500)
    evaluationCount shouldEqual 1
  }

  it should "reload nested value even if was no changes in value when such option is enabledd" in { fixture =>
    import fixture._
    Given("reloadable initial value")
    val initialValue = 1
    val reloadable = loadReloadableConfig(initialValue, propagateOnlyIfChanged = false)

    When("transform reloadable config to return nested value")
    var evaluationCount = 0
    val reloadableFooBar = reloadable.map { cfg: Config =>
      evaluationCount += 1
      cfg.getInt("foo.bar")
    }

    When("write the same value to config file")
    Thread.sleep(1000) // for make sure that last modified was changed
    writeValueToConfigFile(initialValue)

    Then("after reload nested value should be same as new value")
    Thread.sleep(ReloadableConfigFactory.TICK_SECONDS * 1000 + 500)
    evaluationCount shouldEqual 2
  }

  it should "cooperate with ficus" in { fixture =>
    import fixture._
    Given("reloadable initial config")
    val initialFooBarValue = 1
    val reloadable = loadReloadableConfig(initialFooBarValue)
    val reloadableFoo: Reloadable[Foo] = reloadable.map((cfg: Config) => cfg.as[Foo]("foo"))
    reloadableFoo.currentValue().bar shouldEqual initialFooBarValue

    When("write new value to config file")
    val nextFooBarValue = 2
    Thread.sleep(1000) // for make sure that last modified was changed
    writeValueToConfigFile(nextFooBarValue)

    Then("after reload nested value should be same as new value")
    Thread.sleep(ReloadableConfigFactory.TICK_SECONDS * 1000 + 500)
    reloadableFoo.currentValue().bar shouldEqual nextFooBarValue
  }

  private val idx = new AtomicInteger(0)

  override protected def withFixture(test: OneArgTest): Outcome = {
    test(FixtureParam(idx.incrementAndGet()))
  }

  case class FixtureParam(i: Int) {
    val configFile = new File(s"target/foo_$i.conf")

    def loadReloadableValue(initialFooBarValue: Int): Reloadable[Int] = {
      val reloadable = loadReloadableConfig(initialFooBarValue)

      reloadable.map((cfg: Config) => cfg.getInt("foo.bar"))
    }

    def loadReloadableConfig(initialFooBarValue: Int, propagateOnlyIfChanged: Boolean = true): Reloadable[Config] = {
      writeValueToConfigFile(initialFooBarValue)

      if (propagateOnlyIfChanged)
        TscReloadableConfigFactory.parseFile(configFile, Duration.ofSeconds(0))
      else
        ReloadableConfigFactory.load(List(configFile).asJava, Duration.ofSeconds(0),
          (prev: Optional[Config]) => new TransformationResult(ConfigFactory.parseFile(configFile), true))
    }

    def writeValueToConfigFile(value: Int): Unit = {
      val wrt = new PrintWriter(configFile, "UTF-8")
      try {
        wrt.write(
          s"""foo {
              | bar: $value
              |}""".stripMargin)
      } finally {
        wrt.flush()
        wrt.close()
      }
    }
  }

  case class Foo(bar: Int)

}
