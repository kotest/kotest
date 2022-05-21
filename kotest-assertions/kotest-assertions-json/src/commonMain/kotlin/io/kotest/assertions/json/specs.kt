import io.kotest.assertions.json.schema.JsonSchemaElement
import kotlinx.serialization.Serializable

@Serializable
data class ContainsSpec(val schema: JsonSchemaElement)
