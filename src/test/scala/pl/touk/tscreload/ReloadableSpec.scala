package pl.touk.tscreload

import java.io.File
import java.nio.charset.Charset
import java.nio.file.{Files, Paths}
import java.util.function.Function

import com.typesafe.config.Config
import org.scalatest.{FlatSpec, Matchers}

class ReloadableSpec extends FlatSpec with Matchers {

  it should "reload nested value after change" in {
    val configFile = new File("target/foo.conf")
    def writeValueToConfigFile(value: Int) =
      Files.write(
        Paths.get(configFile.toURI),
        s"""foo {
            |  bar: $value
            |}
            |""".stripMargin.getBytes(Charset.forName("UTF-8"))
      )

    val initialFooBarValue = 1
    writeValueToConfigFile(initialFooBarValue)

    val reloadable = ReloadableConfigFactory.parseFile(configFile)
    val reloadableFooBar = reloadable.map(new Function[Config, Int] {
      override def apply(cfg: Config): Int = cfg.getInt("foo.bar")
    })

    reloadableFooBar.currentValue() shouldEqual initialFooBarValue

    val nextFooBarValue = 2
    writeValueToConfigFile(nextFooBarValue)

    reloadableFooBar.currentValue() shouldEqual nextFooBarValue
  }

}