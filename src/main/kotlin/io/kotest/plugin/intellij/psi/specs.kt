package io.kotest.plugin.intellij.psi

import com.intellij.openapi.roots.ProjectRootManager
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.impl.source.tree.LeafPsiElement
import io.kotest.plugin.intellij.styles.SpecStyle
import org.jetbrains.kotlin.asJava.classes.KtLightClass
import org.jetbrains.kotlin.asJava.classes.KtUltraLightClass
import org.jetbrains.kotlin.lexer.KtKeywordToken
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtBlockExpression
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtClassBody
import org.jetbrains.kotlin.psi.KtClassInitializer
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtFunctionLiteral
import org.jetbrains.kotlin.psi.KtLambdaArgument
import org.jetbrains.kotlin.psi.KtLambdaExpression
import org.jetbrains.kotlin.psi.KtNameReferenceExpression
import org.jetbrains.kotlin.psi.KtObjectDeclaration
import org.jetbrains.kotlin.psi.KtSuperTypeCallEntry
import org.jetbrains.kotlin.psi.KtSuperTypeList
import org.jetbrains.kotlin.psi.KtValueArgument
import org.jetbrains.kotlin.psi.KtValueArgumentList
import org.jetbrains.kotlin.psi.psiUtil.getChildrenOfType
import org.jetbrains.kotlin.psi.psiUtil.getStrictParentOfType
import org.jetbrains.kotlin.psi.psiUtil.isAbstract

/**
 * Returns all [KtClassOrObject] children of this [PsiFile] that are instances of a spec class.
 */
fun PsiFile.specs(): List<KtClassOrObject> {
   return this.classes().filter { it.isSpec() }
}

/**
 * Returns a [KtClassOrObject] if this leaf element is the entry point to a spec.
 * The entry point is the class or object keyword that defines a class or object,
 * and that class or object must be a subclass of a spec style.
 */
fun LeafPsiElement.getSpecEntryPoint(): KtClassOrObject? {
   return if (elementType is KtKeywordToken && (text == "class" || text == "object")) {
      return when (val context = context) {
         is KtObjectDeclaration -> if (context.isSpec()) context else null
         is KtClass -> if (context.isSpec() && !context.isAbstract()) context else null
         else -> null
      }
   } else null
}

/**
 * Returns true if this class is subclass of a spec (including classes which themselves subclass spec).
 */
fun KtClassOrObject.isSpec(): Boolean {
   return if (isUnderTestSources(this)) this.specStyle() != null else false
}

/**
 * Returns true if this element is a kotlin class and it is a subclass of a spec.
 * See [isSpec]
 */
fun PsiElement.isSpec(): Boolean = when (this) {
   is KtUltraLightClass -> kotlinOrigin.isSpec()
   is KtLightClass -> kotlinOrigin?.isSpec() ?: false
   is KtClassOrObject -> isSpec()
   else -> false
}

/**
 * Returns the spec style for this class if it is a subclass of a spec, or null otherwise.
 */
fun KtClassOrObject.specStyle(): SpecStyle? {
   val supers = getAllSuperClasses()
   return SpecStyle.styles.find { supers.contains(it.fqn()) }
}

fun KtCallExpression.isDslInvocation(): Boolean {
   return children.size == 2
      && children[0] is KtNameReferenceExpression
      && children[1] is KtLambdaArgument
}

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

/**
 * If this call expression is an include(factory) or include(factory()) then will
 * return an [Include] describing that.
 *
 * Otherwise returns null.
 */
fun KtCallExpression.include(): Include? {
   if (children.isNotEmpty() &&
      children[0] is KtNameReferenceExpression &&
      children[0].text == "include"
   ) {
      val args = valueArgumentList
      if (args != null) {
         val maybeKtValueArgument = args.arguments.firstOrNull()
         if (maybeKtValueArgument is KtValueArgument) {
            when (val param = maybeKtValueArgument.children.firstOrNull()) {
               is KtCallExpression -> {
                  val name = param.children[0].text
                  return Include(name, IncludeType.Function, param)
               }
               is KtNameReferenceExpression -> {
                  val name = param.text
                  return Include(name, IncludeType.Value, param)
               }
            }
         }
      }
   }
   return null
}

/**
 * Returns any test factory 'include' definitions defined in this class.
 */
fun KtClassOrObject.includes(): List<Include> {

   val body = this.getChildrenOfType<KtClassBody>().firstOrNull()
   if (body != null) return body.includes()

   val superlist = this.getChildrenOfType<KtSuperTypeList>().firstOrNull()
   if (superlist != null) return superlist.includes()

   return emptyList()
}

/**
 * Returns any test factory 'include' functions defined in this class body.
 */
fun KtClassBody.includes(): List<Include> {
   val init = getChildrenOfType<KtClassInitializer>().firstOrNull()
   if (init != null) {
      val block = init.getChildrenOfType<KtBlockExpression>().firstOrNull()
      if (block != null) {
         return block.includes()
      }
   }
   return emptyList()
}

fun KtSuperTypeList.includes(): List<Include> {
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
                     return block.includes()
                  }
               }
            }

         }
      }
   }
   return emptyList()
}

/**
 * Returns any test factory 'include' function calls defined in this block.
 */
fun KtBlockExpression.includes(): List<Include> {
   val calls = getChildrenOfType<KtCallExpression>()
   return calls.mapNotNull { it.include() }
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

/**
 * Returns true if this [PsiElement] is contained within a class that is a subclass
 * of the given spec FQN.
 */
fun PsiElement.isContainedInSpecificSpec(fqn: FqName): Boolean {
   val enclosingClass = getStrictParentOfType<KtClass>() ?: return false
   return if (isUnderTestSources(enclosingClass)) enclosingClass.isSubclass(fqn) else false
}

fun isUnderTestSources(clazz: KtClassOrObject): Boolean {
   return true
//   val psiFile = clazz.containingFile
//   val vFile = psiFile.virtualFile ?: return false
//   return ProjectRootManager.getInstance(clazz.project).fileIndex.isInTestSourceContent(vFile)
}

/**
 * Returns true if this [PsiElement] is located inside a class that subclasses any spec.
 */
fun PsiElement.isContainedInSpec(): Boolean {
   val enclosingClass = getStrictParentOfType<KtClass>() ?: return false
   return enclosingClass.isSpec()
}

/**
 * Returns the Spec that contains this element, or null if this element is not located inside a spec class.
 */
fun PsiElement.enclosingSpec(): KtClass? {
   val ktclass = this.getStrictParentOfType<KtClass>()
   return when {
      ktclass == null -> null
      ktclass.isSpec() -> ktclass
      else -> ktclass.enclosingSpec()
   }
}


enum class IncludeType { Value, Function }

data class Include(val name: String, val type: IncludeType, val psi: PsiElement)

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

