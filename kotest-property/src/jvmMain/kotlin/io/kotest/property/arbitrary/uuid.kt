package io.kotest.property.arbitrary

import io.kotest.properties.UUIDVersion
import io.kotest.property.RandomSource
import io.kotest.property.Sample
import java.util.*

fun Arb.Companion.uuid(
   uuidVersion: UUIDVersion = UUIDVersion.V4,
   allowNilValue: Boolean = true
): Arb<UUID> = object : Arb<UUID> {

   override fun edgecases() = if (allowNilValue)
      listOf(UUID.fromString("00000000-0000-0000-0000-000000000000"))
   else emptyList()

   override fun samples(rs: RandomSource): Sequence<Sample<UUID>> {
      return Arb.stringPattern(uuidVersion.uuidRegex.pattern).samples(rs).map { Sample(UUID.fromString(it.value)) }
   }
}
