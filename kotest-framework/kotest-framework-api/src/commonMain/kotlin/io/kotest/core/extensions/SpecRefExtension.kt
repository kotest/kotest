package io.kotest.core.extensions

import io.kotest.common.ExperimentalKotest
import io.kotest.core.spec.SpecRef

/**
 * An [Extension] point that allows intercepting execution of [SpecRef]s
 * once they have been selected for execution but before any instantiation
 * has occured.
 *
 * Extensions of this type can elect to continue processing the spec by
 * invoking the process function. By not invoking this function, the spec
 * can be skipped.
 *
 * Any coroutine context changes are propagated downstream.
 */
@ExperimentalKotest
interface SpecRefExtension {
   suspend fun interceptRef(ref: SpecRef, process: suspend () -> Unit)
}
