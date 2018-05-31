package io.kotlintest.specs

import io.kotlintest.AbstractSpec
import io.kotlintest.TestCase
import io.kotlintest.TestType

annotation class Test

abstract class AbstractAnnotationSpec(body: AbstractAnnotationSpec.() -> Unit = {}) : AbstractSpec() {

  init {
    body()
  }

  override fun testCases(): List<TestCase> {
    return javaClass.methods.filter { it.isAnnotationPresent(Test::class.java) }.map {
      createTestCase(it.name, { it.invoke(this@AbstractAnnotationSpec) }, defaultTestCaseConfig, TestType.Test)
    }
  }
}