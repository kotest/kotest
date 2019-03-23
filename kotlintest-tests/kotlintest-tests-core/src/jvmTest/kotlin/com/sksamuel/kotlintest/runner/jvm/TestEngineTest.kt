//package com.sksamuel.kotlintest.runner.jvm
//
//import com.nhaarman.mockito_kotlin.any
//import com.nhaarman.mockito_kotlin.argThat
//import com.nhaarman.mockito_kotlin.mock
//import com.nhaarman.mockito_kotlin.then
//import io.kotlintest.runner.jvm.TestEngine
//import io.kotlintest.runner.jvm.TestEngineListener
//import io.kotlintest.specs.StringSpec
//import io.kotlintest.specs.WordSpec
//import java.lang.reflect.InvocationTargetException
//import kotlin.reflect.jvm.jvmName
//
//class TestEngineTest : WordSpec({
//
//  "TestEngine" should {
//
//    System.setProperty("TestEngineTest", "true")
//
//    "notify of a project failure if a spec has an init error" {
//      val listener = mock<TestEngineListener> {}
//      val engine = TestEngine(listOf(InitErrorSpec::class), emptyList(), 1, listener)
//      engine.execute()
//      then(listener).should().engineFinished(any<InvocationTargetException>())
//    }
//
//    "add placeholder spec if a spec has an init error" {
//      val listener = mock<TestEngineListener> {}
//      val engine = TestEngine(listOf(InitErrorSpec::class), emptyList(), 1, listener)
//      engine.execute()
//      then(listener).should().beforeSpecClass(argThat { jvmName == "com.sksamuel.kotlintest.runner.jvm.InitErrorSpec" })
//      then(listener).should().afterSpecClass(any(), any<InvocationTargetException>())
//      then(listener).should().engineFinished(any<InvocationTargetException>())
//    }
//
//    System.getProperties().remove("TestEngineTest")
//  }
//})
//
//class InitErrorSpec : StringSpec() {
//  init {
//    // we only want to throw this when are testing it via TestEngineTest above
//    // and not through normal discovery of all tests
//    if (System.getProperty("TestEngineTest") == "true")
//      throw RuntimeException("kaboom")
//  }
//}