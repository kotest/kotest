package io.kotest.assertions.print

/**
 * Returns an instance of [Print] suitable for data classes.
 *
 * On platforms without reflective capabilities, the instance
 * returned will be a basic toString implementation.
 */
expect fun <A : Any> dataClassPrint(): Print<A>
