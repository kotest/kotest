package io.kotest.core.runtime

external fun describe(name: String, fn: () -> Unit)
external fun xdescribe(name: String, fn: () -> Unit)
external fun it(name: String, fn: (dynamic) -> Any?)
external fun xit(name: String, fn: () -> Any?)
