package io.kotest.engine.spec

import io.kotest.core.config.ProjectConfiguration
import io.kotest.core.descriptors.Descriptor
import io.kotest.core.descriptors.DescriptorId
import io.kotest.engine.descriptors.toDescriptor
import io.kotest.core.names.TestName
import io.kotest.core.source.sourceRef
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.AssertionMode
import io.kotest.core.test.Enabled
import io.kotest.core.test.NestedTest
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestCaseSeverityLevel
import io.kotest.core.test.TestType
import io.kotest.core.test.config.ResolvedTestConfig
import io.kotest.matchers.shouldBe
import kotlin.time.Duration.Companion.seconds

class MaterializerTest : FunSpec({

   val self = this

   test("common parent names should be differentiated with a dash") {

      val parent = TestCase(
         descriptor = Descriptor.TestDescriptor(
            parent = MaterializerTest::class.toDescriptor(),
            id = DescriptorId(value = "quidam")
         ), name = TestName(
            testName = "prefix",
            focus = false,
            bang = false,
            prefix = null,
            suffix = null,
            defaultAffixes = false,
            originalName = "prefix"
         ), spec = self,
         test = {},
         source = sourceRef(),
         type = TestType.Dynamic,
         config = ResolvedTestConfig(
            enabled = { Enabled.enabled },
            invocations = 3075,
            threads = 8051,
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
            blockingTest = false
         ), factoryId = null, parent = null
      )

      val nested = NestedTest(
         name = TestName("prefixes are swallowed"),
         test = { },
         disabled = false,
         config = null,
         type = TestType.Container,
         source = sourceRef(),
      )
      Materializer(ProjectConfiguration()).materialize(nested, parent).name.testName shouldBe "- prefixes are swallowed"
   }


})
