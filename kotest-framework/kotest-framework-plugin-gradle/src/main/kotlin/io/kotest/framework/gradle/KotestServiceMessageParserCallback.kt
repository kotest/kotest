package io.kotest.framework.gradle

import jetbrains.buildServer.messages.serviceMessages.ServiceMessage
import jetbrains.buildServer.messages.serviceMessages.ServiceMessageParserCallback
import jetbrains.buildServer.messages.serviceMessages.ServiceMessageTypes
import org.gradle.api.internal.tasks.testing.DefaultTestSuiteDescriptor
import org.gradle.api.internal.tasks.testing.TestDescriptorInternal
import org.gradle.api.internal.tasks.testing.results.DefaultTestResult
import org.gradle.api.tasks.testing.TestFailure
import org.gradle.api.tasks.testing.TestListener
import org.gradle.api.tasks.testing.TestOutputListener
import org.gradle.api.tasks.testing.TestResult
import java.text.ParseException

class KotestServiceMessageParserCallback(
   private val root: DefaultTestSuiteDescriptor,
   private val listeners: List<TestListener>,
   private val outputListeners: MutableList<TestOutputListener>
) : ServiceMessageParserCallback {

   private val startTimes = mutableMapOf<String, Long>()
   private val descriptors = mutableMapOf<String, KotestTestDescriptor>()

   override fun regularText(p0: String) {
      println("Regular text $p0")
   }

   override fun parseException(p0: ParseException, p1: String) {
      println("Parse exception $p0 $p1")
   }

   override fun serviceMessage(msg: ServiceMessage) {
      println("Service message $msg")
      when (msg.messageName) {
         ServiceMessageTypes.TEST_STARTED -> notifyBeforeTest(msg)
         ServiceMessageTypes.TEST_FAILED,
         ServiceMessageTypes.TEST_IGNORED,
         ServiceMessageTypes.TEST_FINISHED -> notifyAfterTest(msg)

         ServiceMessageTypes.TEST_SUITE_STARTED -> notifyBeforeTestSuite(msg)
         ServiceMessageTypes.TEST_SUITE_FINISHED -> notifyAfterTestSuite(msg)
      }
   }

   private fun createDescriptor(msg: ServiceMessage): KotestTestDescriptor {

      val id = msg.attributes["id"] ?: error("id must be defined")
      val name = msg.attributes["name"] ?: error("name must be defined")
      val parent = descriptors[msg.attributes["parent_id"]]

      var temp = parent
      while (temp != null)
         temp = temp.parentTestDescription()
      val fqn = temp?.testId ?: id

      val desc = when (msg.attributes["test_type"]?.lowercase()) {
         "spec" -> SpecDescriptor(name, root)
         "test" -> TestCaseDescriptor(id, name, fqn, parent ?: error("must have parent"))
         "container" -> ContainerDescriptor(id, name, fqn, parent ?: error("must have parent"))
         else -> error("Unknown test type $msg")
      }

      descriptors[desc.testId] = desc
      return desc
   }

   private fun getDescriptor(msg: ServiceMessage): KotestTestDescriptor {
      val id = msg.attributes["id"] ?: error("id must be defined")
      return descriptors[id] ?: error("description for $id must exist")
   }

   private fun notifyBeforeTest(msg: ServiceMessage) {
      val desc = createDescriptor(msg)
      startTimes[desc.testId] = System.currentTimeMillis()
      listeners.forEach { it.beforeTest(desc) }
   }

   private fun notifyBeforeTestSuite(msg: ServiceMessage) {
      val desc = createDescriptor(msg)
      startTimes[desc.testId] = System.currentTimeMillis()
      listeners.forEach { it.beforeSuite(desc) }
   }

   private fun notifyAfterTest(msg: ServiceMessage) {

      val desc = getDescriptor(msg)
      val start = startTimes[desc.testId] ?: 0L

      val resultType = when (msg.messageName) {
         ServiceMessageTypes.TEST_FAILED -> TestResult.ResultType.FAILURE
         ServiceMessageTypes.TEST_FINISHED -> TestResult.ResultType.SUCCESS
         ServiceMessageTypes.TEST_IGNORED -> TestResult.ResultType.SKIPPED
         else -> error("Unsupported type for result ${msg.messageName}")
      }

      // if we have a FAILURE, we treat the message if set as an error message
      val errors = when (resultType) {
         TestResult.ResultType.FAILURE ->
            TestFailure.fromTestAssertionFailure(RuntimeException(msg.attributes["message"]), null, null)

         else -> null
      }

      val result = DefaultTestResult(resultType, start, System.currentTimeMillis(), 0, 0, 0, listOf(errors))
      listeners.forEach { it.afterTest(desc, result) }
   }

   private fun notifyAfterTestSuite(msg: ServiceMessage) {

      val desc = getDescriptor(msg)
      val start = startTimes[desc.testId] ?: 0L

      // teamcity format doesn't have a status for test suite, so we use kotest's own
      val resultType = when (msg.attributes["result_status"]?.lowercase()) {
         "success" -> TestResult.ResultType.SUCCESS
         else -> TestResult.ResultType.FAILURE
      }

      // if we have a FAILURE, we treat the message if set as an error message
      val errors = when (resultType) {
         TestResult.ResultType.FAILURE ->
            TestFailure.fromTestAssertionFailure(RuntimeException(msg.attributes["message"]), null, null)

         else -> null
      }

      val result = DefaultTestResult(resultType, start, System.currentTimeMillis(), 0, 0, 0, listOf(errors))
      listeners.forEach { it.afterSuite(desc, result) }
   }
}

interface KotestTestDescriptor : TestDescriptorInternal {
   val testId: String
   val testType: String
   fun parentTestDescription(): KotestTestDescriptor?
}

data class TestCaseDescriptor(
   override val testId: String,
   val testName: String,
   val fqn: String,
   val parentTestDescriptor: KotestTestDescriptor
) : KotestTestDescriptor {
   override fun getName(): String = testName
   override fun getDisplayName(): String = testName
   override fun getClassName(): String = fqn
   override fun isComposite(): Boolean = false
   override fun getParent(): TestDescriptorInternal = parentTestDescriptor
   override fun parentTestDescription(): KotestTestDescriptor = parentTestDescriptor
   override fun getId(): Any = testId
   override fun getClassDisplayName(): String = fqn
   override val testType: String = "Test"
}

data class ContainerDescriptor(
   override val testId: String,
   val testName: String,
   val fqn: String,
   val parentTestDescriptor: KotestTestDescriptor
) : KotestTestDescriptor {
   override fun getName(): String = testName
   override fun getDisplayName(): String = testName
   override fun getClassName(): String = fqn
   override fun isComposite(): Boolean = true
   override fun getParent(): TestDescriptorInternal = parentTestDescriptor
   override fun parentTestDescription(): KotestTestDescriptor = parentTestDescriptor
   override fun getId(): Any = testId
   override fun getClassDisplayName(): String = fqn
   override val testType: String = "Container"
}

data class SpecDescriptor(
   val fqn: String,
   val root: DefaultTestSuiteDescriptor
) : KotestTestDescriptor {
   override fun getName(): String = fqn
   override fun getDisplayName(): String = fqn
   override fun getClassName(): String = fqn
   override fun isComposite(): Boolean = true
   override fun getParent(): TestDescriptorInternal = root
   override fun parentTestDescription(): KotestTestDescriptor? = null
   override fun getId(): Any = fqn
   override val testId: String = fqn
   override fun getClassDisplayName(): String = fqn
   override val testType: String = "Spec"
}
