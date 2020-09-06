Arrow Matchers
==========

This page lists all current matchers in the Kotest arrow matchers extension library. To use this library
 you need to add `kotest-assertions-arrow` to your build.

| Option | |
| -------- | ---- |
| `option.shouldBeSome(v)` | Asserts that the option is of type Some with value v |
| `option.shouldBeNone()` | Asserts that the option is of type None |

| Either | |
| -------- | ---- |
| `either.shouldBeRight()` | Asserts that the either is of type Right |
| `either.shouldBeRight(v)` | Asserts that the either is of type Right with specified value v |
| `either.shouldBeLeft()` | Asserts that the either is of type Left |
| `either.shouldBeLeft(v)` | Asserts that the either is of type Left with specific value v |
| `either.shouldBeLeftOfType<A>()` | Asserts that the either is of type Left with a specified type A |

| NonEmptyList | |
| -------- | ---- |
| `nel.shouldContain(e)` | Asserts that the NonEmptyList contains the given element e |
| `nel.shouldContainAll(e1,e2,...,en)` | Asserts that the NonEmptyList contains all the given elements e1,e2,...,en |
| `nel.shouldContainNull()` | Asserts that the NonEmptyList contains at least one null |
| `nel.shouldContainNoNulls()` | Asserts that the NonEmptyList contains no nulls |
| `nel.shouldContainOnlyNulls()` | Asserts that the NonEmptyList contains only nulls or is empty |
| `nel.shouldHaveDuplicates()` | Asserts that the NonEmptyList has at least one duplicate |
| `nel.shouldBeSingleElement(e)` | Asserts that the NonEmptyList has a single element which is e |
| `nel.shouldBeSorted()` | Asserts that the NonEmptyList is sorted |

| Validated | |
| -------- | ---- |
| `validated.shouldBeValid()` | Asserts that the validated is of type Valid |
| `validated.shouldBeValid(v)` | Asserts that the validated is of type Valid with specific value v |
| `validated.shouldBeInvalid()` | Asserts that the validated is of type Invalid |
