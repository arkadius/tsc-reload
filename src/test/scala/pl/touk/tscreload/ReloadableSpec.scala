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
import java.util.function.Function

import com.typesafe.config.Config
import org.scalatest.{FlatSpec, GivenWhenThen, Matchers}
import pl.touk.tscreload.impl.ConfigsReloader

class ReloadableSpec extends FlatSpec with Matchers with GivenWhenThen{

  it should "reload nested value after change" in {
    Given("configuration file with initial value")
    val configFile = new File("target/foo.conf")
    def writeValueToConfigFile(value: Int) = {
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
    val initialFooBarValue = 1
    writeValueToConfigFile(initialFooBarValue)

    When("parse reloadable config file")
    val reloadable = ReloadableConfigFactory.parseFile(configFile, Duration.ofSeconds(0))

    And("transform reloadable config to return nested value")
    val reloadableFooBar = reloadable.map(new Function[Config, Int] {
      override def apply(cfg: Config): Int = cfg.getInt("foo.bar")
    })

    Then("nested value should be same as initial")
    reloadableFooBar.currentValue() shouldEqual initialFooBarValue

    When("write new value to config file")
    val nextFooBarValue = 2
    Thread.sleep(1000) // for make sure that last modified was changed
    writeValueToConfigFile(nextFooBarValue)

    Then("after reload nested value should be same as new value")
    Thread.sleep(ConfigsReloader.TICK_SECONDS * 1000 + 500)
    reloadableFooBar.currentValue() shouldEqual nextFooBarValue
  }

}