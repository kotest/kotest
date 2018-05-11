package com.sksamuel.kotlintest.runner.jvm

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.argThat
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.then
import io.kotlintest.runner.jvm.TestEngine
import io.kotlintest.runner.jvm.TestEngineListener
import io.kotlintest.specs.StringSpec
import io.kotlintest.specs.WordSpec
import java.lang.reflect.InvocationTargetException
import kotlin.reflect.jvm.jvmName

class TestEngineTest : WordSpec({
  "TestEngine" should {
    "notify of a project failure if a spec has an init error" {
      val listener = mock<TestEngineListener> {}
      val engine = TestEngine(listOf(InitErrorSpec::class), 1, listener)
      engine.execute()
      then(listener).should().engineFinished(any<InvocationTargetException>())
    }
    "add placeholder spec if a spec has an init error" {
      val listener = mock<TestEngineListener> {}
      val engine = TestEngine(listOf(InitErrorSpec::class), 1, listener)
      engine.execute()
      then(listener).should().prepareSpec(argThat { name == "com.sksamuel.kotlintest.runner.jvm.InitErrorSpec" }, argThat { jvmName == "com.sksamuel.kotlintest.runner.jvm.InitErrorSpec" })
      then(listener).should().completeSpec(argThat { name == "com.sksamuel.kotlintest.runner.jvm.InitErrorSpec" }, any<InvocationTargetException>())
      then(listener).should().engineFinished(any<InvocationTargetException>())
    }
  }
})

class InitErrorSpec : StringSpec() {
  init {
    throw RuntimeException("kabloom")
  }
}