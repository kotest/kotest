import utils.configureGradleDaemonJvm

plugins {
   id("kotest-base")
   java
   alias(libs.plugins.kotlinBinaryCompatibilityValidator)
}

apiValidation {
   ignoredPackages.addAll(
      listOf(
         "io.kotest.framework.multiplatform.embeddablecompiler",
         "io.kotest.framework.multiplatform.gradle",
         "io.kotest.framework.multiplatform.native"
      )
   )
}

configureGradleDaemonJvm(
   project = project,
   updateDaemonJvm = tasks.updateDaemonJvm,
   gradleDaemonJvmVersion = libs.versions.gradleDaemonJvm.map { JavaVersion.toVersion(it) },
)

