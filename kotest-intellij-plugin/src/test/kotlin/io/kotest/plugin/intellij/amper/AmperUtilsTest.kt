package io.kotest.plugin.intellij.amper

import com.intellij.openapi.application.WriteAction
import com.intellij.openapi.roots.ModuleRootManager
import com.intellij.openapi.roots.ModuleRootModificationUtil
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import io.kotest.matchers.shouldBe

class AmperUtilsTest : BasePlatformTestCase() {

   fun testNonAmperModuleIsNotDetected() {
      AmperUtils.isAmperModule(myFixture.module) shouldBe false
      AmperUtils.amperModuleRoot(myFixture.module) shouldBe null
   }

   fun testAmperModuleDetectedFromContentRootContainingModuleYaml() {
      val contentRoot = ModuleRootManager.getInstance(myFixture.module).contentRoots.first()
      writeFile(contentRoot, "module.yaml", "product: jvm/lib\n")

      AmperUtils.isAmperModule(myFixture.module) shouldBe true
      AmperUtils.amperModuleRoot(myFixture.module) shouldBe contentRoot
   }

   fun testAmperModuleDetectedWhenContentRootIsNestedInsideModuleDir() {
      // Simulate Amper-native layout where IntelliJ may register `src/` and `test/` as separate
      // content roots under a directory holding `module.yaml`.
      val moduleRoot = ModuleRootManager.getInstance(myFixture.module).contentRoots.first()
      val srcDir = WriteAction.computeAndWait<VirtualFile, RuntimeException> {
         moduleRoot.createChildDirectory(this, "src")
      }
      writeFile(moduleRoot, "module.yaml", "product: jvm/lib\n")

      // Reset the module's content roots so that only `src/` is registered (not the parent).
      ModuleRootModificationUtil.updateModel(myFixture.module) { model ->
         model.contentEntries.forEach { model.removeContentEntry(it) }
         model.addContentEntry(srcDir)
      }

      AmperUtils.isAmperModule(myFixture.module) shouldBe true
      AmperUtils.amperModuleRoot(myFixture.module) shouldBe moduleRoot
   }

   private fun writeFile(parent: VirtualFile, name: String, content: String): VirtualFile =
      WriteAction.computeAndWait<VirtualFile, RuntimeException> {
         val existing = parent.findChild(name)
         val file = existing ?: parent.createChildData(this, name)
         file.setBinaryContent(content.toByteArray(Charsets.UTF_8))
         file
      }
}
