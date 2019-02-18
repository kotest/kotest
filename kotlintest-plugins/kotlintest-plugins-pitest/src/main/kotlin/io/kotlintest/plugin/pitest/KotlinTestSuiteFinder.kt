package io.kotlintest.plugin.pitest

import org.pitest.testapi.TestSuiteFinder

class KotlinTestSuiteFinder : TestSuiteFinder {
  override fun apply(t: Class<*>?): MutableList<Class<*>> = mutableListOf()
}
