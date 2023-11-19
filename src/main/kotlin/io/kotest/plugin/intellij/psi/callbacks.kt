package io.kotest.plugin.intellij.psi

import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.psi.KtBlockExpression
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtClassBody
import org.jetbrains.kotlin.psi.KtClassInitializer
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtFunctionLiteral
import org.jetbrains.kotlin.psi.KtLambdaExpression
import org.jetbrains.kotlin.psi.KtSuperTypeCallEntry
import org.jetbrains.kotlin.psi.KtSuperTypeList
import org.jetbrains.kotlin.psi.KtValueArgument
import org.jetbrains.kotlin.psi.KtValueArgumentList
import org.jetbrains.kotlin.psi.psiUtil.getChildrenOfType

/**
 * Returns any test lifecycle callbacks defined in this class.
 */
fun KtClassOrObject.callbacks(): List<Callback> {

   val body = this.getChildrenOfType<KtClassBody>().firstOrNull()
   if (body != null) return body.callbacks()

   val superlist = this.getChildrenOfType<KtSuperTypeList>().firstOrNull()
   if (superlist != null) return superlist.callbacks()

   return emptyList()
}

fun KtClassBody.callbacks(): List<Callback> {
   val init = getChildrenOfType<KtClassInitializer>().firstOrNull()
   if (init != null) {
      // callbacks are in the form <callback> <block>, ie `afterTest { }` which are
      // represented in kotlin's PSI as instance of KtBlockExpression
      val block = init.getChildrenOfType<KtBlockExpression>().firstOrNull()
      if (block != null) {
         return block.callbacks()
      }
   }
   return emptyList()
}

fun KtSuperTypeList.callbacks(): List<Callback> {
   val entry = getChildrenOfType<KtSuperTypeCallEntry>().firstOrNull()
   if (entry != null) {
      val argList = entry.getChildrenOfType<KtValueArgumentList>().firstOrNull()
      if (argList != null) {
         val valueArg = argList.getChildrenOfType<KtValueArgument>().firstOrNull()
         if (valueArg != null) {
            val lambda = valueArg.getChildrenOfType<KtLambdaExpression>().firstOrNull()
            if (lambda != null) {
               val fliteral = lambda.getChildrenOfType<KtFunctionLiteral>().firstOrNull()
               if (fliteral != null) {
                  val block = fliteral.getChildrenOfType<KtBlockExpression>().firstOrNull()
                  if (block != null) {
                     return block.callbacks()
                  }
               }
            }

         }
      }
   }
   return emptyList()
}

fun KtBlockExpression.callbacks(): List<Callback> {
   val calls = getChildrenOfType<KtCallExpression>()
   return calls
      .filter { it.isDslInvocation() }
      .mapNotNull { call ->
         val fname = call.functionName()
         CallbackType.values().find { it.text == fname }?.let { Callback(it, call) }
      }
}

data class Callback(val type: CallbackType, val psi: PsiElement)

enum class CallbackType {

   BeforeTest {
      override val text = "beforeTest"
   },
   AfterTest {
      override val text = "afterTest"
   },
   BeforeContainer {
      override val text = "beforeContainer"
   },
   AfterContainer {
      override val text = "afterContainer"
   },
   BeforeEach {
      override val text = "beforeEach"
   },
   AfterEach {
      override val text = "afterEach"
   },
   BeforeAny {
      override val text = "beforeAny"
   },
   AfterAny {
      override val text = "afterAny"
   },
   BeforeSpec {
      override val text = "beforeSpec"
   },
   AfterSpec {
      override val text = "afterSpec"
   };

   abstract val text: String
}
