package io.kotest.plugin.pitest

import io.kotest.Spec
import org.pitest.testapi.TestUnit
import org.pitest.testapi.TestUnitFinder
import kotlin.reflect.KClass

class KotestUnitFinder : TestUnitFinder {

  override fun findTestUnits(clazz: Class<*>): MutableList<TestUnit> {
    return when {
      Spec::class.java.isAssignableFrom(clazz) -> mutableListOf(KotestUnit(clazz.kotlin as KClass<out Spec>))
      else -> mutableListOf()
    }
  }
}
