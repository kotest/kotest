package io.kotest.engine.test.path

import io.kotest.common.TestPath
import io.kotest.mpp.bestName
import kotlin.reflect.KClass

/**
 * A builder for constructing a [TestPath] instance.
 *
 * The path is constructed by appending an optional spec name and then the test names using the provided delimiters.
 */
internal data class TestPathBuilder(
   private val spec: KClass<*>,
   private val tests: List<String>,
) {

   companion object {

      const val SPEC_DELIMITER = "/"
      const val TEST_DELIMITER = " -- "

      /**
       * Returns a new [TestPathBuilder] instance for the given spec.
       */
      fun builder(spec: KClass<*>): TestPathBuilder = TestPathBuilder(spec, emptyList())

      /**
       * Returns a new [TestPathBuilder] instance for the given spec.
       */
      inline fun <reified T> builder(): TestPathBuilder = builder(T::class)

      fun parse(path: String): TestPath {
//         val parts = path.split(TEST_DELIMITER)
//         return parts.fold(builder()) { acc, op -> acc.withTest(op) }.build()
         return TestPath("")
      }
   }

   fun withTest(testName: String): TestPathBuilder {
      return this.copy(tests = tests + testName.trim().lines().joinToString(" ") { it.trim() })
   }

   fun build(): TestPath {
      require(tests.isNotEmpty())
      val testPath = tests.joinToString(TEST_DELIMITER)
      val path = listOf(spec.bestName(), testPath).joinToString(SPEC_DELIMITER)
      return TestPath(path.trim())
   }
}
