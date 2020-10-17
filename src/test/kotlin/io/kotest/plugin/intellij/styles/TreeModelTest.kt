package io.kotest.plugin.intellij.styles

import com.intellij.testFramework.fixtures.LightJavaCodeInsightFixtureTestCase
import io.kotest.matchers.shouldBe
import io.kotest.plugin.intellij.Constants
import io.kotest.plugin.intellij.psi.specs
import io.kotest.plugin.intellij.toolwindow.CallbackNodeDescriptor
import io.kotest.plugin.intellij.toolwindow.IncludeNodeDescriptor
import io.kotest.plugin.intellij.toolwindow.KotestRootNodeDescriptor
import io.kotest.plugin.intellij.toolwindow.TestNodeDescriptor
import io.kotest.plugin.intellij.toolwindow.createTreeModel
import java.nio.file.Paths
import javax.swing.tree.DefaultMutableTreeNode

@Suppress("UNCHECKED_CAST")
class TreeModelTest : LightJavaCodeInsightFixtureTestCase() {

   override fun getTestDataPath(): String {
      val path = Paths.get("./src/test/resources/").toAbsolutePath()
      return path.toString()
   }

   fun testGutterIcons() {

      myFixture.configureByFiles(
         "/funspec_callbacks.kt",
         "/io/kotest/core/spec/style/specs.kt"
      )

      val model = createTreeModel(myFixture.file.virtualFile, myFixture.project, myFixture.file.specs(), myFixture.module)

      val root = model.root as DefaultMutableTreeNode
      val kotest = root.userObject as KotestRootNodeDescriptor
      kotest.presentation.presentableText shouldBe Constants.FrameworkName

      val children = root.children().toList() as List<DefaultMutableTreeNode>
      children.size shouldBe 2
//      children[0].userObject.shouldBeInstanceOf<ModulesNodeDescriptor>()

      val testfile = children[1]
//      testfile.userObject.shouldBeInstanceOf<TestFileNodeDescriptor>()

      val specs = testfile.children().toList() as List<DefaultMutableTreeNode>
      specs.size shouldBe 1
      val spec = specs[0]
//      spec.userObject.shouldBeInstanceOf<SpecNodeDescriptor>()

      val elements = spec.children().toList() as List<DefaultMutableTreeNode>
      elements.size shouldBe 5

      (elements[0].userObject as CallbackNodeDescriptor).presentation.presentableText shouldBe "beforeTest"
      (elements[1].userObject as CallbackNodeDescriptor).presentation.presentableText shouldBe "afterTest"
      (elements[2].userObject as IncludeNodeDescriptor).presentation.presentableText shouldBe "Include: myfactory"
      (elements[3].userObject as IncludeNodeDescriptor).presentation.presentableText shouldBe "Include: myfactory2"
      (elements[4].userObject as TestNodeDescriptor).presentation.presentableText shouldBe "a string cannot be blank"
   }
}
