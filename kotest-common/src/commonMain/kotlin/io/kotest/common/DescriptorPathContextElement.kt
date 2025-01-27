package io.kotest.common

import kotlin.coroutines.AbstractCoroutineContextElement
import kotlin.coroutines.CoroutineContext

@ExperimentalKotest
class DescriptorPathContextElement(
   val path: DescriptorPath,
) : AbstractCoroutineContextElement(Key) {
   companion object Key : CoroutineContext.Key<DescriptorPathContextElement>
}
