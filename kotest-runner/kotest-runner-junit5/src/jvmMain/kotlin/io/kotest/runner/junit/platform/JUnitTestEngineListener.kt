package io.kotest.runner.junit.platform

import io.kotest.core.config.configuration
import io.kotest.core.plan.toDescriptor
import io.kotest.core.sourceRef
import io.kotest.core.spec.Spec
import io.kotest.core.spec.toDescription
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestPath
import io.kotest.core.test.TestResult
import io.kotest.engine.events.AfterProjectListenerException
import io.kotest.engine.events.BeforeProjectListenerException
import io.kotest.engine.listener.TestEngineListener
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

   // contains a mapping of junit TestDescriptors, so we can look up the parent
   // when we need to register a new test
   private val descriptors = mutableMapOf<TestPath, TestDescriptor>()

   // these are specs that have been started
   private val started = mutableSetOf<KClass<*>>()

   private val ignored = mutableSetOf<KClass<*>>()

   // contains an exception throw during beforeSpec or spec instantiation
   private var exceptionThrownBySpec: Throwable? = null

   private var hasVisibleTest = false
   private var hasIgnoredTest = false

   override suspend fun engineStarted(classes: List<KClass<*>>) {
      log { "Engine started; classes=[$classes]" }
      listener.executionStarted(root)
   }

   override suspend fun engineFinished(t: List<Throwable>) {
      log { "Engine finished; throwables=[${t.joinToString(separator = "\n", transform = { it.toString() })}]" }

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

      log { "JUnitTestEngineListener: Notifying junit that root descriptor completed $root" }
      listener.executionFinished(root, result)
   }

   override suspend fun specStarted(kclass: KClass<*>) {
      markSpecStarted(kclass)
   }

   override suspend fun specInactive(kclass: KClass<*>, results: Map<TestCase, TestResult>) {
      // if we display the spec whenever there are no active tests or not
      // then we can just mark it as ignored
      if (configuration.displaySpecIfNoActiveTests) {
         markSpecIgnored(kclass)
      }
   }

   /**
    * Registers a spec level [TestDescriptor]and marks it as started.
    * Should not be invoked twice or JUnit will error.
    */
   private fun markSpecStarted(kclass: KClass<*>) {
      try {

         val descriptor = kclass.descriptor(root)
         val path = kclass.toDescription().toDescriptor(sourceRef()).testPath()
         descriptors[path] = descriptor

         log { "JUnitTestEngineListener: Registering junit dynamic test and notifiying start: $descriptor" }
         listener.dynamicTestRegistered(descriptor)
         listener.executionStarted(descriptor)
         started.add(kclass)

      } catch (t: Throwable) {
         log(t) { "JUnitTestEngineListener: Error in JUnit Platform listener" }
         throw t
      }
   }

   override suspend fun specIgnored(kclass: KClass<out Spec>) {
      markSpecIgnored(kclass)
   }

   private fun markSpecIgnored(kclass: KClass<*>) {
      val descriptor: TestDescriptor = kclass.descriptor(root)
      val path = kclass.toDescription().toDescriptor(sourceRef()).testPath()
      descriptors[path] = descriptor
      log { "JUnitTestEngineListener: Registering junit dynamic test: $descriptor" }
      listener.dynamicTestRegistered(descriptor)
      log { "JUnitTestEngineListener: Notifying junit that a spec was ignored [$descriptor]" }
      listener.executionSkipped(descriptor, null)
      ignored.add(kclass)
   }

   override suspend fun specInstantiationError(kclass: KClass<*>, t: Throwable) {
      exceptionThrownBySpec = t
   }

   override suspend fun specExit(kclass: KClass<out Spec>, t: Throwable?) {

      // if the test was already ignored there's no further notifications we can do
      // but if an error we should throw it to let the caller handle it as a root error
      if (ignored.contains(kclass)) {
         if (t != null) throw t
         return
      }

      val result = when {
         t != null -> TestExecutionResult.failed(t)
         exceptionThrownBySpec != null -> TestExecutionResult.failed(exceptionThrownBySpec)
         else -> TestExecutionResult.successful()
      }

      // if the test wasn't started (because an error happened before specStarted) then we need
      // to start it here
      if (!started.contains(kclass))
         markSpecStarted(kclass)

      val descriptor = descriptors[kclass.toDescription().toDescriptor(sourceRef()).testPath()]
         ?: throw RuntimeException("Error retrieving description for spec: ${kclass.qualifiedName}")

      log { "JUnitTestEngineListener: Notifying junit that a spec has finished [$descriptor, $result]" }
      listener.executionFinished(descriptor, result)
   }

   override suspend fun testStarted(testCase: TestCase) {
      val descriptor = createTestDescriptor(testCase)
      log { "JUnitTestEngineListener: : Registering junit dynamic test: $descriptor" }
      listener.dynamicTestRegistered(descriptor)
      log { "JUnitTestEngineListener: : Notifying junit that execution has started: $descriptor" }
      listener.executionStarted(descriptor)
      hasVisibleTest = true
   }

   override suspend fun testFinished(testCase: TestCase, result: TestResult) {
      val descriptor = descriptors[testCase.description.toDescriptor(testCase.source).testPath()]
         ?: throw RuntimeException("Error retrieving description for: ${testCase.description}")
      log { "Notifying junit that a test has finished [$descriptor]" }
      listener.executionFinished(descriptor, result.testExecutionResult())
   }

   override suspend fun testIgnored(testCase: TestCase, reason: String?) {
      val descriptor = createTestDescriptor(testCase)
      hasIgnoredTest = true
      log { "Notifying junit that a test was ignored [$descriptor]" }
      listener.dynamicTestRegistered(descriptor)
      listener.executionSkipped(descriptor, reason)
   }

   private fun createAndRegisterTest(name: String): TestDescriptor {
      val descriptor = root.append(name, TestDescriptor.Type.TEST, null, Segment.Spec)
      listener.dynamicTestRegistered(descriptor)
      return descriptor
   }

   private fun createTestDescriptor(testCase: TestCase): TestDescriptor {
      val parent = descriptors[testCase.description.parent.toDescriptor(testCase.source).testPath()]
      if (parent == null) {
         val msg = "Cannot find parent description for: ${testCase.description}"
         log { msg }
         error(msg)
      }
      val descriptor = parent.descriptor(testCase)
      descriptors[testCase.description.toDescriptor(testCase.source).testPath()] = descriptor
      return descriptor
   }
}
