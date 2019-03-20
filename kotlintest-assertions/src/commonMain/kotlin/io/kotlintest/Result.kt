package io.kotlintest

/**
 * The [Result] class contains the result of an evaluation of a matcher.
 *
 * @param passed set to true if the matcher indicated this was a valid
 * value and false if the matcher indicated an invalid value
 *
 * @param failureMessage a message indicating why the evaluation failed
 * for when this matcher is used in the positive sense. For example,
 * if a size matcher was used like `mylist should haveSize(5)` then
 * an appropriate error message would be "list should be size 5".
 *
 * @param negatedFailureMessage a message indicating why the evaluation
 * failed for when this matcher is used in the negative sense. For example,
 * if a size matcher was used like `mylist shouldNot haveSize(5)` then
 * an appropriate negated failure would be "List should not have size 5".
 */
data class Result(val passed: Boolean, val failureMessage: String, val negatedFailureMessage: String)