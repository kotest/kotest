package io.kotest.core.datatest

import io.kotest.assertions.failure
import io.kotest.core.spec.style.scopes.ContainerScope
import io.kotest.core.spec.style.scopes.RootScope
import io.kotest.core.test.Identifiers
import io.kotest.core.test.TestType
import io.kotest.core.test.createTestName
import kotlin.jvm.*

suspend fun <T> ContainerScope.forNone(vararg data: Pair<String, T>, test: suspend (T) -> Unit) =
   forNone(data.toList(), test)

suspend fun <T : Any> ContainerScope.forNone(vararg data: T, test: suspend (T) -> Unit) {
   forNone(data.map { Pair(Identifiers.stableIdentifier(it), it) }, test)
}

suspend fun <T> ContainerScope.forNone(data: List<Pair<String, T>>, test: suspend (T) -> Unit) {
   data.forEach { (name, t) ->
      addTest(name, TestType.Test) {
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

suspend fun <T : Any> ContainerScope.forAll(vararg data: Pair<String, T>, test: suspend (T) -> Unit) =
   forAll(data.toList(), test)

suspend fun <T : Any> ContainerScope.forAll(ts: List<T>, test: suspend (T) -> Unit) {
   forAll(ts.map { Pair(Identifiers.stableIdentifier(it), it) }, test)
}

@JvmName("forAllWithNames")
suspend fun <T : Any> ContainerScope.forAll(data: List<Pair<String, T>>, test: suspend (T) -> Unit) {
   data.forEach { (name, t) ->
      addTest(name, TestType.Test) { test(t) }
   }
}

fun <T> RootScope.forNone(vararg data: Pair<String, T>, test: suspend (T) -> Unit) = this.forNone(data.toList(), test)

fun <T : Any> RootScope.forNone(vararg data: T, test: suspend (T) -> Unit) {
   this.forNone(data.map { Pair(Identifiers.stableIdentifier(it), it) }, test)
}

fun <T> RootScope.forNone(data: List<Pair<String, T>>, test: suspend (T) -> Unit) {
   data.forEach { (name, t) ->
      registration().addTest(createTestName(name), false) {
         try {
            test(t)
            null
         } catch (e: AssertionError) {
            e
         } ?: throw failure("Test passed for $t but expected failure")
      }
   }
}

fun <T : Any> RootScope.forAll(vararg ts: T, test: suspend (T) -> Unit) = this.forAll(ts.toList(), test)

fun <T : Any> RootScope.forAll(vararg data: Pair<String, T>, test: suspend (T) -> Unit) =
   this.forAll(data.toList(), test)

fun <T : Any> RootScope.forAll(ts: List<T>, test: suspend (T) -> Unit) {
   this.forAll(ts.map { Pair(Identifiers.stableIdentifier(it), it) }, test)
}

@JvmName("forAllWithNames")
fun <T : Any> RootScope.forAll(data: List<Pair<String, T>>, test: suspend (T) -> Unit) {
   data.forEach { (name, t) ->
      registration().addTest(createTestName(name), false) { test(t) }
   }
}
