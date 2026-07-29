package io.kotest.plugin.intellij.locations

import com.intellij.psi.search.GlobalSearchScope
import com.intellij.testFramework.fixtures.LightJavaCodeInsightFixtureTestCase
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.ints.shouldBeGreaterThan
import java.nio.file.Paths

/**
 * The `contexts` segments passed to [EmbeddedLocationTestLocator] come from the engine's runtime
 * test names, which are whitespace-normalized ([io.kotest.core.names.TestNameBuilder]). The PSI
 * side must normalize the literal source name the same way before comparing, or a nested test
 * whose source string has irregular whitespace never resolves - "Jump to Source" then silently
 * does nothing for it, even though its parent container (which navigates via a different,
 * class-level mechanism) works fine.
 */
class EmbeddedLocationTestLocatorTest : LightJavaCodeInsightFixtureTestCase() {

   override fun getTestDataPath(): String {
      return Paths.get("./src/test/resources/").toAbsolutePath().toString()
   }

   private fun resolve(location: EmbeddedLocation) =
      EmbeddedLocationTestLocator(location).getLocation("kotest", location.path, project, GlobalSearchScope.allScope(project))

   fun `test resolves a top level test with a clean literal name`() {
      myFixture.configureByFiles("/whitespace-funspec.kt", "/io/kotest/core/spec/style/specs.kt")
      val location = EmbeddedLocation(
         "io.kotest.samples.gradle.WhitespaceFunSpecExampleTest/outer context",
         "outer context"
      )
      resolve(location) shouldHaveSize 1
   }

   fun `test resolves a nested test whose literal source name has irregular whitespace`() {
      myFixture.configureByFiles("/whitespace-funspec.kt", "/io/kotest/core/spec/style/specs.kt")
      // the engine normalizes "a  nested   test" (double/triple spaces in source) down to
      // "a nested test" before it ever reaches the locationUrl/contexts path.
      val location = EmbeddedLocation(
         "io.kotest.samples.gradle.WhitespaceFunSpecExampleTest/outer context -- a nested test",
         "a nested test"
      )
      resolve(location) shouldHaveSize 1
   }

   fun `test resolves a nested test from a single segment path`() {
      // IntelliJ's Gradle test event integration only carries a leaf test's own name on the
      // locationUrl - unlike the JUnit Platform launcher's MethodSource, it drops every ancestor
      // container segment. A nested test therefore arrives here as a single-segment path
      // indistinguishable in shape from a top-level test, and must still resolve by searching
      // the whole tree rather than only the top level.
      myFixture.configureByFiles("/funspec.kt", "/io/kotest/core/spec/style/specs.kt")
      val location = EmbeddedLocation(
         "io.kotest.samples.gradle.FunSpecExampleTest/a nested test",
         "a nested test"
      )
      resolve(location) shouldHaveSize 1
   }

   fun `test resolves the correct leaf when the same name is nested under two different top level containers`() {
      // "child1" appears twice - once under "base", once under "base 2" - a bare single-segment
      // path can't tell these apart (see EmbeddedLocationTestLocator#findByNameAtAnyDepth), but
      // once GradleParentIdParser restores the real ancestor chain onto the path (as
      // EmbeddedLocationParser#parseLocationUrl now does), navigation must land on the exact one.
      myFixture.configureByFiles("/duplicate-leaf-funspec.kt", "/io/kotest/core/spec/style/specs.kt")
      val location = EmbeddedLocation(
         "io.kotest.samples.gradle.DuplicateLeafFunSpecExampleTest/base 2 -- inner root -- child1",
         "child1"
      )
      val results = resolve(location)
      results shouldHaveSize 1

      val fileText = results.first().psiElement.containingFile.text
      val resolvedOffset = results.first().psiElement.textRange.startOffset
      // the "base 2" block starts strictly after the "base" block in source, so landing after
      // "base 2"'s own declaration (and not merely after "base"'s) proves we resolved the leaf
      // nested under "base 2", not the identically-named one under "base".
      resolvedOffset shouldBeGreaterThan fileText.indexOf("context(\"base 2\")")
   }
}
