@file:Suppress("unused")

package io.kotest.framework.multiplatform.gradle

import org.gradle.api.Project
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
      const val KotestJsArtifactId = "kotest-framework-multiplatform-plugin-js-jvm"
      const val KotestNativeArtifactId = "kotest-framework-multiplatform-plugin-native-jvm"
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
   private fun version(): String? {
      val project = target ?: error(missingProjectValError)

      val versionFromExtension = extension?.compilerPluginVersion?.orNull
      if (versionFromExtension != null) {
         println("Kotest compiler plugin [$versionFromExtension]")
         return versionFromExtension
      }

      val version = engineDeps(project).firstOrNull()?.version ?: return null
      if (version.contains("LOCAL")) {
         println("Detected dev engine version [$version]")
      }
      return version
   }

   private fun engineDeps(project: Project) =
      project.configurations
         .flatMap { it.all }
         .flatMap { it.dependencies }
         .filter { it.group == KotestGroupId && it.name.startsWith(engineDepPrefix) }

   override fun getCompilerPluginId() = compilerPluginId

   override fun getPluginArtifact(): SubpluginArtifact =
      SubpluginArtifact(KotestGroupId, KotestJsArtifactId, version())

   override fun getPluginArtifactForNative(): SubpluginArtifact =
      SubpluginArtifact(KotestGroupId, KotestNativeArtifactId, version())

   override fun isApplicable(kotlinCompilation: KotlinCompilation<*>): Boolean {
      return when {
         // if we can't find a version to use then we won't apply to this module
         version() == null -> false
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
