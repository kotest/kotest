package io.kotest.runner.junit4

import io.kotest.core.extensions.TestCaseExtension
import io.kotest.core.test.TestCase
import io.kotest.engine.test.TestResult
import kotlinx.coroutines.runBlocking
import org.junit.rules.MethodRule
import org.junit.rules.TestRule
import org.junit.runners.model.FrameworkMethod
import org.junit.runners.model.Statement
import java.lang.reflect.Field
import java.lang.reflect.Method
import kotlin.reflect.KClass
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
      val testRules = testRules(classFor(spec::class), spec)
      // Exclude rules already captured as TestRule to avoid double-application.
      // Identity comparison (===) is used because a rule implementing both interfaces
      // would be the same instance in both lists.
      val methodRules = methodRules(classFor(spec::class), spec).filter { rule -> testRules.none { it === rule } }

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
         // If no rule actually invoked the inner statement (e.g. a ConditionalIgnoreRule that
         // chooses to skip), `result` stays null. Treat that as the rule's authoritative
         // decision to skip — running the test outside the rule chain would defeat its purpose.
         result ?: TestResult.Ignored("JUnit4 @Rule chose not to execute the test")
      } catch (e: Throwable) {
         TestResult.Error(Duration.ZERO, e)
      }
   }
}

/**
 * Traverses [target]'s entire class hierarchy collecting values from
 * [@Rule][org.junit.Rule]-annotated fields and zero-arg methods.
 *
 *  Both `@field:Rule` (backing field) and `@get:Rule` (getter method) Kotlin
 * annotation targeting are handled, as is plain Java `@Rule` on fields.
 */
internal fun testRules(target: Class<*>, instance: Any): List<TestRule> {
   val results = collectFieldRules(target, instance).mapNotNull { it as? TestRule } +
      collectMethodRules(target, instance).mapNotNull { it as? TestRule }
   val supe = target.superclass
   return if (supe == null) results else results + testRules(supe, instance)
}

/**
 * Traverses [target]'s entire class hierarchy collecting values from
 * [@Rule][org.junit.Rule]-annotated fields and zero-arg methods.
 *
 *  Both `@field:Rule` (backing field) and `@get:Rule` (getter method) Kotlin
 * annotation targeting are handled, as is plain Java `@Rule` on fields.
 */
internal fun methodRules(target: Class<*>, instance: Any): List<MethodRule> {
   val results = collectFieldRules(target, instance).mapNotNull { it as? MethodRule } +
      collectMethodRules(target, instance).mapNotNull { it as? MethodRule }
   val supe = target.superclass
   return if (supe == null) results else results + methodRules(supe, instance)
}

internal fun collectFieldRules(target: Class<*>, instance: Any): List<Any> {
   return fields(target).mapNotNull { field ->
      if (hasRule(field.annotations)) {
         field.isAccessible = true
         field.get(instance)
      } else null
   }
}

internal fun collectMethodRules(target: Class<*>, instance: Any): List<Any> {
   return methods(target).mapNotNull { method ->
      if (hasRule(method.annotations) && method.parameterCount == 0) {
         method.isAccessible = true
         method.invoke(instance)
      } else null
   }
}

internal expect fun hasRule(annotations: Array<Annotation>): Boolean
internal expect fun classFor(target: KClass<*>): Class<*>
internal expect fun fields(target: Class<*>): List<Field>
internal expect fun methods(target: Class<*>): List<Method>

internal expect fun syntheticFrameworkMethod(target: Any): FrameworkMethod
