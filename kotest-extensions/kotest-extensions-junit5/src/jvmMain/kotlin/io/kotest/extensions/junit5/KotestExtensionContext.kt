package io.kotest.extensions.junit5

import io.kotest.core.config.ProjectConfiguration
import io.kotest.core.descriptors.toDescriptor
import io.kotest.core.spec.Spec
import io.kotest.core.test.TestCase
import io.kotest.engine.test.names.getFallbackDisplayNameFormatter
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.api.extension.TestInstances
import org.junit.jupiter.api.parallel.ExecutionMode
import java.lang.reflect.AnnotatedElement
import java.lang.reflect.Method
import java.util.Optional
import java.util.function.Function

class KotestExtensionContext(
   private val spec: Spec,
   private val testCase: TestCase?
) : ExtensionContext {

   private val formatter = getFallbackDisplayNameFormatter(ProjectConfiguration().registry, ProjectConfiguration())

   override fun getExecutionMode(): ExecutionMode = ExecutionMode.CONCURRENT

   override fun getParent(): Optional<ExtensionContext> = Optional.empty()
   override fun getRoot(): ExtensionContext = this

   override fun getUniqueId(): String = spec::class.toDescriptor().id.value

   override fun <T> getConfigurationParameter(key: String?, transformer: Function<String, T & Any>): Optional<T> {
      return Optional.empty<T & Any>()
   }

   override fun getDisplayName(): String = when (testCase) {
      null -> formatter.format(spec::class)
      else -> formatter.format(testCase)
   }

   override fun getTags(): MutableSet<String> = spec.tags().map { it.name }.toMutableSet()

   override fun getElement(): Optional<AnnotatedElement> = Optional.empty()

   override fun getTestClass(): Optional<Class<*>> = Optional.of(spec::class.java)
   override fun getTestMethod(): Optional<Method> = Optional.empty()

   override fun getTestInstanceLifecycle(): Optional<TestInstance.Lifecycle> =
      Optional.of(TestInstance.Lifecycle.PER_CLASS)

   override fun getTestInstance(): Optional<Any> = when (testCase) {
      null -> Optional.of(spec)
      else -> Optional.of(testCase)
   }

   override fun getTestInstances(): Optional<TestInstances> = Optional.of(KotestTestInstances(spec))

   override fun getExecutionException(): Optional<Throwable> = Optional.empty()
   override fun getConfigurationParameter(key: String?): Optional<String> = Optional.empty()

   override fun publishReportEntry(map: MutableMap<String, String>?) {}

   override fun getStore(namespace: ExtensionContext.Namespace): ExtensionContext.Store {
      return ExtensionStore(namespace)
   }
}

@Suppress("UNCHECKED_CAST")
class KotestTestInstances(private val instance: Spec) : TestInstances {
   override fun getInnermostInstance(): Any = instance
   override fun getEnclosingInstances(): MutableList<Any> = mutableListOf(instance)
   override fun getAllInstances(): MutableList<Any> = mutableListOf(instance)
   override fun <T : Any> findInstance(requiredType: Class<T>): Optional<T> =
      when (requiredType.name) {
         instance::class.java.name -> Optional.of(instance as T)
         else -> Optional.empty()
      }
}

@Suppress("UNCHECKED_CAST")
class ExtensionStore(private val namespace: ExtensionContext.Namespace) : ExtensionContext.Store {

   private val map = mutableMapOf<Pair<ExtensionContext.Namespace, Any>, Any?>()

   override fun get(key: Any): Any? = map[key]

   override fun <V : Any> get(key: Any, requiredType: Class<V>): V? {
      val value = map[namespace to key]
      return when {
         value == null -> null
         value::class.java.name == requiredType.name -> value as V
         else -> error("Value is not of required type $requiredType")
      }
   }

   override fun <K : Any, V : Any> getOrComputeIfAbsent(key: K, defaultCreator: Function<K, V>): Any? {
      return when (val value = map[namespace to key]) {
         null -> defaultCreator.apply(key)
         else -> value
      }
   }

   override fun <K : Any, V : Any> getOrComputeIfAbsent(
      key: K,
      defaultCreator: Function<K, V>,
      requiredType: Class<V>
   ): V {
      val value = map[namespace to key]
      return when {
         value == null -> defaultCreator.apply(key)
         value::class.java.name == requiredType.name -> value as V
         else -> error("Value is not of required type $requiredType")
      }
   }

   override fun put(key: Any, value: Any?) {
      map[namespace to key] = value
   }

   override fun remove(key: Any): Any? {
      return map.remove(key)
   }

   override fun <V : Any> remove(key: Any, requiredType: Class<V>): V? {
      val value = map[namespace to key]
      return when {
         value == null -> null
         value::class.java.name == requiredType.name -> map.remove(namespace to key) as V
         else -> error("Value is not of required type $requiredType")
      }
   }
}
