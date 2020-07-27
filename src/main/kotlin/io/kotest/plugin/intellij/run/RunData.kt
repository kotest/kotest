package io.kotest.plugin.intellij.run

data class RunData(
   val specName: String?,
   val testPath: String?,
   val packageName: String?
)

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
