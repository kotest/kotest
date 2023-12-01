package com.sksamuel.kotest.matchers.collections.detailed

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.detailed.ItemsMatch
import io.kotest.matchers.collections.detailed.MatchResultType

class ItemsMatchTest: StringSpec() {
    init {
        "cannotCreateMatchWithoutLeftItemPresent" {
            shouldThrow<IllegalArgumentException> {
                ItemsMatch(true, MatchResultType.RIGHT_ELEMENT_ONLY)
            }
        }

        "cannotCreateMatchWithoutRightItemPresent" {
            shouldThrow<IllegalArgumentException> {
                ItemsMatch(true, MatchResultType.LEFT_ELEMENT_ONLY)
            }
        }
    }
}
