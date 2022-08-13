package io.kotest.framework.multiplatform.gradle

import javax.inject.Inject
import org.gradle.api.Project
import org.gradle.api.artifacts.Dependency
import org.gradle.api.logging.Logger
import org.gradle.api.logging.Logging
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.provider.ProviderFactory
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.domainObjectSet
import org.gradle.kotlin.dsl.findByType
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilation
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilerPluginSupportPlugin
import org.jetbrains.kotlin.gradle.plugin.SubpluginArtifact
import org.jetbrains.kotlin.gradle.plugin.SubpluginOption
import org.jetbrains.kotlin.gradle.plugin.mpp.AbstractKotlinNativeCompilation
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinJsCompilation


@Suppress("unused")
abstract class KotestMultiplatformCompilerGradlePlugin @Inject constructor(
   private val objects: ObjectFactory,
   private val providers: ProviderFactory,
) : KotlinCompilerPluginSupportPlugin {

   private val logger: Logger = Logging.getLogger(this::class.java)

   companion object {
      const val kotestPluginExtensionName = "kotest"
      const val compilerPluginId = "io.kotest.multiplatform"
      const val KotestGroupId = "io.kotest"
      const val KotestEmbeddableCompilerArtifactId = "kotest-framework-multiplatform-plugin-embeddable-compiler"
      const val KotestNativeArtifactId = "kotest-framework-multiplatform-plugin-legacy-native"
      const val engineDepPrefix = "kotest-framework-engine"
   }

   /**
    * For use in [getPluginArtifact] and [getPluginArtifactForNative], as an instance of the targeted Gradle [Project]
    * is not available.
    *
    * In [isApplicable] a [Project] instance is available, so fetch the extension 'normally'. This helps ensure the
    * Gradle API is used correctly.
    */
   private var kotestExtension: KotestPluginExtension? = null

   override fun apply(target: Project) {
      kotestExtension = target.createKotestExtension()
   }

   private fun Project.createKotestExtension(): KotestPluginExtension =
      extensions.create<KotestPluginExtension>(kotestPluginExtensionName).apply {
         kotestCompilerVersion.convention(engineVersionConvention())
      }

   private fun Project.engineVersionConvention(): Provider<String> {
      // collect dependencies from configurations into a DomainObjectSet, then fetch the first match.

      val kotestDeps = objects.domainObjectSet(Dependency::class)

      configurations.configureEach {
         kotestDeps.addAllLater(
            providers.provider {
               incoming.dependencies.matching { dep ->
                  dep.group == KotestGroupId && dep.name.startsWith(engineDepPrefix)
               }
            }
         )
      }

      return providers.provider {
         kotestDeps.firstOrNull { it.version != null }?.version
      }
   }

   override fun getCompilerPluginId() = compilerPluginId

   override fun getPluginArtifact(): SubpluginArtifact =
      SubpluginArtifact(
         KotestGroupId,
         KotestEmbeddableCompilerArtifactId,
         kotestExtension?.kotestCompilerVersion?.orNull
      )

   // This will soon be deprecated and removed, see https://youtrack.jetbrains.com/issue/KT-51301.
   override fun getPluginArtifactForNative(): SubpluginArtifact =
      SubpluginArtifact(KotestGroupId, KotestNativeArtifactId, kotestExtension?.kotestCompilerVersion?.orNull)

   override fun isApplicable(kotlinCompilation: KotlinCompilation<*>): Boolean {
      val project = kotlinCompilation.target.project
      val kotestExtension = project.extensions.findByType<KotestPluginExtension>()

      return when {
         kotestExtension == null                              -> {
            // this shouldn't happen
            logger.warn("Warning: could not find Kotest extension in $project. Kotest will not be enabled.")
            false
         }

         !kotestExtension.kotestCompilerVersion.isPresent     -> {
            logger.warn("Warning: Kotest plugin has been added to $project, but could not determine Kotest engine version. Kotest will not be enabled.")
            false
         }

         kotlinCompilation is KotlinJsCompilation             -> true
         kotlinCompilation is AbstractKotlinNativeCompilation -> true
         else                                                 -> false
      }
   }

   override fun applyToCompilation(kotlinCompilation: KotlinCompilation<*>): Provider<List<SubpluginOption>> {
      return providers.provider { emptyList() }
   }

   /** workaround https://github.com/gradle/gradle/issues/12388 */
   private fun <T, R> Provider<T>.mapNotNull(transform: (T) -> R?): Provider<R> =
      flatMap { providers.provider { transform(it) } }
}

abstract class KotestPluginExtension {
   /**
    * The version to use for the Kotest compiler plugins.
    *
    * Defaults to using the same version as the Kotest engine dependency that is defined in `commonTest`.
    */
   abstract val kotestCompilerVersion: Property<String>
}
