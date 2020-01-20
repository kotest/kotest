package io.kotest.core.runtime

import io.kotest.core.test.TestCase
import io.kotest.core.test.TestType

/**
 * This is invoked at compile time by the Javascript compiler to generate calls to kotest for each test case.
 *
 * Note: we need to use this: https://youtrack.jetbrains.com/issue/KT-22228
 */
actual fun executeJavascriptTests(rootTests: List<TestCase>) {
   rootTests.forEach {
      when (it.type) {
         TestType.Container -> describe(it.name) { it.executeAsPromise() }
         TestType.Test -> it(it.name) { it.executeAsPromise() }
      }
   }
}

actual fun configureRuntime() {
   setAdapter(KotestAdapter)
}
