package io.kotest.assertions.plugin.gradle

import org.gradle.api.Project
import org.gradle.api.logging.Logger
import org.gradle.api.logging.Logging
import org.gradle.api.provider.Provider
import org.gradle.api.provider.ProviderFactory
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilation
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilerPluginSupportPlugin
import org.jetbrains.kotlin.gradle.plugin.SubpluginArtifact
import org.jetbrains.kotlin.gradle.plugin.SubpluginOption
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinJsCompilation
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinJvmCompilation
import javax.inject.Inject

abstract class KotestAssertionsCompilerGradlePlugin @Inject constructor(
   private val providers: ProviderFactory,
) : KotlinCompilerPluginSupportPlugin {

   private val logger: Logger = Logging.getLogger(this::class.java)

   companion object {
      const val compilerPluginId = "io.kotest.assertions"
      const val KotestGroupId = "io.kotest"
      const val KotestCompilerArtifactId = "kotest-assertions-plugin-compiler"
   }

   override fun apply(target: Project) {
   }

   override fun getCompilerPluginId() = compilerPluginId

   override fun getPluginArtifact(): SubpluginArtifact = SubpluginArtifact(
      KotestGroupId,
      KotestCompilerArtifactId,
      "5.7.0-LOCAL",
   )

   override fun isApplicable(kotlinCompilation: KotlinCompilation<*>): Boolean {
      return when (kotlinCompilation) {
         is KotlinJvmCompilation -> true
         is KotlinJsCompilation -> true
         else -> false
      }
   }

   override fun applyToCompilation(kotlinCompilation: KotlinCompilation<*>): Provider<List<SubpluginOption>> =
      providers.provider { emptyList() }
}
