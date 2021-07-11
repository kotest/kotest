package io.kotest.framework.gradle

import org.gradle.api.provider.Provider
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilation
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilerPluginSupportPlugin
import org.jetbrains.kotlin.gradle.plugin.SubpluginArtifact
import org.jetbrains.kotlin.gradle.plugin.SubpluginOption

class KotestMultiplatformCompilerPlugin : KotlinCompilerPluginSupportPlugin {

   override fun getCompilerPluginId() = "io.kotest.multiplatform"

   override fun getPluginArtifact() = SubpluginArtifact(
      "io.kotest",
      "kotest-framework-compiler-plugin-js-jvm",
      "5.0.0-LOCAL",
   )

   override fun getPluginArtifactForNative(): SubpluginArtifact = SubpluginArtifact(
      "io.kotest",
      "kotest-framework-compiler-plugin-native-jvm",
      "5.0.0-LOCAL",
   )

   override fun isApplicable(kotlinCompilation: KotlinCompilation<*>): Boolean {
      return kotlinCompilation.target.project.plugins.hasPlugin(KotestMultiplatformCompilerPlugin::class.java)
   }

   override fun applyToCompilation(kotlinCompilation: KotlinCompilation<*>): Provider<List<SubpluginOption>> {
      return kotlinCompilation.target.project.provider { emptyList() }
   }
}
