package io.kotest.assertions.json.paths

import io.kotest.assertions.json.ArrayOrder
import io.kotest.assertions.json.CompareJsonOptions
import io.kotest.assertions.json.FieldComparison
import io.kotest.assertions.json.equalJson
import io.kotest.matchers.and
import io.kotest.matchers.paths.aFile
import io.kotest.matchers.paths.exist
import io.kotest.matchers.should
import io.kotest.matchers.shouldNot
import java.nio.file.Path
import kotlin.io.path.readText

infix fun Path.shouldEqualJson(expected: String): Path {
   this should (exist() and aFile() and equalJson(expected, CompareJsonOptions()).contramap { it.readText() })
   return this
}

infix fun Path.shouldEqualJson(configureAndProvideExpected: CompareJsonOptions.() -> String): Path {
   val options = CompareJsonOptions()
   val expected = configureAndProvideExpected(options)
   this should (exist() and aFile() and equalJson(expected, options).contramap { it.readText() })
   return this
}

infix fun Path.shouldNotEqualJson(expected: String): Path {
   this.readText() shouldNot equalJson(expected, CompareJsonOptions())
   return this
}

infix fun Path.shouldNotEqualJson(configureAndProvideExpected: CompareJsonOptions.() -> String): Path {
   val options = CompareJsonOptions()
   val expected = configureAndProvideExpected(options)
   this shouldNot (exist() and aFile() and equalJson(expected, options).contramap<Path> { it.readText() }.invert())
   return this
}

infix fun Path.shouldEqualSpecifiedJson(expected: String) {
   this.shouldEqualJson {
      fieldComparison = FieldComparison.Lenient
      expected
   }
}

infix fun Path.shouldEqualSpecifiedJsonIgnoringOrder(expected: String) {
   this.shouldEqualJson {
      fieldComparison = FieldComparison.Lenient
      arrayOrder = ArrayOrder.Lenient
      expected
   }
}

infix fun Path.shouldNotEqualSpecifiedJson(expected: String) {
   this.shouldNotEqualJson {
      fieldComparison = FieldComparison.Lenient
      expected
   }
}
