package io.kotest.extensions.testcontainers

import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.Ignored
import io.kotest.core.annotation.LinuxOnlyGithubCondition
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

@Ignored
@EnabledIf(LinuxOnlyGithubCondition::class)
class TestLifecycleAwareListenerTest : StringSpec({
   val startableTestLifecycleAware = StartableTestLifecycleAware()
   val startable = TestStartable()
   val testLifecycleAwareListener = TestLifecycleAwareListener(startableTestLifecycleAware)
   val anotherTestLifecycleAwareListener = TestLifecycleAwareListener(startable)

   extensions(testLifecycleAwareListener, anotherTestLifecycleAwareListener)

   "it should not break for listener having startable which is not of type testLifecycleAware" {
      startable.startCount shouldBe 0
   }

   "test id in test description should be combination of test name and spec name" {
      val testDescription = startableTestLifecycleAware.testDescriptions[1]
      testDescription?.testId shouldBe "io.kotest.extensions.testcontainers.TestLifecycleAwareListenerTest/test id in test description should be combination of test name and spec name"
   }

   "fileSystemFriendlyName .. in /// test description should be encoded test name" {
      val testDescription = startableTestLifecycleAware.testDescriptions[2]
      val encodedTestName =
         "io.kotest.extensions.testcontainers.TestLifecycleAwareListenerTest_fileSystemFriendlyName_.._in_____test_description_should_be_encoded_test_name"

      testDescription?.filesystemFriendlyName shouldBe encodedTestName
   }
})
