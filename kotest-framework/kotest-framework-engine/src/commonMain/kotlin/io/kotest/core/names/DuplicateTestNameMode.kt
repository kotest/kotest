package io.kotest.core.names

/**
 * Controls what should happen when a test is registered with the same name (in the same scope)
 * as another test.
 */
enum class DuplicateTestNameMode {

   /**
    * Fails a test if it has the same name as another test.
    *
    * Tests names are compared using the full path, so a nested test `foo` inside a context `bar`, is
    * considered to be a different test from a nested `foo` inside another context `baz`
    */
   Error,

   /**
    * Outputs a warning on a duplicated test name, and renames the test without failing.
    * The name is prepended with a counter, so a duplicated test `foo` would become `(1) foo`.
    */
   Warn,

   /**
    * Silently adjusts the name of a test when a duplicate is discovered so that the name is unique.
    * The name is prepended with a counter, so a duplicated test `foo` would become `(1) foo`.
    */
   Silent
}
