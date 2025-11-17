package io.kotest.framework.symbol.processor

import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSDeclaration
import com.google.devtools.ksp.symbol.KSFile
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.KSName
import com.google.devtools.ksp.symbol.KSNode
import com.google.devtools.ksp.symbol.KSPropertyDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.symbol.KSTypeArgument
import com.google.devtools.ksp.symbol.KSTypeParameter
import com.google.devtools.ksp.symbol.KSTypeReference
import com.google.devtools.ksp.symbol.KSVisitor
import com.google.devtools.ksp.symbol.Location
import com.google.devtools.ksp.symbol.Modifier
import com.google.devtools.ksp.symbol.Origin

class SimpleKSName(private val name: String) : KSName {
   override fun asString(): String = name
   override fun getQualifier(): String {
      TODO("Not yet implemented")
   }

   override fun getShortName(): String {
      TODO("Not yet implemented")
   }

}

class SimpleKSClassDeclaration(val className: String) : KSClassDeclaration {

   override val classKind: ClassKind
      get() = TODO("Not yet implemented")
   override val isCompanionObject: Boolean
      get() = TODO("Not yet implemented")
   override val primaryConstructor: KSFunctionDeclaration?
      get() = TODO("Not yet implemented")
   override val superTypes: Sequence<KSTypeReference>
      get() = TODO("Not yet implemented")

   override fun asStarProjectedType(): KSType {
      TODO("Not yet implemented")
   }

   override fun asType(typeArguments: List<KSTypeArgument>): KSType {
      TODO("Not yet implemented")
   }

   override fun getAllFunctions(): Sequence<KSFunctionDeclaration> {
      TODO("Not yet implemented")
   }

   override fun getAllProperties(): Sequence<KSPropertyDeclaration> {
      TODO("Not yet implemented")
   }

   override fun getSealedSubclasses(): Sequence<KSClassDeclaration> {
      TODO("Not yet implemented")
   }

   override val containingFile: KSFile?
      get() = TODO("Not yet implemented")
   override val docString: String?
      get() = TODO("Not yet implemented")

   override val packageName: KSName = SimpleKSName(className.substringBeforeLast('.'))

   override val parentDeclaration: KSDeclaration?
      get() = TODO("Not yet implemented")

   override val qualifiedName: KSName = SimpleKSName(className)

   override val simpleName: KSName = SimpleKSName(className.substringAfterLast('.'))

   override val typeParameters: List<KSTypeParameter>
      get() = TODO("Not yet implemented")
   override val modifiers: Set<Modifier>
      get() = TODO("Not yet implemented")
   override val location: Location
      get() = TODO("Not yet implemented")
   override val origin: Origin
      get() = TODO("Not yet implemented")
   override val parent: KSNode?
      get() = TODO("Not yet implemented")

   override fun <D, R> accept(visitor: KSVisitor<D, R>, data: D): R {
      TODO("Not yet implemented")
   }

   override val annotations: Sequence<KSAnnotation>
      get() = TODO("Not yet implemented")
   override val isActual: Boolean
      get() = TODO("Not yet implemented")
   override val isExpect: Boolean
      get() = TODO("Not yet implemented")

   override fun findActuals(): Sequence<KSDeclaration> {
      TODO("Not yet implemented")
   }

   override fun findExpects(): Sequence<KSDeclaration> {
      TODO("Not yet implemented")
   }

   override val declarations: Sequence<KSDeclaration>
      get() = TODO("Not yet implemented")
}
