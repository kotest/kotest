package io.kotest.assertions.json

import java.io.File
import java.nio.file.Path
import kotlin.io.path.readText

infix fun Path.shouldEqualJson(expected: String): Path {
   this.readText().shouldEqualJson(expected)
   return this
}

infix fun File.shouldEqualJson(expected: String): File {
   this.readText().shouldEqualJson(expected)
   return this
}

infix fun Path.shouldEqualJson(configureAndProvideExpected: CompareJsonOptions.() -> String): Path {
   this.readText().shouldEqualJson(configureAndProvideExpected)
   return this
}

infix fun File.shouldEqualJson(configureAndProvideExpected: CompareJsonOptions.() -> String): File {
   this.readText().shouldEqualJson(configureAndProvideExpected)
   return this
}

infix fun Path.shouldNotEqualJson(expected: String): Path {
   this.readText().shouldNotEqualJson(expected)
   return this
}

infix fun File.shouldNotEqualJson(expected: String): File {
   this.readText().shouldNotEqualJson(expected)
   return this
}

infix fun Path.shouldNotEqualJson(configureAndProvideExpected: CompareJsonOptions.() -> String): Path {
   this.readText().shouldNotEqualJson(configureAndProvideExpected)
   return this
}

infix fun File.shouldNotEqualJson(configureAndProvideExpected: CompareJsonOptions.() -> String): File {
   this.readText().shouldNotEqualJson(configureAndProvideExpected)
   return this
}

infix fun Path.shouldEqualSpecifiedJson(expected: String) = this.readText().shouldEqualSpecifiedJson(expected)
infix fun File.shouldEqualSpecifiedJson(expected: String) = this.readText().shouldEqualSpecifiedJson(expected)

infix fun Path.shouldEqualSpecifiedJsonIgnoringOrder(expected: String) = this.readText().shouldEqualSpecifiedJsonIgnoringOrder(expected)
infix fun File.shouldEqualSpecifiedJsonIgnoringOrder(expected: String) = this.readText().shouldEqualSpecifiedJsonIgnoringOrder(expected)

infix fun Path.shouldNotEqualSpecifiedJson(expected: String) = this.readText().shouldNotEqualSpecifiedJson(expected)
infix fun File.shouldNotEqualSpecifiedJson(expected: String) = this.readText().shouldNotEqualSpecifiedJson(expected)
