package io.kotest.engine.spec

import io.kotest.core.config.ProjectConfiguration
import io.kotest.core.descriptors.Descriptor
import io.kotest.core.descriptors.DescriptorId
import io.kotest.core.names.TestName
import io.kotest.core.names.TestNameBuilder
import io.kotest.core.source.sourceRef
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.AssertionMode
import io.kotest.core.test.Enabled
import io.kotest.core.test.NestedTest
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestCaseSeverityLevel
import io.kotest.core.test.TestType
import io.kotest.core.test.config.ResolvedTestConfig
import io.kotest.engine.descriptors.toDescriptor
import io.kotest.matchers.shouldBe
import kotlin.time.Duration.Companion.seconds

class MaterializerTest : FunSpec({

   val self = this

   test("common parent names should be differentiated with a dash") {

      val parent = TestCase(
         descriptor = Descriptor.TestDescriptor(
            parent = MaterializerTest::class.toDescriptor(),
            id = DescriptorId(value = "quidam")
         ),
         name = TestName(
            name = "prefix",
            focus = false,
            bang = false,
            prefix = null,
            suffix = null,
            defaultAffixes = false,
         ),
         spec = self,
         test = {},
         source = sourceRef(),
         type = TestType.Test,
         config = ResolvedTestConfig(
            enabled = { Enabled.enabled },
            invocations = 3075,
            timeout = 10.seconds,
            invocationTimeout = 10.seconds,
            tags = setOf(),
            extensions = listOf(),
            severity = TestCaseSeverityLevel.NORMAL,
            failfast = false,
            assertionMode = AssertionMode.Error,
            assertSoftly = false,
            coroutineDebugProbes = false,
            coroutineTestScope = false,
            blockingTest = false,
            retries = null,
            retryFn = null,
            retryDelay = null,
            retryDelayFn = null,
         ),
         factoryId = null,
         parent = null,
      )

      val nested = NestedTest(
         name = TestNameBuilder.builder("prefixes are swallowed").build(),
         test = { },
         disabled = false,
         config = null,
         type = TestType.Container,
         source = sourceRef(),
      )
      Materializer().materialize(nested, parent).name.name shouldBe "- prefixes are swallowed"
   }


})
