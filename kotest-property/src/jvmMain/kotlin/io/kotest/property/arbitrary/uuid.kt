package io.kotest.property.arbitrary

import io.kotest.property.Arb
import java.util.*

enum class UUIDVersion(
   val uuidRegex: Regex
) {
   ANY("[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[089ab][0-9a-f]{3}-[0-9a-f]{12}".toRegex(RegexOption.IGNORE_CASE)),
   V1("[0-9a-f]{8}-[0-9a-f]{4}-[1][0-9a-f]{3}-[089ab][0-9a-f]{3}-[0-9a-f]{12}".toRegex(RegexOption.IGNORE_CASE)),
   V2("[0-9a-f]{8}-[0-9a-f]{4}-[2][0-9a-f]{3}-[089ab][0-9a-f]{3}-[0-9a-f]{12}".toRegex(RegexOption.IGNORE_CASE)),
   V3("[0-9a-f]{8}-[0-9a-f]{4}-[3][0-9a-f]{3}-[089ab][0-9a-f]{3}-[0-9a-f]{12}".toRegex(RegexOption.IGNORE_CASE)),
   V4("[0-9a-f]{8}-[0-9a-f]{4}-[4][0-9a-f]{3}-[089ab][0-9a-f]{3}-[0-9a-f]{12}".toRegex(RegexOption.IGNORE_CASE)),
   V5("[0-9a-f]{8}-[0-9a-f]{4}-[5][0-9a-f]{3}-[089ab][0-9a-f]{3}-[0-9a-f]{12}".toRegex(RegexOption.IGNORE_CASE));
}

fun Arb.Companion.uuid(
   uuidVersion: UUIDVersion = UUIDVersion.V4,
   allowNilValue: Boolean = true
): Arb<UUID> {
   val edgeCases = if (allowNilValue)
      listOf(UUID.fromString("00000000-0000-0000-0000-000000000000"))
   else emptyList()

   val arb = when (uuidVersion) {
      UUIDVersion.ANY -> arbUuidAny
      UUIDVersion.V1 -> arbV1
      UUIDVersion.V2 -> arbV2
      UUIDVersion.V3 -> arbV3
      UUIDVersion.V4 -> arbV4
      UUIDVersion.V5 -> arbV5
   }

   return arbitrary(edgeCases) {
      val value = arb.next(it)
      UUID.fromString(value)
   }
}

// UUID regex patterns are predictable.
// The reason we put these Arbs here is so that RgxGen instances can be reused for better performance
private val arbUuidAny = Arb.stringPattern(UUIDVersion.ANY.uuidRegex.pattern)
private val arbV1 = Arb.stringPattern(UUIDVersion.V1.uuidRegex.pattern)
private val arbV2 = Arb.stringPattern(UUIDVersion.V2.uuidRegex.pattern)
private val arbV3 = Arb.stringPattern(UUIDVersion.V3.uuidRegex.pattern)
private val arbV4 = Arb.stringPattern(UUIDVersion.V4.uuidRegex.pattern)
private val arbV5 = Arb.stringPattern(UUIDVersion.V5.uuidRegex.pattern)
