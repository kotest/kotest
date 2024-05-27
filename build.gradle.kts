plugins {
   alias(libs.plugins.kotlinBinaryCompatibilityValidator)
}

repositories {
   mavenCentral()
   mavenLocal()
   maven("https://oss.sonatype.org/content/repositories/snapshots/")
   google()
   gradlePluginPortal() // tvOS builds need to be able to fetch a kotlin gradle plugin
}

apiValidation {
   @OptIn(kotlinx.validation.ExperimentalBCVApi::class)
   klib {
      enabled = true
   }

   ignoredPackages.addAll(
      listOf(
         "io.kotest.framework.multiplatform.embeddablecompiler",
         "io.kotest.framework.multiplatform.gradle",
         "io.kotest.framework.multiplatform.native"
      )
   )
}
