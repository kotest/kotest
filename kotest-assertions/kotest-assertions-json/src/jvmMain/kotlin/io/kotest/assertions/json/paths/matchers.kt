package io.kotest.assertions.json.paths

import io.kotest.assertions.json.CompareJsonOptions
import io.kotest.assertions.json.shouldEqualJson
import io.kotest.assertions.json.shouldNotEqualJson
import io.kotest.assertions.json.shouldEqualSpecifiedJson
import io.kotest.assertions.json.shouldEqualSpecifiedJsonIgnoringOrder
import io.kotest.assertions.json.shouldNotEqualSpecifiedJson
import java.nio.file.Path
import kotlin.io.path.readText

infix fun Path.shouldEqualJson(expected: String): Path {
   this.readText().shouldEqualJson(expected)
   return this
}

infix fun Path.shouldEqualJson(configureAndProvideExpected: CompareJsonOptions.() -> String): Path {
   this.readText().shouldEqualJson(configureAndProvideExpected)
   return this
}

infix fun Path.shouldNotEqualJson(expected: String): Path {
   this.readText().shouldNotEqualJson(expected)
   return this
}

infix fun Path.shouldNotEqualJson(configureAndProvideExpected: CompareJsonOptions.() -> String): Path {
   this.readText().shouldNotEqualJson(configureAndProvideExpected)
   return this
}

infix fun Path.shouldEqualSpecifiedJson(expected: String) {
   this.readText().shouldEqualSpecifiedJson(expected)
}

infix fun Path.shouldEqualSpecifiedJsonIgnoringOrder(expected: String) {
   this.readText().shouldEqualSpecifiedJsonIgnoringOrder(expected)
}

infix fun Path.shouldNotEqualSpecifiedJson(expected: String) {
   this.readText().shouldNotEqualSpecifiedJson(expected)
}
