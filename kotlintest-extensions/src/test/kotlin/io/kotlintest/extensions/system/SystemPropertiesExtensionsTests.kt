package io.kotlintest.extensions.system

import io.kotlintest.*
import io.kotlintest.extensions.SpecLevelExtension
import io.kotlintest.extensions.TopLevelTest
import io.kotlintest.specs.FunSpec
import io.kotlintest.specs.WordSpec
import java.util.*

class SystemPropertyFunctionTest : FunSpec({

  test("withSystemProperty should set and then restore sys property when null") {
    System.getProperty("wibblewobble") shouldBe null
    withSystemProperty("wibblewobble", "dibble") {
      System.getProperty("wibblewobble") shouldBe "dibble"
    }
    System.getProperty("wibblewobble") shouldBe null
  }

  test("withSystemProperty should set and then restore sys property when not null") {
    System.setProperty("wibblewobble", "dobble")
    System.getProperty("wibblewobble") shouldBe "dobble"
    withSystemProperty("wibblewobble", "dibble") {
      System.getProperty("wibblewobble") shouldBe "dibble"
    }
    System.getProperty("wibblewobble") shouldBe "dobble"
  }

  test("withSystemProperties from pairs should set and then restore all props") {
    System.setProperty("a", "foo")
    System.getProperty("a") shouldBe "foo"
    System.getProperty("b") shouldBe null
    withSystemProperties(listOf("a" to "y", "b" to "z")) {
      System.getProperty("a") shouldBe "y"
      System.getProperty("b") shouldBe "z"
    }
    System.getProperty("a") shouldBe "foo"
    System.getProperty("b") shouldBe null
  }

  test("withSystemProperties from Properties should set and then restore all props") {
    System.setProperty("a", "foo")
    System.getProperty("a") shouldBe "foo"
    System.getProperty("b") shouldBe null

    val props = Properties()
    props.setProperty("a", "m")
    props.setProperty("b", "n")

    withSystemProperties(props) {
      System.getProperty("a") shouldBe "m"
      System.getProperty("b") shouldBe "n"
    }
    System.getProperty("a") shouldBe "foo"
    System.getProperty("b") shouldBe null
  }

  test("an error in the thunk should restore properties") {
    System.setProperty("wibblewobble", "dobble")
    System.getProperty("wibblewobble") shouldBe "dobble"
    shouldThrowAny {
      withSystemProperty("wibblewobble", "dibble") {
        System.getProperty("wibblewobble") shouldBe "dibble"
        throw RuntimeException()
      }
    }
    System.getProperty("wibblewobble") shouldBe "dobble"
  }
})

class SystemPropertyExtensionTest : WordSpec() {

  override fun extensions(): List<SpecLevelExtension> = listOf(SystemPropertyExtension("wibble", "wobble"))

  override fun beforeSpecClass(spec: Spec, tests: List<TopLevelTest>) {
    System.getProperty("wibble") shouldBe null
  }

  override fun afterSpecClass(spec: Spec, results: Map<TestCase, TestResult>) {
    System.getProperty("wibble") shouldBe null
  }

  init {
    "sys prop extension" should {
      "set sys prop" {
        System.getProperty("wibble") shouldBe "wobble"
      }
    }
  }
}