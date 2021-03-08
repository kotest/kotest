@file:Suppress("MemberVisibilityCanBePrivate")

package io.kotest.extensions.spring

import io.kotest.core.extensions.SpecExtension
import io.kotest.core.extensions.TestCaseExtension
import io.kotest.core.internal.KotestEngineProperties
import io.kotest.core.spec.Spec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.core.test.TestType
import io.kotest.mpp.sysprop
import io.kotest.spring.SpringTestLifecycleMode
import kotlinx.coroutines.withContext
import net.bytebuddy.ByteBuddy
import net.bytebuddy.description.modifier.Visibility
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy
import net.bytebuddy.implementation.FixedValue
import org.springframework.test.context.TestContextManager
import java.lang.reflect.Method
import java.lang.reflect.Modifier
import kotlin.coroutines.AbstractCoroutineContextElement
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.coroutineContext

class SpringTestContextCoroutineContextElement(val value: TestContextManager) : AbstractCoroutineContextElement(Key) {
   companion object Key : CoroutineContext.Key<SpringTestContextCoroutineContextElement>
}

/**
 * Returns the [TestContextManager] from a test or spec.
 */
suspend fun testContextManager(): TestContextManager =
   coroutineContext[SpringTestContextCoroutineContextElement]?.value
      ?: error("No TestContextManager defined in this coroutine context")

val SpringExtension = SpringTestExtension(SpringTestLifecycleMode.Test)

class SpringTestExtension(private val mode: SpringTestLifecycleMode) : TestCaseExtension, SpecExtension {

   var ignoreSpringListenerOnFinalClassWarning: Boolean = false

   override suspend fun intercept(spec: Spec, execute: suspend (Spec) -> Unit) {
      val context = TestContextManager(spec::class.java)
      withContext(SpringTestContextCoroutineContextElement(context)) {
         testContextManager().beforeTestClass()
         testContextManager().prepareTestInstance(spec)
         execute(spec)
         testContextManager().afterTestClass()
      }
   }

   override suspend fun intercept(testCase: TestCase, execute: suspend (TestCase) -> TestResult): TestResult {
      if (testCase.isApplicable()) {
         testContextManager().beforeTestMethod(testCase.spec, method(testCase))
         testContextManager().beforeTestExecution(testCase.spec, method(testCase))
      }
      val result = execute(testCase)
      if (testCase.isApplicable()) {
         testContextManager().afterTestMethod(testCase.spec, method(testCase), null as Throwable?)
         testContextManager().afterTestExecution(testCase.spec, method(testCase), null as Throwable?)
      }
      return result
   }

   /**
    * Returns true if this test case should have the spring lifecycle methods applied
    */
   private fun TestCase.isApplicable() = (mode == SpringTestLifecycleMode.Root && description.isRootTest()) ||
      (mode == SpringTestLifecycleMode.Test && type == TestType.Test)

   /**
    * Generates a fake [Method] for the given [TestCase].
    *
    * Check https://github.com/kotest/kotest/issues/950#issuecomment-524127221
    * for a in-depth explanation. Too much to write here
    */
   private fun method(testCase: TestCase): Method {
      val klass = testCase.spec::class.java

      return if (Modifier.isFinal(klass.modifiers)) {
         if (!ignoreFinalWarning) {
            println("Using SpringListener on a final class. If any Spring annotation fails to work, try making this class open.")
         }
         // the method here must exist since we can't add our own
         this@SpringTestExtension::class.java.methods.firstOrNull { it.name == "intercept" }
            ?: error("Could not find method 'intercept' to attach spring lifecycle methods to")
      } else {
         val methodName = methodName(testCase)
         val fakeSpec = ByteBuddy()
            .subclass(klass)
            .defineMethod(methodName, String::class.java, Visibility.PUBLIC)
            .intercept(FixedValue.value("Foo"))
            .make()
            .load(this::class.java.classLoader, ClassLoadingStrategy.Default.CHILD_FIRST)
            .loaded
         fakeSpec.getMethod(methodName)
      }
   }

   /**
    * Generates a fake method name for the given [TestCase].
    * The method name is taken from the test case path.
    */
   internal fun methodName(testCase: TestCase): String {
      return testCase.description.testPath().value.replace("[^a-zA-Z_0-9]".toRegex(), "_").let {
         if (it.first().isLetter()) it else "_$it"
      }
   }

   private val ignoreFinalWarning =
      ignoreSpringListenerOnFinalClassWarning ||
         !sysprop(KotestEngineProperties.springIgnoreWarning, "false").toBoolean()
}
