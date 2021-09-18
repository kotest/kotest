package io.kotest.assertions.plugin.gradle

import org.gradle.api.Project
import org.gradle.api.provider.Provider
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilation
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilerPluginSupportPlugin
import org.jetbrains.kotlin.gradle.plugin.SubpluginArtifact
import org.jetbrains.kotlin.gradle.plugin.SubpluginOption
import org.jetbrains.kotlin.gradle.plugin.mpp.AbstractKotlinNativeCompilation
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinJsCompilation
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinJvmCompilation

class KotestAssertionsPluginGradle : KotlinCompilerPluginSupportPlugin {

   companion object {
      const val compilerPluginId = "io.kotest.assertions"
      const val groupId = "io.kotest"
      const val artifactId = "kotest-assertions-plugin-jvm-jvm"
      const val nativeArtifactId = "kotest-assertions-plugin-native-jvm"
      const val missingProjectValError = "Project is not initialized"
      const val assertionsDepPrefix = "kotest-assertions-shared"
   }

   private var target: Project? = null

   override fun apply(target: Project) {
      super.apply(target)
      this.target = target
   }

   private fun version(): String? {
      val project = target ?: error(missingProjectValError)

//      val versionFromExtension = extension?.compilerPluginVersion?.orNull
//      if (versionFromExtension != null) {
//         println("Using compiler plugin version: $versionFromExtension")
//         return versionFromExtension
//      }

      val version = assertionsDep(project).firstOrNull()?.version ?: return null
      if (version.contains("LOCAL"))
         println("Using DEV version for assertions compiler plugin: $version")
      return version
   }

   private fun assertionsDep(project: Project) =
      project.configurations
         .flatMap { it.all }
         .flatMap { it.dependencies }
         .filter { it.group == groupId && it.name.startsWith(assertionsDepPrefix) }

   override fun getCompilerPluginId() = compilerPluginId

   override fun getPluginArtifact(): SubpluginArtifact =
      SubpluginArtifact(groupId, artifactId, version())

   override fun getPluginArtifactForNative(): SubpluginArtifact =
      SubpluginArtifact(groupId, nativeArtifactId, version())

   override fun isApplicable(kotlinCompilation: KotlinCompilation<*>): Boolean {
      return when {
         // if we can't find the assertions dep then we won't apply the plugin to this module
         version() == null -> false
         kotlinCompilation is KotlinJvmCompilation -> true
         kotlinCompilation is KotlinJsCompilation -> true
         kotlinCompilation is AbstractKotlinNativeCompilation -> true
         else -> false
      }
   }

   override fun applyToCompilation(kotlinCompilation: KotlinCompilation<*>): Provider<List<SubpluginOption>> {
      return kotlinCompilation.target.project.provider { emptyList() }
   }
}
