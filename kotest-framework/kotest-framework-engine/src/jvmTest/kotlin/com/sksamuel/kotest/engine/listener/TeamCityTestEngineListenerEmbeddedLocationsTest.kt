package com.sksamuel.kotest.engine.listener

import io.kotest.core.descriptors.toDescriptor
import io.kotest.core.names.TestNameBuilder
import io.kotest.core.source.SourceRef
import io.kotest.core.spec.SpecRef
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestType
import io.kotest.engine.listener.TeamCityTestEngineListener
import io.kotest.engine.test.TestResult
import io.kotest.extensions.system.captureStandardOut
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldNotContain
import io.kotest.matchers.string.shouldStartWith

/**
 * The engine no longer wraps test names with `<kotest>...</kotest>` location tags - jump-to-source
 * navigation now flows via the JUnit Platform `MethodSource` (and `proxy.locationUrl`).  This test
 * pins the resulting [TeamCityTestEngineListener] output: every lifecycle message names the test
 * by its plain descriptor path, with no embedded `<kotest>` tag and no legacy ` -- ` separator
 * between nested segments.
 */
class TeamCityTestEngineListenerEmbeddedLocationsTest : FunSpec() {
   init {

      // Use a dedicated dummy spec class - referencing this class itself in the spec field would
      // trigger infinite recursion (constructing the test class re-runs the init block).
      val specFqn = "com.sksamuel.kotest.engine.listener.DummyTcSpec"
      val tc = TestCase(
         DummyTcSpec::class.toDescriptor().append("a"),
         TestNameBuilder.builder("a").build(),
         DummyTcSpec(),
         {},
         SourceRef.None,
         TestType.Test,
      )

      // The TeamCity path renderer needs to see the spec before it can render any test inside it.
      // We discard the specStarted output - we only want to assert on the per-test messages.
      fun freshListenerWithSpec(): TeamCityTestEngineListener {
         val l = TeamCityTestEngineListener(prefix = "tc")
         captureStandardOut {
            kotlinx.coroutines.runBlocking { l.specStarted(SpecRef.Reference(DummyTcSpec::class)) }
         }
         return l
      }

      test("testIgnored should emit a plain test name with no <kotest> tag") {
         val listener = freshListenerWithSpec()
         val stdout = captureStandardOut {
            listener.testIgnored(tc, null)
         }
         stdout shouldNotContain "<kotest>"
         stdout shouldNotContain " -- "
         stdout shouldStartWith "tc[testIgnored "
         stdout shouldContain "name='$specFqn/a'"
         stdout shouldContain "result_status='Ignored'"
      }

      test("testStarted should emit a plain test name with no <kotest> tag") {
         val listener = freshListenerWithSpec()
         val stdout = captureStandardOut {
            listener.testStarted(tc)
         }
         stdout shouldNotContain "<kotest>"
         stdout shouldNotContain " -- "
         stdout shouldStartWith "tc[testStarted "
      }

      test("testFinished should emit a plain test name with no <kotest> tag") {
         val listener = freshListenerWithSpec()
         val stdout = captureStandardOut {
            listener.testFinished(tc, TestResult.Ignored(null))
         }
         stdout shouldNotContain "<kotest>"
         stdout shouldNotContain " -- "
      }
   }
}

private class DummyTcSpec : FunSpec({})
