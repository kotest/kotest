package io.kotest.core

import io.kotest.core.plan.Source

/**
 * Returns a [Source] for the current execution point.
 *
 * Will return null if executing on a platform where source cannot be determined, or if source
 * evaluation is disabled at runtime.
 */
expect fun source(): Source?
