@file:OptIn(ExperimentalJsExport::class)

package io.kotest.engine

@JsExport
fun kotestEntry() {
   // this is a placeholder, which the compiler plugin will overwrite
   js("runKotest()")
}
