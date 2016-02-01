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

import com.typesafe.config.Config
import net.ceedubs.ficus.Ficus
import net.ceedubs.ficus.readers.ArbitraryTypeReader
import org.scalatest.{FlatSpec, GivenWhenThen, Matchers}

class ReloadableSpec extends FlatSpec with Matchers with GivenWhenThen {
  import ArbitraryTypeReader._
  import Ficus._
  import JFunctionConversions._

  private val configFile = new File("target/foo.conf")
  
  it should "provide initial value" in {
    When("load reloadable initial value")
    val initialFooBarValue = 1
    val reloadableFooBar = loadReloadableValue(initialFooBarValue)

    Then("nested value should be same as initial")
    reloadableFooBar.currentValue() shouldEqual initialFooBarValue
  }

  it should "reload nested value after change" in {
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

  it should "reload composed value after change" in {
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

  it should "cache evaluation of nested values" in {
    Given("reloadable initial config")
    val initialFooBarValue = 1
    val reloadable = loadReloadableConfig(initialFooBarValue)

    When("transform reloadable config to return nested value")
    var evaluationCount = 0
    val reloadableFooBar = reloadable.map { (cfg: Config) =>
      evaluationCount += 1
      cfg.getInt("foo.bar")
    }

    And("double get current value")
    reloadableFooBar.currentValue() shouldEqual initialFooBarValue
    reloadableFooBar.currentValue() shouldEqual initialFooBarValue

    Then("value should be evaluated once")
    evaluationCount shouldEqual 1
  }

  it should "not reload nested value if was no changes in value" in {
    Given("reloadable initial value")
    val initialValue = 1
    val reloadable = loadReloadableConfig(initialValue)

    When("transform reloadable config to return nested value")
    var evaluationCount = 0
    val reloadableFooBar = reloadable.map { (cfg: Config) =>
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

  it should "cooperate with ficus" in {
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

  private def loadReloadableValue(initialFooBarValue: Int): Reloadable[Int] = {
    val reloadable = loadReloadableConfig(initialFooBarValue)

    reloadable.map((cfg: Config) => cfg.getInt("foo.bar"))
  }

  private def loadReloadableConfig(initialFooBarValue: Int): Reloadable[Config] = {
    writeValueToConfigFile(initialFooBarValue)

    ReloadableConfigFactory.parseFile(configFile, Duration.ofSeconds(0))
  }

  private def writeValueToConfigFile(value: Int) = {
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

  case class Foo(bar: Int)

}
