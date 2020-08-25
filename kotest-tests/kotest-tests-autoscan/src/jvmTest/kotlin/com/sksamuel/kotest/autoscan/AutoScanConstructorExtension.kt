package com.sksamuel.kotest.autoscan

import io.kotest.core.spec.AutoScan
import io.kotest.core.extensions.ConstructorExtension
import io.kotest.core.spec.Spec
import kotlin.reflect.KClass
import kotlin.reflect.full.primaryConstructor

@AutoScan
class AutoScanConstructorExtension : ConstructorExtension {
   override fun <T : Spec> instantiate(clazz: KClass<T>): Spec? {
      return when (clazz.simpleName) {
         "AutoScanConstructorSpec" -> clazz.primaryConstructor?.call("foo", "foo")
         else -> null
      }
   }
}
