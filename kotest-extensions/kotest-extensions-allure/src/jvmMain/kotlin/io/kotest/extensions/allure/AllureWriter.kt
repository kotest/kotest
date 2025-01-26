package io.kotest.extensions.allure

import io.kotest.core.descriptors.Descriptor
import io.kotest.common.DescriptorPath
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.engine.config.ProjectConfigResolver
import io.kotest.engine.config.TestConfigResolver
import io.kotest.engine.test.names.DefaultDisplayNameFormatter
import io.kotest.engine.test.names.FallbackDisplayNameFormatter
import io.kotest.engine.test.names.formatTestPath
import io.qameta.allure.Allure
import io.qameta.allure.AllureLifecycle
import io.qameta.allure.model.Status
import io.qameta.allure.model.StatusDetails
import io.qameta.allure.model.StepResult
import io.qameta.allure.util.ResultsUtils
import java.lang.reflect.InvocationTargetException
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KClass

class AllureWriter {

   companion object {
      const val LANGUAGE_LABEL = "kotlin"
      const val FRAMEWORK_LABEL = "kotest"
   }

   private val formatter = FallbackDisplayNameFormatter(
      fallback = DefaultDisplayNameFormatter(
         projectConfigResolver = ProjectConfigResolver(),
         testConfigResolver = TestConfigResolver()
      )
   )

   /**
    * Loads the [AllureLifecycle] object which is used to report test lifecycle events.
    */
   internal val allure = try {
      Allure.getLifecycle() ?: throw IllegalStateException("Allure lifecycle was null")
   } catch (t: Throwable) {
      t.printStackTrace()
      throw t
   }

   private val uuids = ConcurrentHashMap<DescriptorPath, String>()

   fun id(testCase: TestCase) = uuids[testCase.descriptor.path()]

   fun startTestCase(testCase: TestCase) {
      val labels = listOfNotNull(
         testCase.epic(),
         testCase.feature(),
         ResultsUtils.createFrameworkLabel(FRAMEWORK_LABEL),
         ResultsUtils.createHostLabel(),
         ResultsUtils.createLanguageLabel(LANGUAGE_LABEL),
         ResultsUtils.createTestClassLabel(testCase.spec::class.java.simpleName),
         testCase.owner(),
         ResultsUtils.createPackageLabel(testCase.spec::class.java.`package`.name),
         ResultsUtils.createSuiteLabel(testCase.descriptor.spec().id.value),
         testCase.maxSeverity()?.let { ResultsUtils.createSeverityLabel(it) },
         testCase.story(),
         ResultsUtils.createThreadLabel(),
      )

      val links = links(testCase)
      val uuid = UUID.randomUUID().toString()
      uuids[testCase.descriptor.path()] = uuid

      val result = io.qameta.allure.model.TestResult()
         .setFullName(formatter.formatTestPath(testCase, " / "))
         .setName(formatter.formatTestPath(testCase, " "))
         .setUuid(uuid)
         .setTestCaseId(safeId(testCase.descriptor))
         .setHistoryId(safeId(testCase.descriptor))
         .setLabels(labels)
         .setLinks(links)
         .setDescription(testCase.description())

      allure.scheduleTestCase(result)
      allure.startTestCase(uuid)
   }

   fun finishTestCase(testCase: TestCase, result: TestResult) {
      val status = when (result) {
         // what we call an error, allure calls broken
         is TestResult.Error -> Status.BROKEN
         is TestResult.Failure -> Status.FAILED
         is TestResult.Ignored -> Status.SKIPPED
         is TestResult.Success -> Status.PASSED
      }

      val uuid = uuids[testCase.descriptor.path()] ?: "Unknown test ${testCase.descriptor.path().value}"
      val details = ResultsUtils.getStatusDetails(result.errorOrNull)

      allure.stopTestCase(uuid)
      allure.updateTestCase(uuid) {
         it.status = status
         it.statusDetails = details.orElseGet { null }
         testCase.descriptor.parents().forEach { d ->
            it.steps.add(
               StepResult()
                  .setName(d.id.value)
                  .setStatus(Status.PASSED)
                  .setStart(0L)
                  .setStop(0L)
            )
         }
      }
      allure.writeTestCase(uuid)
   }

   private fun links(kclass: KClass<*>): List<io.qameta.allure.model.Link?> {
      return listOfNotNull(
         kclass.issue(),
         kclass.link(),
      ) + kclass.links()
   }

   private fun links(testCase: TestCase): List<io.qameta.allure.model.Link?> {
      return listOfNotNull(
         testCase.issue(),
         testCase.link(),
      ) + testCase.links()
   }

   fun allureResultSpecInitFailure(kclass: KClass<*>, t: Throwable) {
      val uuid = UUID.randomUUID()
      val labels = listOfNotNull(
         ResultsUtils.createSuiteLabel(kclass.qualifiedName),
         ResultsUtils.createThreadLabel(),
         ResultsUtils.createHostLabel(),
         ResultsUtils.createLanguageLabel(LANGUAGE_LABEL),
         ResultsUtils.createFrameworkLabel(FRAMEWORK_LABEL),
         ResultsUtils.createPackageLabel(kclass.java.`package`.name),
         kclass.severity(),
         kclass.owner(),
         kclass.epic(),
         kclass.feature(),
         kclass.story()
      )

      val links = links(kclass)

      val result = io.qameta.allure.model.TestResult()
         .setFullName(kclass.qualifiedName)
         .setName(kclass.simpleName)
         .setUuid(uuid.toString())
         .setLabels(labels)
         .setLinks(links)

      allure.scheduleTestCase(result)
      allure.startTestCase(uuid.toString())

      val instanceError = (t.cause as InvocationTargetException).targetException

      val details = StatusDetails()
      details.message = instanceError?.message ?: "Unknown error"
      var trace = ""
      instanceError.stackTrace?.forEach {
         trace += "$it\n"
      }
      details.trace = trace

      allure.updateTestCase(uuid.toString()) {
         it.status = Status.FAILED
         it.statusDetails = details
      }
      allure.stopTestCase(uuid.toString())
      allure.writeTestCase(uuid.toString())
   }

   // returns an id that's acceptable in format for allure
   private fun safeId(descriptor: Descriptor.TestDescriptor): String = descriptor.path(true).value
}
