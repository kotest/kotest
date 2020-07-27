package io.kotest.plugin.intellij

import com.intellij.psi.PsiElement

data class TestElement(
   val psi: PsiElement,
   val test: Test,
   val tests: List<TestElement>
)

data class TestName(
   val name: String,
   val interpolated: Boolean // set to true if the name contains one or more interpolated variables
)

// components for the path, should not include prefixes
data class TestPathEntry(val name: String)

data class Test(
   val name: TestName, // the human readable name for this test. Includes prefixes.
   val path: List<TestPathEntry>, // components for the path, should not include prefixes
   val testType: TestType,
   val xdisabled: Boolean, // if true then this test was defined using one of the x methods
   val root: Boolean, // true if this test is a top level test
   val psi: PsiElement // the canonical element that identifies this test
) {

   val isBang: Boolean = name.name.startsWith("!")

   val isFocus: Boolean = name.name.startsWith("f:")

   val isNested: Boolean = !root

   val enabled: Boolean = !xdisabled && !isBang

   /**
    * Returns the test path with delimiters so that the launcher can parse into components
    */
   fun testPath(): String = path.joinToString(" -- ") { it.name }

   /**
    * Returns the test path without delimiters for display to a user.
    */
   fun readableTestPath() = path.joinToString(" ") { it.name }
}

enum class TestType {
   Container, Test
}
