package io.kotest.engine.reports

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import nl.adaptivity.xmlutil.serialization.XmlSerialName
import nl.adaptivity.xmlutil.serialization.XmlValue
import kotlin.math.round

/**
 * Models the JUnit-style XML report output.
 *
 * This implementation handles nesting, whereas the JUnit implementation will only output for leaf tests.
 *
 * Note: This site has a good set of schemas and examples of the format: https://github.com/testmoapp/junitxml
 * Another useful, but less details is https://llg.cubic.org/docs/junit
 */
@Serializable
@XmlSerialName("testsuite", "", "")
data class TestSuite(
   val name: String,
   val tests: Int,
   val failures: Int,
   val errors: Int,
   // Total number of skipped tests in this file (what we call ignored)
   val skipped: Int,
   // Date and time of when the test run was executed (in ISO 8601 format)
   val timestamp: String,
   val hostname: String,
   // Aggregated time of all tests in this file in seconds
   @Serializable(with = TimeSerializer::class) val time: Double,
   val cases: List<TestCaseElement>
)

@Serializable
@XmlSerialName("testcase", "", "")
data class TestCaseElement(
   val name: String,
   val classname: String, // we use the spec FQN
   @Serializable(with = TimeSerializer::class) val time: Double,
   val failure: FailureElement? = null,
   val error: ErrorElement? = null,
   val skipped: SkippedElement? = null, // must be a nested element if present
)

@Serializable
@XmlSerialName("failure", "", "")
data class FailureElement(
   val message: String,
   val type: String,
   @XmlValue val stack: String?,
)

@Serializable
@XmlSerialName("error", "", "")
data class ErrorElement(
   val message: String,
   val type: String,
   @XmlValue val stack: String?,
)

@Serializable
@XmlSerialName("skipped", "", "")
class SkippedElement(val message: String?)

object TimeSerializer : KSerializer<Double> {
   override val descriptor = PrimitiveSerialDescriptor("JunitXMLDouble", PrimitiveKind.STRING)

   override fun serialize(encoder: Encoder, value: Double) {
      encoder.encodeString(value.toThreeDecimals())
   }

   override fun deserialize(decoder: Decoder): Double {
      return decoder.decodeString().toDouble()
   }

   private fun Double.toThreeDecimals(): String {
      val multiplied = this * 1000
      val rounded = round(multiplied) / 1000
      return rounded.toString()
   }
}
