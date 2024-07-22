package datatest

/**
 *  In case you want to customise the test name generation for a given type when used in data testing,
 *  you can implement the [WithDataTestName] interface and return the test name to be used.
 *
 *  Note:
 *  1) If you want to use the toString method for creating test name for a type use [IsStableType] annotation on that type.
 *  2) When a type implements [WithDataTestName] and is also annotated with [IsStableType], preference will be given to [IsStableType]
 * */
interface WithDataTestName {
   fun dataTestName(): String
}
