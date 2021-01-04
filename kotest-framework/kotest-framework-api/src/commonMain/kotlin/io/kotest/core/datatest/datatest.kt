package io.kotest.core.datatest

import io.kotest.assertions.failure
import io.kotest.core.spec.style.scopes.ContainerScope
import io.kotest.core.test.createTestName
import kotlin.jvm.*

suspend fun <T> ContainerScope.forNone(vararg data: Pair<String, T>, test: suspend (T) -> Unit) = forNone(data.toList(), test)

suspend fun <T : Any> ContainerScope.forNone(vararg data: T, test: suspend (T) -> Unit) {
   val identifiers = Identifiers()
   forNone(data.map { Pair(identifiers.stableIdentifier(it), it) }, test)
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

suspend fun <T : Any> ContainerScope.forAll(vararg ts: T, test: suspend (T) -> Unit) = forAll(ts.toList(), test)

suspend fun <T : Any> ContainerScope.forAll(vararg data: Pair<String, T>, test: suspend (T) -> Unit) = forAll(data.toList(), test)

suspend fun <T : Any> ContainerScope.forAll(ts: List<T>, test: suspend (T) -> Unit) {
   val identifiers = Identifiers()
   forAll(ts.map { Pair(identifiers.stableIdentifier(it), it) }, test)
}

@JvmName("forAllWithNames")
suspend fun <T : Any> ContainerScope.forAll(data: List<Pair<String, T>>, test: suspend (T) -> Unit) {
   data.forEach { (name, t) ->
      addTest(createTestName(name), false) { test(t) }
   }
}

