package io.kotlintest.plugin.pitest

import io.kotlintest.Spec
import org.pitest.testapi.TestUnit
import org.pitest.testapi.TestUnitFinder
import kotlin.reflect.KClass

class KotlinTestUnitFinder : TestUnitFinder {

  override fun findTestUnits(clazz: Class<*>): MutableList<TestUnit> {
    return when {
      Spec::class.java.isAssignableFrom(clazz) -> mutableListOf(KotlinTestUnit(clazz.kotlin as KClass<out Spec>))
      else -> mutableListOf()
    }
  }
}
