package io.kotest.runner.junit.platform

import io.kotest.core.spec.Spec
import io.kotest.core.config.configuration
import io.kotest.engine.listener.TestEngineListener
import io.kotest.engine.writeSpecFailures
import io.kotest.core.listeners.AfterProjectListenerException
import io.kotest.core.listeners.BeforeProjectListenerException
import io.kotest.core.test.Description
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.core.test.TestStatus
import io.kotest.core.test.TestType
import io.kotest.core.spec.toDescription
import io.kotest.core.test.createTestName
import io.kotest.mpp.log
import org.junit.platform.engine.EngineExecutionListener
import org.junit.platform.engine.TestDescriptor
import org.junit.platform.engine.TestExecutionResult
import org.junit.platform.engine.support.descriptor.EngineDescriptor
import kotlin.reflect.KClass

/**
 * Notifies JUnit Platform of test statuses via a [EngineExecutionListener].
 *
 * JUnit platform supports out of order notification of tests, in that sibling
 * tests can be executing in parallel and updating JUnit out of order. However the gradle test
 * task gets confused if we are executing two or more tests directly under the root at once.
 * Therefore we must queue up notifications until each spec is completed.
 *
 * Gradle test run observations:
 *
 * using Platform 1.6.0 --
 * TestDescriptor.Type.CONTAINER seem to be ignored in output.
 * TestDescriptor.Type.CONTAINER_AND_TEST appear as siblings of their nested tests if not added as a child
 * Add child first, then register dynamic test, then start the test
 *
 * Top level descriptors must have a source attached or the execution will fail with a parent attached exception.
 * Type.CONTAINER_TEST doesn't seem to work as a top level descriptor, it will hang
 * leaf tests do not need to be completed but they will be marked as uncomplete in intellij.
 * Dynamic test can be called after or before addChild.
 * A Type.TEST can be a child of a Type.TEST.
 * Intermediate Type.CONTAINER seem to be ignored in output.
 * Intermediate containers can have same class source as parent.
 * Type.TEST as top level seems to hang.
 * A TEST doesn't seem to be able to have the same source as a parent, or hang.
 * A TEST seems to hang if it has a ClassSource.
 * MethodSource seems to be ok with a TEST.
 * Container test names seem to be taken from a Source.
 * Nested tests are outputted as siblings.
 * Can complete executions out of order.
 * Child failures will fail parent CONTAINER.
 * Sibling containers can start and finish in parallel.
 *
 * Intellij runner observations:
 *
 * Intermediate Type.CONTAINERs are shown.
 * Intermediate Type.TESTs are shown.
 * A Type.TEST can be a child of a Type.TEST
 * MethodSource seems to be ok with a TEST.
 * Container test names seem to be taken from the name property.
 * Nested tests are outputted as nested.
 * Child failures will not fail containing TEST.
 * child failures will fail a containing CONTAINER.
 * Call addChild _before_ registering test otherwise will appear in the display out of order.
 * Must start tests after their parent or they can go missing.
 * Sibling containers can start and finish in parallel.
 */
