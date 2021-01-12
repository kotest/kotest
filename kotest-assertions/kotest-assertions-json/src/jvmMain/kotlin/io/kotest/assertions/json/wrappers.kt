package io.kotest.assertions.json

import com.fasterxml.jackson.databind.JsonNode as JacksonNode
import com.fasterxml.jackson.databind.node.ArrayNode as JacksonArray
import com.fasterxml.jackson.databind.node.BooleanNode as JacksonBoolean
import com.fasterxml.jackson.databind.node.DoubleNode as JacksonDouble
import com.fasterxml.jackson.databind.node.LongNode as JacksonLong
import com.fasterxml.jackson.databind.node.ObjectNode as JacksonObject
import com.fasterxml.jackson.databind.node.TextNode as JacksonText
import com.fasterxml.jackson.databind.node.NumericNode as JacksonNumber
import com.fasterxml.jackson.databind.node.NullNode as JacksonNull

fun JacksonNode.toJsonNode(): JsonNode = when (this) {
   is JacksonText -> JsonNode.StringNode(this.textValue())
   is JacksonDouble -> JsonNode.DoubleNode(this.doubleValue())
   is JacksonLong -> JsonNode.LongNode(this.longValue())
   is JacksonBoolean -> JsonNode.BooleanNode(this.booleanValue())
   is JacksonObject -> JsonNode.ObjectNode(this.fields().asSequence().map { it.key to it.value.toJsonNode() }.toMap())
   is JacksonArray -> JsonNode.ArrayNode(this.elements().asSequence().toList().map { it.toJsonNode() })
   is JacksonNumber -> if (this.isDouble) JsonNode.DoubleNode(this.doubleValue()) else JsonNode.LongNode(this.longValue())
   is JacksonNull -> JsonNode.NullNode
   else -> error("Unsupported jackson type ${this.nodeType}")
}
