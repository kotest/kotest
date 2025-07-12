package io.kotest.property.stateful

interface Action<STATE> {
   fun precondition(state: STATE): Boolean = true

   fun apply(state: STATE): STATE

   fun postcondition(state: STATE) {

   }
}
