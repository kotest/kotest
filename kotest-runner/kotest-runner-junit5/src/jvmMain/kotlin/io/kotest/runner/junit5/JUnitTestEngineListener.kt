package io.kotest.runner.junit5

import io.kotest.core.config.Project
import io.kotest.core.internal.writeSpecFailures
import io.kotest.core.spec.Spec
import io.kotest.core.spec.description
import io.kotest.core.test.*
import io.kotest.runner.jvm.TestEngineListener
import org.junit.platform.engine.*
import org.junit.platform.engine.support.descriptor.*
import org.slf4j.LoggerFactory
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
   val root: EngineDescriptor
) : TestEngineListener {

   private val logger = LoggerFactory.getLogger(this.javaClass)

   // contains a mapping of a Description to a junit TestDescription, so we can look up the parent
   // when we need to register a new test
   private val descriptors = mutableMapOf<Description, TestDescriptor>()

   // contains all the results so we can fail a parent when a child has failed
   private val results = mutableListOf<Pair<Description, TestResult>>()

   // contains any spec that failed so we can write out the failed specs file
   private val failedSpecs = mutableSetOf<KClass<out Spec>>()

   private var specException: Throwable? = null

   override fun engineStarted(classes: List<KClass<out Spec>>) {
      logger.trace("Engine started; classes=[$classes]")
      listener.executionStarted(root)
   }

   /**
    * Returns true if any test result has status of [TestStatus.Ignored].
    */
   private fun hasIgnored() = results.any { it.second.status == TestStatus.Ignored }

   override fun engineFinished(t: Throwable?) {
      logger.trace("Engine finished; throwable=[$t]")

      if (Project.writeSpecFailureFile())
         writeSpecFailures(failedSpecs, Project.specFailureFilePath())

      val result = when {
         t != null -> TestExecutionResult.failed(t)
         Project.failOnIgnoredTests() && hasIgnored() ->
            TestExecutionResult.failed(RuntimeException("Build contained ignored test"))
         else -> TestExecutionResult.successful()
      }

      logger.trace("Notifying junit that root descriptor completed $root")
      listener.executionFinished(root, result)
   }

   override fun specStarted(kclass: KClass<out Spec>) {
      logger.trace("specStarted [${kclass.qualifiedName}]")
      try {
         val descriptor = kclass.descriptor(root)
         descriptors[kclass.description()] = descriptor

         logger.trace("Registering junit dynamic test and notifiying start: $descriptor")
         listener.dynamicTestRegistered(descriptor)
         listener.executionStarted(descriptor)
      } catch (t: Throwable) {
         logger.error("Error in JUnit Platform listener", t)
         specException = t
      }
   }

   override fun specFinished(
      kclass: KClass<out Spec>,
      t: Throwable?,
      results: Map<TestCase, TestResult>
   ) {
      logger.trace("specFinished [$kclass]")

      val descriptor = descriptors[kclass.description()]
         ?: throw RuntimeException("Error retrieving description for spec: ${kclass.qualifiedName}")

      // we are ignoring junit guidelines here and failing the spec if any of it's tests failed
      // this is because in gradle and intellij nested errors are not very obvious
      val nestedFailure = findChildFailure(kclass.description())

      (specException ?: t ?: nestedFailure?.error)?.apply {
         checkSpecVisiblity(kclass, this)
      }

      val result = when {
         t != null -> TestExecutionResult.failed(t)
         specException != null -> TestExecutionResult.failed(specException)
         nestedFailure != null -> nestedFailure.testExecutionResult()
         else -> TestExecutionResult.successful()
      }

      logger.trace("Notifying junit that execution has finished: $descriptor, $result")
      listener.executionFinished(descriptor, result)
   }

   /**
    * If the spec fails to be created, then there will be no tests, so we should insert an instantiation
    * failed test so that the spec shows up.
    */
   override fun specInstantiationError(kclass: KClass<out Spec>, t: Throwable) {
      specException = t
   }

   /**
    * Checks that the spec has at least one test attached in case of failure.
    */
   private fun checkSpecVisiblity(kclass: KClass<out Spec>, t: Throwable) {
      val description = kclass.description()
      if (!isVisible(description)) {
         val spec = descriptors[description]!!
         val test =
            spec.append(description.append("Spec execution failed"), TestDescriptor.Type.TEST, null, Segments.test)
         listener.dynamicTestRegistered(test)
         listener.executionStarted(test)
         listener.executionFinished(test, TestExecutionResult.aborted(t))
      }
   }

   /**
    * Returns true if the given description is visible.
    * That means it must have at least one non container test attached to it.
    */
   private fun isVisible(description: Description) =
      results.any { description.isAncestorOf(it.first) }

   override fun testStarted(testCase: TestCase) {
      val descriptor = createTestDescriptor(testCase)
      logger.trace("Registering junit dynamic test: $descriptor")
      listener.dynamicTestRegistered(descriptor)
      logger.trace("Notifying junit that execution has started: $descriptor")
      listener.executionStarted(descriptor)
   }

   override fun testFinished(testCase: TestCase, result: TestResult) {
      val descriptor = descriptors[testCase.description]
         ?: throw RuntimeException("Error retrieving description for: ${testCase.description}")
      results.add(Pair(testCase.description, result))

      // if we have a success we override with a child error if one exists
      val resultp = when (result.status) {
         TestStatus.Success -> findChildFailure(testCase.description) ?: result
         else -> result
      }

      logger.trace("Notifying junit that execution has finished: $descriptor")
      listener.executionFinished(descriptor, resultp.testExecutionResult())
   }

   override fun testIgnored(testCase: TestCase, reason: String?) {
      val descriptor = createTestDescriptor(testCase)
      listener.dynamicTestRegistered(descriptor)
      listener.executionSkipped(descriptor, reason)
   }

   private fun createTestDescriptor(testCase: TestCase): TestDescriptor {
      val parent = descriptors[testCase.description.parent()]
      if (parent == null) {
         val msg = "Cannot find parent description for: ${testCase.description}"
         logger.error(msg)
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

   /**
    * Returns a failed or errored [TestResult] for a given description's children by searching
    * the results list.
    */
   private fun findChildFailure(description: Description): TestResult? {
      return results
         .filter { description.isAncestorOf(it.first) }
         .filter { it.second.status == TestStatus.Error || it.second.status == TestStatus.Failure }
         // the lowest level test should be what we pick
         .sortedBy { it.first.depth() }
         .reversed()
         .map { it.second }
         .firstOrNull()
   }
}
