package io.kotlintest.specs

import io.kotlintest.AbstractSpec
import io.kotlintest.TestScope

annotation class Test

abstract class AbstractAnnotationSpec(body: AbstractAnnotationSpec.() -> Unit = {}) : AbstractSpec() {

  init {
    body()
  }

  override fun testCases(): List<TestScope> {
    return javaClass.methods.filter { it.isAnnotationPresent(Test::class.java) }.map {
      createTestCase(it.name, { it.invoke(this@AbstractAnnotationSpec) }, defaultTestCaseConfig)
    }
  }
}