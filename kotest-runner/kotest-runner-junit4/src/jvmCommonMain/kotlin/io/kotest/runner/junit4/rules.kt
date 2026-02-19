package io.kotest.runner.junit4

import io.kotest.common.ExperimentalKotest
import io.kotest.core.test.TestScope
import kotlinx.coroutines.runBlocking
import org.junit.rules.TestRule
import org.junit.runners.model.Statement

@ExperimentalKotest
fun <R : TestRule> TestScope.withRule(rule: R, fn: suspend (rule: R) -> Unit) {
   val base = object : Statement() {
      override fun evaluate() {
         runBlocking { fn.invoke(rule) }
      }
   }
   rule.apply(base, Descriptions.createTestDescription(this.testCase)).evaluate()
}
