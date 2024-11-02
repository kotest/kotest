import com.charleskorn.kaml.Yaml
import com.charleskorn.kaml.YamlNode
import io.kotest.matchers.ComparableMatcherResult
import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
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
            { "expected: actual YAML to be valid YAML: $value" },
            { "expected: actual YAML to be invalid YAML: $value" }
         )
      }
   }
}

infix fun String.shouldEqualYaml(expected: String): String {
   this should equalYaml(expected)
   return this
}

infix fun String.shouldNotEqualYaml(expected: String): String {
   this shouldNot equalYaml(expected)
   return this
}

fun equalYaml(
   expected: String,
): Matcher<String?> =
   Matcher { actual ->
      if (actual == null) {
         MatcherResult(
            expected == "null",
            { "Expected value to be equal to YAML '$expected', but was: null" },
            { "Expected value to be not equal to YAML '$expected', but was: null" }
         )
      } else {
         val (expectedTree, actualTree) = parse(expected, actual)
         equalYamlNode(expectedTree).test(actualTree)
      }
   }

private fun equalYamlNode(
   expected: YamlNode,
): Matcher<YamlNode> =
   Matcher { value ->
      val error = expected.equivalentContentTo( value)

      ComparableMatcherResult(
         error,
         { "$error\n" },
         { "Expected values to not match" },
         value.contentToString(),
         expected.contentToString(),
      )
   }

internal fun parse(expected: String, actual: String): Pair<YamlNode, YamlNode> {
   val enode = yaml.parseToYamlNode(expected)
   val anode = yaml.parseToYamlNode(actual)
   return Pair(enode, anode)
}
