package com.sksamuel.kt.extensions.system

import io.kotlintest.*
import io.kotlintest.extensions.TopLevelTest
import io.kotlintest.extensions.system.SystemPropertyTestListener
import io.kotlintest.extensions.system.withSystemProperties
import io.kotlintest.extensions.system.withSystemProperty
import io.kotlintest.specs.FreeSpec
import io.kotlintest.specs.FunSpec
import io.kotlintest.specs.WordSpec
import java.util.*

class SystemPropertiesSuspendTest : FreeSpec() {
  
  init {
    "The system properties function" - {
      "Must accept a suspend block" - {
        
        val suspendBlock: suspend () -> Unit = {  }
        
        "Key Value overload" {
            withSystemProperty("Key", "Value") {
              suspendBlock()
          }
        }
        
        "Properties value overload" {
          withSystemProperties(Properties()) {
            suspendBlock()
          }
        }
  
        "Map value overload" {
          withSystemProperties(mapOf("Key" to "Value")) {
            suspendBlock()
          }
        }
      }
    }
  }
}

class SystemPropertyFunctionTest : FunSpec({

  test("withSystemProperty should set and then restore sys property when null") {
    System.getProperty("wibblewobble") shouldBe null
    withSystemProperty("wibblewobble", "dibble") {
      System.getProperty("wibblewobble") shouldBe "dibble"
    }
    System.getProperty("wibblewobble") shouldBe null
  }

  test("withSystemProperty should set and then restore sys property when not null") {
    System.setProperty("fib", "fab")
    System.getProperty("fib") shouldBe "fab"
    withSystemProperty("fib", "fob") {
      System.getProperty("fib") shouldBe "fob"
    }
    System.getProperty("fib") shouldBe "fab"
  }

  test("withSystemProperties from pairs should set and then restore all props") {
    System.setProperty("a", "foo")
    System.getProperty("a") shouldBe "foo"
    System.getProperty("b") shouldBe null
    withSystemProperties(mapOf("a" to "y", "b" to "z")) {
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

class SystemPropertyListenerTest : WordSpec() {

  override fun listeners() = listOf(SystemPropertyTestListener("wibble", "wobble"))

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