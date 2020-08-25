package io.kotest.extensions.testcontainers

import io.kotest.core.listeners.TestListener
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import org.testcontainers.lifecycle.Startable
import org.testcontainers.lifecycle.TestDescription
import org.testcontainers.lifecycle.TestLifecycleAware
import java.net.URLEncoder
import java.util.Optional

class TestLifecycleAwareListener(startable: Startable) : TestListener {
   private val testLifecycleAware = startable as? TestLifecycleAware

   override suspend fun beforeTest(testCase: TestCase) {
      testLifecycleAware?.beforeTest(testCase.toTestDescription())
   }

   override suspend fun afterTest(testCase: TestCase, result: TestResult) {
      testLifecycleAware?.afterTest(testCase.toTestDescription(), Optional.ofNullable(result.error))
   }
}

private fun TestCase.toTestDescription() = object : TestDescription {

   override fun getFilesystemFriendlyName(): String {
      return URLEncoder.encode(
         description.displayName(),
         "UTF-8"
      )
   }

   override fun getTestId(): String = description.id().value
}
