package io.kotest.assertions.json

//fun toJsonNode(any: Any?): JsonNode {
//   return when {
//      any == null -> JsonNode.NullNode
//      js("Array").isArray(any).unsafeCast<Boolean>() ->
//         JsonNode.ArrayNode(any.unsafeCast<Array<Any>>().map { toJsonNode(it) })
//      jsTypeOf(any) == "string" -> JsonNode.StringNode(any.toString())
//      jsTypeOf(any) == "boolean" -> JsonNode.BooleanNode(any.unsafeCast<Boolean>())
//      jsTypeOf(any) == "number" -> {
//         val maybeLong = any.toString().toLongOrNull()
//         if (maybeLong == null) JsonNode.DoubleNode(any.unsafeCast<Double>()) else JsonNode.LongNode(maybeLong)
//      }
//      jsTypeOf(any) == "object" -> {
//         val map = js("Object").entries(any).unsafeCast<Array<Array<dynamic>>>().map {
//            it[0].unsafeCast<String>() to toJsonNode(it[1].unsafeCast<Any>())
//         }.toMap()
//         JsonNode.ObjectNode(map)
//      }
//      else -> error("Unhandled js type $any")
//   }
//}
