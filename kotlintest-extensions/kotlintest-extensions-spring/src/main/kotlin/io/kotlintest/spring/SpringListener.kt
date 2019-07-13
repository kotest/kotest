package io.kotlintest.spring

import io.kotlintest.Spec
import io.kotlintest.TestCase
import io.kotlintest.TestResult
import io.kotlintest.extensions.ConstructorExtension
import io.kotlintest.extensions.TestListener
import io.kotlintest.extensions.TopLevelTest
import org.springframework.beans.factory.config.AutowireCapableBeanFactory.AUTOWIRE_CONSTRUCTOR
import org.springframework.test.context.TestContextManager
import kotlin.reflect.KClass
import kotlin.reflect.full.primaryConstructor

object SpringListener : TestListener {

  // Each Spec needs its own context. However, this listener is a singleton, so we need
  // to keep this map to separate those contexts instead of making this class non-singleton, thus
  // breaking client code
  private val testContexts = mutableMapOf<Spec, TestContextManager>()

  override fun beforeSpecClass(spec: Spec, tests: List<TopLevelTest>) {
    testContexts[spec] = TestContextManager(spec.javaClass)
  }

  override fun beforeSpec(spec: Spec) {
    spec.testContext.beforeTestClass()
  }

  override fun beforeTest(testCase: TestCase) {
    testCase.spec.testContext.beforeTestMethod(testCase.spec, method)
    testCase.spec.testContext.prepareTestInstance(testCase.spec)
    testCase.spec.testContext.beforeTestExecution(testCase.spec, method)

  }

  override fun afterTest(testCase: TestCase, result: TestResult) {
    testCase.spec.testContext.afterTestMethod(testCase.spec, method, null as Throwable?)
    testCase.spec.testContext.afterTestExecution(testCase.spec, method, null as Throwable?)
  }

  override fun afterSpec(spec: Spec) {
    spec.testContext.afterTestClass()
  }

  private val Spec.testContext: TestContextManager
    get() = testContexts.getValue(this)

  // We don't run methods, but we need to pass one to TestContextManager, so we'll pass any.
  private val method = SpringListener::class.java.getMethod("hashCode")

}

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