class JUnitTestEngineListener(
   private val listener: EngineExecutionListener,
   val root: EngineDescriptor,
) : TestEngineListener {

   // contains a mapping of a Description to a junit TestDescription, so we can look up the parent
   // when we need to register a new test
   private val descriptors = mutableMapOf<Description, TestDescriptor>()

   // contains any spec that failed so we can write out the failed specs file
   private val failedSpecs = mutableSetOf<KClass<out Spec>>()

   // contains an exception throw during beforeSpec or spec instantiation
   private var exceptionThrowBySpec: Throwable? = null

   private var hasVisibleTest = false

   private var hasIgnoredTest = false

   override fun engineStarted(classes: List<KClass<out Spec>>) {
      log("Engine started; classes=[$classes]")
      listener.executionStarted(root)
   }

   override fun engineFinished(t: List<Throwable>) {
      log("Engine finished; throwables=[${t.joinToString(separator = "\n", transform = { it.toString() })}]")

      if (configuration.writeSpecFailureFile)
         writeSpecFailures(failedSpecs, configuration.specFailureFilePath)

      val result = t.map {
         when (it) {
            is AfterProjectListenerException -> {
               val container = createAndRegisterTest(it.name)
               listener.executionStarted(container)
               listener.executionFinished(container, TestExecutionResult.failed(it))
               TestExecutionResult.successful()
            }
            is BeforeProjectListenerException -> {
               val container = createAndRegisterTest(it.name)
               listener.executionStarted(container)
               listener.executionFinished(container, TestExecutionResult.failed(it))
               TestExecutionResult.successful()
            }
            else -> TestExecutionResult.failed(it)
         }
      }.find { it.status == TestExecutionResult.Status.FAILED }
         ?: if (configuration.failOnIgnoredTests && hasIgnoredTest) {
            TestExecutionResult.failed(RuntimeException("Build contained ignored test"))
         } else {
            TestExecutionResult.successful()
         }

      log("Notifying junit that root descriptor completed $root")
      listener.executionFinished(root, result)
   }

   override fun specStarted(kclass: KClass<out Spec>) {
      log("specStarted [${kclass.qualifiedName}]")

      // reset the flags for this spec
      hasVisibleTest = false
      hasIgnoredTest = false

      try {
         val descriptor = kclass.descriptor(root)
         descriptors[kclass.toDescription()] = descriptor

         log("Registering junit dynamic test and notifiying start: $descriptor")
         listener.dynamicTestRegistered(descriptor)
         listener.executionStarted(descriptor)
      } catch (t: Throwable) {
         log("Error in JUnit Platform listener", t)
         exceptionThrowBySpec = t
      }
   }

   override fun specFinished(
      kclass: KClass<out Spec>,
      t: Throwable?,
      results: Map<TestCase, TestResult>,
   ) {
      log("specFinished [$kclass]")

      val descriptor = descriptors[kclass.toDescription()]
         ?: throw RuntimeException("Error retrieving description for spec: ${kclass.qualifiedName}")

      // if the spec itself had an error then we must make sure we add at least one nested test so that
      // the test shows up properly in intellij
      (exceptionThrowBySpec ?: t)?.apply {
         ensureSpecIsVisible(kclass, this)
      }

      val result = when {
         t != null -> TestExecutionResult.failed(t)
         exceptionThrowBySpec != null -> TestExecutionResult.failed(exceptionThrowBySpec)
         else -> TestExecutionResult.successful()
      }

      log("Notifying junit that a spec has finished [$descriptor, $result]")
      listener.executionFinished(descriptor, result)
   }

   /**
    * If the spec fails to be created, then there will be no tests, so we should insert an instantiation
    * failed test so that the spec shows up.
    */
   override fun specInstantiationError(kclass: KClass<out Spec>, t: Throwable) {
      exceptionThrowBySpec = t
   }

   /**
    * Checks that the spec has at least one test attached in case of failure.
    * If it doesn't, then it will add a dummy test name to ensure it appears.
    */
   private fun ensureSpecIsVisible(kclass: KClass<out Spec>, t: Throwable) {
      if (!hasVisibleTest) {
         val description = kclass.toDescription()
         val spec = descriptors[description]!!
         val test = spec.append(
            description.append(createTestName("Spec execution failed"), TestType.Test), TestDescriptor.Type.TEST, null,
            Segment.Test
         )
         listener.dynamicTestRegistered(test)
         listener.executionStarted(test)
         listener.executionFinished(test, TestExecutionResult.aborted(t))
      }
   }

   override fun testStarted(testCase: TestCase) {
      val descriptor = createTestDescriptor(testCase)
      log("Registering junit dynamic test: $descriptor")
      listener.dynamicTestRegistered(descriptor)
      log("Notifying junit that execution has started: $descriptor")
      listener.executionStarted(descriptor)
      hasVisibleTest = true
   }

   override fun testFinished(testCase: TestCase, result: TestResult) {
      val descriptor = descriptors[testCase.description]
         ?: throw RuntimeException("Error retrieving description for: ${testCase.description}")
      log("Notifying junit that a test has finished [$descriptor]")
      listener.executionFinished(descriptor, result.testExecutionResult())
   }

   override fun testIgnored(testCase: TestCase, reason: String?) {
      val descriptor = createTestDescriptor(testCase)
      hasIgnoredTest = true
      log("Notifying junit that a test was ignored [$descriptor]")
      listener.dynamicTestRegistered(descriptor)
      listener.executionSkipped(descriptor, reason)
   }

   private fun createAndRegisterTest(name: String): TestDescriptor {
      val descriptor = root.append(name, TestDescriptor.Type.TEST, null, Segment.Spec)
      listener.dynamicTestRegistered(descriptor)
      return descriptor
   }

   private fun createTestDescriptor(testCase: TestCase): TestDescriptor {
      val parent = descriptors[testCase.description.parent]
      if (parent == null) {
         val msg = "Cannot find parent description for: ${testCase.description}"
         log(msg)
         error(msg)
      }
      val descriptor = parent.descriptor(testCase)
      descriptors[testCase.description] = descriptor
      return descriptor
   }

   /**
    * Returns a JUnit [TestExecutionResult] populated from the values of the Kotest [TestResult].
    */
   private fun TestResult.testExecutionResult(): TestExecutionResult = when (this.status) {
      TestStatus.Ignored -> error("An ignored test cannot reach this state")
      TestStatus.Success -> TestExecutionResult.successful()
      TestStatus.Error -> TestExecutionResult.failed(this.error)
      TestStatus.Failure -> TestExecutionResult.failed(this.error)
   }
}
