package com.sksamuel.kotest.framework.symbol.processor

import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.symbol.KSTypeArgument
import com.google.devtools.ksp.symbol.Nullability

class SimpleKSType(override val declaration: KSDeclaration) : KSType {

   override val annotations: Sequence<KSAnnotation>
      get() = TODO("Not yet implemented")
   override val arguments: List<KSTypeArgument>
      get() = TODO("Not yet implemented")
   override val isError: Boolean
      get() = TODO("Not yet implemented")
   override val isFunctionType: Boolean
      get() = TODO("Not yet implemented")
   override val isMarkedNullable: Boolean
      get() = TODO("Not yet implemented")
   override val isSuspendFunctionType: Boolean
      get() = TODO("Not yet implemented")
   override val nullability: Nullability
      get() = TODO("Not yet implemented")

   override fun isAssignableFrom(that: KSType): Boolean {
      TODO("Not yet implemented")
   }

   override fun isCovarianceFlexible(): Boolean {
      TODO("Not yet implemented")
   }

   override fun isMutabilityFlexible(): Boolean {
      TODO("Not yet implemented")
   }

   override fun makeNotNullable(): KSType {
      TODO("Not yet implemented")
   }

   override fun makeNullable(): KSType {
      TODO("Not yet implemented")
   }

   override fun replace(arguments: List<KSTypeArgument>): KSType {
      TODO("Not yet implemented")
   }

   override fun starProjection(): KSType {
      TODO("Not yet implemented")
   }
}
