package io.kotest.engine

import io.kotest.common.JVMOnly
import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.spec.Spec
import io.kotest.engine.config.KotestPropertiesLoader
import io.kotest.engine.config.ProjectConfigLoader
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.reflect.KClass

/**
 * Writes failed specs to a file so that the [io.kotest.engine.spec.FailureFirstSorter]
 * can use the file to run failed specs first.
 *
 * Note: This is a JVM-only feature.
 */
@JVMOnly
internal actual fun writeFailuresIfEnabled(context: TestEngineContext) {
   if (context.projectConfigResolver.writeSpecFailureFile()) {
      val failedSpecs = context.collector.tests
         .filterValues { it.isErrorOrFailure }
         .map { it.key.specClass }
         .toSet()
      writeSpecFailures(failedSpecs, context.projectConfigResolver.specFailureFilePath())
   }
}

private fun writeSpecFailures(failures: Set<KClass<out Spec>>, filename: String) {
   val path = Paths.get(filename).toAbsolutePath()
   path.parent.toFile().mkdirs()
   val content = failures.distinct().joinToString("\n") { it.java.canonicalName }
   Files.write(path, content.toByteArray())
}

/**
 * Loads system properties from a well-known props file from the classpath.
 */
@JVMOnly
internal actual fun loadSystemProperties() {
   KotestPropertiesLoader.loadAndApplySystemPropsFile()
}

@JVMOnly
internal actual fun resolveProjectConfig(
   projectConfig: AbstractProjectConfig?,
   specFqns: Set<String>,
): AbstractProjectConfig? {
   return ProjectConfigLoader.load(specFqns) ?: projectConfig
}
