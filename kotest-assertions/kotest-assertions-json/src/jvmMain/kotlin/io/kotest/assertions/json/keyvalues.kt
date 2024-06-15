package io.kotest.assertions.json

import com.jayway.jsonpath.InvalidPathException
import com.jayway.jsonpath.JsonPath
import com.jayway.jsonpath.PathNotFoundException
import io.kotest.assertions.Actual
import io.kotest.assertions.Expected
import io.kotest.assertions.intellijFormatError
import io.kotest.assertions.print.print
import io.kotest.common.KotestInternal
import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.should
import io.kotest.matchers.shouldNot
import kotlin.contracts.contract

inline fun <reified T> String?.shouldContainJsonKeyValue(path: String, value: T) {
   contract {
      returns() implies (this@shouldContainJsonKeyValue != null)
   }

   this should containJsonKeyValue(path, value)
}

inline fun <reified T> String.shouldNotContainJsonKeyValue(path: String, value: T) =
   this shouldNot containJsonKeyValue(path, value)

inline fun <reified T> containJsonKeyValue(path: String, t: T) = object : Matcher<String?> {
   private fun keyIsAbsentFailure(validSubPathDescription: String) = MatcherResult(
      false,
      { "Expected given to contain json key <'$path'> but key was not found.$validSubPathDescription" },
      { "Expected given to not contain json key <'$path'> but key was found." }
   )

   private fun invalidJsonFailure(actualJson: String?) = MatcherResult(
      false,
      { "Expected a valid JSON, but was ${if (actualJson == null) "null" else "empty" }" },
      { "Expected a valid JSON, but was ${if (actualJson == null) "null" else "empty" }" },
   )

   private fun extractKey(value: String?): T? {
      return try {
         JsonPath.parse(value).read(path, T::class.java)
      } catch (e: PathNotFoundException) {
         null
      } catch (e: InvalidPathException) {
         throw AssertionError("$path is not a valid JSON path")
      }
   }

   override fun test(value: String?): MatcherResult {
      if (value.isNullOrEmpty()) return invalidJsonFailure(value)

      val sub =
         if (value.length < 50) value.trim()
         else value.substring(0, 50).trim() + "..."

      when(val actualKeyValue = extractByPath<T>(json = value, path = path)) {
          is ExtractedValue<*> -> {
             val actualValue = actualKeyValue.value
             val passed = t == actualValue
             return MatcherResult(
                passed,
                { "Value mismatch at '$path': ${intellijFormatError(Expected(t.print()), Actual(actualValue.print()))}" },
                {
                   "$sub should not contain the element $path = $t"
                }
             )
          }
          is JsonPathNotFound -> {
             val validSubPathDescription = findValidSubPath(value, path)?.let { subpath ->
                " Found shorter valid subpath: <'$subpath'>"
             } ?: ""
             return keyIsAbsentFailure(validSubPathDescription)
          }
      }
   }
}

@KotestInternal
inline fun<reified T> extractByPath(json: String?, path: String): ExtractValueOutcome {
   val parsedJson = JsonPath.parse(json)
   return try {
      val extractedValue = parsedJson.read(path, T::class.java)
      ExtractedValue(extractedValue)
   } catch (e: PathNotFoundException) {
      JsonPathNotFound
   } catch (e: InvalidPathException) {
      throw AssertionError("$path is not a valid JSON path")
   }
}

@KotestInternal
inline fun findValidSubPath(json: String?, path: String): String? {
   val parsedJson = JsonPath.parse(json)
   var subPath = path
   while(subPath.isNotEmpty() && subPath != "$") {
      try {
         parsedJson.read(subPath, Any::class.java)
         return subPath
      } catch (e: PathNotFoundException) {
         subPath = removeLastPartFromPath(subPath)
      }
   }
   return null
}

@KotestInternal
fun removeLastPartFromPath(path: String): String {
   val tokens = path.split(".")
   return tokens.take(tokens.size - 1).joinToString(".")
}

@KotestInternal
sealed interface ExtractValueOutcome

@KotestInternal
data class ExtractedValue<T>(
   val value: T
): ExtractValueOutcome

@KotestInternal
object JsonPathNotFound : ExtractValueOutcome

@KotestInternal
inline fun findValidSubPath2(json: String?, path: String): JsonSubPathSearchOutcome {
   val parsedJson = JsonPath.parse(json)
   var subPath = path
   while(subPath.isNotEmpty() && subPath != "$") {
      try {
         parsedJson.read(subPath, Any::class.java)
         return JsonSubPathFound(subPath)
      } catch (e: PathNotFoundException) {
         subPath = removeLastPartFromPath(subPath)
      }
   }
   return JsonSubPathNotFound
}


@KotestInternal
sealed interface JsonSubPathSearchOutcome

@KotestInternal
data class JsonSubPathFound(
   val subPath: String
): JsonSubPathSearchOutcome

@KotestInternal
data class JsonSubPathJsonArrayTooShort(
   val subPath: String,
   val arraySize: Int,
   val expectedIndex: Int
): JsonSubPathSearchOutcome {
   init {
      require(arraySize >= 0) { "Array size should be non-negative, was: $arraySize" }
      require(expectedIndex >= arraySize) { "Expected index should be out of bounds for array of size $arraySize, was: $expectedIndex" }
   }
}

object JsonSubPathNotFound: JsonSubPathSearchOutcome

