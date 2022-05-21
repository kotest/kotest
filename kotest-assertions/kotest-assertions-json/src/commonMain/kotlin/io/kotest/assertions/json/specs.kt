import io.kotest.assertions.json.schema.JsonSchema
import io.kotest.assertions.json.schema.JsonSchemaElement
import kotlinx.serialization.Serializable

@kotlinx.serialization.Serializable
data class ContainsSpec(val schema: JsonSchemaElement)
