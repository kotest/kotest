package io.kotlintest.specs

import io.kotlintest.AbstractSpec
import io.kotlintest.TestContainer
import io.kotlintest.TestContext

/**
 * Example:
 *
 * "some test" {
 *   "with context" {
 *      should("do something") {
 *        // test here
 *      }
 *    }
 *  }
 *
 *  or
 *
 *  should("do something") {
 *    // test here
 *  }
 */
abstract class AbstractShouldSpec(body: AbstractShouldSpec.() -> Unit = {}) : AbstractSpec() {

  init {
    body()
  }

  final override fun isInstancePerTest(): Boolean = false

  fun should(name: String) = RootTestBuilder("should $name")
  fun should(name: String, test: TestContext.() -> Unit) = should(name).invoke(test)

  operator fun String.invoke(init: ShouldContext.() -> Unit) =
      addRootScope(TestContainer(rootDescription().append(this), this@AbstractShouldSpec::class, { ShouldContext(it).init() }))

  inner class ShouldContext(val context: TestContext) {

    operator fun String.invoke(init: ShouldContext.() -> Unit) =
        context.executeScope(TestContainer(context.currentScope().description().append(this), this@AbstractShouldSpec::class, { ShouldContext(it).init() }))

    fun should(name: String) = TestBuilder(context, "should $name")
    fun should(name: String, test: TestContext.() -> Unit) = should(name).invoke(test)
  }
}