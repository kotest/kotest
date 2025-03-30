//package io.kotest.plugin.intellij.psi
//
//import com.intellij.testFramework.fixtures.LightJavaCodeInsightFixtureTestCase
//import io.kotest.matchers.shouldBe
//import org.jetbrains.kotlin.name.FqName
//import org.jetbrains.kotlin.psi.KtClassOrObject
//import org.jetbrains.kotlin.psi.KtPsiFactory
//
//class SupertypesTest : LightJavaCodeInsightFixtureTestCase() {
//
//   fun testHappyPath() {
//      val text =
//         """
//    package test
//
//    open class MySuperType
//    class MyType : MySuperType()
//    """
//
//      val factory = KtPsiFactory(project)
//      val file = factory.createFile(text)
//      val myType = file.findChildrenByClass(KtClassOrObject::class.java).single { it.name == "MyType" }
//      myType.getAllSuperClasses() shouldBe listOf(FqName("test.MySuperType"))
//   }
//}
//
