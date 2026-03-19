//package io.kotest.plugin.intellij.inspections
//
//import com.intellij.lang.annotation.HighlightSeverity
//import com.intellij.openapi.command.CommandProcessor
//import com.intellij.testFramework.fixtures.BasePlatformTestCasev
//import io.kotest.matchers.shouldBe
//import org.jetbrains.kotlin.idea.util.application.runWriteAction
//import java.nio.file.Paths
//
//internal class ShouldBeInstanceOfInspectionTest : BasePlatformTestCase() {
//
//   override fun getTestDataPath(): String {
//      val path = Paths.get("./src/test/resources/").toAbsolutePath()
//      return path.toString()
//   }
//
//   fun testInspection() {
//      myFixture.configureByFile("/inspections/shouldBeInstanceOf.kt")
//      myFixture.enableInspections(ShouldBeInstanceOfInspection::class.java)
//      val highlight = myFixture.doHighlighting().find { it.description == "Replace with shouldBeInstanceOf" }!!
//      highlight.text shouldBe "(a is Long) shouldBe true"
//      highlight.severity shouldBe HighlightSeverity.WARNING
//      highlight.startOffset shouldBe 190
//      highlight.endOffset shouldBe 215
//
//      CommandProcessor.getInstance().runUndoTransparentAction {
//         runWriteAction {
//            highlight.quickFixActionMarkers.first().first.action.invoke(myFixture.project, myFixture.editor, myFixture.file)
//         }
//      }
//
//      file.text shouldBe """package inspections
//
//import io.kotest.core.spec.style.FunSpec
//import io.kotest.matchers.shouldBe
// import io.kotest.matchers.types.shouldBeInstanceOf
//
//class ShouldBeInstanceOfExample : FunSpec({
//
//   test("foo") {
//      val a: Number = 1
//      a.shouldBeInstanceOf<Long>()
//   }
//})
//"""
//
//   }
//}
