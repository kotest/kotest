@file:OptIn(kotlin.uuid.ExperimentalUuidApi::class)

package com.sksamuel.kotest.property.arbitrary

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.string.shouldMatch
import io.kotest.property.Arb
import io.kotest.property.arbitrary.UUIDVersion
import io.kotest.property.arbitrary.take
import io.kotest.property.arbitrary.uuid

class UuidCommonTest : FunSpec({
   UUIDVersion.entries.forEach { version ->
      test("Arb.uuid generates parseable $version UUIDs on every target") {
         Arb.uuid(uuidVersion = version, allowNilValue = false).take(20).forEach {
            it.toString().shouldMatch(version.uuidRegex)
         }
      }
   }
})
