package utils

import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.TaskProvider
import org.gradle.buildconfiguration.tasks.UpdateDaemonJvm
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.gradle.jvm.toolchain.JavaToolchainService
import org.gradle.kotlin.dsl.assign
import org.gradle.kotlin.dsl.invoke
import org.gradle.kotlin.dsl.support.serviceOf

/**
 * Lazily configure the version of Java Gradle uses to run the daemon.
 *
 * In order to maximise Build Cache hits the same JDK version should be used to run Gradle on all machines.
 *
 * Gradle has an incubating feature to set the Daemon JVM
 * https://docs.gradle.org/current/userguide/gradle_daemon.html#sec:daemon_jvm_criteria,
 * but it doesn't auto-download the required JVM. This is annoying, because Gradle will just fail
 * until the user manually installs the required JVM.
 *
 * Instead, this function configures Gradle so it will only set the Daemon JVM if the
 * required JDK is installed.
 *
 * This custom logic can be removed when Gradle supports auto-provisioning of the Daemon JVM
 * https://github.com/gradle/gradle/pull/29166
 */
fun configureGradleDaemonJvm(
   project: Project,
   updateDaemonJvm: TaskProvider<UpdateDaemonJvm>,
   gradleDaemonJvmVersion: Provider<String>,
) {
   updateDaemonJvm {
      languageVersion.set(gradleDaemonJvmVersion.map { JavaLanguageVersion.of(it) })

      val javaToolchains = project.serviceOf<JavaToolchainService>()
      val isGradleDaemonJvmVersionInstalled = languageVersion.isInstalled(javaToolchains)
      inputs.property("isGradleDaemonJvmVersionInstalled", isGradleDaemonJvmVersionInstalled)
      onlyIf { isGradleDaemonJvmVersionInstalled.get() }
   }

   // Depend on some common tasks, so the Daemon JVM properties file will be generated automatically.
   project.tasks
      .matching { it.name in setOf("prepareKotlinIdeaImport", "prepareKotlinBuildScriptModel", "assemble") }
      .configureEach {
         dependsOn(updateDaemonJvm)
      }
}

/**
 * Determine if the [JavaToolchainService] can detect, or automatically install,
 * a toolchain for this [JavaVersion].
 */
private fun Provider<JavaLanguageVersion>.isInstalled(
   javaToolchains: JavaToolchainService
): Provider<Boolean> {
   // Must catch exceptions because Toolchains don't properly support the Provider API.
   // See https://github.com/gradle/gradle/issues/29758
   return map { version ->
      runCatching {
         javaToolchains
            .launcherFor { languageVersion = version }
            .orNull
      }.getOrNull() != null
   }
}
