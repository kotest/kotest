package io.kotest.framework.gradle.internal.adapters

import com.android.build.gradle.AppExtension
import com.android.build.gradle.BaseExtension
import com.android.build.gradle.LibraryExtension
import com.android.build.gradle.TestExtension
import io.kotest.framework.gradle.KotestExtension
import io.kotest.framework.gradle.config.TestCandidate
import io.kotest.framework.gradle.utils.*
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.result.ResolvedArtifactResult
import org.gradle.api.file.FileCollection
import org.gradle.api.logging.Logging
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Provider
import org.gradle.api.provider.ProviderFactory
import org.gradle.kotlin.dsl.getByType
import java.io.File

/**
 * Discovers Android Gradle Plugin specific configuration and uses it to configure Kotest.
 */
internal fun androidAdapter(
   project: Project,
   kotestExtension: KotestExtension,
) {
   project.pluginManager.apply {
      withPlugin("com.android.base") { configure(project, kotestExtension) }
      withPlugin("com.android.application") { configure(project, kotestExtension) }
      withPlugin("com.android.library") { configure(project, kotestExtension) }
   }
}

private fun configure(
   project: Project,
   kotestExtension: KotestExtension,
) {
   val androidExt = AndroidExtensionWrapper(project) ?: return

   kotestExtension.testCandidates.configureEach {
      classpath.from(androidExt.bootClasspath())

      classpath.from(
         kotlinTarget.map { analysisPlatform ->
            when (analysisPlatform) {
               TestCandidate.KotlinTarget.AndroidJVM ->
                  androidClasspathCollector(
                     androidExt = androidExt,
                     objects = project.objects,
                  )

               else ->
                  project.objects.fileCollection()
            }
         }
      )
   }
}


private val logger = Logging.getLogger("Kotest AndroidAdapter")


/** Create a [AndroidExtensionWrapper] */
private fun AndroidExtensionWrapper(
   project: Project
): AndroidExtensionWrapper? {
   val androidExt: BaseExtension = try {
      project.extensions.getByType()
   } catch (ex: Exception) {
      logger.warn("${AndroidExtensionWrapper::class} could not get Android Extension for project ${project.path}")
      return null
   }
   return AndroidExtensionWrapper.forBaseExtension(
      androidExt = androidExt,
      providers = project.providers,
      objects = project.objects
   )
}


/**
 * Wrap the Android extension so that Dokka can still access the configuration names without
 * caring about the AGP version in use.
 */
private interface AndroidExtensionWrapper {

   fun variantsCompileClasspath(): FileCollection

   fun bootClasspath(): Provider<List<File>>

   companion object {

      fun forBaseExtension(
         androidExt: BaseExtension,
         providers: ProviderFactory,
         objects: ObjectFactory,
      ): AndroidExtensionWrapper {
         return object : AndroidExtensionWrapper {

            override fun variantsCompileClasspath(): FileCollection {
               val androidComponentsCompileClasspath = objects.fileCollection()

               val variants = when (androidExt) {
                  is LibraryExtension -> androidExt.libraryVariants
                  is AppExtension -> androidExt.applicationVariants
                  is TestExtension -> androidExt.applicationVariants
                  else -> {
                     logger.warn("${AndroidExtensionWrapper::class} found unknown Android Extension $androidExt")
                     return objects.fileCollection()
                  }
               }

               fun Configuration.collect(artifactType: String) {
                  val artifactTypeFiles = incoming
                     .artifactView {
                        attributes {
                           artifactType(artifactType)
                        }
                        lenient(true)
                     }
                     .artifacts
                     .resolvedArtifacts
                     .map { artifacts -> artifacts.map(ResolvedArtifactResult::getFile) }

                  androidComponentsCompileClasspath.from(artifactTypeFiles)
               }

               variants.all {
                  compileConfiguration.collect("jar")
                  //runtimeConfiguration.collect("jar")
               }

               return androidComponentsCompileClasspath
            }

            override fun bootClasspath(): Provider<List<File>> {
               return providers.provider { androidExt.bootClasspath }
            }
         }
      }
   }
}


/**
 * A utility for determining the classpath of an Android compilation.
 */
private fun androidClasspathCollector(
   androidExt: AndroidExtensionWrapper,
   objects: ObjectFactory,
): FileCollection {
   val compilationClasspath = objects.fileCollection()

   compilationClasspath.from({ androidExt.variantsCompileClasspath() })

   return compilationClasspath
}
