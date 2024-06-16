plugins {
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
