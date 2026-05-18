package io.kotest.property.arbitrary

import io.kotest.property.Arb

/**
 * Platform-specific UUID type. Aliases `java.util.UUID` on JVM and
 * `kotlin.uuid.Uuid` on every other Kotest target. Shared code can only rely
 * on members common to both (e.g. [toString], [equals], [hashCode]).
 */
expect class PlatformUuid

internal expect fun String.toPlatformUuid(): PlatformUuid

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
): Arb<PlatformUuid> {
   val edgeCases = if (allowNilValue)
      listOf("00000000-0000-0000-0000-000000000000".toPlatformUuid())
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
      value.toPlatformUuid()
   }
}

// UUID regex patterns are predictable.
// The reason we put these Arbs here is so that RgxGen instances can be reused for better performance
private val arbUuidAny = Arb.pattern(UUIDVersion.ANY.uuidRegex.pattern)
private val arbV1 = Arb.pattern(UUIDVersion.V1.uuidRegex.pattern)
private val arbV2 = Arb.pattern(UUIDVersion.V2.uuidRegex.pattern)
private val arbV3 = Arb.pattern(UUIDVersion.V3.uuidRegex.pattern)
private val arbV4 = Arb.pattern(UUIDVersion.V4.uuidRegex.pattern)
private val arbV5 = Arb.pattern(UUIDVersion.V5.uuidRegex.pattern)
