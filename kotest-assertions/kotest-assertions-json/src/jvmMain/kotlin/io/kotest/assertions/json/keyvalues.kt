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
      { "Expected given to contain json key <'$path'> but key was not found. $validSubPathDescription" },
      { "Expected given to not contain json key <'$path'> but key was found." }
   )

   private fun invalidJsonFailure(actualJson: String?) = MatcherResult(
      false,
      { "Expected a valid JSON, but was ${if (actualJson == null) "null" else "empty" }" },
      { "Expected a valid JSON, but was ${if (actualJson == null) "null" else "empty" }" },
   )

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
             val subPathDescription = findValidSubPath(value, path).description()
             return keyIsAbsentFailure(subPathDescription)
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
inline fun findValidSubPath(json: String?, path: String): JsonSubPathSearchOutcome {
   val parsedJson = JsonPath.parse(json)
   var subPath = path
   while(subPath.isNotEmpty() && subPath != "$") {
      try {
         parsedJson.read(subPath, Any::class.java)
         return JsonSubPathFound(subPath)
      } catch (e: PathNotFoundException) {
         extractPossiblePathOfJsonArray(subPath)?.let { possiblePathOfJsonArray ->
            getPossibleSizeOfJsonArray(json, possiblePathOfJsonArray.pathToArray)?.let { sizeOfJsonArray ->
               return JsonSubPathJsonArrayTooShort(
                  subPath = possiblePathOfJsonArray.pathToArray,
                  arraySize = sizeOfJsonArray,
                  expectedIndex = possiblePathOfJsonArray.index
               )
            }
         }

         subPath = removeLastPartFromPath(subPath)
      }
   }
   return JsonSubPathNotFound
}

@KotestInternal
fun getPossibleSizeOfJsonArray(json: String?, path: String): Int? {
   return try {
      val parsedJson = JsonPath.parse(json)
      val possibleJsonArray = parsedJson.read(path, List::class.java)
      return possibleJsonArray.size
   } catch (ignore: Exception) {
      null
   }
}

@KotestInternal
fun extractPossiblePathOfJsonArray(path: String): JsonArrayElementRef? {
   val pathElements = path.split(".")
   val lastPathElement = pathElements.last()
   val tokens = lastPathElement.split("[")
   when {
      path.last() != ']' -> return null
      tokens.size != 2 -> return null
      else -> {
         val possibleNumber = tokens[1].dropLast(1)
         possibleNumber.toIntOrNull()?.let {
            return JsonArrayElementRef(
               pathToArray = (pathElements.dropLast(1) + tokens[0]).joinToString("."),
               index = it
            )
         }
      }
   }
   return null
}

@KotestInternal
data class JsonArrayElementRef(
   val pathToArray: String,
   val index: Int
)

@KotestInternal
sealed interface JsonSubPathSearchOutcome {
   fun description(): String
}

@KotestInternal
data class JsonSubPathFound(
   val subPath: String
): JsonSubPathSearchOutcome {
   override fun description() = "Found shorter valid subpath: <'$subPath'>."
}

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
   override fun description() = "The array at path <'$subPath'> has size $arraySize, so index $expectedIndex is out of bounds."
}

object JsonSubPathNotFound: JsonSubPathSearchOutcome {
   override fun description() = ""
}

