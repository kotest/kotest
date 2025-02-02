package io.kotest.framework.gradle

import io.kotest.framework.gradle.config.BaseKotestSpec
import io.kotest.framework.gradle.config.KotestSpecContainer
import io.kotest.framework.gradle.config.KotestSpecContainer.Companion.newKotestSpecContainer
import io.kotest.framework.gradle.internal.InternalKotestGradlePluginApi
import org.gradle.api.model.ObjectFactory
import org.gradle.api.plugins.ExtensionAware
import org.gradle.api.plugins.ExtensionContainer
import org.gradle.api.provider.Property
import org.gradle.api.tasks.TaskContainer
import org.gradle.jvm.toolchain.JavaLauncher
import javax.inject.Inject

abstract class KotestExtension @Inject internal constructor(
   objects: ObjectFactory,
   tasks: TaskContainer,
) {
   /**
    * Set a tag expression directly in the plugin configuration.
    * This can be useful if you want to run a subset of tests during development.
    */
   abstract val tagExpression: Property<String>

   /**
    * If true, then the build will fail if no spec classes are by the plugin.
    */
   abstract val failOnEmptySpecs: Property<Boolean>

   /**
    * The location of the compiled kotlin classes for Android builds.
    * By default, Android Studio sets this to be `build/tmp/kotlin-classes`.
    * If a relative path, then assumes inside build, otherwise if non-relative can be anywhere.
    */
   val androidTestSource: String = "tmp/kotlin-classes"

   /**
    * A container of [BaseKotestSpec]s.
    *
    * Each contains details of how Kotest should launch tests.
    *
    * The Kotest Gradle plugin will automatically discover and populate this container.
    * It has been left accessible (but protected with an opt-in flag) in case some
    * projects need to do some tinkering.
    */
   @InternalKotestGradlePluginApi
//   val testExecutions: TestExecutionsContainer =
//      objects.newTestExecutionsContainer().apply {
//         this@KotestExtension.extensions.add("testExecutions", this)
//      }
   val testExecutions: KotestSpecContainer =
      objects.newKotestSpecContainer(tasks).apply {
         this@KotestExtension.extensions.add("testExecutions", this)
      }

   /**
    * Java Launcher used to run JVM tests.
    */
   abstract val javaLauncher: Property<JavaLauncher>

   /**
    * Because [KotestExtension] is a Gradle 'managed type' it implicitly extends [ExtensionAware].
    *
    * To minimise the API surface we don't want to expose this to users
    * (it makes buildscripts more confusing, and auto-complete much more busy.)
    * Instead, we just internally cast.
    */
   private val extensions: ExtensionContainer
      get() = (this as ExtensionAware).extensions
}
