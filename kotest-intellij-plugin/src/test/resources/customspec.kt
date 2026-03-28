package io.kotest.samples.gradle

import io.kotest.core.spec.style.CustomSpec
import io.kotest.core.annotation.TestRunnable

@TestRunnable
fun runTest(name: String, action: () -> Unit) { action() }

fun notAnnotated(name: String, action: () -> Unit) { action() }

class CustomSpecExample : CustomSpec() {
   init {
      runTest("a test") {
      }
      notAnnotated("not a test") {
      }
   }
}
