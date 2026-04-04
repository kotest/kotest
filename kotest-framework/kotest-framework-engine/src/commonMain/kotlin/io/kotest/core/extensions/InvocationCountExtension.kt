package io.kotest.core.extensions

internal interface InvocationCountExtension: Extension {
   fun getInvocationCount(): Int?
}
