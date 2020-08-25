package io.kotest.extensions.testcontainers

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class TestLifecycleAwareListenerTest : StringSpec({
   val startableTestLifecycleAware = StartableTestLifecycleAware()
   val startable = TestStartable()
   val testLifecycleAwareListener = TestLifecycleAwareListener(startableTestLifecycleAware)
   val anotherTestLifecycleAwareListener = TestLifecycleAwareListener(startable)

   listeners(testLifecycleAwareListener, anotherTestLifecycleAwareListener)

   "it should not break for listener having startable which is not of type testLifecycleAware" {
      startable.startCount shouldBe 0
   }

   "test id in test description should be combination of test name and package name" {
      val testDescription = startableTestLifecycleAware.testDescriptions[1]
      testDescription?.testId shouldBe "io.kotest.extensions.testcontainers.TestLifecycleAwareListenerTest/test_id_in_test_description_should_be_combination_of_test_name_and_package_name"
   }

   "fileSystemFriendlyName .. in /// test description should be encoded test name" {
      val testDescription = startableTestLifecycleAware.testDescriptions[2]
      val encodedTestName = "fileSystemFriendlyName+..+in+%2F%2F%2F+test+description+should+be+encoded+test+name"

      testDescription?.filesystemFriendlyName shouldBe encodedTestName
   }
})
