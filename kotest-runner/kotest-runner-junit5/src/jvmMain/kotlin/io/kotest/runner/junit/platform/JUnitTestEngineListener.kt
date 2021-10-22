package io.kotest.runner.junit.platform

import io.kotest.core.config.Configuration
import io.kotest.core.descriptors.Descriptor
import io.kotest.core.descriptors.DescriptorId
import io.kotest.core.descriptors.spec
import io.kotest.core.descriptors.toDescriptor
import io.kotest.core.names.UniqueNames
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.engine.events.AfterProjectListenerException
import io.kotest.engine.events.BeforeProjectListenerException
import io.kotest.engine.listener.TestEngineListener
import io.kotest.engine.test.names.getDisplayNameFormatter
import io.kotest.mpp.bestName
import io.kotest.mpp.log
import org.junit.platform.engine.EngineExecutionListener
import org.junit.platform.engine.TestDescriptor
import org.junit.platform.engine.TestExecutionResult
import org.junit.platform.engine.support.descriptor.ClassSource
import org.junit.platform.engine.support.descriptor.EngineDescriptor
import kotlin.reflect.KClass
import kotlin.time.Duration

/**
 * Notifies JUnit Platform of test statuses via a [EngineExecutionListener].
 *
 * This is not thread safe and should only be invoked by one spec at a time.
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
   private val configuration: Configuration,
) : TestEngineListener {

   companion object {
      const val PlaceholderName = "<error>"
   }

   private val formatter = getDisplayNameFormatter(configuration)

   // contains a mapping of junit TestDescriptor's, so we can find previously registered tests
   private val descriptors = mutableMapOf<Descriptor, TestDescriptor>()

   // contains an exception throw during instantiation
   private var instantiationException: Throwable? = null

   private var ignored = false
   private var started = false
   private var inactive = false
   private var inactiveTests: Map<TestCase, TestResult> = emptyMap()

   // the root tests are our entry point when outputting results
   private val rootTests = mutableListOf<TestCase>()

   private val children = mutableMapOf<Descriptor, MutableList<TestCase>>()

   private val results = mutableMapOf<Descriptor, TestResult>()

   private val dummies = hashSetOf<String>()

   override suspend fun engineStarted() {
      log { "JUnitTestEngineListener: Engine started" }
      listener.executionStarted(root)
   }

   override suspend fun engineFinished(t: List<Throwable>) {
      log { "JUnitTestEngineListener: Engine finished; throwables=[${t}]" }

      registerExceptionPlaceholders(t)

      val result = if (configuration.failOnIgnoredTests && results.values.any { it.isIgnored }) {
         TestExecutionResult.failed(RuntimeException("Build contained ignored test"))
      } else {
         TestExecutionResult.successful()
      }

      log { "JUnitTestEngineListener: Notifying junit that root descriptor completed $root" }
      listener.executionFinished(root, result)
   }

   private fun registerExceptionPlaceholders(ts: List<Throwable>) {
      ts.forEach {
         when (it) {
            is AfterProjectListenerException -> {
               val container = createAndRegisterDummySpec(it.name)
               listener.executionStarted(container)
               listener.executionFinished(container, TestExecutionResult.failed(it))
            }
            is BeforeProjectListenerException -> {
               val container = createAndRegisterDummySpec(it.name)
               listener.executionStarted(container)
               listener.executionFinished(container, TestExecutionResult.failed(it))
            }
            else -> {
               val container = createAndRegisterDummySpec(it::class.bestName())
               listener.executionStarted(container)
               listener.executionFinished(container, TestExecutionResult.failed(it))
            }
         }
      }
   }

   override suspend fun specStarted(kclass: KClass<*>) {
      markSpecStarted(kclass)
   }

   private fun markSpecStarted(kclass: KClass<*>): TestDescriptor {
      return try {

         val descriptor =
            createDescriptorForSpec(kclass.toDescriptor(), formatter.format(kclass), root)
         descriptors[kclass.toDescriptor()] = descriptor

         log { "JUnitTestEngineListener: Registering junit dynamic test: $descriptor" }
         listener.dynamicTestRegistered(descriptor)

         log { "JUnitTestEngineListener: Notifying junit that a spec was started [$descriptor]" }
         listener.executionStarted(descriptor)

         started = true
         descriptor

      } catch (t: Throwable) {
         log(t) { "JUnitTestEngineListener: Error in JUnit Platform listener" }
         throw t
      }
   }

   override suspend fun specInactive(kclass: KClass<*>, results: Map<TestCase, TestResult>) {
      log { "JUnitTestEngineListener: Spec is being flagged as inactive: $kclass" }
      inactive = true
      inactiveTests = results
   }

   override suspend fun specIgnored(kclass: KClass<*>) {
      ignored = true
   }

   private suspend fun markSpecInactive(kclass: KClass<*>) {

      val descriptor: TestDescriptor =
         createDescriptorForSpec(kclass.toDescriptor(), formatter.format(kclass), root)
      descriptors[kclass.toDescriptor()] = descriptor

      log { "JUnitTestEngineListener: Registering junit dynamic test: $descriptor" }
      listener.dynamicTestRegistered(descriptor)

      inactiveTests.forEach { (tc, result) ->
         testIgnored(tc, if (result is TestResult.Ignored) result.reason else null)
         handleTest(tc)
      }

      log { "JUnitTestEngineListener: Notifying junit that a spec was ignored [$descriptor]" }
      listener.executionSkipped(descriptor, null)
   }

   override suspend fun specInstantiationError(kclass: KClass<*>, t: Throwable) {
      instantiationException = t
   }

   override suspend fun specExit(kclass: KClass<*>, t: Throwable?) {
      when {
         t == null && ignored -> Unit
         t == null && inactive -> markSpecInactive(kclass)
         // if we have a spec error before we even started the spec, we will start the spec, add a placeholder
         // to hold the error, mark that test as failed, and then fail the spec as well
         t != null && !started -> {
            val descriptor = markSpecStarted(kclass)
            addPlaceholderTest(descriptor, t)
            log { "JUnitTestEngineListener: Notifying junit that a spec failed [$descriptor, $t]" }
            listener.executionFinished(descriptor, TestExecutionResult.failed(t))
         }
         // if we had an error in the spec, and we had no tests, we'll add the dummy and return
         t != null && rootTests.isEmpty() -> {
            val descriptor = descriptors[kclass.toDescriptor()]!!
            addPlaceholderTest(descriptor, t)
            log { "JUnitTestEngineListener: Notifying junit that a spec failed [$descriptor, $t]" }
            listener.executionFinished(descriptor, TestExecutionResult.failed(t))
         }
         else -> {
            val descriptor = descriptors[kclass.toDescriptor()]
            rootTests.forEach { handleTest(it) }

            val result = when {
               t != null -> TestExecutionResult.failed(t)
               instantiationException != null -> TestExecutionResult.failed(instantiationException)
               else -> TestExecutionResult.successful()
            }

            if (descriptor == null) {
               log { "JUnitTestEngineListener: Error retrieving description for spec[${kclass.qualifiedName}]" }
               throw RuntimeException("Error retrieving description for spec ${kclass.qualifiedName}")
            }

            log { "JUnitTestEngineListener: Notifying junit that a spec has finished [$descriptor, $result]" }
            listener.executionFinished(descriptor, result)
         }
      }
      reset()
   }

   private fun reset() {
      rootTests.clear()
      children.clear()
      results.clear()
      started = false
      ignored = false
      inactive = false
      inactiveTests = emptyMap()
      descriptors.clear()
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
      listener.executionFinished(descriptor, TestResult.Error(Duration.ZERO, t).testExecutionResult())
   }

   override suspend fun testStarted(testCase: TestCase) {
      if (testCase.parent == null) rootTests.add(testCase)
      addChild(testCase)
   }

   override suspend fun testFinished(testCase: TestCase, result: TestResult) {
      results[testCase.descriptor] = result
   }

   override suspend fun testIgnored(testCase: TestCase, reason: String?) {
      if (testCase.parent == null) rootTests.add(testCase)
      addChild(testCase)
      results[testCase.descriptor] = TestResult.Ignored(reason)
   }

   private fun addChild(testCase: TestCase) {
      children.getOrPut(testCase.descriptor.parent) { mutableListOf() }.add(testCase)
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

      val result = results[testCase.descriptor] ?: error("Must have result for a finished test: ${testCase.descriptor}")

      log { "JUnitTestEngineListener: Registering junit dynamic test: $descriptor" }
      listener.dynamicTestRegistered(descriptor)

      if (result is TestResult.Ignored) {

         log { "JUnitTestEngineListener: Notifying junit that a test was ignored [$descriptor]" }
         listener.executionSkipped(descriptor, result.reason)

      } else {

         log { "JUnitTestEngineListener: Notifying junit that a test was started [$descriptor]" }
         listener.executionStarted(descriptor)

         children[testCase.descriptor]?.distinctBy { it.descriptor }?.forEach { handleTest(it) }

         log { "JUnitTestEngineListener: Notifying junit that a test has finished [$descriptor]" }
         listener.executionFinished(descriptor, result.testExecutionResult())
      }
   }

   private fun createAndRegisterDummySpec(name: String): TestDescriptor {
      val unique = UniqueNames.unique(name, dummies) ?: name
      dummies.add(unique)
      val descriptor =
         createDescriptorForSpec(Descriptor.SpecDescriptor(DescriptorId(unique), this::class), unique, root)
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
