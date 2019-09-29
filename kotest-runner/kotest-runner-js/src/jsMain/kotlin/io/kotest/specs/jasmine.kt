package io.kotest.specs

external fun describe(name: String, fn: () -> Unit)
external fun xdescribe(name: String, fn: () -> Unit)
external fun it(name: String, fn: () -> Any?)
external fun xit(name: String, fn: () -> Any?)
