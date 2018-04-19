package io.kotlintest.specs

import io.kotlintest.AbstractSpec
import io.kotlintest.TestCase
import io.kotlintest.TestContainer
import io.kotlintest.lineNumber

annotation class Test

abstract class AbstractAnnotationSpec(body: AbstractAnnotationSpec.() -> Unit = {}) : AbstractSpec() {

  init {
    body()
  }

  override fun root(): TestContainer {
    val tests = javaClass.methods.filter { it.isAnnotationPresent(Test::class.java) }.map {
      TestCase(rootDescription().append(it.name), this@AbstractAnnotationSpec, { it.invoke(this@AbstractAnnotationSpec) }, lineNumber(), defaultTestCaseConfig)
    }
    return TestContainer(rootDescription(), this::class, { context -> tests.forEach { context.executeScope(it) } })
  }
}