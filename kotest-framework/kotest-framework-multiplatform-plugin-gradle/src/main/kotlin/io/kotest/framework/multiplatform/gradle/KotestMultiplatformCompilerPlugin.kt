package io.kotest.framework.multiplatform.gradle

import org.gradle.api.provider.Provider
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilation
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilerPluginSupportPlugin
import org.jetbrains.kotlin.gradle.plugin.SubpluginArtifact
import org.jetbrains.kotlin.gradle.plugin.SubpluginOption

class KotestMultiplatformCompilerPlugin : KotlinCompilerPluginSupportPlugin {

   companion object {
      const val compilerPluginId = "io.kotest.multiplatform"
      const val groupId = "io.kotest"
      const val artifactId = "kotest-framework-multiplatform-plugin-js-jvm"
      const val nativeArtifactId = "kotest-framework-multiplatform-plugin-native-jvm"
      const val version = "5.0.0.328-SNAPSHOT"
   }

   override fun getCompilerPluginId() = compilerPluginId

   override fun getPluginArtifact() = SubpluginArtifact(groupId, artifactId, version)

   override fun getPluginArtifactForNative(): SubpluginArtifact = SubpluginArtifact(groupId, nativeArtifactId, version)

   override fun isApplicable(kotlinCompilation: KotlinCompilation<*>): Boolean {
      return kotlinCompilation.target.project.plugins.hasPlugin(KotestMultiplatformCompilerPlugin::class.java)
   }

   override fun applyToCompilation(kotlinCompilation: KotlinCompilation<*>): Provider<List<SubpluginOption>> {
      return kotlinCompilation.target.project.provider { emptyList() }
   }
}
