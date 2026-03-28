package io.kotest.core.extensions

interface InvocationCountExtension: Extension {
   fun getInvocationCount(): Int?
}
