package io.kotest.plugin.pitest

import io.kotest.engine.spec.AbstractSpec
import org.pitest.testapi.TestUnit
import org.pitest.testapi.TestUnitFinder
import kotlin.reflect.KClass

class KotestUnitFinder : TestUnitFinder {

   @Suppress("UNCHECKED_CAST")
   override fun findTestUnits(clazz: Class<*>): MutableList<TestUnit> {
      return when {
         AbstractSpec::class.java.isAssignableFrom(clazz) -> mutableListOf(KotestUnit(clazz.kotlin as KClass<out AbstractSpec>))
         else -> mutableListOf()
      }
   }
}
