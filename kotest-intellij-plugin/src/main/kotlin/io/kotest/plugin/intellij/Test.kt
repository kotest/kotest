package io.kotest.plugin.intellij

import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.psi.KtClassOrObject

data class TestElement(
   val psi: PsiElement,
   val test: Test,
   val nestedTests: List<TestElement>,
)

data class TestName(
   val prefix: String?,
   val name: String,
   val focus: Boolean,
   val bang: Boolean,
   val interpolated: Boolean // set to true if the name contains one or more interpolated variables
) {
   companion object {
      operator fun invoke(prefix: String?, name: String, interpolated: Boolean): TestName {
         return when {
            name.trim().startsWith("!") -> TestName(
               prefix,
               name.trim().drop(1).trim(),
               focus = false,
               bang = true,
               interpolated = interpolated
            )
            name.trim().startsWith("f:") -> TestName(
               prefix,
               name.trim().drop(2).trim(),
               focus = true,
               bang = false,
               interpolated = interpolated
            )
            else -> TestName(prefix, name, focus = false, bang = false, interpolated = interpolated)
         }
      }
   }

   /**
    * Returns a flattened name that can be used for a single line ui component
    */
   fun displayName(): String {
      val flattened = name.trim().replace("\n", "")
      return if (prefix == null) flattened else "$prefix$flattened"
   }
}

// components for the path, should not include prefixes
data class TestPathEntry(val name: String)

data class Test(
   val name: TestName, // the name as entered by the user
   val parent: Test?, // can be null if this is a root test
   val specClassName: KtClassOrObject, // the containing class name, which all tests must have
   val testType: TestType,
   val xdisabled: Boolean, // if true then this test was defined using one of the x methods
   val psi: PsiElement, // the canonical element that identifies this test
   val isDataTest: Boolean = false
) {

   // true if this test is not xdisabled and not disabled by a bang and not nested inside another disabled test
   val enabled: Boolean = !xdisabled && !name.bang && (parent == null || parent.enabled)

   // true if this is a top level test (aka has no parents)
   val root = parent == null

   // true if this is not a top level test (aka is nested inside another test case)
   val isNested: Boolean = !root

   /**
    * Full path to this test is all parents plus this test
    */
   fun path(): List<TestPathEntry> = when (parent) {
      null -> listOf(TestPathEntry(name.name))
      else -> parent.path() + TestPathEntry(name.name)
   }

   /**
    * Returns the test path with delimiters so that the launcher can parse into components.
    */
   @Deprecated("Used by the Kotest5 run producers. Use descriptor for all new code")
   fun testPath(): String = path().joinToString(" -- ") { it.name }

   /**
    * Returns the descriptor path for this test.
    */
   fun descriptorPath(): String = specClassName.fqName?.asString() + "/" + path().joinToString(" -- ") { it.name }

   /**
    * Returns the test path without delimiters for display to a user.
    */
   fun readableTestPath() = path().joinToString(" ") { it.name }
}

enum class TestType {
   Container, Test
}
