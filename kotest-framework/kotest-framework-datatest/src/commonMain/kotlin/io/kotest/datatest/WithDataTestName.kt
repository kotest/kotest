package io.kotest.datatest

/**
 *  In case you want to customise the test name generation for a given type when used in data test,
 *  you can implement [WithDataTestName] and implement [dataTestName] method to customise the test
 *  case name.
 *
 *  Note:
 *  1) If you want to use the toString method for creating test name for a type use [IsStableType] annotation on that type.
 *  2) When a type implements [WithDataTestName] and is also annotated with [IsStableType], preference will be given to [IsStableType]
 * */
interface WithDataTestName {
   fun dataTestName(): String
}
