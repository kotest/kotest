package io.kotest.extensions.pitest

import io.kotest.core.spec.Spec
import org.pitest.testapi.TestUnit
import org.pitest.testapi.TestUnitExecutionListener
import org.pitest.testapi.TestUnitFinder
import kotlin.reflect.KClass

class KotestUnitFinder : TestUnitFinder {

   override fun findTestUnits(clazz: Class<*>, listener: TestUnitExecutionListener?): MutableList<TestUnit> {
      return when {
         Spec::class.java.isAssignableFrom(clazz) -> mutableListOf(KotestUnit(clazz.kotlin as KClass<out Spec>))
         else -> mutableListOf()
      }
   }
}
