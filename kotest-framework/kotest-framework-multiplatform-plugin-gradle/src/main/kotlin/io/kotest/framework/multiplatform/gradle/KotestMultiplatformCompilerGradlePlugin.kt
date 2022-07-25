@file:Suppress("unused")

package io.kotest.framework.multiplatform.gradle

import org.gradle.api.Project
import org.gradle.api.artifacts.Dependency
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilation
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilerPluginSupportPlugin
import org.jetbrains.kotlin.gradle.plugin.SubpluginArtifact
import org.jetbrains.kotlin.gradle.plugin.SubpluginOption
import org.jetbrains.kotlin.gradle.plugin.mpp.AbstractKotlinNativeCompilation
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinJsCompilation

class KotestMultiplatformCompilerGradlePlugin : KotlinCompilerPluginSupportPlugin {

   companion object {
      const val compilerPluginId = "io.kotest.multiplatform"
      const val KotestGroupId = "io.kotest"
      const val KotestEmbeddableCompilerArtifactId = "kotest-framework-multiplatform-plugin-embeddable-compiler"
      const val KotestNativeArtifactId = "kotest-framework-multiplatform-plugin-native"
      const val missingProjectValError = "Project is not initialized"
      const val engineDepPrefix = "kotest-framework-engine"
   }

   private var target: Project? = null
   private var extension: KotestPluginExtension? = null

   override fun apply(target: Project) {
      super.apply(target)
      this.target = target
      extension = target.extensions.create("kotest", KotestPluginExtension::class.java)
   }

   /**
    * Returns the version to use for the compiler plugins.
    *
    * Takes the version from the gradle extension configuration first, or if not
    * specified, then defaults to using the same version as the engine dependency.
    */
   private val version: String? by lazy {
      val versionFromExtension = extension?.compilerPluginVersion?.orNull
      if (versionFromExtension != null) {
         println("Kotest compiler plugin [$versionFromExtension]")
         return@lazy versionFromExtension
      }

      val engineDep = engineDeps().firstOrNull() ?: error("Cannot determine Kotest compiler plugin version if no Kotest engine dependencies are present")
      val version = engineDep.version ?: return@lazy null

      if (version.contains("LOCAL")) {
         println("Detected dev engine version [$version]")
      }

      return@lazy version
   }

   private fun engineDeps(): List<Dependency> {
      val project = target ?: error(missingProjectValError)

      return project.configurations
         .flatMap { it.all }
         .flatMap { it.dependencies }
         .filter { it.group == KotestGroupId && it.name.startsWith(engineDepPrefix) }
   }

   override fun getCompilerPluginId() = compilerPluginId

   override fun getPluginArtifact(): SubpluginArtifact =
      SubpluginArtifact(KotestGroupId, KotestEmbeddableCompilerArtifactId, version)

   override fun getPluginArtifactForNative(): SubpluginArtifact =
      SubpluginArtifact(KotestGroupId, KotestNativeArtifactId, version)

   override fun isApplicable(kotlinCompilation: KotlinCompilation<*>): Boolean {
      return when {
         // if we can't find a version to use then we won't apply to this module
         engineDeps().isEmpty() -> {
            println("Warning: Kotest plugin has been added to project $target, but the project does not contain a Kotest engine dependency. Kotest will not be enabled.")
            false
         }
         version == null -> {
            println("Warning: Kotest plugin has been added to project $target, and the project does contain a Kotest engine dependency, but no explicit dependency version has been provided. Kotest will not be enabled.")
            false
         }
         kotlinCompilation is KotlinJsCompilation -> true
         kotlinCompilation is AbstractKotlinNativeCompilation -> true
         else -> false
      }
   }

   override fun applyToCompilation(kotlinCompilation: KotlinCompilation<*>): Provider<List<SubpluginOption>> {
      return kotlinCompilation.target.project.provider { emptyList() }
   }
}

abstract class KotestPluginExtension {
   abstract val compilerPluginVersion: Property<String>

   init {
      compilerPluginVersion.convention(null as String?)
   }
}
