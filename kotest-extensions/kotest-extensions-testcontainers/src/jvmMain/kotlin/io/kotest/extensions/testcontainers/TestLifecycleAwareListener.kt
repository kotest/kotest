package io.kotest.extensions.testcontainers

import io.kotest.core.listeners.TestListener
import io.kotest.core.test.TestCase
import io.kotest.engine.test.TestResult
import org.testcontainers.lifecycle.Startable
import org.testcontainers.lifecycle.TestDescription
import org.testcontainers.lifecycle.TestLifecycleAware
import java.net.URLEncoder
import java.util.Optional

@Deprecated("Use TestContainerProjectExtension or TestContainerSpeccExtension instead")
class TestLifecycleAwareListener(startable: Startable) : TestListener {
   private val testLifecycleAware = startable as? TestLifecycleAware

   override suspend fun beforeTest(testCase: TestCase) {
      testLifecycleAware?.beforeTest(testCase.toTestDescription())
   }

   override suspend fun afterTest(testCase: TestCase, result: TestResult) {
      testLifecycleAware?.afterTest(testCase.toTestDescription(), Optional.ofNullable(result.errorOrNull))
   }
}

@Deprecated("Use TestContainerProjectExtension or TestContainerSpeccExtension instead")
internal fun TestCase.toTestDescription() = object : TestDescription {

   override fun getFilesystemFriendlyName(): String {
      return URLEncoder.encode(testId.replace(" ", "_").replace(" -- ", "__").replace("/", "_"), "UTF-8")
   }

   override fun getTestId(): String = this@toTestDescription.descriptor.ids().joinToString("/") { it.value }
}
