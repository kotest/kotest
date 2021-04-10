package io.kotest.spring

import io.kotest.core.extensions.ConstructorExtension
import io.kotest.core.internal.KotestEngineProperties
import io.kotest.core.listeners.TestListener
import io.kotest.core.spec.AutoScan
import io.kotest.core.spec.Spec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.core.test.TestType
import io.kotest.mpp.sysprop
import net.bytebuddy.ByteBuddy
import net.bytebuddy.description.modifier.Visibility
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy
import net.bytebuddy.implementation.FixedValue
import org.springframework.beans.factory.config.AutowireCapableBeanFactory.AUTOWIRE_CONSTRUCTOR
import org.springframework.test.context.TestContextManager
import java.lang.reflect.Method
import java.lang.reflect.Modifier
import java.util.Collections.synchronizedMap
import kotlin.coroutines.Continuation
import kotlin.reflect.KClass
import kotlin.reflect.full.primaryConstructor

val SpringListener = SpringTestListener(SpringTestLifecycleMode.Test)

/**
 * Determines how the spring test context lifecycle is mapped to test cases.
 *
 * [SpringTestLifecycleMode.Root] will setup and teardown the test context before and after root tests only.
 * [SpringTestLifecycleMode.Test] will setup and teardown the test context only at leaf tests.
 *
 */
enum class SpringTestLifecycleMode {
   Root, Test
}

class SpringTestListener(private val mode: SpringTestLifecycleMode) : TestListener {

   var ignoreSpringListenerOnFinalClassWarning: Boolean = false

   // Each Spec needs its own context. However, this listener is a singleton, so we need
   // to keep this map to separate those contexts instead of making this class non-singleton, thus
   // breaking client code
   private val testContexts = synchronizedMap(mutableMapOf<Spec, TestContextManager>())

   override suspend fun beforeSpec(spec: Spec) {
      testContexts[spec] = TestContextManager(spec.javaClass)
      spec.testContext.beforeTestClass()
      spec.testContext.prepareTestInstance(spec)
   }

   private fun TestCase.isApplicable() = (mode == SpringTestLifecycleMode.Root && description.isRootTest()) ||
      (mode == SpringTestLifecycleMode.Test && type == TestType.Test)

   override suspend fun beforeTest(testCase: TestCase) {
      if (testCase.isApplicable()) {
         testCase.spec.testContext.beforeTestMethod(testCase.spec, method(testCase))
         testCase.spec.testContext.beforeTestExecution(testCase.spec, method(testCase))
      }
   }

   override suspend fun afterTest(testCase: TestCase, result: TestResult) {
      if (testCase.isApplicable()) {
         testCase.spec.testContext.afterTestMethod(testCase.spec, method(testCase), null as Throwable?)
         testCase.spec.testContext.afterTestExecution(testCase.spec, method(testCase), null as Throwable?)
      }
   }

   override suspend fun afterSpec(spec: Spec) {
      spec.testContext.afterTestClass()
      testContexts.remove(spec)
   }

   private val Spec.testContext: TestContextManager
      get() = testContexts.getValue(this)

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
         this@SpringTestListener::class.java.getMethod("afterSpec", Spec::class.java, Continuation::class.java)
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
   fun methodName(testCase: TestCase): String {
      return testCase.description.testPath().value.replace("[^a-zA-Z_0-9]".toRegex(), "_").let {
         if (it.first().isLetter()) it else "_$it"
      }
   }

   private val ignoreFinalWarning =
      ignoreSpringListenerOnFinalClassWarning ||
         !sysprop(KotestEngineProperties.springIgnoreWarning, "false").toBoolean()
}

/**
 * A [ConstructorExtension] which will attempt to instantiate test classes if they have a
 * non-zero arg constructor.
 *
 * The extension will delegate to spring's [TestContextManager] to autowire the constructors.
 */
@AutoScan
object SpringAutowireConstructorExtension : ConstructorExtension {
   override fun <T : Spec> instantiate(clazz: KClass<T>): Spec? {
      // we only instantiate via spring if there's actually parameters in the constructor
      // otherwise there's nothing to inject there
      val constructor = clazz.primaryConstructor
      return if (constructor == null || constructor.parameters.isEmpty()) {
         null
      } else {
         val manager = TestContextManager(clazz.java)
         val context = manager.testContext.applicationContext
         context.autowireCapableBeanFactory.autowire(clazz.java, AUTOWIRE_CONSTRUCTOR, true) as Spec
      }
   }
}
