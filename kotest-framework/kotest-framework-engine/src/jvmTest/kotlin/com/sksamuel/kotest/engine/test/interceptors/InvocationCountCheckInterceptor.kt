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
import io.kotest.engine.config.ProjectConfigResolver
import io.kotest.engine.config.SpecConfigResolver
import io.kotest.engine.config.TestConfigResolver
import io.kotest.engine.extensions.DefaultExtensionRegistry
import io.kotest.core.descriptors.toDescriptor
import io.kotest.engine.test.interceptors.InvocationCountCheckInterceptor
import io.kotest.engine.test.interceptors.TestEnabledCheckInterceptor
import io.kotest.engine.test.scopes.NoopTestScope
import io.kotest.matchers.booleans.shouldBeFalse
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

      describe("interceptor ordering: enabled check before invocation count check") {

         it("should skip a disabled container with invocations > 1 without error") {
            // A container test that is disabled (name starts with !) and has invocations > 1.
            // With correct ordering, TestEnabledCheckInterceptor returns Ignored before
            // InvocationCountCheckInterceptor sees it, so no error is produced.
            val registry = DefaultExtensionRegistry()
            registry.add(object : InvocationCountExtension {
               override fun getInvocationCount(): Int = 5
            })
            val testConfigResolver = TestConfigResolver(null, registry)

            val tc = TestCase(
               InvocationCountCheckInterceptorTest::class.toDescriptor().append("!disabled container"),
               TestNameBuilder.builder("!disabled container").build(),
               InvocationCountCheckInterceptorTest(),
               {},
               SourceRef.None,
               TestType.Container,
            )

            // Chain: TestEnabledCheckInterceptor -> InvocationCountCheckInterceptor -> downstream
            val enabledCheck = TestEnabledCheckInterceptor(
               ProjectConfigResolver(), SpecConfigResolver(), testConfigResolver
            )
            val invocationCheck = InvocationCountCheckInterceptor(testConfigResolver)

            var downstreamFired = false
            val result = enabledCheck.intercept(tc, NoopTestScope(tc, coroutineContext)) { tc2, scope2 ->
               invocationCheck.intercept(tc2, scope2) { _, _ ->
                  downstreamFired = true
                  TestResult.Success(0.milliseconds)
               }
            }

            // The test should be ignored by the enabled check, never reaching the invocation check
            downstreamFired.shouldBeFalse()
            result.isIgnored shouldBe true
            result.isError shouldBe false
         }

         it("should pass through for an enabled container when invocation count comes from extension") {
            // Extensions are ignored for containers, so even with an extension returning 5,
            // the container resolves to the default invocation count of 1 and passes through.
            val registry = DefaultExtensionRegistry()
            registry.add(object : InvocationCountExtension {
               override fun getInvocationCount(): Int = 5
            })
            val testConfigResolver = TestConfigResolver(null, registry)

            val tc = TestCase(
               InvocationCountCheckInterceptorTest::class.toDescriptor().append("enabled container"),
               TestNameBuilder.builder("enabled container").build(),
               InvocationCountCheckInterceptorTest(),
               {},
               SourceRef.None,
               TestType.Container,
            )

            val enabledCheck = TestEnabledCheckInterceptor(
               ProjectConfigResolver(), SpecConfigResolver(), testConfigResolver
            )
            val invocationCheck = InvocationCountCheckInterceptor(testConfigResolver)

            var downstreamFired = false
            val result = enabledCheck.intercept(tc, NoopTestScope(tc, coroutineContext)) { tc2, scope2 ->
               invocationCheck.intercept(tc2, scope2) { _, _ ->
                  downstreamFired = true
                  TestResult.Success(0.milliseconds)
               }
            }

            downstreamFired.shouldBeTrue()
            result.isError shouldBe false
         }
      }
   }
}
