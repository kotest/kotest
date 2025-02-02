package io.kotest.framework.gradle.internal.adapters

import io.kotest.framework.gradle.KotestExtension
import io.kotest.framework.gradle.config.KotestAndroidJvmSpec
import io.kotest.framework.gradle.config.KotestJsSpec
import io.kotest.framework.gradle.config.KotestJvmSpec
import io.kotest.framework.gradle.config.KotestNativeSpec
import io.kotest.framework.gradle.config.KotestWasmSpec
import io.kotest.framework.gradle.internal.utils.artifactType
import io.kotest.framework.gradle.internal.utils.domainObjectContainer
import io.kotest.framework.gradle.internal.utils.letAll
import io.kotest.framework.gradle.internal.utils.warn
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
import org.jetbrains.kotlin.gradle.plugin.KotlinPlatformType
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

   val kotlinExtension = findKotlinExtension(project)
   if (kotlinExtension == null) {
      logger.info("Skipping applying Kotest KotlinAdapter in ${project.path} - could not find KotlinProjectExtension")
      return
   }
   logger.info("Configuring Kotest KotlinAdapter in Gradle Kotlin Project ${project.path}")

   kotlinExtension.allKotlinCompilations()
      .matching { it.isTest() }
      .letAll { compilation: KotlinCompilation<*> ->

         fun compilationClasspath(): FileCollection {
            return collectKotlinCompilationClasspath(
               configurations = project.configurations,
               objects = objects,
               compilation = compilation,
            )
         }

         when (compilation.platformType) {
            KotlinPlatformType.common -> {}

            KotlinPlatformType.androidJvm ->
               kotestExtension.testExecutions.register<KotestAndroidJvmSpec>(compilation.name) {
                  classpath.from(compilationClasspath())
               }

            KotlinPlatformType.jvm ->
               kotestExtension.testExecutions.register<KotestJvmSpec>(compilation.name) {
                  classpath.from(compilationClasspath())
               }

            KotlinPlatformType.js ->
               kotestExtension.testExecutions.register<KotestJsSpec>(compilation.name) {
                  // TODO register JS target ...
               }

            KotlinPlatformType.native ->
               kotestExtension.testExecutions.register<KotestNativeSpec>(compilation.name) {
                  // TODO register Native target ...
               }

            KotlinPlatformType.wasm ->
               kotestExtension.testExecutions.register<KotestWasmSpec>(compilation.name) {
                  // TODO register Wasm target ...
               }
         }

//         kotestExtension.testExecutions.register(compilation.name) {
////         this.kotlinTargetName.set(compilation.target.targetName)
//            this.kotlinTarget.set(
//               when (compilation.platformType) {
//                  common -> KotlinTarget.Common
//                  jvm -> KotlinTarget.JVM
//                  js -> KotlinTarget.JS
//                  androidJvm -> KotlinTarget.AndroidJVM
//                  native -> KotlinTarget.Native
//                  wasm -> KotlinTarget.Wasm
//               }
//            )
//            this.classpath.from(compilationClasspath)
//         }
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

   if (compilation.target.platformType == KotlinPlatformType.androidJvm) {
      compilationClasspath.from(kotlinCompileDependencyFiles(configurations, compilation))
      compilationClasspath.from(kotlinCompileDependencyFiles(configurations, compilation, selectAndroidJars = true))
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
   selectAndroidJars: Boolean = false,
): Provider<FileCollection> {
   val jarArtifactType = if (selectAndroidJars) "android-classes-jar" else "jar"

   return configurations
      .named(compilation.compileDependencyConfigurationName)
      .map {
         it.incoming
            .artifactView {
               // Android publishes many variants, which can cause Gradle to get confused,
               // so specify that we need a JAR and resolve leniently
               if (compilation.target.platformType == KotlinPlatformType.androidJvm) {
                  attributes {
                     artifactType(jarArtifactType)
                  }

                  // Setting lenient=true is not ideal, because it might hide problems.
                  // Unfortunately, Gradle has no chill and dependency resolution errors
                  // will cause tasks to completely fail, even if the dependencies aren't necessary.
                  // (There's a chance that the dependencies aren't even used in the project!)
                  // So, resolve leniently, just in case it works.
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
               it.platformType == KotlinPlatformType.common && it.name == MAIN_COMPILATION_NAME
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
 * Determine if a [KotlinCompilation] compiles tests.
 * If it does, then Kotest should run tests with it.
 */
private fun KotlinCompilation<*>.isTest(): Boolean {
   return when (this) {
      is KotlinMetadataTarget -> false

      is KotlinMetadataCompilation<*> -> false

      is KotlinJvmAndroidCompilation -> {
         // Use string-based comparison, not the actual classes, because AGP has deprecated and
         // moved the Library/Application/Test classes to a different package.
         // Using strings is more widely compatible.
         val variantName = androidVariant::class.jvmName
         "TestExtension" in variantName
      }

      else ->
         name == KotlinCompilation.TEST_COMPILATION_NAME
   }
}
