package io.kotest.extensions.pitest

import org.pitest.testapi.TestSuiteFinder

class KotestSuiteFinder : TestSuiteFinder {
   override fun apply(t: Class<*>?): MutableList<Class<*>> = mutableListOf()
}
