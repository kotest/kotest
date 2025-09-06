package io.kotest.core.extensions

import io.kotest.engine.names.DisplayNameFormatter

/**
 * An extension point that allows users to inject their own [DisplayNameFormatter].
 *
 * Note: If multiple [DisplayNameFormatterExtension]s are registered, then one
 * will be picked arbitrarily.
 */
interface DisplayNameFormatterExtension : Extension {
   fun formatter(): DisplayNameFormatter
}
