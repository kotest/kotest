package io.kotlintest.specs

import io.kotlintest.AbstractSpec
import io.kotlintest.TestCase
import io.kotlintest.TestContainer
import io.kotlintest.lineNumber

annotation class Test

open class AbstractAnnotationSpec(body: AbstractAnnotationSpec.() -> Unit = {}) : AbstractSpec() {

  init {
    body()
  }

  override fun root(): TestContainer {
    val tests = javaClass.methods.filter { it.isAnnotationPresent(Test::class.java) }.map {
      TestCase(it.name, name() + "/" + it.name, this@AbstractAnnotationSpec, { it.invoke(this@AbstractAnnotationSpec) }, lineNumber(), defaultTestCaseConfig)
    }
    return TestContainer(name(), name(), this, { context -> tests.forEach { context.addScope(it) } }, true)
  }
}