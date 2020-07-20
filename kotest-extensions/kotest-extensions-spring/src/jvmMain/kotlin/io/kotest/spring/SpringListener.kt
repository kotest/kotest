package io.kotest.spring

import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.core.spec.Spec
import io.kotest.core.extensions.ConstructorExtension
import io.kotest.core.listeners.TestListener
import io.kotest.core.spec.AutoScan
import net.bytebuddy.ByteBuddy
import net.bytebuddy.description.modifier.Visibility
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy
import net.bytebuddy.implementation.FixedValue
import org.springframework.beans.factory.config.AutowireCapableBeanFactory.AUTOWIRE_CONSTRUCTOR
import org.springframework.test.context.TestContextManager
import java.lang.reflect.Method
import java.lang.reflect.Modifier
import kotlin.coroutines.Continuation
import kotlin.reflect.KClass
import kotlin.reflect.full.primaryConstructor

object SpringListener : TestListener {

   // Each Spec needs its own context. However, this listener is a singleton, so we need
   // to keep this map to separate those contexts instead of making this class non-singleton, thus
   // breaking client code
   private val testContexts = mutableMapOf<Spec, TestContextManager>()

   override suspend fun beforeSpec(spec: Spec) {
      testContexts[spec] = TestContextManager(spec.javaClass)
      spec.testContext.beforeTestClass()
      spec.testContext.prepareTestInstance(spec)
   }

   override suspend fun beforeTest(testCase: TestCase) {
      testCase.spec.testContext.beforeTestMethod(testCase.spec, testCase.spec.method)
      testCase.spec.testContext.beforeTestExecution(testCase.spec, testCase.spec.method)
   }

   override suspend fun afterTest(testCase: TestCase, result: TestResult) {
      testCase.spec.testContext.afterTestMethod(testCase.spec, testCase.spec.method, null as Throwable?)
      testCase.spec.testContext.afterTestExecution(testCase.spec, testCase.spec.method, null as Throwable?)
   }

   override suspend fun afterSpec(spec: Spec) {
      spec.testContext.afterTestClass()
      testContexts.remove(spec)
   }

   private val Spec.testContext: TestContextManager
      get() = testContexts.getValue(this)

   // Check https://github.com/kotest/kotest/issues/950#issuecomment-524127221
   // for a in-depth explanation. Too much to write here
   private val Spec.method: Method
      get() {
         val klass = this::class.java

         return if (Modifier.isFinal(klass.modifiers)) {
            println("Using SpringListener on a final class. If any Spring annotation fails to work, try making this class open.")
            this@SpringListener::class.java.getMethod("afterSpec", Spec::class.java, Continuation::class.java)
         } else {
            val fakeSpec = ByteBuddy()
               .subclass(klass)
               .defineMethod("kotestDummyMethod", String::class.java, Visibility.PUBLIC)
               .intercept(FixedValue.value("Foo"))
               .make()
               .load(this::class.java.classLoader, ClassLoadingStrategy.Default.CHILD_FIRST)
               .loaded

            fakeSpec.getMethod("kotestDummyMethod")
         }
      }
}

/**
 * A Kotest [ConstructorExtension] which will attempt to instantiate test classes if they have a
 * non-zero arg constructor.
 *
 * The extension wilil delegate to spring's [TestContextManager] to autowire the constructors.
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
         val ac = manager.testContext.applicationContext
         ac.autowireCapableBeanFactory.autowire(clazz.java, AUTOWIRE_CONSTRUCTOR, true) as Spec
      }
   }
}
