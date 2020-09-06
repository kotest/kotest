package io.kotest.datatest

import io.kotest.core.spec.style.scopes.ContainerScope
import io.kotest.core.test.createTestName

suspend fun <T> ContainerScope.rollup(vararg data: Pair<String, T>, test: suspend (T) -> Unit) {
   data.forEach { (name, t) ->
      addTest(createTestName(name)) { test(t) }
   }
}

suspend fun <T> ContainerScope.rollup(vararg data: T, test: suspend (T) -> Unit) {
   data.forEach { t ->
      addTest(createTestName("$t")) { test(t) }
   }
}

