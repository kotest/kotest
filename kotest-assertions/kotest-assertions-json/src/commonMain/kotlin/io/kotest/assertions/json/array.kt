import io.kotest.assertions.json.pretty
import io.kotest.assertions.json.toJsonNode
import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.should
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.decodeFromJsonElement

fun String.shouldBeJsonArray(path: String = "$") = pretty.parseToJsonElement(this) should beJsonArray(path)
fun JsonElement.shouldBeJsonArray(path: String = "$") = this should beJsonArray(path)

fun String.shouldBeJsonObject(path: String = "$") = pretty.parseToJsonElement(this) should beJsonObject(path)
fun JsonElement.shouldBeJsonObject(path: String = "$") = this should beJsonObject(path)

fun beJsonArray(path: String = "$") = object : Matcher<JsonElement> {
   override fun test(value: JsonElement): MatcherResult {
      val extracted = runCatching { value.extrakt(path) }
      if (extracted.isFailure) return MatcherResult(false, { "Failed to parse string as JSON: ${extracted.exceptionOrNull()!!.message}" }, { "" })
      return MatcherResult(
         extracted.getOrThrow() is JsonArray,
         { "Expected ${extracted.getOrThrow().toJsonNode().type()} to be a JSON array" },
         { "Expected element not to be a JSON array, but it was." }
      )
   }
}

fun beJsonObject(path: String = "$") = object : Matcher<JsonElement> {
   override fun test(value: JsonElement): MatcherResult {
      val extracted = runCatching { value.extrakt(path) }
      if (extracted.isFailure) return MatcherResult(false, { "Failed to parse string as JSON" }, { "" })
      return MatcherResult(
         extracted.getOrThrow() is JsonObject,
         { "Was not a JSON object" },
         { "Expected not to be JSON object, but it is " }
      )
   }
}

private sealed interface JsonExtraction
data class ByIndexing(val pos: Int) : JsonExtraction
data class ByProperty(val name: String) : JsonExtraction

// $[0]
// $.prop
// $

abstract class ExtraktException(message: String, cause: Throwable? = null) : RuntimeException(message, cause)

class MissingFieldException(message: String) : ExtraktException(message)
class IndexOutOfRangeException : ExtraktException("Index out of range")
class MismatchingTypeException(expectedType: String) :
   ExtraktException("Failed to extract bla bla, expected $expectedType but was bla?")

private fun String.extrakt(path: String): JsonElement = pretty.parseToJsonElement(this).extrakt(path)
private fun JsonElement.extrakt(path: String): JsonElement {
   val actions = parseActions(path)
   var p = this

   for (action in actions) {
      p = when (action) {
         is ByIndexing -> (p as? JsonArray ?: throw MismatchingTypeException("JsonArray"))[action.pos]
         is ByProperty -> (p as? JsonObject ?: throw MismatchingTypeException("JsonObject"))[action.name]
            ?: throw MissingFieldException("No field by name '${action.name}' in $p")
         else -> error("No clue why this is required. Sealed interface should have covered all branches above, exhasutively..")
      }
   }

   return p
}

private fun parseActions(path: String): List<JsonExtraction> {
   var i = if (path.startsWith("$")) 1 else 0
   val jsonExtractions = mutableListOf<JsonExtraction>()
   fun nextToken() = path.indexOfAny(charArrayOf('.', '[', ']'), i + 1)
      .let {
         if (it == -1) path.length
         else it
      }
      .also { i = it }

   while (i < path.length - 1) {
      when (path[i]) {
         ']' -> i++
         '.' -> jsonExtractions.add(ByProperty(path.substring(i + 1, nextToken())))
         '[' -> jsonExtractions.add(ByIndexing(path.substring(i + 1, nextToken()).toInt()))
         else -> error("Invalid input $path. (i = $i, actions = $jsonExtractions)")
      }
   }

   println("Returning actions: $jsonExtractions")

   return jsonExtractions
}
