package io.kotest.core.datatest

import io.kotest.assertions.failure
import io.kotest.core.spec.style.scopes.ContainerScope
import io.kotest.core.test.createTestName

@Deprecated("Use ContainerScope.forAll(vararg Pair<String, T>) or ContainerScope.forAll(List<Pair<String, T>>). Will be removed in 4.6")
suspend fun <T> ContainerScope.forAll(first: Pair<String, T>, vararg rest: Pair<String, T>, test: suspend (T) -> Unit) {
   (listOf(first) + rest).forEach { (name, t) ->
      addTest(createTestName(name), false) { test(t) }
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

suspend fun <T> ContainerScope.forNone(data: List<Pair<String, T>>, test: suspend (T) -> Unit) {
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

suspend fun <T : Any> ContainerScope.forAll(ts: List<T>, test: suspend (T) -> Unit) {
   val idents = Identifiers()
   ts.forEach { t ->
      val name = idents.stableIdentifier(t)
      addTest(createTestName(name), false) { test(t) }
   }
}

suspend fun <T : Any> ContainerScope.forAll(vararg ts: T, test: suspend (T) -> Unit) {
   val idents = Identifiers()
   ts.forEach { t ->
      val name = idents.stableIdentifier(t)
      addTest(createTestName(name), false) { test(t) }
   }
}

@Deprecated("Use ContainerScope.forAll(vararg) or ContainerScope.forAll(list). Will be removed in 4.6")
suspend fun <T : Any> ContainerScope.forAll(first: T, vararg rest: T, test: suspend (T) -> Unit) {
   val idents = Identifiers()
   (listOf(first) + rest).forEach { t ->
      val name = idents.stableIdentifier(t)
      addTest(createTestName(name), false) { test(t) }
   }
}

suspend fun <T : Any> ContainerScope.forNone(vararg data: T, test: suspend (T) -> Unit) {
   val idents = Identifiers()
   data.forEach { t ->
      val name = idents.stableIdentifier(t)
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
