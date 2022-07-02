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
class ContainsSpec(val schema: JsonSchemaElement)

@ExperimentalKotest
object ContainsSpecSerializer : KSerializer<ContainsSpec> {
   override val descriptor: SerialDescriptor = buildClassSerialDescriptor("ContainsSpec") {
      element<String>("type")
   }

   override fun deserialize(decoder: Decoder): ContainsSpec =
      decoder.decodeStructure(descriptor) {
         ContainsSpec(JsonSchemaElement.serializer().deserialize(decoder))
      }


   override fun serialize(encoder: Encoder, value: ContainsSpec) {
      TODO("Not yet implemented")
   }
}
