package io.kotest.assertions.json

import io.kotest.assertions.json.schema.JsonSchemaElement
import io.kotest.common.ExperimentalKotest
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.decodeStructure

@ExperimentalKotest
class ContainsSpec(
   val schema: JsonSchemaElement,
   val minContains: Int = 0,
   val maxContains: Int = Int.MAX_VALUE,
)

@ExperimentalKotest
object ContainsSpecSerializer : KSerializer<ContainsSpec> {
   override val descriptor: SerialDescriptor = buildClassSerialDescriptor("ContainsSpec") {
      element<String>("type")
      element<String>("minContains")
      element<String>("maxContains")
   }

   override fun deserialize(decoder: Decoder): ContainsSpec =
      decoder.decodeStructure(descriptor) {
         val minContains =
            runCatching { decodeIntElement(descriptor, 1) }.getOrDefault(0)
         val maxContains =
            runCatching { decodeIntElement(descriptor, 2) }.getOrDefault(Int.MAX_VALUE)
         ContainsSpec(JsonSchemaElement.serializer().deserialize(decoder), minContains, maxContains)
      }


   override fun serialize(encoder: Encoder, value: ContainsSpec) {
      TODO("Not yet implemented")
   }
}
