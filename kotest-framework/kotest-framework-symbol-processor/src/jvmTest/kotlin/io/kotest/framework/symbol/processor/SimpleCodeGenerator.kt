package io.kotest.framework.symbol.processor

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFile
import java.io.File
import java.io.OutputStream

class SimpleCodeGenerator : CodeGenerator {

   override val generatedFile: Collection<File>
      get() = TODO("Not yet implemented")

   override fun associate(
      sources: List<KSFile>,
      packageName: String,
      fileName: String,
      extensionName: String
   ) {
      TODO("Not yet implemented")
   }

   override fun associateByPath(
      sources: List<KSFile>,
      path: String,
      extensionName: String
   ) {
      TODO("Not yet implemented")
   }

   override fun associateWithClasses(
      classes: List<KSClassDeclaration>,
      packageName: String,
      fileName: String,
      extensionName: String
   ) {
      TODO("Not yet implemented")
   }

   override fun createNewFile(
      dependencies: Dependencies,
      packageName: String,
      fileName: String,
      extensionName: String
   ): OutputStream {
      TODO("Not yet implemented")
   }

   override fun createNewFileByPath(
      dependencies: Dependencies,
      path: String,
      extensionName: String
   ): OutputStream {
      TODO("Not yet implemented")
   }
}
