package com.sksamuel.kotest.extensions

import io.kotest.core.Tag
import io.kotest.assertions.fail
import io.kotest.core.spec.SpecConfiguration
import io.kotest.extensions.RuntimeTagExtension
import io.kotest.specs.StringSpec

class RuntimeTagExtensionTest : StringSpec() {

    object MyRuntimeExcludedTag : Tag()

    init {

        "Test marked with a runtime excluded tag".config(tags = setOf(MyRuntimeExcludedTag)) {
          fail("Should never execute (configured to be excluded in beforeSpec)")
        }
    }

    override fun beforeSpec(spec: SpecConfiguration) {
        RuntimeTagExtension.excluded += MyRuntimeExcludedTag
    }
}
