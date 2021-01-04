package io.kotest.core.datatest

import io.kotest.assertions.failure
import io.kotest.core.spec.style.scopes.RootScope
import io.kotest.core.test.createTestName
import kotlin.jvm.*

fun <T> RootScope.forNone(vararg data: Pair<String, T>, test: suspend (T) -> Unit) = this.forNone(data.toList(), test)

fun <T : Any> RootScope.forNone(vararg data: T, test: suspend (T) -> Unit) {
   val identifiers = Identifiers()
   this.forNone(data.map { Pair(identifiers.stableIdentifier(it), it) }, test)
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

fun <T : Any> RootScope.forAll(vararg data: Pair<String, T>, test: suspend (T) -> Unit) = this.forAll(data.toList(), test)

fun <T : Any> RootScope.forAll(ts: List<T>, test: suspend (T) -> Unit) {
   val identifiers = Identifiers()
   this.forAll(ts.map { Pair(identifiers.stableIdentifier(it), it) }, test)
}

@JvmName("forAllWithNames")
fun <T : Any> RootScope.forAll(data: List<Pair<String, T>>, test: suspend (T) -> Unit) {
   data.forEach { (name, t) ->
      registration().addTest(createTestName(name), false) { test(t) }
   }
}
