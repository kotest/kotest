package com.sksamuel.kotest.extensions

import io.kotest.SpecInterface
import io.kotest.core.tags.Tag
import io.kotest.assertions.fail
import io.kotest.extensions.RuntimeTagExtension
import io.kotest.specs.StringSpec

class RuntimeTagExtensionTest : StringSpec() {

    object MyRuntimeExcludedTag : Tag()

    init {

        "Test marked with a runtime excluded tag".config(tags = setOf(MyRuntimeExcludedTag)) {
          fail("Should never execute (configured to be excluded in beforeSpec)")
        }
    }

    override fun beforeSpec(spec: SpecInterface) {
        RuntimeTagExtension.excluded += MyRuntimeExcludedTag
    }
}
