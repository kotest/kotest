package io.kotest.plugin.intellij

import com.intellij.codeInsight.daemon.quickFix.ExternalLibraryResolver
import com.intellij.openapi.module.Module
import com.intellij.openapi.roots.ExternalLibraryDescriptor
import com.intellij.util.ThreeState

class KotestExternalLibraryResolver : ExternalLibraryResolver() {

   companion object {
      val Specs = listOf(
         "FunSpec",
         "BehaviorSpec",
         "StringSpec",
         "ShouldSpec",
         "AnnotationSpec",
         "ExpectSpec",
         "FeatureSpec",
         "DescribeSpec",
         "WordSpec",
         "FreeSpec"
      )
   }

   override fun resolveClass(
      shortClassName: String,
      isAnnotation: ThreeState,
      contextModule: Module
   ): ExternalClassResolveResult? {
      if (Specs.contains(shortClassName)) {
         return ExternalClassResolveResult("io.kotest.core.spec.style.$shortClassName", KotestExternalLibraryDescriptor)
      }
      return null
   }
}

object KotestExternalLibraryDescriptor : ExternalLibraryDescriptor(
   /* libraryGroupId = */ "io.kotest",
   /* libraryArtifactId = */ "kotest-framework-engine",
   /* minVersion = */ "5.9.1",
   /* maxVersion = */ null,
   /* preferredVersion = */ "5.9.1"
) {
   override fun getPresentableName(): String = "Kotest6"
}
