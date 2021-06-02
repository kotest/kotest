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

   return arbitrary(edgeCases) {
      val value = Arb.stringPattern(uuidVersion.uuidRegex.pattern).next(it)
      UUID.fromString(value)
   }
}
