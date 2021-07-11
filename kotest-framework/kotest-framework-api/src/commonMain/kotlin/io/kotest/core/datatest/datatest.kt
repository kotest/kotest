package io.kotest.core.datatest

import io.kotest.assertions.failure
import io.kotest.core.spec.style.scopes.*
import io.kotest.core.test.Identifiers
import io.kotest.core.test.TestType
import io.kotest.core.test.createTestName
import kotlin.jvm.*

@Deprecated("Deprecated without replacement. Will be removed in 6.0")
suspend fun <T> ContainerContext.forNone(vararg data: Pair<String, T>, test: suspend (T) -> Unit) =
   forNone(data.toList(), test)

@Deprecated("Deprecated without replacement. Will be removed in 6.0")
suspend fun <T : Any> ContainerContext.forNone(vararg data: T, test: suspend (T) -> Unit) {
   forNone(data.map { Pair(Identifiers.stableIdentifier(it), it) }, test)
}

@Deprecated("Deprecated without replacement. Will be removed in 6.0")
suspend fun <T> ContainerContext.forNone(data: List<Pair<String, T>>, test: suspend (T) -> Unit) {
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

@Deprecated("Replaced with withData. Will be removed in 6.0")
suspend fun <T : Any> ContainerContext.forAll(vararg ts: T, test: suspend (T) -> Unit) = forAll(ts.toList(), test)

@Deprecated("Replaced with withData. Will be removed in 6.0")
suspend fun <T : Any> ContainerContext.forAll(vararg data: Pair<String, T>, test: suspend (T) -> Unit) =
   forAll(data.toList(), test)

@Deprecated("Replaced with withData. Will be removed in 6.0")
suspend fun <T : Any> ContainerContext.forAll(ts: List<T>, test: suspend (T) -> Unit) {
   forAll(ts.map { Pair(Identifiers.stableIdentifier(it), it) }, test)
}

@JvmName("forAllWithNames")
@Deprecated("Replaced with withData. Will be removed in 6.0")
suspend fun <T : Any> ContainerContext.forAll(data: List<Pair<String, T>>, test: suspend (T) -> Unit) {
   data.forEach { (name, t) ->
      addTest(name, TestType.Test) { test(t) }
   }
}

@Deprecated("Deprecated without replacement. Will be removed in 6.0")
fun <T> RootContext.forNone(vararg data: Pair<String, T>, test: suspend (T) -> Unit) = this.forNone(data.toList(), test)

@Deprecated("Deprecated without replacement. Will be removed in 6.0")
fun <T : Any> RootContext.forNone(vararg data: T, test: suspend (T) -> Unit) {
   this.forNone(data.map { Pair(Identifiers.stableIdentifier(it), it) }, test)
}

@Deprecated("Deprecated without replacement. Will be removed in 6.0")
fun <T> RootContext.forNone(data: List<Pair<String, T>>, test: suspend (T) -> Unit) {
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

@Deprecated("Replaced with withData. Will be removed in 6.0")
fun <T : Any> RootContext.forAll(vararg ts: T, test: suspend (T) -> Unit) = this.forAll(ts.toList(), test)

@Deprecated("Replaced with withData. Will be removed in 6.0")
fun <T : Any> RootContext.forAll(vararg data: Pair<String, T>, test: suspend (T) -> Unit) =
   this.forAll(data.toList(), test)

@Deprecated("Replaced with withData. Will be removed in 6.0")
fun <T : Any> RootContext.forAll(ts: List<T>, test: suspend (T) -> Unit) {
   this.forAll(ts.map { Pair(Identifiers.stableIdentifier(it), it) }, test)
}

@JvmName("forAllWithNames")
@Deprecated("Replaced with withData. Will be removed in 6.0")
fun <T : Any> RootContext.forAll(data: List<Pair<String, T>>, test: suspend (T) -> Unit) {
   data.forEach { (name, t) ->
      registration().addTest(createTestName(name), false) { test(t) }
   }
}
