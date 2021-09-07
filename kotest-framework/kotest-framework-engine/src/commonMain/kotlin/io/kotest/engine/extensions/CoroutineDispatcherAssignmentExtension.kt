package io.kotest.engine.extensions

import io.kotest.common.ExperimentalKotest
import io.kotest.engine.concurrency.CoroutineDispatcherAssignment

/**
 * An extension point that can be used to return a [CoroutineDispatcherAssignment] which
 * is then used to switch the coroutine dispatchers context specs and tests.
 *
 * If this is not implemented then the Kotest engine will use a default implementation.
 * If two or more instances of this extension are used, then one will be arbitrarily picked.
 */
@ExperimentalKotest
interface CoroutineDispatcherAssignmentExtension {
   fun provider(): CoroutineDispatcherAssignment
}
