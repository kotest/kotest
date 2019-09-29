package com.sksamuel.kotest.extensions

import io.kotest.Spec
import io.kotest.Tag
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

    override fun beforeSpec(spec: Spec) {
        RuntimeTagExtension.excluded += MyRuntimeExcludedTag
    }
}
