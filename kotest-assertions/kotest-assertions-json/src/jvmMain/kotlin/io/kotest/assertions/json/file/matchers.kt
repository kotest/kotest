package io.kotest.assertions.json.file

import io.kotest.assertions.json.CompareJsonOptions
import io.kotest.assertions.json.paths.shouldEqualJson
import io.kotest.assertions.json.paths.shouldEqualSpecifiedJson
import io.kotest.assertions.json.paths.shouldEqualSpecifiedJsonIgnoringOrder
import io.kotest.assertions.json.paths.shouldNotEqualJson
import io.kotest.assertions.json.paths.shouldNotEqualSpecifiedJson
import kotlinx.serialization.json.Json
import org.intellij.lang.annotations.Language
import java.io.File

@IgnorableReturnValue
infix fun File.shouldEqualJson(@Language("json") expected: String): File {
   this.toPath().shouldEqualJson(expected)
   return this
}

@IgnorableReturnValue
infix fun File.shouldEqualJson(configureAndProvideExpected: CompareJsonOptions.() -> String): File {
   this.toPath().shouldEqualJson(configureAndProvideExpected)
   return this
}

@IgnorableReturnValue
fun File.shouldEqualJson(@Language("json") expected: String, parser: Json): File {
   this.toPath().shouldEqualJson(expected, parser)
   return this
}

@IgnorableReturnValue
infix fun File.shouldNotEqualJson(@Language("json") expected: String): File {
   this.toPath().shouldNotEqualJson(expected)
   return this
}

@IgnorableReturnValue
infix fun File.shouldNotEqualJson(configureAndProvideExpected: CompareJsonOptions.() -> String): File {
   this.toPath().shouldNotEqualJson(configureAndProvideExpected)
   return this
}

@IgnorableReturnValue
fun File.shouldNotEqualJson(@Language("json") expected: String, parser: Json): File {
   this.toPath().shouldNotEqualJson(expected, parser)
   return this
}

@IgnorableReturnValue
infix fun File.shouldEqualSpecifiedJson(@Language("json") expected: String) {
   this.toPath().shouldEqualSpecifiedJson(expected)
}

@IgnorableReturnValue
infix fun File.shouldEqualSpecifiedJsonIgnoringOrder(@Language("json") expected: String) {
   this.toPath().shouldEqualSpecifiedJsonIgnoringOrder(expected)
}

@IgnorableReturnValue
infix fun File.shouldNotEqualSpecifiedJson(@Language("json") expected: String) {
   this.toPath().shouldNotEqualSpecifiedJson(expected)
}
