package com.sksamuel.kotest.engine.test.interceptors

import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.LinuxOnlyGithubCondition
import io.kotest.core.extensions.InvocationCountExtension
import io.kotest.core.names.TestNameBuilder
import io.kotest.core.source.SourceRef
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.core.test.TestCase
import io.kotest.engine.test.TestResult
import io.kotest.core.test.TestType
import io.kotest.core.test.config.TestConfig
import io.kotest.engine.config.TestConfigResolver
import io.kotest.engine.extensions.DefaultExtensionRegistry
import io.kotest.core.descriptors.toDescriptor
import io.kotest.engine.test.interceptors.InvocationCountCheckInterceptor
import io.kotest.engine.test.scopes.NoopTestScope
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe
import kotlin.time.Duration.Companion.milliseconds

@EnabledIf(LinuxOnlyGithubCondition::class)
class InvocationCountCheckInterceptorTest : DescribeSpec() {
   init {
      describe("InvocationCountCheckInterceptor") {

         it("should invoke downstream if invocation count == 1 for containers") {
            val tc = TestCase(
               InvocationCountCheckInterceptorTest::class.toDescriptor().append("foo"),
               TestNameBuilder.builder("foo").build(),
               InvocationCountCheckInterceptorTest(),
               {},
               SourceRef.None,
               TestType.Container,
            )
            var fired = false
            InvocationCountCheckInterceptor(TestConfigResolver()).intercept(
               tc.copy(config = tc.config?.copy(invocations = 1)),
               NoopTestScope(tc, coroutineContext)
            ) { _, _ ->
               fired = true
               TestResult.Success(0.milliseconds)
            }
            fired.shouldBeTrue()
         }

         it("should invoke downstream if invocation count > 1 for tests") {
            val tc = TestCase(
               InvocationCountCheckInterceptorTest::class.toDescriptor().append("foo"),
               TestNameBuilder.builder("foo").build(),
               InvocationCountCheckInterceptorTest(),
               {},
               SourceRef.None,
               TestType.Test,
            )
            var fired = false
            InvocationCountCheckInterceptor(TestConfigResolver()).intercept(
               tc.copy(config = tc.config?.copy(invocations = 44)),
               NoopTestScope(tc, coroutineContext)
            ) { _, _ ->
               fired = true
               TestResult.Success(0.milliseconds)
            }
            fired.shouldBeTrue()
         }

         it("should error if invocation count > 1 for containers") {
            val tc = TestCase(
               InvocationCountCheckInterceptorTest::class.toDescriptor().append("foo"),
               TestNameBuilder.builder("foo").build(),
               InvocationCountCheckInterceptorTest(),
               {},
               SourceRef.None,
               TestType.Container,
            )

            InvocationCountCheckInterceptor(TestConfigResolver()).intercept(
               tc.copy(config = TestConfig(invocations = 4)),
               NoopTestScope(tc, coroutineContext)
            ) { _, _ -> TestResult.Success(0.milliseconds) }.isError shouldBe true
         }
      }

      describe("TestConfigResolver.invocations with InvocationCountExtension") {

         it("should use invocation count from extension when no test config sets invocations") {
            val registry = DefaultExtensionRegistry()
            registry.add(object : InvocationCountExtension {
               override fun getInvocationCount(): Int = 5
            })
            val resolver = TestConfigResolver(null, registry)

            val tc = TestCase(
               InvocationCountCheckInterceptorTest::class.toDescriptor().append("foo"),
               TestNameBuilder.builder("foo").build(),
               InvocationCountCheckInterceptorTest(),
               {},
               SourceRef.None,
               TestType.Test,
            )

            resolver.invocations(tc) shouldBe 5
         }

         it("should prefer test config invocations over extension") {
            val registry = DefaultExtensionRegistry()
            registry.add(object : InvocationCountExtension {
               override fun getInvocationCount(): Int = 5
            })
            val resolver = TestConfigResolver(null, registry)

            val tc = TestCase(
               InvocationCountCheckInterceptorTest::class.toDescriptor().append("foo"),
               TestNameBuilder.builder("foo").build(),
               InvocationCountCheckInterceptorTest(),
               {},
               SourceRef.None,
               TestType.Test,
            )

            resolver.invocations(tc.copy(config = TestConfig(invocations = 3))) shouldBe 3
         }

         it("should return default invocations when no extension is registered") {
            val resolver = TestConfigResolver()

            val tc = TestCase(
               InvocationCountCheckInterceptorTest::class.toDescriptor().append("foo"),
               TestNameBuilder.builder("foo").build(),
               InvocationCountCheckInterceptorTest(),
               {},
               SourceRef.None,
               TestType.Test,
            )

            resolver.invocations(tc) shouldBe 1
         }

         it("should ignore extension invocation count for container tests") {
            val registry = DefaultExtensionRegistry()
            registry.add(object : InvocationCountExtension {
               override fun getInvocationCount(): Int = 5
            })
            val resolver = TestConfigResolver(null, registry)

            val tc = TestCase(
               InvocationCountCheckInterceptorTest::class.toDescriptor().append("a context"),
               TestNameBuilder.builder("a context").build(),
               InvocationCountCheckInterceptorTest(),
               {},
               SourceRef.None,
               TestType.Container,
            )

            resolver.invocations(tc) shouldBe 1
         }
      }
   }
}
