package io.kotest.framework.gradle

import io.kotest.framework.gradle.config.TestCandidates
import io.kotest.framework.gradle.config.TestCandidates.Companion.newTestCandidates
import io.kotest.framework.gradle.internal.InternalKotestGradlePluginApi
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.model.ObjectFactory
import org.gradle.api.plugins.ExtensionAware
import org.gradle.api.plugins.ExtensionContainer
import org.gradle.api.provider.Property
import javax.inject.Inject

abstract class KotestExtension @Inject internal constructor(
   objects: ObjectFactory
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

   val testCandidates: TestCandidates =
      objects.newTestCandidates().apply {
         this@KotestExtension.extensions.add("testCandidates", this)
      }

   /**
    * The Konan home directory, which contains libraries for Kotlin/Native development.
    *
    * This is only required as a workaround to fetch the compile-time dependencies in Kotlin/Native
    * projects with a version below 2.0.
    */
   // This property should be removed when Kotest only supports KGP 2 or higher.
   @InternalKotestGradlePluginApi
   abstract val konanHome: RegularFileProperty

   private val extensions: ExtensionContainer
      get() = (this as ExtensionAware).extensions
}
