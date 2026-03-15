package io.kotest.runner.junit4

import io.kotest.core.extensions.TestCaseExtension
import io.kotest.core.test.TestCase
import io.kotest.engine.test.TestResult
import kotlinx.coroutines.runBlocking
import org.junit.Rule
import org.junit.rules.MethodRule
import org.junit.rules.TestRule
import org.junit.runners.model.FrameworkMethod
import org.junit.runners.model.Statement
import kotlin.time.Duration

/**
 * A [TestCaseExtension] that discovers and applies JUnit4 [org.junit.Rule]-annotated
 * [TestRule] and [MethodRule] fields and getter methods on the spec instance,
 * mirroring the behaviour of [org.junit.runners.BlockJUnit4ClassRunner].
 *
 * Rules that implement both [TestRule] and [MethodRule] are applied once as a
 * [TestRule], matching the deduplication logic in [org.junit.runners.BlockJUnit4ClassRunner].
 *
 * This extension is automatically installed by [KotestTestRunner].
 */
internal object JUnit4RuleExtension : TestCaseExtension {

   override suspend fun intercept(
      testCase: TestCase,
      execute: suspend (TestCase) -> TestResult
   ): TestResult {
      val spec = testCase.spec
      val testRules = collectTestRules(spec)
      // Exclude rules already captured as TestRule to avoid double-application.
      // Identity comparison (===) is used because a rule implementing both interfaces
      // would be the same instance in both lists.
      val methodRules = collectMethodRules(spec).filter { rule -> testRules.none { it === rule } }

      if (testRules.isEmpty() && methodRules.isEmpty()) return execute(testCase)

      val description = Descriptions.createTestDescription(testCase)
      var result: TestResult? = null

      var statement: Statement = object : Statement() {
         override fun evaluate() {
            runBlocking { result = execute(testCase) }
         }
      }

      // Apply MethodRules first (innermost), then TestRules (outermost),
      // matching the order in BlockJUnit4ClassRunner.withRules via RuleContainer.
      for (rule in methodRules) {
         statement = rule.apply(statement, syntheticFrameworkMethod(spec), spec)
      }
      for (rule in testRules) {
         statement = rule.apply(statement, description)
      }

      return try {
         statement.evaluate()
         result ?: execute(testCase)
      } catch (e: Throwable) {
         TestResult.Error(Duration.ZERO, e)
      }
   }
}

/**
 * Traverses [target]'s entire class hierarchy collecting values from
 * [@Rule][org.junit.Rule]-annotated fields and zero-arg methods, filtered by [transform].
 *
 * Accepts [targetClass] as a parameter so callers in platform source sets supply the
 * [Class] (via `target.javaClass`) without this function needing `.java` or `.javaClass`.
 *
 * Both `@field:Rule` (backing field) and `@get:Rule` (getter method) Kotlin
 * annotation targeting are handled, as is plain Java `@Rule` on fields.
 */
internal fun <T> collectAnnotatedRules(target: Any, targetClass: Class<*>, transform: (Any?) -> T?): List<T> {
   val results = mutableListOf<T>()
   var klass: Class<*>? = targetClass
   while (klass != null) {
      for (field in klass.declaredFields) {
         if (field.annotations.any { it is Rule }) {
            field.isAccessible = true
            transform(field.get(target))?.let { results.add(it) }
         }
      }
      for (method in klass.declaredMethods) {
         if (method.annotations.any { it is Rule } && method.parameterCount == 0) {
            method.isAccessible = true
            transform(method.invoke(target))?.let { results.add(it) }
         }
      }
      klass = klass.superclass
   }
   return results
}

internal expect fun collectTestRules(target: Any): List<TestRule>
internal expect fun collectMethodRules(target: Any): List<MethodRule>
internal expect fun syntheticFrameworkMethod(target: Any): FrameworkMethod
