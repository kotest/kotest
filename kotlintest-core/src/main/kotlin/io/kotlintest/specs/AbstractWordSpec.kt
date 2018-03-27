package io.kotlintest.specs

import io.kotlintest.AbstractSpec
import io.kotlintest.TestCase
import io.kotlintest.TestContainer
import io.kotlintest.TestContext
import io.kotlintest.lineNumber
import kotlin.reflect.full.memberExtensionFunctions

/**
 * Example:
 *
 * "some test" should {
 *    "do something" {
 *      // test here
 *    }
 * }
 *
 */
abstract class AbstractWordSpec(body: AbstractWordSpec.() -> Unit = {}) : AbstractSpec() {

  init {
    body()
  }

  final override fun isInstancePerTest(): Boolean = false

  infix fun String.should(init: WordContext.() -> Unit) {
    val func = this@AbstractWordSpec::class.memberExtensionFunctions.find { it.name == "should" }!!
    println("Annotations=" + func.annotations)
    rootScopes.add(TestContainer(rootDescription().append(this), this@AbstractWordSpec, { WordContext(it).init() }))
  }

  inner class WordContext(val context: TestContext) {
    infix operator fun String.invoke(test: TestContext.() -> Unit): TestCase {
      println("Annotations=" + this::class.annotations)
      val tc = TestCase(context.currentScope().description().append("should " + this), this@AbstractWordSpec, test, lineNumber(), defaultTestCaseConfig)
      context.addScope(tc)
      return tc
    }
  }
}