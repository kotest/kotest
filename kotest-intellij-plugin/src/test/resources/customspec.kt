package io.kotest.samples.gradle

import io.kotest.core.spec.AbstractSpec
import io.kotest.core.spec.style.TestRunnable

@TestRunnable
fun runTest(name: String, action: () -> Unit) { action() }

fun notAnnotated(name: String, action: () -> Unit) { action() }

class CustomSpecExample : AbstractSpec() {
   init {
      runTest("a test") {
      }
      notAnnotated("not a test") {
      }
   }
}

@TestRunnable
fun context(name: String, action: () -> Unit) { action() }

class NestedCustomSpecExample : AbstractSpec() {
   init {
      context("outer") {
         runTest("inner") {
         }
      }
   }
}
