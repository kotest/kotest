package io.kotest.engine.test.path

import io.kotest.common.TestPath
import kotlin.reflect.KClass

/**
 * A builder for constructing a [TestPath] instance.
 *
 * The path is constructed by appending an optional spec name and then the test names using the provided delimiters.
 */
internal data class TestPathBuilder(
   private val spec: KClass<*>? = null,
   private val tests: List<String>,
) {

   companion object {

      const val SPEC_DELIMITER = "/"
      const val TEST_DELIMITER = " -- "

      /**
       * Returns a new [TestPathBuilder] instance with the default delimiters.
       */
      fun builder(): TestPathBuilder = TestPathBuilder(null, emptyList())

      fun parse(path: String): TestPath {
         val parts = path.split(TEST_DELIMITER)
         return parts.fold(builder()) { acc, op -> acc.withTest(op) }.build()
      }
   }

   fun withSpec(spec: KClass<*>): TestPathBuilder {
      require(tests.isEmpty()) { "Must add spec before tests" }
      return this.copy(spec = spec)
   }

   fun withTest(testName: String): TestPathBuilder {
      return this.copy(tests = tests + testName.trim().lines().joinToString(" ") { it.trim() })
   }

   fun build(): TestPath {
      require(tests.isNotEmpty())
      val specPath = spec?.simpleName
      val testPath = tests.joinToString(TEST_DELIMITER)
      val path = listOfNotNull(specPath, testPath).joinToString(SPEC_DELIMITER)
      return TestPath(path.trim())
   }
}
