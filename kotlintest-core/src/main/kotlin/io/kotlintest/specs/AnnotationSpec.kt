package io.kotlintest.specs

import io.kotlintest.AbstractSpec
import io.kotlintest.TestCase
import io.kotlintest.TestContainer
import io.kotlintest.TestScope

annotation class Test

open class AbstractAnnotationSpec(body: AbstractAnnotationSpec.() -> Unit = {}) : AbstractSpec() {

  init {
    body()
  }

  override fun root(): TestContainer {
    val tests = javaClass.methods.filter { it.isAnnotationPresent(Test::class.java) }.map {
      TestCase(it.name, this@AbstractAnnotationSpec, { it.invoke(this@AbstractAnnotationSpec) }, TestScope.lineNumber(), defaultTestCaseConfig)
    }
    return TestContainer(name(), this, { tests }, true)
  }
}