package io.kotest.framework.gradle

import org.gradle.api.file.FileTree
import org.gradle.api.file.FileVisitDetails
import org.gradle.api.file.FileVisitor
import org.objectweb.asm.ClassReader
import org.objectweb.asm.Opcodes

/**
 * Scans a gradle [FileTree] looking for classes that extend a spec class.
 * We use object-asm to scan the bytecode.
 */
class TestClassDetector {

   private val parents = mutableMapOf<String, String>()

   private val specClasses = listOf(
      "io/kotest/core/spec/style/AnnotationSpec",
      "io/kotest/core/spec/style/BehaviorSpec",
      "io/kotest/core/spec/style/DescribeSpec",
      "io/kotest/core/spec/style/ExpectSpec",
      "io/kotest/core/spec/style/FeatureSpec",
      "io/kotest/core/spec/style/FreeSpec",
      "io/kotest/core/spec/style/FunSpec",
      "io/kotest/core/spec/style/ShouldSpec",
      "io/kotest/core/spec/style/StringSpec",
      "io/kotest/core/spec/style/WordSpec",
   )

   fun detect(candidates: FileTree): List<TestClass> {
      parents.clear()
      candidates.filter { it.name.endsWith(".class") }.asFileTree.visit(visitor)
      return parents.filter { isSpecClass(it.value) }.keys.toList().map { toTestClass(it) }
   }

   internal fun toTestClass(className: String): TestClass {
      val qualifiedName = className.replace("/", ".")
      return TestClass(qualifiedName.substringBeforeLast('.'), qualifiedName)
   }

   /**
    * Creates a relationship between the given class and its parent super class.
    * We must do this for all classes, not just spec classes, because we need to know
    * the full heirarchy to determine if a class is a spec class, since we allow classes
    * to extend other classes that are themselves extending spec classes.
    */
   internal fun add(className: String, superName: String) {
      parents[className] = superName
   }

   /**
    * Returns true if this class extends directly a spec class,
    * or indirectly via a chain of super classes.
    */
   private fun isSpecClass(superName: String): Boolean {
      if (specClasses.contains(superName)) return true
      val superSuperName = parents[superName] ?: return false
      return isSpecClass(superSuperName)
   }

   // basic visitor that just adds every class and its parent to the mutable map
   private val visitor = object : FileVisitor {
      override fun visitDir(dirDetails: FileVisitDetails) {
      }

      // Note: Abstract classes are filtered out
      override fun visitFile(fileDetails: FileVisitDetails) {
         val reader = ClassReader(fileDetails.file.readBytes())
         if (reader.access and Opcodes.ACC_ABSTRACT == 0)
            add(reader.className, reader.superName)
      }
   }
}

data class TestClass(val packageName: String, val qualifiedName: String)
