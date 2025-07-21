package io.kotest.framework.gradle.tasks

import io.kotest.framework.gradle.SpecsResolver
import io.kotest.framework.gradle.TestLauncherJavaExecConfiguration
import org.gradle.api.attributes.Attribute
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import org.gradle.process.ExecOperations
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.KotlinAndroidExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilation
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinAndroidTarget
import javax.inject.Inject

// gradle requires the class be extendable
@CacheableTask // this allows gradle to cache our inputs
abstract class KotestAndroidTask @Inject internal constructor(
   private val executors: ExecOperations,
   private val objects: ObjectFactory,
) : AbstractKotestTask() {

   companion object {
      // unsure why this is needed, but without it the resolver complains about too many candidates for AGP plugin
      val ARTIFACT_TYPE = Attribute.of("artifactType", String::class.java)

      // types published by an Android library
      const val TYPE_CLASSES_JAR = "android-classes-jar"; // In AAR
      const val TYPE_CLASSES_DIR = "android-classes-directory"; // Not in AAR
   }

   // this is the name of the compilation to run, usually "debugUnitTest" or "releaseUnitTest"
   // each configured KotestAndroidTask will run tests for a single compilation
   @get:Input
   abstract val compilationName: Property<String>

   @TaskAction
   protected fun execute() {

      val ext = project.extensions.getByType(KotlinAndroidExtension::class.java)
      if (ext.target is KotlinAndroidTarget) {
         // example compilations for a typical project:
         // [debug, debugAndroidTest, debugUnitTest, release, releaseUnitTest]
         val compilation = ext.target.compilations
            .matching { it.name == compilationName.get() }
            .single()
         executeCompilation(compilation)
      }
   }

   @OptIn(ExperimentalKotlinGradlePluginApi::class)
   private fun executeCompilation(compilation: KotlinCompilation<*>) {

      val runtimeDependencyConfigurationName = compilation.runtimeDependencyConfigurationName
         ?: error("No runtimeDependencyConfigurationName defined for compilation ${compilation.name}")

      val rt = project.configurations.findByName(runtimeDependencyConfigurationName)
         ?: error("No configuration found for $runtimeDependencyConfigurationName")

      val runtimeFiles = rt.incoming.artifactView { attributes { attribute(ARTIFACT_TYPE, TYPE_CLASSES_JAR) } }.files

      //    The following types/formats are supported:
      //    - A String or CharSequence path, for example 'src/main/java' or '/usr/include'.
      //    - A String or CharSequence URI, for example 'file:/usr/include'.
      //    - A File instance.
      //    - A Path instance.
      //    - A Directory instance.
      //    - A RegularFile instance.
      //    - A URI or URL instance of file.
      //    - A TextResource instance.
      val classpathWithTests = objects.fileCollection()
         .from(runtimeFiles)
         .from(compilation.output.allOutputs) // this is the compiled output from this compilation

      val specs = SpecsResolver.specs(specs, packages, classpathWithTests)
      if (specs.isEmpty()) {
         println("No specs found for ${compilation.name}, skipping")
         return
      }

      val result = executors.javaexec {
         TestLauncherJavaExecConfiguration()
            .withClasspath(classpathWithTests)
            .withSpecs(specs)
            .withDescriptor(descriptor.orNull)
            .withCommandLineTags(tags.orNull)
            .configure(this)
      }

      if (result.exitValue != 0) {
         println("There were test failures")
         result.rethrowFailure()
      }
   }
}
