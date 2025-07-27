package io.kotest.engine.spec

import io.kotest.core.spec.SpecRef

internal actual val FailureFirstSorter: SpecSorter = NoopSpecSorter
internal actual val AnnotatedSpecSorter: SpecSorter = NoopSpecSorter

internal object NoopSpecSorter : SpecSorter {
   override fun sort(specs: List<SpecRef>): List<SpecRef> = specs
}
