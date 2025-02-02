package io.kotest.framework.gradle.config

import io.kotest.framework.gradle.KotestExtension
import org.gradle.api.Action
import org.gradle.api.DomainObjectCollection
import org.gradle.api.ExtensiblePolymorphicDomainObjectContainer
import org.gradle.api.NamedDomainObjectProvider
import org.gradle.api.model.ObjectFactory
import org.gradle.api.plugins.ExtensionAware
import org.gradle.api.plugins.ExtensionContainer
import org.gradle.api.specs.Spec
import org.gradle.api.tasks.TaskContainer
import org.gradle.kotlin.dsl.named
import org.gradle.kotlin.dsl.newInstance
import org.gradle.kotlin.dsl.polymorphicDomainObjectContainer
import org.gradle.kotlin.dsl.register
import org.gradle.kotlin.dsl.withType
import javax.inject.Inject

private typealias KotestExecutionContainer = ExtensiblePolymorphicDomainObjectContainer<BaseKotestSpec>


abstract class KotestSpecContainer @Inject internal constructor(
   private val objects: ObjectFactory,
   private val tasks: TaskContainer,
   kotestExtension: KotestExtension,
) {

   @PublishedApi
   internal val content: KotestExecutionContainer =
      objects.newKotestExecutionContainer(kotestExtension, tasks) { spec ->
         extensions.add(spec.name, spec)
      }

   /**
    * Register a new [BaseKotestSpec].
    */
   inline fun <reified T : BaseKotestSpec> register(
      name: String,
      configure: Action<in T>,
   ): NamedDomainObjectProvider<out T> =
      content.register<T>(name) { configure.execute(this) }

   /**
    * Configures an object by name and type, without triggering its creation or configuration,
    * failing if there is no such object.
    */
   inline fun <reified T : BaseKotestSpec> configure(
      name: String,
      configure: Action<in T>,
   ) {
      content.named<T>(name) { configure.execute(this) }
   }

   /**
    * Lazily configure all elements, including those added later.
    */
   inline fun <reified T : BaseKotestSpec> configureEach(configure: Action<in T>) {
      content.withType<T>().configureEach(configure)
   }

   /**
    * Return a new [DomainObjectCollection] containing only elements of type [T].
    */
   inline fun <reified T: BaseKotestSpec> withType(): DomainObjectCollection<T> {
      return content.withType<T>()
   }

   /**
    * Return a new [KotestSpecContainer] containing only elements that match [spec].
    */
   fun matching(spec: Spec<in BaseKotestSpec>): KotestSpecContainer {
      val matches = content.matching(spec)
      return objects.newKotestSpecContainer(tasks).apply {
         this.content.addAll(matches)
      }
   }

   private val extensions: ExtensionContainer
      get() = (this as ExtensionAware).extensions

   companion object {
      internal fun ObjectFactory.newKotestSpecContainer(tasks: TaskContainer): KotestSpecContainer {
         return newInstance<KotestSpecContainer>(tasks)
      }
   }
}


/**
 * Create and configure a new [ addExtension(name: String, spec: BaseKotestSpec)].
 */
private fun ObjectFactory.newKotestExecutionContainer(
   kotestExtension: KotestExtension,
   tasks: TaskContainer,
   onNewElement: (BaseKotestSpec) -> Unit,
): KotestExecutionContainer {
   val container = polymorphicDomainObjectContainer(BaseKotestSpec::class)

   container.registerFactory(KotestAndroidJvmSpec::class.java) { name ->
      val spec = newAndroidJvmExecSpec(name, tasks)
      onNewElement(spec)
      spec.javaLauncher.convention(kotestExtension.javaLauncher)
      spec
   }

   container.registerFactory(KotestJvmSpec::class.java) { name ->
      val spec = newJvmExecSpec(name, tasks)
      onNewElement(spec)
      spec.javaLauncher.convention(kotestExtension.javaLauncher)
      spec
   }

   container.registerFactory(KotestJsSpec::class.java) { name ->
      val spec = newJsExecSpec(name, tasks)
      onNewElement(spec)
      spec
   }

   container.registerFactory(KotestWasmSpec::class.java) { name ->
      val spec = newWasmExecSpec(name, tasks)
      onNewElement(spec)
      spec
   }

   container.registerFactory(KotestNativeSpec::class.java) { name ->
      val spec = newNativeExecSpec(name, tasks)
      onNewElement(spec)
      spec
   }

   return container
}

private fun BaseKotestSpec.applyBaseConventions() {
   enabled.convention(true)
}

/**
 * Create and configure a new [KotestAndroidJvmSpec].
 */
private fun ObjectFactory.newAndroidJvmExecSpec(
   name: String,
   tasks: TaskContainer,
): KotestAndroidJvmSpec =
   newInstance<KotestAndroidJvmSpec>(name, tasks).apply {
      applyBaseConventions()
   }

/**
 * Create and configure a new [KotestJvmSpec].
 */
private fun ObjectFactory.newJvmExecSpec(
   name: String,
   tasks: TaskContainer,
): KotestJvmSpec =
   newInstance<KotestJvmSpec>(name, tasks).apply {
      applyBaseConventions()
   }

/**
 * Create and configure a new [KotestJsSpec].
 */
private fun ObjectFactory.newJsExecSpec(
   name: String,
   tasks: TaskContainer,
): KotestJsSpec =
   newInstance<KotestJsSpec>(name, tasks).apply {
      applyBaseConventions()
   }

/**
 * Create and configure a new [KotestWasmSpec].
 */
private fun ObjectFactory.newWasmExecSpec(
   name: String,
   tasks: TaskContainer,
): KotestWasmSpec =
   newInstance<KotestWasmSpec>(name, tasks).apply {
      applyBaseConventions()
   }

/**
 * Create and configure a new [KotestNativeSpec].
 */
private fun ObjectFactory.newNativeExecSpec(
   name: String,
   tasks: TaskContainer,
): KotestNativeSpec =
   newInstance<KotestNativeSpec>(name, tasks).apply {
      applyBaseConventions()
   }
