package io.kotest.runner.junit.platform

import io.kotest.core.descriptors.Descriptor
import io.kotest.core.descriptors.DescriptorId
import io.kotest.core.descriptors.toDescriptor
import io.kotest.core.names.UniqueNames
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.core.test.TestType
import io.kotest.engine.errors.ExtensionExceptionExtractor
import io.kotest.engine.interceptors.EngineContext
import io.kotest.engine.listener.AbstractTestEngineListener
import io.kotest.engine.test.names.FallbackDisplayNameFormatter
import io.kotest.mpp.Logger
import io.kotest.mpp.bestName
import io.kotest.mpp.log
import org.junit.platform.engine.EngineExecutionListener
import org.junit.platform.engine.TestDescriptor
import org.junit.platform.engine.TestExecutionResult
import org.junit.platform.engine.UniqueId
import org.junit.platform.engine.support.descriptor.ClassSource
import org.junit.platform.engine.support.descriptor.EngineDescriptor
import org.junit.platform.engine.support.descriptor.MethodSource
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
   private val formatter: FallbackDisplayNameFormatter,
) : AbstractTestEngineListener() {

   private val logger = Logger(JUnitTestEngineListener::class)

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
      logger.log { "Engine started" }
      listener.executionStarted(root)
   }

   override suspend fun engineInitialized(context: EngineContext) {
      failOnIgnoredTests = context.configuration.failOnIgnoredTests
   }

   override suspend fun engineFinished(t: List<Throwable>) {
      logger.log { "Engine finished; throwables=[${t}]" }

      registerExceptionPlaceholders(t)

      val result = if (failOnIgnoredTests && results.values.any { it.isIgnored }) {
         TestExecutionResult.failed(RuntimeException("Build contained ignored test"))
      } else {
         TestExecutionResult.successful()
      }

      logger.log { "Notifying junit that engine completed $root" }
      listener.executionFinished(root, result)
   }

   override suspend fun specStarted(kclass: KClass<*>) {
      markSpecStarted(kclass)
   }

   override suspend fun specFinished(kclass: KClass<*>, result: TestResult) {
      val t = result.errorOrNull
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
            val descriptor = getSpecDescriptor(kclass)
            addPlaceholderTest(descriptor, t, kclass)
            logger.log { Pair(kclass.bestName(), "execution failed: $descriptor $t") }
            listener.executionFinished(descriptor, TestExecutionResult.failed(t))
         }
         else -> {
            val descriptor = getSpecDescriptor(kclass)
            val r = when (t) {
               null -> TestExecutionResult.successful()
               else -> {
                  addPlaceholderTest(descriptor, t, kclass)
                  TestExecutionResult.successful()
               }
            }

            logger.log { Pair(kclass.bestName(), "executionFinished: $descriptor") }
            listener.executionFinished(descriptor, r)
         }
      }
      reset()
   }

   override suspend fun specIgnored(kclass: KClass<*>, reason: String?) {
      logger.log { Pair(kclass.bestName(), "Spec is being flagged as ignored") }
      listener.executionSkipped(getSpecDescriptor(kclass), reason)
   }

   private fun markSpecStarted(kclass: KClass<*>): TestDescriptor {
      return try {

         log { "Getting TestDescriptor for $kclass" }
         val descriptor = getSpecDescriptor(kclass)

         logger.log { Pair(kclass.bestName(), "Spec executionStarted $descriptor") }
         listener.executionStarted(descriptor)

         started = true
         descriptor

      } catch (t: Throwable) {
         logger.log { Pair(kclass.bestName(), "Error marking spec as started $t") }
         throw t
      }
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
      val id = parent.uniqueId.append(Segment.Test.value, name)
      val descriptor = createTestDescriptor(
         id,
         name,
         TestDescriptor.Type.TEST,
         getMethodSource(kclass, id),
         false
      )
      parent.addChild(descriptor)
      listener.dynamicTestRegistered(descriptor)
      listener.executionStarted(descriptor)
      listener.executionFinished(descriptor, TestResult.Error(Duration.ZERO, cause).toTestExecutionResult())
   }

   override suspend fun testStarted(testCase: TestCase) {

      // depending on the test type, we may want to wait to notify junit, this is because gradle doesn't work
      // properly with the junit test types. Ideally, we'd just set everything to CONTAINER_AND_TEST, which is
      // supposed to mean a test can contain other tests as well as being a test itself, which is exactly how
      // Kotest views tests, but unfortunately it doesn't work properly.
      //
      // Another approach is to wait until the spec finishes to see which tests contain children and which
      // don't and set the test type appropriately, but junit doesn't give us a way to specify test duration
      // (instead it just calculates it itself from the time between marking a test as started and marking
      // it as finished), so this approach works but ends up having all tests as 0ms
      //
      // So the approach we will take is use the TestType from the test definition, unless it is dynamic,
      // then for dynamic we will calculate it later, and accept the 0ms drawback

      logger.log { Pair(testCase.name.testName, "test started") }
      if (testCase.parent != null) rootTests.add(testCase)
      addChild(testCase)

      when (testCase.type) {
         TestType.Container -> startTestIfNotStarted(testCase, TestDescriptor.Type.CONTAINER)
         TestType.Test -> startTestIfNotStarted(testCase, TestDescriptor.Type.TEST)
         TestType.Dynamic -> Unit
      }
   }

   // this test can be output now it has completed as we have all we need to know to complete it
   override suspend fun testFinished(testCase: TestCase, result: TestResult) {
      logger.log { Pair(testCase.name.testName, "test finished $result") }
      results[testCase.descriptor] = result

      val descriptor = getOrCreateTestDescriptor(testCase, null)

      // we need to ensure all parents have been started first
      startParents(testCase)
      startTestIfNotStarted(testCase, null)

      logger.log { Pair(testCase.name.testName, "executionFinished: $descriptor") }
      listener.executionFinished(descriptor, result.toTestExecutionResult())
   }

   override suspend fun testIgnored(testCase: TestCase, reason: String?) {
      logger.log { Pair(testCase.name.testName, "test ignored $reason") }
      if (testCase.parent == null) rootTests.add(testCase)
      addChild(testCase)
      results[testCase.descriptor] = TestResult.Ignored(reason)

      // we need to ensure all parents have been started first
      startParents(testCase)

      val descriptor = getOrCreateTestDescriptor(testCase, TestDescriptor.Type.TEST)

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
         startTestIfNotStarted(parent, null)
      }
   }

   private fun startTestIfNotStarted(testCase: TestCase, type: TestDescriptor.Type?) {
      if (!startedTests.contains(testCase.descriptor)) {

         val descriptor = getOrCreateTestDescriptor(testCase, type)

         logger.log { Pair(testCase.name.testName, "Registering dynamic test: $descriptor") }
         listener.dynamicTestRegistered(descriptor)

         logger.log { Pair(testCase.name.testName, "executionStarted: $descriptor") }
         listener.executionStarted(descriptor)

         startedTests.add(testCase.descriptor)
      }
   }

   private fun getOrCreateTestDescriptor(testCase: TestCase, type: TestDescriptor.Type?): TestDescriptor {

      val existing = descriptors[testCase.descriptor]
      if (existing != null) return existing

      val parent = when (val p = testCase.parent) {
         null -> getSpecDescriptor(testCase.spec::class)
         else -> getOrCreateTestDescriptor(p, null)
      }

      val id = parent.uniqueId.append(testCase.descriptor)

      // we dynamically work out the type if null by looking to see if this test had any children
      val c = children[testCase.descriptor]
      val t = when {
         type != null -> type
         c.isNullOrEmpty() -> TestDescriptor.Type.TEST
         else -> TestDescriptor.Type.CONTAINER
      }

      return createTestDescriptor(
         id,
         formatter.format(testCase),
         t,
         // gradle-junit-platform hides tests if we don't send a source at all
         // surefire-junit-platform (maven) needs a MethodSource in order to separate test cases from each other
         //   and produce more correct XML report with test case name.
         getMethodSource(testCase.spec::class, id),
         type == TestDescriptor.Type.CONTAINER
      ).apply {
         parent.addChild(this)
         descriptors[testCase.descriptor] = this
      }
   }

   private fun getMethodSource(kclass: KClass<*>, id: UniqueId): MethodSource
      = MethodSource.from(kclass.qualifiedName, id.segments.filter { it.type == Segment.Test.value }.map { it.value }.joinToString("/"))

   private fun getSpecDescriptor(kclass: KClass<*>): TestDescriptor {
      return getSpecDescriptor(root, kclass.toDescriptor(), formatter.format(kclass))
   }

   private fun createAndRegisterDummySpec(name: String): TestDescriptor {

      val unique = UniqueNames.unique(name, dummies) { s, k -> "${s}_$k" } ?: name
      dummies.add(unique)

      val descriptor = getSpecDescriptor(root, Descriptor.SpecDescriptor(DescriptorId(unique), this::class), unique)
      listener.dynamicTestRegistered(descriptor)
      return descriptor
   }

   private fun registerExceptionPlaceholders(ts: List<Throwable>) {
      ts.forEach {
         val (name, cause) = ExtensionExceptionExtractor.resolve(it)
         val container = createAndRegisterDummySpec(name)
         listener.executionStarted(container)
         listener.executionFinished(container, TestExecutionResult.failed(cause))
      }
   }
}
