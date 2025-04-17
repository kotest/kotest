package io.kotest.extensions.spring

import io.kotest.core.extensions.ConstructorExtension
import io.kotest.core.spec.Spec
import org.springframework.beans.factory.config.AutowireCapableBeanFactory
import org.springframework.test.context.TestContextManager
import kotlin.reflect.KClass
import kotlin.reflect.full.primaryConstructor

/**
 * A [ConstructorExtension] which will attempt to instantiate test classes if they have a
 * non-zero arg constructor.
 *
 * The extension will delegate to spring's [TestContextManager] to autowire the constructors.
 */
@Deprecated("Use SpringExtension which combines this and SpringTestExtension. Deprecated since 6.0")
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
         context.autowireCapableBeanFactory.autowire(
            clazz.java,
            AutowireCapableBeanFactory.AUTOWIRE_CONSTRUCTOR, true
         ) as Spec
      }
   }
}
