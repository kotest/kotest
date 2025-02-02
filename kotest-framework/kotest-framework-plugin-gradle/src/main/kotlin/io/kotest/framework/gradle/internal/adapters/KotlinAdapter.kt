package io.kotest.framework.gradle.internal.adapters

import io.kotest.framework.gradle.KotestExtension
import io.kotest.framework.gradle.config.TestCandidate
import io.kotest.framework.gradle.utils.artifactType
import io.kotest.framework.gradle.utils.domainObjectContainer
import io.kotest.framework.gradle.utils.letAll
import io.kotest.framework.gradle.utils.warn
import org.gradle.api.NamedDomainObjectSet
import org.gradle.api.Project
import org.gradle.api.artifacts.ConfigurationContainer
import org.gradle.api.file.FileCollection
import org.gradle.api.logging.Logging
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Provider
import org.gradle.kotlin.dsl.findByType
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinProjectExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinSingleTargetExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilation
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilation.Companion.MAIN_COMPILATION_NAME
import org.jetbrains.kotlin.gradle.plugin.KotlinPlatformType.androidJvm
import org.jetbrains.kotlin.gradle.plugin.KotlinPlatformType.common
import org.jetbrains.kotlin.gradle.plugin.KotlinPlatformType.js
import org.jetbrains.kotlin.gradle.plugin.KotlinPlatformType.jvm
import org.jetbrains.kotlin.gradle.plugin.KotlinPlatformType.native
import org.jetbrains.kotlin.gradle.plugin.KotlinPlatformType.wasm
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinJvmAndroidCompilation
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinMetadataCompilation
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinMetadataTarget
import kotlin.reflect.jvm.jvmName


internal fun kotlinAdapter(
   project: Project,
   kotestExtension: KotestExtension,
) {
   project.pluginManager.apply {
      withPlugin("org.jetbrains.kotlin.jvm") { registerTestCandidates(project, kotestExtension) }
      withPlugin("org.jetbrains.kotlin.multiplatform") { registerTestCandidates(project, kotestExtension) }
   }
}


private fun registerTestCandidates(
   project: Project,
   kotestExtension: KotestExtension,
) {
   val objects = project.objects
//   val providers = project.providers

   val kotlinExtension = findKotlinExtension(project)
   if (kotlinExtension == null) {
      logger.info("Skipping applying Kotest KotlinAdapter in ${project.path} - could not find KotlinProjectExtension")
      return
   }
   logger.info("Configuring Kotest KotlinAdapter in Gradle Kotlin Project ${project.path}")

   kotlinExtension.allKotlinCompilations().letAll { compilation: KotlinCompilation<*> ->

      val isMetadataCompilation = compilation is KotlinMetadataTarget
      val isTestCompilation = compilation.isTest()

      val compilationClasspath = collectKotlinCompilationClasspath(
         configurations = project.configurations,
         objects = objects,
         compilation = compilation,
      )

      kotestExtension.testCandidates.register(compilation.name) {
         this.kotlinTargetName.set(compilation.target.targetName)
         this.kotlinTarget.set(
            when (compilation.platformType) {
               common -> TestCandidate.KotlinTarget.Common
               jvm -> TestCandidate.KotlinTarget.JVM
               js -> TestCandidate.KotlinTarget.JS
               androidJvm -> TestCandidate.KotlinTarget.AndroidJVM
               native -> TestCandidate.KotlinTarget.Native
               wasm -> TestCandidate.KotlinTarget.Wasm
            }
         )
         this.enabled.set(!isMetadataCompilation && !isTestCompilation)
         this.classpath.from(compilationClasspath)
      }
   }
}


/**
 * Get the [Configuration][org.gradle.api.artifacts.Configuration] names of all configurations
 * used to build this [KotlinCompilation] and
 * [its source sets][KotlinCompilation.kotlinSourceSets].
 */
private fun collectKotlinCompilationClasspath(
   configurations: ConfigurationContainer,
   objects: ObjectFactory,
   compilation: KotlinCompilation<*>,
): FileCollection {
   val compilationClasspath = objects.fileCollection()

   if (compilation.target.platformType == androidJvm) {
      compilationClasspath.from(kotlinCompileDependencyFiles(configurations, compilation, "jar"))
      compilationClasspath.from(kotlinCompileDependencyFiles(configurations, compilation, "android-classes-jar"))
   } else {
      // using compileDependencyFiles breaks Android projects because AGP fills it with
      // files from so many Configurations it triggers Gradle variant resolution errors.
      compilationClasspath.from({ compilation.compileDependencyFiles })
   }

   return compilationClasspath
}


