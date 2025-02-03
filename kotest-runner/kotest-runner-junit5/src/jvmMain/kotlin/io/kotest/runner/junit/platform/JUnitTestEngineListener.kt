package io.kotest.runner.junit.platform

import io.kotest.core.Logger
import io.kotest.core.descriptors.Descriptor
import io.kotest.core.descriptors.DescriptorId
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.engine.descriptors.toDescriptor
import io.kotest.core.test.TestType
import io.kotest.engine.errors.ExtensionExceptionExtractor
import io.kotest.engine.interceptors.EngineContext
import io.kotest.engine.listener.AbstractTestEngineListener
import io.kotest.engine.listener.TestEngineListener
import io.kotest.engine.names.UniqueNames
import io.kotest.engine.test.names.FallbackDisplayNameFormatter
import io.kotest.mpp.bestName
import org.junit.platform.engine.EngineExecutionListener
import org.junit.platform.engine.TestDescriptor
import org.junit.platform.engine.TestExecutionResult
import org.junit.platform.engine.UniqueId
import org.junit.platform.engine.support.descriptor.EngineDescriptor
import org.junit.platform.engine.support.descriptor.MethodSource
import kotlin.reflect.KClass
import kotlin.time.Duration

/**
 * A Kotest [TestEngineListener] that forwards notifications to a JUnit Platform [EngineExecutionListener].
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

   private val startedTests = mutableSetOf<Descriptor.TestDescriptor>()

   private var failOnIgnoredTests = false

   private val results = mutableMapOf<Descriptor, TestResult>()

   private val dummies = hashSetOf<String>()

   override suspend fun engineStarted() {
      logger.log { "Engine started" }
      listener.executionStarted(root)
   }

   override suspend fun engineInitialized(context: EngineContext) {
      logger.log { "Engine initialized with context $context" }
      failOnIgnoredTests = context.projectConfigResolver.failOnIgnoredTests()
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
      logger.log { "specStarted $kclass" }
      try {

         val descriptor = root.getSpecTestDescriptor(kclass.toDescriptor())
         descriptors[kclass.toDescriptor()] = descriptor

         logger.log { Pair(kclass.bestName(), "executionStarted $descriptor") }
         listener.executionStarted(descriptor)

      } catch (t: Throwable) {
         logger.log { Pair(kclass.bestName(), "Error marking spec as started $t") }
         throw t
      }
   }

   override suspend fun specFinished(kclass: KClass<*>, result: TestResult) {
      logger.log { "specFinished $kclass $result" }

      val t = result.errorOrNull
      when {

         // if we had an error in the spec, we will attach a placeholder error-test to the spec
         // and mark that as failed
         t != null -> {
            val descriptor = root.getSpecTestDescriptor(kclass.toDescriptor())
            addPlaceholderTest(descriptor, t, kclass)
            logger.log { Pair(kclass.bestName(), "executionFinished: $descriptor $t") }
            listener.executionFinished(descriptor, TestExecutionResult.failed(t))
         }

         else -> {
            val descriptor = root.getSpecTestDescriptor(kclass.toDescriptor())
            logger.log { Pair(kclass.bestName(), "executionFinished: $descriptor") }
            listener.executionFinished(descriptor, TestExecutionResult.successful())
         }
      }
      reset()
   }

   override suspend fun specIgnored(kclass: KClass<*>, reason: String?) {

      // an ignored spec will not have been started
      // it will however have been registered when the test suite was created,
      // so we know the test descriptor should exist

      logger.log { Pair(kclass.bestName(), "Spec is being flagged as ignored") }
      listener.executionSkipped(root.getSpecTestDescriptor(kclass.toDescriptor()), reason)
   }

//   private fun markSpecStarted(kclass: KClass<*>): TestDescriptor {
//      return try {
//
//         log { "Getting TestDescriptor for $kclass" }
//         val descriptor = getSpecDescriptor(kclass)
//
//         logger.log { Pair(kclass.bestName(), "Spec executionStarted $descriptor") }
//         listener.executionStarted(descriptor)
//
//         started = true
//         descriptor
//
//      } catch (t: Throwable) {
//         logger.log { Pair(kclass.bestName(), "Error marking spec as started $t") }
//         throw t
//      }
//   }

   private fun reset() {
      results.clear()
      descriptors.clear()
      startedTests.clear()
   }

   /**
    * Dynamically registers a placeholder test with the name derived from the throwable type.
    * We do this so we can add a child test with the name of the callback that failed for extra clarity.
    * This allows multiple callback failures to be stacked.
    */
   private fun addPlaceholderTest(parent: TestDescriptor, t: Throwable, kclass: KClass<*>) {
      val (name, cause) = ExtensionExceptionExtractor.resolve(t)
      val id = parent.uniqueId.append(Segment.Test.value, name)
      val descriptor = createTestTestDescriptor(
         id = id,
         displayName = name,
         type = TestDescriptor.Type.TEST,
         source = getMethodSource(kclass, id),
      )
      parent.addChild(descriptor)
      listener.dynamicTestRegistered(descriptor)
      listener.executionStarted(descriptor)
      listener.executionFinished(descriptor, TestResult.Error(Duration.ZERO, cause).toTestExecutionResult())
   }

   override suspend fun testStarted(testCase: TestCase) {

      // We want to wait to notify junit, this is because gradle doesn't work properly with the junit test types.
      // Ideally, we'd just set everything to CONTAINER_AND_TEST, which is supposed to mean a test can contain
      // other tests as well as being a test itself, which is exactly how Kotest views tests, but unfortunately
      // it's not supported by gradle :(
      //
      // So we need to not start tests until we can determine if they are a TEST or a CONTAINER.
      // Once a nested test starts, we can start the parent, since we know the parent is definitely a CONTAINER
      // at that point.
      //
      // Leaf tests must wait until they complete so we know there were no child tests.
      //
      // Further annoyance is that junit doesn't give us a way to specify test duration
      // (instead it just calculates it itself from the time between marking a test as started and marking
      // it as finished), so we end up having all leaf tests as 0ms
      //
      // Therefore, our workaround is to just add the execution time into the test name.

      logger.log { Pair(testCase.name.name, "test started") }


      // if this test has a parent, we can mark that parent as started, because it's definitely not a leaf
      if (testCase.parent != null)
         startParents(testCase)

      // if the test is a Kotest TEST type then its definitely a leaf test, so we can start it immediately
      if (testCase.type == TestType.Test) {
         startTestIfNotStarted(testCase, TestDescriptor.Type.TEST)
      }
   }

   override suspend fun testFinished(testCase: TestCase, result: TestResult) {

      // this test can be output now as we have all we need to know to complete it
      // we know at this point that any nested tests have also completed
      //
      // we don't need to start parents, because they would have been started by the coresponding
      // call to testStarted
      //
      // because of the gradle bugs described elsewhere, if this was a leaf test, we would not yet have
      // started it, so we need to start it if not.

      logger.log { Pair(testCase.name.name, "test finished $result") }
      results[testCase.descriptor] = result

      // if this test was started, we don't need to register it again
      // if it was not started, then it must be a Type.TEST (otherwise its children would have started it)
      startTestIfNotStarted(testCase, TestDescriptor.Type.TEST)

      val descriptor = createTestDescriptorWithMethodSource(testCase, TestDescriptor.Type.TEST)

      logger.log { Pair(testCase.name.name, "executionFinished: $descriptor") }
      listener.executionFinished(descriptor, result.toTestExecutionResult())
   }

   override suspend fun testIgnored(testCase: TestCase, reason: String?) {

      // an ignored test should never be started or finished
      // however an ingored test may be inside a test that wasn't yet marked as started
      // so we must ensure we start any parents
      startParents(testCase)

      // like all tests, an ignored test should be registered first
      // ignored test should be a TEST type, because an ignored test will never have child tests.
      val testDescriptor = createTestDescriptorWithMethodSource(testCase, TestDescriptor.Type.TEST)
      attachToParent(testCase, testDescriptor)

      logger.log { Pair(testCase.name.name, "Registering dynamic test: $testDescriptor") }
      listener.dynamicTestRegistered(testDescriptor)

      logger.log { Pair(testCase.name.name, "test ignored $reason") }
      results[testCase.descriptor] = TestResult.Ignored(reason)

      logger.log { Pair(testCase.name.name, "executionSkipped: $testDescriptor") }
      listener.executionSkipped(testDescriptor, reason)
   }

   private fun startParents(testCase: TestCase) {
      val parent = testCase.parent
      if (parent != null) {
         startParents(parent)
         // when starting parents, type must be CONTAINER
         startTestIfNotStarted(parent, TestDescriptor.Type.CONTAINER)
      }
   }

   /**
    * If the given testCase has not yet been marked as tested, then now we register it and start it.
    * This method is only invoked for parents, so we know the type is always CONTAINER.
    */
   private fun startTestIfNotStarted(testCase: TestCase, type: TestDescriptor.Type) {
      if (!startedTests.contains(testCase.descriptor)) {

         val testDescriptor = createTestDescriptorWithMethodSource(testCase, type)
         attachToParent(testCase, testDescriptor)
         descriptors[testCase.descriptor] = testDescriptor

         logger.log { Pair(testCase.name.name, "Registering dynamic container test: $testDescriptor") }
         listener.dynamicTestRegistered(testDescriptor)

         logger.log { Pair(testCase.name.name, "executionStarted: $testDescriptor") }
         listener.executionStarted(testDescriptor)

         // now mark it as started so we can safely call this method again
         startedTests.add(testCase.descriptor)
      }
   }

   private fun attachToParent(testCase: TestCase, testDescriptor: TestDescriptor) {
      // must attach to the parent, which we know will have been created prior, either spec or parent test
      val parent = testCase.descriptor.parent
      val p = descriptors[parent] ?: error("No parent found: ${parent.id.value}")
      p.addChild(testDescriptor)
   }

   private fun createTestDescriptorWithMethodSource(
      testCase: TestCase,
      type: TestDescriptor.Type,
   ): TestDescriptor {
      val id = root.deriveTestUniqueId(testCase.descriptor)
      val testDescriptor = createTestTestDescriptor(
         id = id,
         displayName = formatter.format(testCase),
         type = type,
         // gradle-junit-platform hides tests if we don't send a source at all
         // surefire-junit-platform (maven) needs a MethodSource in order to separate test cases from each other
         // and produce more correct XML report with test case name.
         source = getMethodSource(testCase.spec::class, id),
      )
      return testDescriptor
   }

   private fun getMethodSource(kclass: KClass<*>, id: UniqueId): MethodSource = MethodSource.from(
      /* className = */ kclass.qualifiedName,
      /* methodName = */ id.segments.filter { it.type == Segment.Test.value }.joinToString("/") { it.value }
   )

   /**
    * Registers placeholder specs and marks them as failed for each throwable.
    * See [ExtensionExceptionExtractor].
    */
   private fun registerExceptionPlaceholders(ts: List<Throwable>) {
      ts.forEach {
         val (name, cause) = ExtensionExceptionExtractor.resolve(it)
         val container = createAndRegisterDummySpec(name)
         listener.executionStarted(container)
         listener.executionFinished(container, TestExecutionResult.failed(cause))
      }
   }

   /**
    * Registers a placeholder test which we can use to attach lifecycle errors.
    * The test is registered with the engine parent.
    * See [ExtensionExceptionExtractor].
    */
   private fun createAndRegisterDummySpec(name: String): TestDescriptor {

      val unique = UniqueNames.unique(name, dummies) { s, k -> "${s}_$k" } ?: name
      dummies.add(unique)

      val descriptor = createSpecTestDescriptor(root, Descriptor.SpecDescriptor(DescriptorId(unique)), unique)
      root.addChild(descriptor)
      listener.dynamicTestRegistered(descriptor)
      return descriptor
   }
}
