package io.kotest.assertions.json.file

import io.kotest.assertions.json.CompareJsonOptions
import io.kotest.assertions.json.shouldEqualJson
import io.kotest.assertions.json.shouldNotEqualJson
import io.kotest.assertions.json.shouldEqualSpecifiedJson
import io.kotest.assertions.json.shouldEqualSpecifiedJsonIgnoringOrder
import io.kotest.assertions.json.shouldNotEqualSpecifiedJson
import java.io.File

infix fun File.shouldEqualJson(expected: String): File {
   this.readText().shouldEqualJson(expected)
   return this
}

infix fun File.shouldEqualJson(configureAndProvideExpected: CompareJsonOptions.() -> String): File {
   this.readText().shouldEqualJson(configureAndProvideExpected)
   return this
}

infix fun File.shouldNotEqualJson(expected: String): File {
   this.readText().shouldNotEqualJson(expected)
   return this
}

infix fun File.shouldNotEqualJson(configureAndProvideExpected: CompareJsonOptions.() -> String): File {
   this.readText().shouldNotEqualJson(configureAndProvideExpected)
   return this
}

infix fun File.shouldEqualSpecifiedJson(expected: String) {
   this.readText().shouldEqualSpecifiedJson(expected)
}

infix fun File.shouldEqualSpecifiedJsonIgnoringOrder(expected: String) {
   this.readText().shouldEqualSpecifiedJsonIgnoringOrder(expected)
}

infix fun File.shouldNotEqualSpecifiedJson(expected: String) {
   this.readText().shouldNotEqualSpecifiedJson(expected)
}
