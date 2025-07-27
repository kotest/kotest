package io.kotest.core.descriptors

import io.kotest.common.ExperimentalKotest
import kotlin.coroutines.AbstractCoroutineContextElement
import kotlin.coroutines.CoroutineContext

data class DescriptorPath(val value: String)

@ExperimentalKotest
class DescriptorPathContextElement(
   val path: DescriptorPath,
) : AbstractCoroutineContextElement(Key) {
   companion object Key : CoroutineContext.Key<DescriptorPathContextElement>
}
