package io.kotest.engine.concurrency

/**
 * Specifies the order in which tests annotated with [io.kotest.core.annotation.Isolate] are executed.
 *
 * They can either run first, or last.
 */
enum class ConcurrencyOrder {
   IsolateFirst,
   IsolateLast
}
