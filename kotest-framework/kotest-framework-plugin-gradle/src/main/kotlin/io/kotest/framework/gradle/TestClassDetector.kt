package io.kotest.framework.gradle

import org.gradle.api.file.FileTree
import org.gradle.api.file.FileVisitDetails
import org.gradle.api.file.FileVisitor
import org.objectweb.asm.ClassReader
import org.objectweb.asm.Opcodes

/**
 * Scans a gradle [FileTree] looking for classes that extend a spec class.
 * We use object-asm to scan the bytecode the same way the standard gradle Test task does.
 */
internal class TestClassDetector {

   private val parents = mutableMapOf<String, String>()
   private val candidates = mutableSetOf<String>()

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

   fun detect(inputs: FileTree): Set<TestClass> {
      parents.clear()
      inputs.filter { it.name.endsWith(".class") }.asFileTree.visit(visitor)
      return candidates.filter { isSpecClass(it) }.map { toTestClass(it) }.toSet()
   }

   private fun toTestClass(className: String): TestClass {
      val qualifiedName = className.replace("/", ".")
      return TestClass(qualifiedName.substringBeforeLast('.'), qualifiedName)
   }

   /**
    * Creates a relationship between the given class and its parent super class.
    * We must do this for all classes, not just spec classes, because we need to know
    * the full hierarchy to determine if a class is a spec class, since we allow classes
    * to extend other classes that are themselves extending spec classes.
    */
   internal fun add(className: String, superName: String) {
      parents[className] = superName
   }

   /**
    * Returns true if this class directly extends a spec class,
    * or indirectly via a chain of super classes.
    */
   private fun isSpecClass(className: String): Boolean {
      val superName = parents[className] ?: return false
      if (specClasses.contains(superName)) return true
      return isSpecClass(superName)
   }

   // basic visitor that just adds every class and its parent to the mutable map
   private val visitor = object : FileVisitor {
      override fun visitDir(dirDetails: FileVisitDetails) {
      }

      // Note: Abstract classes are filtered out
      override fun visitFile(fileDetails: FileVisitDetails) {
         val reader = ClassReader(fileDetails.file.readBytes())
         // all classes are added to the parents map so we can traverse the hierarchy later to see if
         // a class extends another class that extends a spec and so on
         add(reader.className, reader.superName)
         // only non abstract classes are added to the candidates set though
         if (reader.access and Opcodes.ACC_ABSTRACT == 0)
            candidates.add(reader.className)
      }
   }
}

internal data class TestClass(
   val packageName: String,
   val qualifiedName: String,
)
