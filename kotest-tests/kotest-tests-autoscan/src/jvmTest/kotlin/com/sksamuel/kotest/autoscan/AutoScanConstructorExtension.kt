package com.sksamuel.kotest.autoscan

import io.kotest.core.extensions.ConstructorExtension
import io.kotest.core.spec.AutoScan
import io.kotest.core.spec.SpecConfiguration
import io.kotest.core.spec.style.FunSpec
import io.kotest.shouldBe
import kotlin.reflect.KClass
import kotlin.reflect.full.primaryConstructor

@AutoScan
class AutoScanConstructorExtension : ConstructorExtension {
   override fun <T : SpecConfiguration> instantiate(clazz: KClass<T>): SpecConfiguration? {
      return when (clazz.simpleName) {
         "AutoScanConstructorSpec" -> clazz.primaryConstructor?.call("foo", "foo")
         else -> null
      }
   }
}

class AutoScanConstructorSpec(private val a: String, private val b: String) : FunSpec({
   test("foo") {
      a.shouldBe(b)
   }
})
