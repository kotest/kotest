package io.kotest.runner.junit5

import io.kotest.Project
import io.kotest.core.spec.SpecConfiguration
import io.kotest.core.spec.description
import io.kotest.core.test.*
import io.kotest.fp.Try
import io.kotest.runner.jvm.TestEngineListener
import org.junit.platform.engine.*
import org.junit.platform.engine.support.descriptor.*
import org.slf4j.LoggerFactory
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
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
class JUnitTestRunnerListener(
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
   private val failedSpecs = mutableSetOf<KClass<out SpecConfiguration>>()

   private var specException: Throwable? = null

   override fun engineStarted(classes: List<KClass<out SpecConfiguration>>) {
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
         writeSpecFailures(failedSpecs)

      val result = when {
         t != null -> TestExecutionResult.failed(t)
         Project.failOnIgnoredTests && hasIgnored() -> TestExecutionResult.failed(RuntimeException("Build contained ignored test"))
         else -> TestExecutionResult.successful()
      }

      logger.trace("Notifying junit that root descriptor completed $root")
      listener.executionFinished(root, result)
   }

   private fun writeSpecFailures(failures: Set<KClass<out SpecConfiguration>>): Try<Any> = Try {
      val dir = Paths.get(".kotest")
      dir.toFile().mkdirs()
      val path = dir.resolve("spec_failures").toAbsolutePath()
      logger.trace("Writing report to $path")
      val content = failures.distinct().joinToString("\n") { it.java.canonicalName }
      Files.write(path, content.toByteArray())
   }

   override fun specStarted(kclass: KClass<out SpecConfiguration>) {
      logger.trace("beforeSpecClass [${kclass.qualifiedName}]")
      try {
         val descriptor = createSpecDescriptor(kclass)
         logger.trace("Registering junit dynamic test: $descriptor")
         listener.dynamicTestRegistered(descriptor)
         logger.trace("Notifying junit that execution has started: $descriptor")
         listener.executionStarted(descriptor)
      } catch (t: Throwable) {
         logger.error("Error in JUnit Platform listener", t)
         specException = t
      }
   }

   override fun specFailed(klass: KClass<out SpecConfiguration>, t: Throwable) {
      logger.trace("beforeSpecClass [${klass.qualifiedName}]")
      try {
         val descriptor = createSpecDescriptor(klass)
         logger.trace("Registering junit dynamic test: $descriptor")
         listener.dynamicTestRegistered(descriptor)
         logger.trace("Notifying junit that execution has started: $descriptor")
         listener.executionStarted(descriptor)
         listener.executionFinished(descriptor, TestExecutionResult.aborted(t))
      } catch (t: Throwable) {
         logger.error("Error in JUnit Platform listener", t)
         specException = t
      }
   }

   override fun specFinished(
      klass: KClass<out SpecConfiguration>,
      t: Throwable?,
      results: Map<TestCase, TestResult>
   ) {
      logger.trace("specFinished [$klass]")

      val descriptor = descriptors[klass.description()]
         ?: throw RuntimeException("Error retrieving description for spec: ${klass.qualifiedName}")

      val nestedFailure = findChildFailure(klass.description())

      val result = when {
         specException != null -> TestExecutionResult.failed(specException)
         nestedFailure != null -> nestedFailure.testExecutionResult()
         else -> TestExecutionResult.successful()
      }

      logger.trace("Notifying junit that execution has finished: $descriptor, $result")
      // we are ignoring junit guidelines here and failing the spec if any of it's tests failed
      // this is because in gradle and intellij nested errors are not very obvious
      listener.executionFinished(descriptor, result)
   }

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

   /**
    * Creates a new [TestDescriptor] appended to the receiver, adds it as a child of the receiver,
    * and registers it with the descriptors set.
    */
   private fun TestDescriptor.append(
      description: Description,
      type: TestDescriptor.Type,
      source: TestSource?
   ): TestDescriptor {
      val segment = if (description.isSpec()) "spec" else "test"
      val descriptor =
         object : AbstractTestDescriptor(this.uniqueId.append(segment, description.name), description.name, source) {
            override fun getType(): TestDescriptor.Type = type
            override fun mayRegisterTests(): Boolean = TestDescriptor.Type.CONTAINER_AND_TEST == type
         }
      this.addChild(descriptor)
      descriptors[description] = descriptor
      return descriptor
   }

   private fun createSpecDescriptor(klass: KClass<out SpecConfiguration>): TestDescriptor {
      val source = ClassSource.from(klass.java)
      return root.append(klass.description(), TestDescriptor.Type.CONTAINER_AND_TEST, source)
   }

   private fun createTestDescriptor(testCase: TestCase): TestDescriptor {
      val parent = descriptors[testCase.description.parent()]
      if (parent == null) {
         val msg = "Cannot find parent description for: ${testCase.description}"
         logger.error(msg)
         error(msg)
      }

      val source = FileSource.from(File(testCase.source.fileName), FilePosition.from(testCase.source.lineNumber))

      // there is a bug in gradle 4.7+ whereby CONTAINER_AND_TEST breaks test reporting, as it is not handled
      // see https://github.com/gradle/gradle/issues/4912
      // so we can't use CONTAINER_AND_TEST for our test scopes, but simply container
      val type = when (testCase.type) {
         TestType.Container -> TestDescriptor.Type.CONTAINER_AND_TEST
         TestType.Test -> TestDescriptor.Type.TEST
      }

      return parent.append(testCase.description, type, source)
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

fun UniqueId.appendSpec(description: Description) = this.append("spec", description.name)!!
