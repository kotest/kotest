package io.kotest.property.stateful

interface Action<STATE> {

   /**
    * Implement to check any preconditions before the action is applied.
    *
    * For example, if the action is supposed to get an element from a list,
    * you could check here that the list is not empty.
    */
   fun precondition(state: STATE): Boolean = true

   fun apply(state: STATE): STATE

   /**
    * Implement to check any postconditions after the action has been applied.
    *
    * For example, if the action is supposed to insert a record into a database,
    * you could check here that the record actually exists in the database.
    */
   fun postcondition(state: STATE) {
   }
}
