package io.kotest.assertions.json.file

import io.kotest.assertions.json.CompareJsonOptions
import io.kotest.assertions.json.paths.shouldEqualJson
import io.kotest.assertions.json.paths.shouldEqualSpecifiedJson
import io.kotest.assertions.json.paths.shouldEqualSpecifiedJsonIgnoringOrder
import io.kotest.assertions.json.paths.shouldNotEqualJson
import io.kotest.assertions.json.paths.shouldNotEqualSpecifiedJson
import java.io.File

infix fun File.shouldEqualJson(expected: String): File {
   this.toPath().shouldEqualJson(expected)
   return this
}

infix fun File.shouldEqualJson(configureAndProvideExpected: CompareJsonOptions.() -> String): File {
   this.toPath().shouldEqualJson(configureAndProvideExpected)
   return this
}

infix fun File.shouldNotEqualJson(expected: String): File {
   this.toPath().shouldNotEqualJson(expected)
   return this
}

infix fun File.shouldNotEqualJson(configureAndProvideExpected: CompareJsonOptions.() -> String): File {
   this.toPath().shouldNotEqualJson(configureAndProvideExpected)
   return this
}

infix fun File.shouldEqualSpecifiedJson(expected: String) {
   this.toPath().shouldEqualSpecifiedJson(expected)
}

infix fun File.shouldEqualSpecifiedJsonIgnoringOrder(expected: String) {
   this.toPath().shouldEqualSpecifiedJsonIgnoringOrder(expected)
}

infix fun File.shouldNotEqualSpecifiedJson(expected: String) {
   this.toPath().shouldNotEqualSpecifiedJson(expected)
}
