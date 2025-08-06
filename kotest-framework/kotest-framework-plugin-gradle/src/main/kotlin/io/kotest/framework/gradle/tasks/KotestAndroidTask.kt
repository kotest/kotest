package io.kotest.framework.gradle.tasks

import io.kotest.framework.gradle.SpecsResolver
import io.kotest.framework.gradle.TestLauncherJavaExecConfiguration
import org.gradle.api.GradleException
import org.gradle.api.attributes.Attribute
import org.gradle.api.file.FileCollection
import org.gradle.api.provider.Property
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import org.gradle.process.ExecOperations
import javax.inject.Inject

// gradle requires the class be extendable
@CacheableTask // this allows gradle to cache our inputs
abstract class KotestAndroidTask @Inject internal constructor(
   private val executors: ExecOperations,
) : AbstractKotestTask() {

   companion object {
      // unsure why this is needed, but without it the resolver complains about too many candidates for AGP plugin
      val ARTIFACT_TYPE = Attribute.of("artifactType", String::class.java)

      // artifactTypes published by an Android library
      const val TYPE_CLASSES_JAR = "android-classes-jar"; // In AAR
      const val TYPE_CLASSES_DIR = "android-classes-directory"; // Not in AAR
   }

   // must contain tests and dependencies
   @get:Input
   abstract val runtimeClasspath: Property<FileCollection>

   // path to scan, should just be the output of the compilation
   @get:Input
   abstract val specsClasspath: Property<FileCollection>

   @TaskAction
   protected fun execute() {

      val specs = SpecsResolver.specs(specs, packages, specsClasspath.get())
      if (specs.isEmpty()) {
         println(">> No specs found")
         return
      }

      val result = executors.javaexec {
         TestLauncherJavaExecConfiguration()
            .withClasspath(runtimeClasspath.get())
            .withSpecs(specs)
            .withTestReportsDir(testReportsDir.get().asFile.absolutePath)
            .withDescriptor(include.orNull)
            .withCommandLineTags(tags.orNull)
            .configure(this)
      }

      if (result.exitValue != 0) {
         throw GradleException("Test suite failed with errors")
      }
   }
}
