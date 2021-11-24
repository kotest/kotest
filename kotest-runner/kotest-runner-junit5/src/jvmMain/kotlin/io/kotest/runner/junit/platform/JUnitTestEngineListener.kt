package io.kotest.runner.junit.platform

import io.kotest.core.config.ProjectConfiguration
import io.kotest.core.descriptors.Descriptor
import io.kotest.core.descriptors.DescriptorId
import io.kotest.core.descriptors.toDescriptor
import io.kotest.core.names.DisplayNameFormatter
import io.kotest.core.names.UniqueNames
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.engine.errors.ExtensionExceptionExtractor
import io.kotest.engine.interceptors.EngineContext
import io.kotest.engine.listener.AbstractTestEngineListener
import io.kotest.engine.test.names.DefaultDisplayNameFormatter
import io.kotest.engine.test.names.getDisplayNameFormatter
import io.kotest.mpp.Logger
import io.kotest.mpp.bestName
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
) : AbstractTestEngineListener() {

   private val logger = Logger(JUnitTestEngineListener::class)

   private var formatter: DisplayNameFormatter = DefaultDisplayNameFormatter(ProjectConfiguration())

   // contains a mapping of junit TestDescriptor's, so we can find previously registered tests
   private val descriptors = mutableMapOf<Descriptor, TestDescriptor>()

   private var started = false

   private val startedTests = mutableSetOf<Descriptor.TestDescriptor>()

   // the root tests are our entry point when outputting results
   private val rootTests = mutableListOf<TestCase>()

   private var failOnIgnoredTests = false

   private val children = mutableMapOf<Descriptor, MutableList<TestCase>>()

   private val results = mutableMapOf<Descriptor, TestResult>()

   private val dummies = hashSetOf<String>()

   override suspend fun engineStarted() {
      logger.log { Pair(null, "Engine started") }
      listener.executionStarted(root)
   }

   override suspend fun engineInitialized(context: EngineContext) {
      failOnIgnoredTests = context.configuration.failOnIgnoredTests
      formatter = getDisplayNameFormatter(context.configuration.registry, context.configuration)
   }

   override suspend fun engineFinished(t: List<Throwable>) {
      logger.log { Pair(null, "Engine finished; throwables=[${t}]") }

      registerExceptionPlaceholders(t)

      val result = if (failOnIgnoredTests && results.values.any { it.isIgnored }) {
         TestExecutionResult.failed(RuntimeException("Build contained ignored test"))
      } else {
         TestExecutionResult.successful()
      }

      logger.log { Pair(null, "Notifying junit that engine completed $root") }
      listener.executionFinished(root, result)
   }

   private fun registerExceptionPlaceholders(ts: List<Throwable>) {
      ts.forEach {
         val (name, cause) = ExtensionExceptionExtractor.resolve(it)
         val container = createAndRegisterDummySpec(name)
         listener.executionStarted(container)
         listener.executionFinished(container, TestExecutionResult.failed(cause))
      }
   }

   override suspend fun specStarted(kclass: KClass<*>) {
      markSpecStarted(kclass)
   }

   private fun markSpecStarted(kclass: KClass<*>): TestDescriptor {
      return try {

         val descriptor = getOrCreateSpecDescriptor(kclass)

         logger.log { Pair(kclass.bestName(), "Registering dynamic spec $descriptor") }
         listener.dynamicTestRegistered(descriptor)

         logger.log { Pair(kclass.bestName(), "Spec executionStarted $descriptor") }
         listener.executionStarted(descriptor)

         started = true
         descriptor

      } catch (t: Throwable) {
         logger.log { Pair(kclass.bestName(), "Error in JUnit Platform listener $t") }
         throw t
      }
   }

   private fun getOrCreateSpecDescriptor(kclass: KClass<*>): TestDescriptor {

      val existing = descriptors[kclass.toDescriptor()]
      if (existing != null) return existing

      val descriptor = createDescriptorForSpec(kclass.toDescriptor(), formatter.format(kclass), root)
      logger.log { Pair(kclass.bestName(), "Spec will use source: ${descriptor.source}")}
      descriptors[kclass.toDescriptor()] = descriptor
      return descriptor
   }

   override suspend fun specIgnored(kclass: KClass<*>, reason: String?) {
      logger.log { Pair(kclass.bestName(), "Spec is being flagged as ignored") }
      val descriptor: TestDescriptor =
         createDescriptorForSpec(kclass.toDescriptor(), formatter.format(kclass), root)
      listener.executionSkipped(descriptor, reason)
   }

   override suspend fun specFinished(kclass: KClass<*>, t: Throwable?) {
      when {
         // if we have a spec error before we even started the spec, we will start the spec, add a placeholder
         // to hold the error, mark that test as failed, and then fail the spec as well
         t != null && !started -> {
            val descriptor = markSpecStarted(kclass)
            addPlaceholderTest(descriptor, t, kclass)
            logger.log { Pair(kclass.bestName(), "execution failed: $descriptor $t") }
            listener.executionFinished(descriptor, TestExecutionResult.failed(t))
         }
         // if we had an error in the spec, and we had no tests, we'll add the dummy and return
         t != null && rootTests.isEmpty() -> {
            val descriptor = descriptors[kclass.toDescriptor()]!!
            addPlaceholderTest(descriptor, t, kclass)
            logger.log { Pair(kclass.bestName(), "execution failed: $descriptor $t") }
            listener.executionFinished(descriptor, TestExecutionResult.failed(t))
         }
         else -> {

            val descriptor = descriptors[kclass.toDescriptor()]

            if (descriptor == null) {
               logger.log { Pair(kclass.bestName(), "Error retrieving descriptor") }
               throw RuntimeException("Error retrieving description for spec ${kclass.qualifiedName}")
            }

            val result = when (t) {
               null -> TestExecutionResult.successful()
               else -> {
                  addPlaceholderTest(descriptor, t, kclass)
                  TestExecutionResult.successful()
               }
            }

            logger.log { Pair(kclass.bestName(), "executionFinished: $descriptor") }
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
      descriptors.clear()
      startedTests.clear()
   }

   private fun addPlaceholderTest(parent: TestDescriptor, t: Throwable, kclass: KClass<*>) {
      val (name, cause) = ExtensionExceptionExtractor.resolve(t)
      val descriptor = createTestDescriptor(
         parent.uniqueId.append(Segment.Test.value, name),
         name,
         TestDescriptor.Type.TEST,
         ClassSource.from(kclass.java),
         false
      )
      parent.addChild(descriptor)
      listener.dynamicTestRegistered(descriptor)
      listener.executionStarted(descriptor)
      listener.executionFinished(descriptor, TestResult.Error(Duration.ZERO, cause).testExecutionResult())
   }

   // we don't inform junit of a started test just yet, as we want to wait and see if it has nested tests
   // this is so we can dynamically set junit's container/test type depending on the child count
   override suspend fun testStarted(testCase: TestCase) {
      logger.log { Pair(testCase.name.testName, "test started") }
      if (testCase.parent != null) rootTests.add(testCase)
      addChild(testCase)
   }

   // this test can be output now it has completed as we have all we need to know to complete it
   override suspend fun testFinished(testCase: TestCase, result: TestResult) {
      logger.log { Pair(testCase.name.testName, "test finished $result") }
      results[testCase.descriptor] = result

      val descriptor = getOrCreateTestDescriptor(testCase)

      // we need to ensure all parents have been started first
      startParents(testCase)
      startTestIfNotStarted(testCase)

      logger.log { Pair(testCase.name.testName, "executionFinished: $descriptor") }
      listener.executionFinished(descriptor, result.testExecutionResult())
   }

   override suspend fun testIgnored(testCase: TestCase, reason: String?) {
      logger.log { Pair(testCase.name.testName, "test ignored $reason") }
      if (testCase.parent == null) rootTests.add(testCase)
      addChild(testCase)
      results[testCase.descriptor] = TestResult.Ignored(reason)

      // we need to ensure all parents have been started first
      startParents(testCase)

      val descriptor = getOrCreateTestDescriptor(testCase)

      logger.log { Pair(testCase.name.testName, "Registering dynamic test: $descriptor") }
      listener.dynamicTestRegistered(descriptor)

      logger.log { Pair(testCase.name.testName, "executionSkipped: $descriptor") }
      listener.executionSkipped(descriptor, reason)
   }

   private fun addChild(testCase: TestCase) {
      children.getOrPut(testCase.descriptor.parent) { mutableListOf() }.add(testCase)
   }

   private fun startParents(testCase: TestCase) {
      val parent = testCase.parent
      if (parent != null) {
         startParents(parent)
         startTestIfNotStarted(parent)
      }
   }

   private fun startTestIfNotStarted(testCase: TestCase) {
      if (!startedTests.contains(testCase.descriptor)) {

         val descriptor = getOrCreateTestDescriptor(testCase)

         logger.log { Pair(testCase.name.testName, "Registering dynamic test: $descriptor") }
         listener.dynamicTestRegistered(descriptor)

         logger.log { Pair(testCase.name.testName, "executionStarted: $descriptor") }
         listener.executionStarted(descriptor)

         startedTests.add(testCase.descriptor)
      }
   }

   private fun getOrCreateTestDescriptor(testCase: TestCase): TestDescriptor {

      val existing = descriptors[testCase.descriptor]
      if (existing != null) return existing

      val parent = when (val p = testCase.parent) {
         null -> getOrCreateSpecDescriptor(testCase.spec::class)
         else -> getOrCreateTestDescriptor(p)
      }

      val id = parent.uniqueId.append(testCase.descriptor)

      // we dynamically work out the type by looking to see if this test had any children
      val c = children[testCase.descriptor]
      val type = when {
         c == null || c.isEmpty() -> TestDescriptor.Type.TEST
         else -> TestDescriptor.Type.CONTAINER
      }

      return createTestDescriptor(
         id,
         formatter.format(testCase),
         type,
         ClassSource.from(testCase.spec::class.java, null), // junit hides tests if we don't send this
         type == TestDescriptor.Type.CONTAINER
      ).apply {
         parent.addChild(this)
         descriptors[testCase.descriptor] = this
      }
   }

   private fun createAndRegisterDummySpec(name: String): TestDescriptor {
      val unique = UniqueNames.unique(name, dummies) { s, k -> "${s}_$k" } ?: name
      dummies.add(unique)
      val descriptor =
         createDescriptorForSpec(Descriptor.SpecDescriptor(DescriptorId(unique), this::class), unique, root)
      listener.dynamicTestRegistered(descriptor)
      return descriptor
   }
}
