package io.kotest.framework.gradle.tasks.run

import io.kotest.framework.gradle.internal.TestLauncherExecBuilder
import io.kotest.framework.gradle.internal.TestClassDetector
import io.kotest.framework.gradle.tasks.AbstractKotestTask.Companion.DELIMITER
import org.gradle.api.GradleException
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.FileCollection
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Classpath
import org.gradle.api.tasks.Nested
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.TaskAction
import org.gradle.jvm.toolchain.JavaLauncher

abstract class RunKotestJvmTask internal constructor() : BaseRunKotestTask() {
   @get:Classpath
   abstract val runtimeClasspath: ConfigurableFileCollection

   @get:Nested
   @get:Optional
   abstract val javaLauncher: Property<JavaLauncher>

   @TaskAction
   internal fun action() {
      val candidates = computeCandidates(runtimeClasspath)

      val exec = TestLauncherExecBuilder()
         .withClasspath(runtimeClasspath)
         .withCandidates(candidates)
         .withDescriptor(descriptor.orNull)
         .withCommandLineTags(tags.orNull)

      val javaExecutable = javaLauncher.orNull?.executablePath?.asFile?.invariantSeparatorsPath

      val result = executors.javaexec {
         if (javaExecutable != null) {
            executable = javaExecutable
         }
         exec.configure(this)
      }

      if (result.exitValue != 0) {
         throw GradleException("There were test failures")
      }
   }

   /**
    * Returns the spec classes to include with the launcher command.
    */
   private fun computeCandidates(classpath: FileCollection): List<String> {
      // if the --candidates option was specified, then that is the highest priority and we take
      // that as a delimited list of fully qualified class names
      val candidatesFromOptions = candidates.orNull?.split(DELIMITER)
      if (candidatesFromOptions != null) return candidatesFromOptions

      // If specs was omitted, then we scan the classpath
      val specsFromScanning = TestClassDetector().detect(classpath.asFileTree)
      println("specsFromScanning: $specsFromScanning")

      // if packages was set, we filter down to only classes in those packages
      val packagesFromOptions = packages.orNull?.split(DELIMITER)?.toSet()
      val filteredSpecs = if (packagesFromOptions == null) {
         specsFromScanning
      } else {
         specsFromScanning.filter { spec ->
            packagesFromOptions.contains(spec.packageName)
         }
      }
      return filteredSpecs.map { it.qualifiedName }
   }
}
