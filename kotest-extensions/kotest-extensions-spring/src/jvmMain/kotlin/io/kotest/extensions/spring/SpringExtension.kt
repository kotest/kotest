@file:Suppress("DuplicatedCode")

package io.kotest.extensions.spring

import io.kotest.core.extensions.ConstructorExtension
import io.kotest.core.extensions.Extension
import io.kotest.core.extensions.SpecExtension
import io.kotest.core.extensions.TestCaseExtension
import io.kotest.core.spec.Spec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.core.test.TestType
import io.kotest.core.test.isRootTest
import kotlinx.coroutines.withContext
import net.bytebuddy.ByteBuddy
import net.bytebuddy.description.modifier.Visibility
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy
import net.bytebuddy.implementation.FixedValue
import org.springframework.beans.factory.config.AutowireCapableBeanFactory
import org.springframework.test.context.TestContextManager
import java.lang.reflect.Method
import java.lang.reflect.Modifier
import java.util.UUID
import kotlin.reflect.KClass
import kotlin.reflect.full.primaryConstructor

/**
 * An [Extension] which adds support for testing spring components.
 *
 * This extension has two parts:
 *
 * 1. Supports creating non-zero arg test classes by delegating to spring's [TestContextManager]
 * to autowire the constructors
 *
 * 2. Adds support for spring lifecycle methods to be called before and after tests.
 */
class SpringExtension(
   private val mode: SpringTestLifecycleMode = SpringTestLifecycleMode.Test
) : ConstructorExtension, SpecExtension, TestCaseExtension {

   override fun <T : Spec> instantiate(clazz: KClass<T>): Spec? {
      // we only instantiate via spring if there's actually parameters in the constructor
      // otherwise there's nothing to inject there
      val constructor = clazz.primaryConstructor
      return if (constructor == null || constructor.parameters.isEmpty()) {
         null
      } else {
         val manager = TestContextManager(clazz.java)
         val context = manager.testContext.applicationContext
         context.autowireCapableBeanFactory.autowire(
            clazz.java,
            AutowireCapableBeanFactory.AUTOWIRE_CONSTRUCTOR, true
         ) as Spec
      }
   }

   var ignoreSpringListenerOnFinalClassWarning: Boolean = false

   override suspend fun intercept(spec: Spec, execute: suspend (Spec) -> Unit) {
      safeClassName(spec::class)

      val context = TestContextManager(spec::class.java)
      withContext(SpringTestContextCoroutineContextElement(context)) {
         testContextManager().beforeTestClass()
         testContextManager().prepareTestInstance(spec)
         execute(spec)
         testContextManager().afterTestClass()
      }
   }

   override suspend fun intercept(testCase: TestCase, execute: suspend (TestCase) -> TestResult): TestResult {
      val methodName = method(testCase)
      if (testCase.isApplicable()) {
         testContextManager().beforeTestMethod(testCase.spec, methodName)
         testContextManager().beforeTestExecution(testCase.spec, methodName)
      }
      val result = execute(testCase)
      if (testCase.isApplicable()) {
         testContextManager().afterTestMethod(testCase.spec, methodName, null as Throwable?)
         testContextManager().afterTestExecution(testCase.spec, methodName, null as Throwable?)
      }
      return result
   }

   /**
    * Returns true if this test case should have the spring lifecycle methods applied
    */
   private fun TestCase.isApplicable() = (mode == SpringTestLifecycleMode.Root && isRootTest()) ||
      (mode == SpringTestLifecycleMode.Test && type == TestType.Test)

   /**
    * Generates a fake [Method] for the given [TestCase].
    *
    * Check https://github.com/kotest/kotest/issues/950#issuecomment-524127221
    * for an in-depth explanation. Too much to write here
    */
   private fun method(testCase: TestCase): Method = if (Modifier.isFinal(testCase.spec::class.java.modifiers)) {
      if (!ignoreFinalWarning) {
         @Suppress("MaxLineLength")
         println("Using SpringListener on a final class. If any Spring annotation fails to work, try making this class open.")
      }
      // the method here must exist since we can't add our own
      this@SpringExtension::class.java.methods.firstOrNull { it.name == "intercept" }
         ?: error("Could not find method 'intercept' to attach spring lifecycle methods to")
   } else {
      val methodName = methodName(testCase)
      val fakeSpec = ByteBuddy()
         .subclass(testCase.spec::class.java)
         .defineMethod(methodName, String::class.java, Visibility.PUBLIC)
         .intercept(FixedValue.value("Foo"))
         .make()
         .load(this::class.java.classLoader, ClassLoadingStrategy.Default.CHILD_FIRST)
         .loaded
      fakeSpec.getMethod(methodName)
   }

   /**
    * Checks for a safe class name and throws if invalid
    * https://kotlinlang.org/docs/keyword-reference.html#soft-keywords
    */
   internal fun safeClassName(kclass: KClass<*>) {
      // these are names java won't let us use but are ok from kotlin
      if (kclass.java.name.split('.').any { illegals.contains(it) })
         error("Spec package name cannot contain a java keyword: ${illegals.joinToString(",")}")
   }

   /**
    * Generates a fake method name for the given [TestCase].
    * The method name is taken from the test case name with a random element.
    */
   internal fun methodName(testCase: TestCase): String = (testCase.name.name + "_" + UUID.randomUUID().toString())
      .replace(methodNameRegex, "_")
      .let {
         if (it.first().isLetter()) it else "_$it"
      }

   private val illegals =
      listOf("import", "finally", "catch", "const", "final", "inner", "protected", "private", "public")

   private val methodNameRegex = "[^a-zA-Z_0-9]".toRegex()

   private val ignoreFinalWarning =
      ignoreSpringListenerOnFinalClassWarning ||
         !System.getProperty(Properties.springIgnoreWarning, "false").toBoolean()
}
