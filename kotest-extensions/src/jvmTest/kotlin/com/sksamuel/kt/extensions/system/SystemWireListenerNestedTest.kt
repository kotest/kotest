package com.sksamuel.kt.extensions.system

import io.kotest.core.annotation.Isolate
import io.kotest.core.spec.SpecRef
import io.kotest.core.spec.style.FunSpec
import io.kotest.engine.TestEngineLauncher
import io.kotest.extensions.system.SystemErrWireListener
import io.kotest.extensions.system.SystemOutWireListener
import io.kotest.matchers.types.shouldBeSameInstanceAs

/**
 * Regression test for nested tests with [SystemOutWireListener] and [SystemErrWireListener].
 *
 * The listeners use beforeAny/afterAny, which also fire for containers. Previously a single
 * `previous` field was overwritten by the nested test's beforeAny, so the container's afterAny
 * restored the listener's own capture stream instead of the original System.out/System.err,
 * permanently hijacking standard out/err for the rest of the JVM.
 */
@Isolate
class SystemWireListenerNestedTest : FunSpec() {
   init {

      test("SystemOutWireListener should restore the original System.out after a spec with nested tests") {
         val out = System.out
         TestEngineLauncher()
            .withSpecRefs(SpecRef.Reference(NestedSystemOutSpec::class))
            .execute()
         System.out.shouldBeSameInstanceAs(out)
      }

      test("SystemErrWireListener should restore the original System.err after a spec with nested tests") {
         val err = System.err
         TestEngineLauncher()
            .withSpecRefs(SpecRef.Reference(NestedSystemErrSpec::class))
            .execute()
         System.err.shouldBeSameInstanceAs(err)
      }
   }
}

private class NestedSystemOutSpec : FunSpec() {
   override val extensions = listOf(SystemOutWireListener())

   init {
      context("a container") {
         test("a nested test") {
            println("hello")
         }
      }
   }
}

private class NestedSystemErrSpec : FunSpec() {
   override val extensions = listOf(SystemErrWireListener())

   init {
      context("a container") {
         test("a nested test") {
            System.err.println("hello")
         }
      }
   }
}
