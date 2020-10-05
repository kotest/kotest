package io.kotest.core.datatest

import io.kotest.assertions.failure
import io.kotest.core.spec.style.scopes.ContainerScope
import io.kotest.core.test.createTestName

suspend fun <T> ContainerScope.forAll(vararg data: Pair<String, T>, test: suspend (T) -> Unit) {
   data.forEach { (name, t) ->
      addTest(createTestName(name), false) { test(t) }
   }
}

suspend fun <T> ContainerScope.forAll(vararg data: T, test: suspend (T) -> Unit) {
   data.forEach { t ->
      addTest(createTestName("$t"), false) { test(t) }
   }
}

suspend fun <T> ContainerScope.forNone(vararg data: Pair<String, T>, test: suspend (T) -> Unit) {
   data.forEach { (name, t) ->
      addTest(createTestName(name), false) {
         try {
            test(t)
            null
         } catch (e: AssertionError) {
            e
         } ?: throw failure("Test passed for $t but expected failure")
      }
   }
}

suspend fun <T> ContainerScope.forNone(vararg data: T, test: suspend (T) -> Unit) {
   data.forEach { t ->
      addTest(createTestName("$t"), false) {
         try {
            test(t)
            null
         } catch (e: AssertionError) {
            e
         } ?: throw failure("Test passed for $t but expected failure")
      }
   }
}
