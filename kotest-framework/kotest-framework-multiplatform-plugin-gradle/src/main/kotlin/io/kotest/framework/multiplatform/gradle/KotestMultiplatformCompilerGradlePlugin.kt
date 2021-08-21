@file:Suppress("unused")

package io.kotest.framework.multiplatform.gradle

import org.gradle.api.Project
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
      const val groupId = "io.kotest"
      const val artifactId = "kotest-framework-multiplatform-plugin-js-jvm"
      const val nativeArtifactId = "kotest-framework-multiplatform-plugin-native-jvm"
      const val missingProjectValError = "Project is not initialized"
      const val engineDepPrefix = "kotest-framework-engine"
   }

   private var target: Project? = null

   override fun apply(target: Project) {
      super.apply(target)
      this.target = target
   }

   private fun version(): String? {
      val project = target ?: error(missingProjectValError)
      val version = engineDeps(project).firstOrNull()?.version ?: return null
      if (version.contains("LOCAL"))
         println("Using DEV version for compiler plugin: $version")
      return version
   }

   private fun engineDeps(project: Project) = project.configurations.flatMap { it.all }.flatMap { it.dependencies }
      .filter { it.group == groupId && it.name.startsWith(engineDepPrefix) }

   override fun getCompilerPluginId() = compilerPluginId

   override fun getPluginArtifact(): SubpluginArtifact =
      SubpluginArtifact(groupId, artifactId, version())

   override fun getPluginArtifactForNative(): SubpluginArtifact =
      SubpluginArtifact(groupId, nativeArtifactId, version())

   override fun isApplicable(kotlinCompilation: KotlinCompilation<*>): Boolean {
      return when {
         // if we can't find the engine dep then we won't apply the plugin to this module
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
