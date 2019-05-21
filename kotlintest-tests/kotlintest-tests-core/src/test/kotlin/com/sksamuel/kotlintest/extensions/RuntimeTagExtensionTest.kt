package com.sksamuel.kotlintest.extensions

import io.kotlintest.Spec
import io.kotlintest.Tag
import io.kotlintest.extensions.RuntimeTagExtension
import io.kotlintest.fail
import io.kotlintest.specs.StringSpec

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