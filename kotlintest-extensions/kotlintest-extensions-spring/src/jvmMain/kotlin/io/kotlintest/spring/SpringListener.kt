package io.kotlintest.spring

import io.kotlintest.Spec
import io.kotlintest.listener.TestListener
import io.kotlintest.extensions.ConstructorExtension
import org.springframework.beans.factory.config.AutowireCapableBeanFactory.AUTOWIRE_CONSTRUCTOR
import org.springframework.test.context.TestContextManager
import kotlin.reflect.KClass
import kotlin.reflect.full.primaryConstructor

object SpringListener : TestListener {

  override fun beforeSpec(spec: Spec) {
    try {
      TestContextManager(spec.javaClass).prepareTestInstance(spec)
    } catch (t: Throwable) {
      t.printStackTrace()
    }
  }
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
