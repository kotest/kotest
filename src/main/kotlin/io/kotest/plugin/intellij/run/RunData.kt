package io.kotest.plugin.intellij.run

import io.kotest.plugin.intellij.Test
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtClassOrObject

/**
 * Holds state for generating suggested names for our config.
 */
@Deprecated("Starting with Kotest 6 the preferred method is to run via gradle")
data class RunData(
   val specName: String?,
   val testPath: String?,
   val packageName: String?
)

@Deprecated("Starting with Kotest 6 the preferred method is to run via gradle")
fun generateName(packageName: String): String {
   return "All tests in '$packageName'"
}

@Deprecated("Starting with Kotest 6 the preferred method is to run via gradle")
fun generateName(fqName: FqName, test: Test?): String {
   return if (test == null) {
      fqName.shortName().asString()
   } else {
      fqName.shortName().asString() + ": " + test.name.displayName()
   }
}

@Deprecated("Starting with Kotest 6 the preferred method is to run via gradle")
fun generateName(ktclass: KtClassOrObject, test: Test?): String {
   return if (test == null) {
      ktclass.fqName?.shortName()?.asString() ?: ktclass.toString()
   } else {
      ktclass.fqName?.shortName()?.asString() + ": " + test.name.displayName()
   }
}

@Deprecated("Starting with Kotest 6 the preferred method is to run via gradle")
fun RunData.suggestedName(): String? {
   return when {
      !packageName.isNullOrBlank() -> "All tests in '$packageName'"
      specName.isNullOrBlank() -> null
      testPath.isNullOrBlank() -> specName.split('.').last()
      else -> {
         val simpleName = specName.split('.').last()
         val readableTestPath = testPath.replace(" -- ", " ")
         "$simpleName: $readableTestPath"
      }
   }
}
