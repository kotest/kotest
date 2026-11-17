package io.kotest.assertions.yaml

import com.charleskorn.kaml.Yaml
import com.charleskorn.kaml.YamlNode
import io.kotest.assertions.print.print
import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.MatcherResultBuilder
import io.kotest.matchers.should
import io.kotest.matchers.shouldNot

private val yaml = Yaml.default

fun String.shouldBeValidYaml(): String {
   this should beValidYaml()
   return this
}

fun String.shouldNotBeValidYaml(): String {
   this shouldNot beValidYaml()
   return this
}

fun beValidYaml() = object : Matcher<String?> {
   override fun test(value: String?): MatcherResult {
      return try {
         value?.let(yaml::parseToYamlNode)
         MatcherResult(
            true,
            { "expected: actual YAML to be valid YAML: $value" },
            { "expected: actual YAML to be invalid YAML: $value" }
         )
      } catch (ex: Exception) {
         MatcherResult(
            false,
            { "expected: actual YAML to be valid YAML Reason: ${ex.message} Actual: $value" },
            { "expected: actual YAML to be invalid YAML: $value" }
         )
      }
   }
}

infix fun String.shouldEqualYaml(expected: String): String {
   this should equalYaml(expected)
   return this
}

/**
 * Verifies that the receiver matches the yaml string returned by the given block [configureAndProvideExpected].
 * The function allows configuration of [CompareYamlOptions] before returning the expected yaml.
 */
infix fun String.shouldEqualYaml(configureAndProvideExpected: CompareYamlOptions.() -> String): String {
   val options = CompareYamlOptions()
   val expected = options.configureAndProvideExpected()
   this should equalYaml(expected, options)
   return this
}

infix fun String.shouldNotEqualYaml(expected: String): String {
   this shouldNot equalYaml(expected)
   return this
}

/**
 * Configures [CompareYamlOptions] with the given block, which should also return the expected value
 */
infix fun String.shouldNotEqualYaml(configureAndProvideExpected: CompareYamlOptions.() -> String): String {
   val options = CompareYamlOptions()
   val expected = options.configureAndProvideExpected()
   this shouldNot equalYaml(expected, options)
   return this
}

/**
 * Returns a [io.kotest.matchers.Matcher] that verifies that two YAML strings are equal.
 *
 * This matcher will consider two YAML strings matched if they have the same key-values pairs.
 * The [CompareYamlOptions] parameter can be used to configure the matcher behavior.
 */
fun equalYaml(
   expected: String,
   options: CompareYamlOptions,
): Matcher<String?> =
   Matcher { actual ->
      if (actual == null) {
         MatcherResult(
            expected == "null",
            { "Expected value to be equal to YAML '$expected', but was: null" },
            { "Expected value to be not equal to YAML '$expected', but was: null" }
         )
      } else {
         val actualYaml = try {
            yaml.parseToYamlNode(actual)
         } catch (ex: Exception) {
            return@Matcher MatcherResult(
               false,
               { "expected: actual YAML to be valid YAML Reason: ${ex.message} Actual: $actual" },
               { "expected: actual YAML to be invalid YAML: $actual" }
            )
         }
         val expectedYaml = try {
            yaml.parseToYamlNode(expected)
         } catch (ex: Exception) {
            return@Matcher MatcherResultBuilder.create(false)
               .withFailureMessage { "expected: actual YAML to be valid YAML Reason: ${ex.message} Actual: $actual" }
               .withNegatedFailureMessage { "expected: expected YAML to be invalid YAML: $expected" }
               .build()
         }
         equalYamlNode(expectedYaml, options).test(actualYaml)
      }
   }

/**
 * Returns a [io.kotest.matchers.Matcher] that verifies that two YAML strings are equal,
 * using the default [CompareYamlOptions].
 *
 * This matcher will consider two YAML strings matched if they have the same key-values pairs.
 */
fun equalYaml(
   expected: String,
): Matcher<String?> = equalYaml(expected, CompareYamlOptions())

private fun equalYamlNode(
   expected: YamlNode,
   options: CompareYamlOptions,
): Matcher<YamlNode> =
   Matcher { value ->
      val errors = compare(
         path = listOf(),
         expectedNode = expected,
         actualNode = value,
         options,
      )
      MatcherResultBuilder.create(errors.isEmpty())
         .withValues(
            expected = { expected.contentToString().print() },
            actual = { value.contentToString().print() })
         .withFailureMessage { "${errors.joinToString("\n") { it.asString() }}\n" }
         .withNegatedFailureMessage { "Expected values to not match" }
         .build()
   }
