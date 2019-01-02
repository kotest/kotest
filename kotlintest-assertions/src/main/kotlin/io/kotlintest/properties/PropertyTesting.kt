package io.kotlintest.properties

object PropertyTesting {

  var shouldLogGeneratedValues = readSystemProperty()

  private fun readSystemProperty(): Boolean {
    return System.getProperty("kotlintest.property.testing.genlog")?.toBoolean() ?: false
  }
}