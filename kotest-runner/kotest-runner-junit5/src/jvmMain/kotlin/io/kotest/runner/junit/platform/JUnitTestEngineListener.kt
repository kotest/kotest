package io.kotest.runner.junit.platform

import io.kotest.core.config.configuration
import io.kotest.core.descriptors.Descriptor
import io.kotest.core.descriptors.DescriptorId
import io.kotest.core.descriptors.spec
import io.kotest.core.descriptors.toDescriptor
import io.kotest.core.spec.Spec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.core.test.TestStatus
import io.kotest.engine.events.AfterProjectListenerException
import io.kotest.engine.events.BeforeProjectListenerException
import io.kotest.engine.listener.TestEngineListener
import io.kotest.engine.test.names.DisplayNameFormatter
import io.kotest.mpp.log
import org.junit.platform.engine.EngineExecutionListener
import org.junit.platform.engine.TestDescriptor
import org.junit.platform.engine.TestExecutionResult
import org.junit.platform.engine.support.descriptor.ClassSource
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

   private val formatter = DisplayNameFormatter(configuration)

   // contains a mapping of junit TestDescriptors, so we can look up the parent
   // when we need to register a new test
   private val descriptors = mutableMapOf<Descriptor, TestDescriptor>()

   // these are specs that have been started
   private val started = mutableSetOf<KClass<*>>()
   private val ignored = mutableSetOf<KClass<*>>()
   private val inactive = mutableSetOf<KClass<*>>()

   // contains an exception throw during beforeSpec or spec instantiation
   private var exceptionThrownBySpec: Throwable? = null

   private val rootTests = mutableListOf<TestCase>()

   private val children = mutableMapOf<Descriptor, MutableList<TestCase>>()

   private val results = mutableMapOf<Descriptor, TestResult>()

   // set to true when we have a test that is ignored, so our check can look for it, if configured
   private var hasIgnoredTest = false

   override suspend fun engineStarted(classes: List<KClass<*>>) {
      log { "JUnitTestEngineListener: Engine started; classes=[$classes]" }
      listener.executionStarted(root)
   }

   override suspend fun engineFinished(t: List<Throwable>) {
      log {
         "JUnitTestEngineListener: Engine finished; throwables=" +
            "[${t.joinToString(separator = "\n", transform = { it.toString() })}]"
      }

      val result = t.map {
         when (it) {
            is AfterProjectListenerException -> {
               val container = createAndRegisterDummySpec(it.name)
               listener.executionStarted(container)
               listener.executionFinished(container, TestExecutionResult.failed(it))
               TestExecutionResult.successful()
            }
            is BeforeProjectListenerException -> {
               val container = createAndRegisterDummySpec(it.name)
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

   private fun markSpecStarted(kclass: KClass<*>): TestDescriptor {
      return try {

         val descriptor = createTestDescriptor(kclass.toDescriptor(), formatter.format(kclass), root)
         descriptors[kclass.toDescriptor()] = descriptor

         log { "JUnitTestEngineListener: Registering junit dynamic test: $descriptor" }
         listener.dynamicTestRegistered(descriptor)

         log { "JUnitTestEngineListener: Notifying junit that a spec was started [$descriptor]" }
         listener.executionStarted(descriptor)

         started.add(kclass)
         descriptor

      } catch (t: Throwable) {
         log(t) { "JUnitTestEngineListener: Error in JUnit Platform listener" }
         throw t
      }
   }

   override suspend fun specInactive(kclass: KClass<*>, results: Map<TestCase, TestResult>) {
      inactive.add(kclass)
   }

   override suspend fun specIgnored(kclass: KClass<out Spec>) {
      ignored.add(kclass)
   }

   private fun markSpecIgnored(kclass: KClass<*>) {

      val descriptor: TestDescriptor = createTestDescriptor(kclass.toDescriptor(), formatter.format(kclass), root)
      descriptors[kclass.toDescriptor()] = descriptor

      log { "JUnitTestEngineListener: Registering junit dynamic test: $descriptor" }
      listener.dynamicTestRegistered(descriptor)

      log { "JUnitTestEngineListener: Notifying junit that a spec was ignored [$descriptor]" }
      listener.executionSkipped(descriptor, null)
   }

   override suspend fun specInstantiationError(kclass: KClass<*>, t: Throwable) {
      exceptionThrownBySpec = t
   }

   override suspend fun specExit(kclass: KClass<out Spec>, t: Throwable?) {

      if (t == null && inactive.contains(kclass)) {
         markSpecIgnored(kclass)
         return
      }

      if (t == null && ignored.contains(kclass)) {
         return
      }

      // if we have a spec error before we even started the spec, we will start the spec, add a placeholder
      // to hold the error, mark that test as failed, and then fail the spec as well
      if (t != null && !started.contains(kclass)) {
         val descriptor = markSpecStarted(kclass)
         addPlaceholderTest(descriptor, t)
         log { "JUnitTestEngineListener: Notifying junit that a spec failed [$descriptor, $t]" }
         listener.executionFinished(descriptor, TestExecutionResult.failed(t))
         return
      }

      // if we had an error in the spec, and we had no tests, we'll add the dummy and return
      if (t != null && rootTests.isEmpty()) {
         val descriptor = descriptors[kclass.toDescriptor()]!!
         addPlaceholderTest(descriptor, t)
         log { "JUnitTestEngineListener: Notifying junit that a spec failed [$descriptor, $t]" }
         listener.executionFinished(descriptor, TestExecutionResult.failed(t))
         return
      }

      val descriptor = descriptors[kclass.toDescriptor()]
      rootTests.forEach { handleTest(it) }

      val result = when {
         t != null -> TestExecutionResult.failed(t)
         exceptionThrownBySpec != null -> TestExecutionResult.failed(exceptionThrownBySpec)
         else -> TestExecutionResult.successful()
      }

      if (descriptor == null) {
         log { "JUnitTestEngineListener: Error retrieving description for spec[${kclass.qualifiedName}]" }
         throw RuntimeException("Error retrieving description for spec ${kclass.qualifiedName}")
      }

      log { "JUnitTestEngineListener: Notifying junit that a spec has finished [$descriptor, $result]" }
      listener.executionFinished(descriptor, result)
   }

   private fun addPlaceholderTest(parent: TestDescriptor, t: Throwable) {
      val descriptor = createTestDescriptor(
         parent.uniqueId.append(Segment.Test.value, "<error>"),
         "<error>",
         TestDescriptor.Type.TEST,
         null,
         false
      )
      parent.addChild(descriptor)
      listener.dynamicTestRegistered(descriptor)
      listener.executionStarted(descriptor)
      listener.executionFinished(descriptor, TestResult.error(t, 0).testExecutionResult())
   }

   override suspend fun testStarted(testCase: TestCase) {
      if (testCase.parent == null) rootTests.add(testCase)
      children.getOrPut(testCase.descriptor.parent) { mutableListOf() }.add(testCase)
   }

   override suspend fun testFinished(testCase: TestCase, result: TestResult) {
      results[testCase.descriptor] = result
   }

   override suspend fun testIgnored(testCase: TestCase, reason: String?) {
      if (testCase.parent == null) rootTests.add(testCase)
      children.getOrPut(testCase.descriptor.parent) { mutableListOf() }.add(testCase)
      results[testCase.descriptor] = TestResult.ignored(reason)
      hasIgnoredTest = true
   }

   private fun handleTest(testCase: TestCase) {

      val parent = getExpectedParent(testCase)

      // we dynamically work out the type by looking to see if this test had any children
      val c = children[testCase.descriptor]
      val type = when {
         c == null || c.isEmpty() -> TestDescriptor.Type.TEST
         else -> TestDescriptor.Type.CONTAINER
      }

      val id = parent.uniqueId.append(testCase.descriptor)
      val source = ClassSource.from(testCase.descriptor.spec().kclass.java)
      val descriptor = createTestDescriptor(
         id,
         formatter.format(testCase),
         type,
         source,
         type == TestDescriptor.Type.CONTAINER
      ).apply {
         parent.addChild(this)
         descriptors[testCase.descriptor] = this
      }

      val result = results[testCase.descriptor] ?: error("Must have result for a finished test")

      log { "JUnitTestEngineListener: Registering junit dynamic test: $descriptor" }
      listener.dynamicTestRegistered(descriptor)

      if (result.status == TestStatus.Ignored) {

         log { "JUnitTestEngineListener: Notifying junit that a test was ignored [$descriptor]" }
         listener.executionSkipped(descriptor, result.reason)

      } else {

         log { "JUnitTestEngineListener: Notifying junit that a test was started [$descriptor]" }
         listener.executionStarted(descriptor)

         children[testCase.descriptor]?.forEach { handleTest(it) }

         log { "JUnitTestEngineListener: Notifying junit that a test has finished [$descriptor]" }
         listener.executionFinished(descriptor, result.testExecutionResult())
      }
   }

   private fun createAndRegisterDummySpec(name: String): TestDescriptor {
      val descriptor = createTestDescriptor(Descriptor.SpecDescriptor(DescriptorId(name), this::class), name, root)
      listener.dynamicTestRegistered(descriptor)
      return descriptor
   }

   private fun getExpectedParent(testCase: TestCase): TestDescriptor {
      val parent: TestDescriptor? = descriptors[testCase.descriptor.parent]
      if (parent == null) {
         val msg = "Cannot find parent description for: ${testCase.descriptor}"
         log { msg }
         error(msg)
      }
      return parent
   }
}
