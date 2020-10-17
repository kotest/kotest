package io.kotest.plugin.intellij.run

import io.kotest.plugin.intellij.Test
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtClassOrObject

data class RunData(
   val specName: String?,
   val testPath: String?,
   val packageName: String?
)

fun generateName(packageName: String): String {
   return "All tests in '$packageName'"
}

fun generateName(fqName: FqName, test: Test?): String {
   return if (test == null) {
      fqName.shortName().asString()
   } else {
      fqName.shortName().asString() + ": " + test.name.displayName()
   }
}

fun generateName(ktclass: KtClassOrObject, test: Test?): String {
   return if (test == null) {
      ktclass.fqName?.shortName()?.asString() ?: ktclass.toString()
   } else {
      ktclass.fqName?.shortName()?.asString() + ": " + test.name.displayName()
   }
}

@Deprecated("Set names manually")
fun RunData.suggestedName(): String? {
   return when {
      packageName != null && packageName.isNotBlank() -> "All tests in '$packageName'"
      specName == null || specName.isBlank() -> null
      testPath == null || testPath.isBlank() -> specName.split('.').last()
      else -> {
         val simpleName = specName.split('.').last()
         val readableTestPath = testPath.replace(" -- ", " ")
         "$simpleName: $readableTestPath"
      }
   }
}

fun RunData.actionName(): String? {
   return when {
      packageName != null && packageName.isNotBlank() -> "All tests in '$packageName'"
      testPath != null && testPath.isNotBlank() -> testPath.split(" -- ".toRegex()).last()
      specName != null && specName.isNotBlank() -> specName.split('.').last()
      else -> "Test" // shouldn't happen
   }
}
