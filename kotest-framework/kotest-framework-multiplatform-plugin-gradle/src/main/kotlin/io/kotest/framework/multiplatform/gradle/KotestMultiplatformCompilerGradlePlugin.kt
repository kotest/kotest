package io.kotest.framework.multiplatform.gradle

import org.gradle.api.Project
import org.gradle.api.logging.Logger
import org.gradle.api.logging.Logging
import org.gradle.api.provider.Provider
import org.gradle.api.provider.ProviderFactory
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.findByType
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilation
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilerPluginSupportPlugin
import org.jetbrains.kotlin.gradle.plugin.SubpluginArtifact
import org.jetbrains.kotlin.gradle.plugin.SubpluginOption
import org.jetbrains.kotlin.gradle.plugin.mpp.AbstractKotlinNativeCompilation
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinJsCompilation
import javax.inject.Inject

abstract class KotestMultiplatformCompilerGradlePlugin @Inject constructor(
   private val providers: ProviderFactory,
) : KotlinCompilerPluginSupportPlugin {

   private val logger: Logger = Logging.getLogger(this::class.java)

   companion object {
      const val kotestPluginExtensionName = "kotest"
      const val compilerPluginId = "io.kotest.multiplatform"
      const val KotestGroupId = "io.kotest"
      const val KotestEmbeddableCompilerArtifactId = "kotest-framework-multiplatform-plugin-embeddable-compiler"
      const val KotestNativeArtifactId = "kotest-framework-multiplatform-plugin-legacy-native"
   }

   /**
    * For use in [getPluginArtifact] and [getPluginArtifactForNative], as an instance of the targeted Gradle [Project]
    * is not available there.
    *
    * In [isApplicable] a [Project] instance is available, so fetch the extension 'normally'. This helps ensure the
    * Gradle API is used correctly.
    */
   private var kotestExtension: KotestPluginExtension? = null

   override fun apply(target: Project) {
      kotestExtension = target.createKotestExtension()
   }

   private fun Project.createKotestExtension(): KotestPluginExtension {
      return extensions.create<KotestPluginExtension>(kotestPluginExtensionName).apply {
         kotestCompilerPluginVersion.convention(KOTEST_COMPILER_PLUGIN_VERSION)
      }
   }

   override fun getCompilerPluginId() = compilerPluginId

   override fun getPluginArtifact(): SubpluginArtifact =
      SubpluginArtifact(
         KotestGroupId,
         KotestEmbeddableCompilerArtifactId,
         kotestExtension?.kotestCompilerPluginVersion?.orNull,
      )

   // This will soon be deprecated and removed, see https://youtrack.jetbrains.com/issue/KT-51301.
   override fun getPluginArtifactForNative(): SubpluginArtifact =
      SubpluginArtifact(
         KotestGroupId,
         KotestNativeArtifactId,
         kotestExtension?.kotestCompilerPluginVersion?.orNull,
      )

   override fun isApplicable(kotlinCompilation: KotlinCompilation<*>): Boolean {
      val project = kotlinCompilation.target.project
      val kotestExtension = project.extensions.findByType<KotestPluginExtension>()
         ?: error("Could not find Kotest extension in $project")

      return when {
         !kotestExtension.kotestCompilerPluginVersion.isPresent -> {
            logger.warn("Warning: the Kotest plugin has been added to $project, but kotestCompilerPluginVersion has been set to null. Kotest will not be enabled.")
            false
         }

         kotlinCompilation is KotlinJsCompilation -> true
         kotlinCompilation is AbstractKotlinNativeCompilation -> true
         else -> false
      }
   }

   override fun applyToCompilation(kotlinCompilation: KotlinCompilation<*>): Provider<List<SubpluginOption>> =
      providers.provider { emptyList() }
}