private fun kotlinCompileDependencyFiles(
   configurations: ConfigurationContainer,
   compilation: KotlinCompilation<*>,
   /** `android-classes-jar` or `jar` */
   artifactType: String,
): Provider<FileCollection> {
   return configurations
      .named(compilation.compileDependencyConfigurationName)
      .map {
         it.incoming
            .artifactView {
               // Android publishes many variants, which can cause Gradle to get confused,
               // so specify that we need a JAR and resolve leniently
               if (compilation.target.platformType == androidJvm) {
                  attributes { artifactType(artifactType) }

                  // Setting lenient=true is not ideal, because it might hide problems.
                  // Unfortunately, Gradle has no chill and dependency resolution errors
                  // will cause tasks to completely fail, even if the dependencies aren't necessary.
                  // (There's a chance that the dependencies aren't even used in the project!)
                  // So, resolve leniently to at least permit generating _something_,
                  // even if the generated output might be incomplete and missing some classes.
                  lenient(true)
               }
               // 'Regular' Kotlin compilations have non-JAR files (e.g. Kotlin/Native klibs),
               // so don't add attributes for non-Android projects.
            }
            .artifacts
            .artifactFiles
      }
}


/** Try and get [KotlinProjectExtension], or `null` if it's not present. */
private fun findKotlinExtension(project: Project): KotlinProjectExtension? {
   val kotlinExtension = try {
      project.extensions.findByType()
      // fallback to trying to get the JVM extension
         ?: project.extensions.findByType<org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension>()
   } catch (e: Throwable) {
      when (e) {
         is TypeNotPresentException,
         is ClassNotFoundException,
         is NoClassDefFoundError -> {
            logger.info("[Kotest] failed to find KotlinExtension ${e::class} ${e.message}")
            null
         }

         else -> throw e
      }
   }

   if (kotlinExtension == null && project.extensions.findByName("kotlin") != null) {
      // uh oh - the Kotlin extension is present but findKotlinExtension() failed.
      // Is there a class loader issue? https://github.com/gradle/gradle/issues/27218
      logger.warn {
         val allPlugins =
            project.plugins.joinToString { it::class.qualifiedName ?: "${it::class}" }
         val allExtensions =
            project.extensions.extensionsSchema.elements.joinToString { "${it.name} ${it.publicType}" }

         /* language=TEXT */
         """
         |[Kotest KotlinAdapter] failed to get KotlinProjectExtension in ${project.path}
         |  Applied plugins: $allPlugins
         |  Available extensions: $allExtensions
         """.trimMargin()
      }
   }

   return kotlinExtension
}


private val logger = Logging.getLogger("Kotest-KotlinAdapter")


private fun KotlinProjectExtension.allKotlinCompilations(): NamedDomainObjectSet<KotlinCompilation<*>> {

   val compilations = project.objects.domainObjectContainer<KotlinCompilation<*>>()

   when (this) {
      is KotlinMultiplatformExtension -> {
         targets.all {
            compilations.addAll(compilations)
         }

         return compilations.matching {
            // Exclude legacy KMP metadata compilations, only present in KGP 1.8
            val isMedataCompilation =
               it.platformType == common && it.name == MAIN_COMPILATION_NAME
            !isMedataCompilation
         }
      }

      is KotlinSingleTargetExtension<*> -> {
         compilations.addAll(target.compilations)
      }

      else -> {} // shouldn't happen?
   }

   return compilations
}

/**
 * Determine if a [KotlinCompilation] is for tests, and so should be enabled by default.
 */
private fun KotlinCompilation<*>.isTest(): Boolean {
   return when (this) {
      is KotlinMetadataCompilation<*> -> false

      is KotlinJvmAndroidCompilation -> {
         // Use string-based comparison, not the actual classes, because AGP has deprecated and
         // moved the Library/Application classes to a different package.
         // Using strings is more widely compatible.
         val variantName = androidVariant::class.jvmName
         "LibraryVariant" !in variantName && "ApplicationVariant" !in variantName
      }

      else ->
         name == KotlinCompilation.TEST_COMPILATION_NAME
   }
}
