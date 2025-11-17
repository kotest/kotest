---
id: core
title: Core Matchers
slug: core-matchers.html
sidebar_label: Core
---




Matchers provided by the `kotest-assertions-core` module.

| General                                 |                                                                                                  |
|-----------------------------------------|--------------------------------------------------------------------------------------------------|
| `obj.shouldBe(other)`                   | General purpose assertion that the given obj and other are both equal                            |
| `obj::prop.shouldHaveValue(other)`      | General purpose assertion on a property value printing information of the property on failure.   |
| `expr.shouldBeTrue()`                   | Convenience assertion that the expression is true. Equivalent to `expr.shouldBe(true)`           |
| `expr.shouldBeFalse()`                  | Convenience assertion that the expression is false. Equivalent to `expr.shouldBe(false)`         |
| `shouldThrow<T> { block }`              | General purpose construct that asserts that the block throws a `T` Throwable or a subtype of `T` |
| `shouldThrowExactly<T> { block }`       | General purpose construct that asserts that the block throws exactly `T`                         |
| `shouldThrowAny { block }`              | General purpose construct that asserts that the block throws a Throwable of any type             |
| `shouldThrowMessage(message) { block }` | Verifies that a block of code throws any Throwable with given message                            |

| Types                                       ||
|---------------------------------------------| ---- |
| `obj.shouldBeSameInstanceAs(other)`         | Compares objects by identity, that is, they are the same exact reference. |
| `obj.shouldBeTypeOf<T>()`                   | Asserts that the given reference is exactly of type T. Subclass will fail. Ie, `1 should beOfType<Number>` would fail because although 1 _is_ a Number, the runtime type is not Number. |
| `obj.shouldBeInstanceOf<T>()`               | Asserts that the given reference is of type T or a subclass of T. |
| `obj.shouldHaveAnnotation(annotationClass)` | Asserts that the object has an annotation of the given type. |
| `obj.shouldBeNull()`                        | Asserts that a given reference is null. |
| `obj shouldNotBeNull { block }`             | Asserts that a given reference is not null. |


| Comparables                                        ||
|----------------------------------------------------| ---- |
| `comp.shouldBeLessThan(other)`                     | Uses `compareTo` to verify that `comp` is less than `other` |
| `comp.shouldBeLessThanOrEqualTo(other)`            | Uses `compareTo` to verify that `comp` is less than or equal to `other` |
| `comp.shouldBeEqualComparingTo(other)`             | Uses `compareTo` to verify that `comp` is equal to `other` |
| `comp.shouldBeEqualComparingTo(other, comparator)` | Uses `comparator.compare` to verify that `comp` is equal to `other` |
| `comp.shouldBeGreaterThan(other)`                  | Uses `compareTo` to verify that `comp` is greater than `other` |
| `comp.shouldBeGreaterThanOrEqualTo(other)`         | Uses `compareTo` to verify that `comp` is greater than or equal to `other` |
| `comp.shouldBeBetween(lower, upper)`               | Uses `compareTo` to verify that `comp` is in range `lower..upper` (inclusive, inclusive) |


Collections: also see [inspectors](inspectors.md) which are useful ways to test multiple elements in a collection.

| Collections                                     |                                                                                                                                                                   |
|-------------------------------------------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `collection.shouldBeEmpty()`                    | Asserts that the collections has zero elements.                                                                                                                   |
| `collection.shouldBeUnique()`                   | Asserts that all the elements of the collection are distinct using the natural equals of the elements.                                                            |
| `collection.shouldBeUnique(comparator)`         | Asserts that all the elements of the collection are distinct by comparing elements using the given `comparator`.                                                  |
| `collection.shouldContain(element)`             | Asserts that the collection contains the given element.                                                                                                           |
| `collection.shouldContainAll(e1, e2, ..., en)`  | Asserts that the collection contains all the elements listed, where order is not important. Ie, element 2 can be in the collection before element 1.              |
| `collection.shouldContainDuplicates()`          | Asserts that the collection contains at least one duplicate element.                                                                                              |
| `collection.shouldContainExactly()`             | Assert that a collection contains exactly the given values and nothing else, in order.                                                                            |
| `collection.shouldContainExactlyInAnyOrder()`   | Assert that a collection contains exactly the given values and nothing else, in _any_ order.                                                                      |
| `collection.shouldContainAllInAnyOrder()`       | Assert that a collection contains all the given values, in _any_ order.                                                                          |
| `collection.shouldContainNoNulls()`             | Asserts that the collection contains no null elements, or is empty.                                                                                               |
| `collection.shouldContainNull()`                | Asserts that the collection contains at least one null element.                                                                                                   |
| `collection.shouldContainOnlyNulls()`           | Asserts that the collection contains only null elements, or is empty.                                                                                             |
| `collection.shouldContainAllIgnoringFields()`   | Asserts that the collection contains all the elements listed ignoring one or more fields.                                                                         |
| `collection.shouldHaveSingleElement(element)`   | Asserts that the collection only contains a single element and that that element is the given one.                                                                |
| `collection.shouldHaveSingleElement { block }`  | Asserts that the collection contains a single element by a given predicate.                                                                                       |
| `collection.shouldHaveSize(length)`             | Asserts that the collection is exactly the given length.                                                                                                          |
| `collection.shouldBeSingleton()`                | Asserts that the collection contains only one element.                                                                                                            |
| `collection.shouldBeSingleton { block }`        | Asserts that the collection contains only one element, and then runs the block with this element.                                                                         |
| `collection.shouldHaveLowerBound(element)`      | Asserts that the given element is smaller or equal to every element of the collection. Works only for elements that implement Comparable.                         |
| `collection.shouldHaveUpperBound(element)`      | Asserts that the given element is larger or equal to every element of the collection. Works only for elements that implement Comparable.                          |
| `collection.shouldBeSmallerThan(col)`           | Asserts that the collection is smaller than the other collection.                                                                                                 |
| `collection.shouldBeLargerThan(col)`            | Asserts that the collection is larger than the other collection.                                                                                                  |
| `collection.shouldBeSameSizeAs(col)`            | Asserts that the collection has the same size as the other collection.                                                                                            |
| `collection.shouldHaveAtLeastSize(n)`           | Asserts that the collection has at least size n.                                                                                                                  |
| `collection.shouldHaveAtMostSize(n)`            | Asserts that the collection has at most size n.                                                                                                                   |
| `list.shouldBeSorted()`                         | Asserts that the list is sorted.                                                                                                                                  |
| `list.shouldBeSortedBy { transform }`           | Asserts that the list is sorted by the value after applying the transform.                                                                                        |
| `list.shouldContainInOrder(other)`              | Asserts that this list contains the given list in order. Other elements may appear either side of the given list.                                                 |
| `list.shouldExistInOrder({ element }, ...)`     | Asserts that this list contains elements matching the predicates in order. Other elements may appear around or between the elements matching the predicates.      |
| `list.shouldHaveElementAt(index, element)`      | Asserts that this list contains the given element at the given position.                                                                                          |
| `list.shouldStartWith(lst)`                     | Asserts that this list starts with the elements of the given list, in order.                                                                                      |
| `list.shouldEndWith(lst)`                       | Asserts that this list ends with the elements of the given list, in order.                                                                                        |
| `iterable.shouldMatchEach(assertions)`          | Iterates over this list and the assertions and asserts that each element of this list passes the associated assertion. Fails if size of the collections mismatch. |
| `iterable.shouldMatchInOrder(assertions)`       | Asserts that there is a subsequence of this iterator that matches the assertions in order, with no gaps allowed.                                                  |
| `iterable.shouldMatchInOrderSubset(assertions)` | Asserts that there is a subsequence (possibly with gaps) that matches the assertions in order.                                                                    |
| `value.shouldBeOneOf(collection)`               | Asserts that a specific instance is contained in a collection.                                                                                                    |
| `collection.shouldContainAnyOf(collection)`     | Asserts that the collection has at least one of the elements in `collection`                                                                                      |
| `value.shouldBeIn(collection)`                  | Asserts that an object is contained in collection, checking by value and not by reference.                                                                        |


| Iterator                    ||
|-----------------------------| ---- |
| `iterator.shouldBeEmpty()`  | Asserts that the iterator does not have a next value. |
| `iterator.shouldHaveNext()` | Asserts that the iterator has a next value |

| Maps                                                                                  ||
|---------------------------------------------------------------------------------------| ---- |
| `map.shouldContain("key", "value")`                                                   | Asserts that the map contains the mapping "key" to "value" |
| `map.shouldContainAll(other)`                                                         | Asserts that the map contains all the pairs from the given map. |
| `map.shouldContainExactly(other)`                                                     | Asserts that the map contains exactly the pairs from given map, and no extra. |
| `map.shouldContainKey(key)`                                                           | Asserts that the map contains a key called `key` with any value |
| `map.shouldContainKeys(keys)`                                                         | Asserts that the map contains mappings for all the given keys. |
| `map.shouldContainValue(value)`                                                       | Asserts that the map contains at least one mapping where the value is `value`. |
| `map.shouldContainValues(values)`                                                     | Asserts that the map contains all the given values. |
| `map.shouldBeEmpty()`                                                                 | Asserts that this map is empty. |
| `map.shouldMatchAll("k1" to {it shouldBe "v1"}, "k2" to {it shouldBe "v2"}, ...)`     | Asserts that all the entries in the map can be matched with the provided matchers, extra keys in the map are ignored. |
| `map.shouldMatchExactly("k1" to {it shouldBe "v1"}, "k2" to {it shouldBe "v2"}, ...)` | Asserts that the entries in the map can be exactly matched with the provided matchers. |

| Sets ||
|------|----|
| shouldIntersect | Asserts that the set has at least one element in common with the other set. |

| Strings                                     ||
|---------------------------------------------| ---- |
| `str.shouldBeBlank()`                       | Asserts that the string contains only whitespace, or is empty. |
| `str.shouldBeEmpty()`                       | Asserts that the string has length zero. |
| `str.shouldBeLowerCase()`                   | Asserts that the string is all in lower case. |
| `str.shouldBeUpperCase()`                   | Asserts that the string is all in upper case. |
| `str.shouldContain("substr")`               | Asserts that the string includes the given substring. The substring can be equal to the string. This matcher is case sensitive. To make this case insensitive use shouldContainIgnoringCase(). |
| `str.shouldContain(regex)`                  | Asserts that the string includes the given regular expression. |
| `str.shouldContainADigit()`                 | Asserts that the string contains at least one digit. |
| `str.shouldContainIgnoringCase(substring)`  | Asserts that the string contains the substring ignoring case. |
| `str.shouldContainOnlyDigits()`             | Asserts that the string contains only digits, or is empty. |
| `str.shouldBeInteger([radix])`              | Asserts that the string contains an integer and returns it. |
| `str.shouldContainOnlyOnce(substring)`      | Asserts that the string contains the substring exactly once. |
| `str.shouldEndWith("suffix")`               | Asserts that the string ends with the given suffix. The suffix can be equal to the string. This matcher is case sensitive. To make this case insensitive call `toLowerCase()` on the value before the matcher. |
| `str.shouldHaveLength(length)`              | Asserts that the string has the given length. |
| `str.shouldHaveLineCount(count)`            | Asserts that the string contains the given number of lines. Similar to `str.split("\n").length.shouldBe(n)` |
| `str.shouldHaveMaxLength(max)`              | Asserts that the string is no longer than the given max length. |
| `str.shouldHaveMinLength(min)`              | Asserts that the string is no shorter than the given min length. |
| `str.shouldHaveSameLengthAs(anotherString)` | Asserts that the string has the same length as another string. |
| `str.shouldMatch(regex)`                    | Asserts that the string fully matches the given regex. |
| `str.shouldStartWith("prefix")`             | Asserts that the string starts with the given prefix. The prefix can be equal to the string. This matcher is case sensitive. To make this case insensitive call `toLowerCase()` on the value before the matcher. |
| `str.shouldBeEqualIgnoringCase(other)`      | Asserts that the string is equal to another string ignoring case. |
| `str.shouldBeTruthy()`                      | Asserts that the string is truthy. Truthy is one of the followings: ["true", "yes", "y", "1"] |
| `str.shouldBeFalsy()`                       | Asserts that the string is falsy. Falsy is one of the followings: ["false", "no", "n", "0"] |

| Integers                            ||
|-------------------------------------| ---- |
| `int.shouldBeBetween(x, y)`         | Asserts that the integer is between x and y, inclusive on both x and y |
| `int.shouldBeLessThan(n)`           | Asserts that the integer is less than the given value n |
| `int.shouldBeLessThanOrEqual(n)`    | Asserts that the integer is less or equal to than the given value n |
| `int.shouldBeGreaterThan(n)`        | Asserts that the integer is greater than the given value n |
| `int.shouldBeGreaterThanOrEqual(n)` | Asserts that the integer is greater than or equal to the given value n |
| `int.shouldBeEven()`                | Asserts that the integer is even. |
| `int.shouldBeOdd()`                 | Asserts that the integer is odd. |
| `int.shouldBeInRange(range)`        | Asserts that the integer is included in the given range. |
| `int.shouldBeZero()`                | Asserts that the integer is zero |

| Longs                                ||
|--------------------------------------| ---- |
| `long.shouldBeBetween(x, y)`         | Asserts that the long is between x and y, inclusive on both x and y |
| `long.shouldBeLessThan(n)`           | Asserts that the long is less than the given value n |
| `long.shouldBeLessThanOrEqual(n)`    | Asserts that the long is less or equal to than the given value n |
| `long.shouldBeGreaterThan(n)`        | Asserts that the long is greater than the given value n |
| `long.shouldBeGreaterThanOrEqual(n)` | Asserts that the long is greater than or equal to the given value n |
| `long.shouldBeInRange(range)`        | Asserts that the long is included in the given range. |
| `long.shouldBeEven()`                | Asserts that the long is even. |
| `long.shouldBeOdd()`                 | Asserts that the long is odd. |
| `long.shouldBeZero()`                | Asserts that the long is zero |

| Doubles or Floats                               ||
|-------------------------------------------------| ---- |
| `double.shouldBe(value plusOrMinus(tolerance))` | Asserts that the double is equal to the given value within a tolerance range. This is the recommended way of testing for double equality. |
| `double.shouldBeBetween(x, y)`                  | Asserts that the double is between x and y, inclusive on both x and y |
| `double.shouldBeLessThan(n)`                    | Asserts that the double is less than the given value n |
| `double.shouldBeLessThanOrEqual(n)`             | Asserts that the double is less or equal to than the given value n |
| `double.shouldBeGreaterThan(n)`                 | Asserts that the double is greater than the given value n |
| `double.shouldBeGreaterThanOrEqual(n)`          | Asserts that the double is greater than or equal to the given value n |
| `double.shouldBePositive()`                     | Asserts that the double is positive |
| `double.shouldBeNegative()`                     | Asserts that the double is negative |
| `double.shouldBePositiveInfinity()`             | Asserts that the double is positive infinity |
| `double.shouldBeNegativeInfinity()`             | Asserts that the double is negative infinity |
| `double.shouldBeNaN()`                          | Asserts that the double is the Not-a-Number constant NaN |
| `double.shouldBeZero()`                         | Asserts that the double is zero |

| BigDecimal                                  |                                                                            |
|---------------------------------------------|----------------------------------------------------------------------------|
| `bigDecimal.shouldHavePrecision(n)`         | Asserts that the bigDecimal precision is equals than the given value n     |
| `bigDecimal.shouldHaveScale(n)`             | Asserts that the bigDecimal scale is equals than the given value n         |
| `bigDecimal.shouldBePositive()`             | Asserts that the bigDecimal is positive                                    |
| `bigDecimal.shouldBeNegative()`             | Asserts that the bigDecimal is negative                                    |
| `bigDecimal.shouldNotBePositive()`          | Asserts that the bigDecimal is not positive                                |
| `bigDecimal.shouldNotBeNegative()`          | Asserts that the bigDecimal is not negative                                |
| `bigDecimal.shouldBeZero()`                 | Asserts that the bigDecimal is zero                                        |
| `bigDecimal.shouldBeLessThan(n)`            | Asserts that the bigDecimal is less than the given value n                 |
| `bigDecimal.shouldBeLessThanOrEquals(n)`    | Asserts that the bigDecimal is less than or equal to n                     |
| `bigDecimal.shouldBeGreaterThan(n)`         | Asserts that the bigDecimal is greater than the given value n              |
| `bigDecimal.shouldBeGreaterThanOrEquals(n)` | Asserts that the bigDecimal is greater than or equals to the given value n |
| `bigDecimal.shouldBeInRange(r)`             | Asserts that the bigDecimal is in the given range                          |
| `bigDecimal.shouldBeEqualIgnoringScale(r)`  | Asserts that the bigDecimal is equal to the given value n ignoring scale   |
| `bigDecimal.shouldBe(value plusOrMinus(tolerance))` | Asserts that the bigDecimal is equal to the given value within a tolerance range. |

| Channels                                          ||
|---------------------------------------------------| ---- |
| `channel.shouldReceiveWithin(duration)`           | Asserts that the channel should receive within duration |
| `channel.shouldReceiveNoElementsWithin(duration)` | Asserts that the channel should not receive any elements within duration |
| `channel.shouldHaveSize(n)`                       | Asserts that the channel should receive exactly n elements before closing |
| `channel.shouldReceiveAtLeast(n)`                 | Asserts that the channel should receive >= n elements |
| `channel.shouldReceiveAtMost(n)`                  | Asserts that the channel should receive <=n elements before closing |
| `channel.shouldBeClosed()`                        | Asserts that the channel is closed |
| `channel.shouldBeOpen()`                          | Asserts that the channel is open |
| `channel.shouldBeEmpty()`                         | Asserts that the channel is empty |

| URIs                                ||
|-------------------------------------| ---- |
| `uri.shouldHaveAuthority(fragment)` | Asserts that the uri has the given authority. |
| `uri.shouldHaveFragment(fragment)`  | Asserts that the uri has the given fragment. |
| `uri.shouldHaveHost(scheme)`        | Asserts that the uri has the given hostname. |
| `uri.shouldHaveParameter(scheme)`   | Asserts that the uri's query string contains the given parameter. |
| `uri.shouldHavePath(scheme)`        | Asserts that the uri has the given path. |
| `uri.shouldHavePort(scheme)`        | Asserts that the uri has the given port. |
| `uri.shouldHaveQuery(fragment)`     | Asserts that the uri has the given query. |
| `uri.shouldHaveScheme(scheme)`      | Asserts that the uri has the given scheme. |

| Files                                              ||
|----------------------------------------------------| ---- |
| `file.shouldBeAbsolute()`                          | Asserts that the file represents an absolute path. |
| `file.shouldBeADirectory()`                        | Asserts that the file denotes a directory. |
| `file.shouldBeAFile()`                             | Asserts that the file denotes a file. |
| `file.shouldBeCanonical()`                         | Asserts that the file is in canonical format. |
| `file.shouldBeEmpty()`                             | Asserts that the file exists but is empty. |
| `file.shouldBeExecutable()`                        | Asserts that the file is executable by the current process. |
| `file.shouldBeHidden()`                            | Asserts that the file exists on disk and is a hidden file. |
| `file.shouldBeReadable()`                          | Asserts that the file is readable by the current process. |
| `file.shouldBeRelative()`                          | Asserts that the file represents a relative path. |
| `file.shouldBeSmaller(file)`                       | Asserts that this file is smaller than the given file. |
| `file.shouldBeLarger(file)`                        | Asserts that this file is larger than the given file. |
| `file.shouldBeWriteable()`                         | Asserts that the file is writeable by the current process. |
| `dir.shouldBeNonEmptyDirectory()`                  | Asserts that the file is a directory and is non empty. |
| `dir.shouldContainFile(name)`                      | Asserts that the file is a directory and that it contains a file with the given name. |
| `dir.shouldContainNFiles(name)`                    | Asserts that the file is a directory and that it contains exactly n files. |
| `file.shouldExist()`                               | Asserts that the file exists on disk, either a directory or as a file. |
| `file.shouldHaveExtension(ext)`                    | Asserts that the file ends with the given extension. |
| `file.shouldHaveFileSize(size)`                    | Asserts that the file has the given file size. |
| `file.shouldHaveName(name)`                        | Asserts that the file's name matches the given name. |
| `file.shouldHavePath(path)`                        | Asserts that the file's path matches the given path. |
| `file.shouldStartWithPath(prefix)`                 | Asserts that the file's path starts with the given prefix. |
| `dir.shouldContainFileDeep(name)`                  | Assert that file is a directory and that it or any sub directory contains a file with the given name. |
| `dir.shouldContainFiles(name1, name2, ..., nameN)` | Asserts that the file is a directory and that it contains al files with the given name. |
| `file.shouldBeSymbolicLink()`                      | Asserts that the file is a symbolic link. |
| `file.shouldHaveParent(name)`                      |  Assert that the file has a parent with the given name |

| Dates                                         |                                                                                         |
|-----------------------------------------------|-----------------------------------------------------------------------------------------|
| `date.shouldHaveSameYearAs(otherDate)`        | Asserts that the date has the same year as the given date.                              |
| `date.shouldHaveSameMonthAs(otherDate)`       | Asserts that the date has the same month as the given date.                             |
| `date.shouldHaveSameDayAs(otherDate)`         | Asserts that the date has the same day of the month as the given date.                  |
| `date.shouldBeBefore(otherDate)`              | Asserts that the date is before the given date.                                         |
| `date.shouldBeAfter(otherDate)`               | Asserts that the date is after the given date.                                          |
| `date.shouldBeWithin(period, otherDate)`      | Asserts that the date is within the period of the given date.                           |
| `date.shouldBeWithin(duration, otherDate)`    | Asserts that the date is within the duration of the given date.                         |
| `date.shouldBeBetween(firstDate, secondDate)` | Asserts that the date is between firstdate and seconddate.                              |
| `date.shouldHaveYear(year)`                   | Asserts that the date have correct year.                                                |
| `date.shouldHaveMonth(month)`                 | Asserts that the date have correct month.                                               |
| `date.shouldHaveDayOfYear(day)`               | Asserts that the date have correct day of year.                                         |
| `date.shouldHaveDayOfMonth(day)`              | Asserts that the date have correct day of month.                                        |
| `date.shouldHaveDayOfWeek(day)`               | Asserts that the date have correct day of week.                                         |
| `date.shouldHaveHour(hour)`                   | Asserts that the date have correct hour.                                                |
| `date.shouldHaveMinute(Minute)`               | Asserts that the date have correct minute.                                              |
| `date.shouldHaveSecond(second)`               | Asserts that the date have correct second.                                              |
| `date.shouldHaveNano(nao)`                    | Asserts that the date have correct nano second.                                         |
| `date.shouldBe(value plusOrMinus(tolerance))` | Asserts that the date is equal to the given value within a tolerance range of duration. |

| ZonedDateTime                                                        ||
|----------------------------------------------------------------------| ---- |
| `zonedDateTime.shouldBeToday()`                                      | Asserts that the ZonedDateTime has the same day as the today. |
| `zonedDateTime.shouldHaveSameInstantAs(other: ZonedDateTime)`        | Asserts that the ZonedDateTime is equal to other ZonedDateTime using ```ChronoZonedDateTime.isEqual```. |
| `zonedDateTime.shouldBe(other plusOrMinus 1.minutes)` | Asserts that the ZonedDateTime is within 1 minute of `other` ZonedDateTime. |

| OffsetDateTime                                                           |                                                                                                      |
|--------------------------------------------------------------------------|------------------------------------------------------------------------------------------------------|
| `offsetDateTime.shouldBeToday()`                                         | Asserts that the OffsetDateTime has the same day as today.                                           |
| `offsetDateTime.shouldHaveSameInstantAs(other: OffsetDateTime)`          | Asserts that the OffsetDateTime is equal to other OffsetDateTime using ```OffsetDateTime.isEqual```. |
| `offsetDateTime.shouldBe(other: OffsetDateTime plusOrMinus 1.minutes)`   | Asserts that the OffsetDateTime is within 1 minute of other OffsetDateTime.                          |

| Times                                               ||
|-----------------------------------------------------| ---- |
| `time.shouldHaveSameHoursAs(otherTime)`             | Asserts that the time has the same hours as the given time. |
| `time.shouldHaveSameMinutesAs(otherTime)`           | Asserts that the time has the same minutes as the given time. |
| `time.shouldHaveSameSecondsAs(otherTime)`           | Asserts that the time has the same seconds as the given time. |
| `time.shouldHaveSameNanosAs(otherTime)`             | Asserts that the time has the same nanos as the given time. |
| `time.shouldBeBefore(otherTime)`                    | Asserts that the time is before the given time. |
| `time.shouldBeAfter(otherTime)`                     | Asserts that the time is after the given time. |
| `time.shouldBeBetween(firstTime, secondTime)`       | Asserts that the time is between firstTime and secondTime. |
| `time.shouldBe(otherTime plusOrMinus 1.minutes)`    | Asserts that the time is within duration of other time. |



| Instant                                               ||
|-------------------------------------------------------| ---- |
| `instant.shouldBeAfter(anotherInstant)`               | Asserts that the instant is after anotherInstant |
| `instant.shouldBeBefore(anotherInstant)`              | Asserts that the instant is before anotherInstant |
| `instant.shouldBeBetween(fromInstant, toInstant)`     | Asserts that the instant is between fromInstant and toInstant |
| `instant.shouldBeCloseTo(anotherInstant, duration)`   | Asserts that the instant is close To anotherInstant in range by duration |
| `instant.shouldBe(otherTime plusOrMinus 1.minutes)`   | Asserts that the instant is within duration of other instant. |

| Timestamp                                               ||
|---------------------------------------------------------| ---- |
| `timestamp.shouldBeAfter(anotherTimestamp)`             | Asserts that the timestamp is after anotherTimestamp |
| `timestamp.shouldBeBefore(anotherTimestamp)`            | Asserts that the timestamp is before anotherTimestamp |
| `timestamp.shouldBeBetween(fromTimestamp, toTimestamp)` | Asserts that the timestamp is between fromTimestamp and toTimestamp|


| Concurrent                                      ||
|-------------------------------------------------| ---- |
| `shouldCompleteWithin(timeout, unit, function)` | Asserts that the given function completes within the given duration. |
| `shouldTimeout(timeout, unit, function)`        | Asserts that given function does not complete within the given duration. |
| `shouldTimeout(duration, suspendableFunction)`  | Asserts that given suspendable function does not complete within the given duration. |

| Futures                                             ||
|-----------------------------------------------------| ---- |
| `future.shouldBeCancelled()`                        | Asserts that the future has been cancelled. |
| `future.shouldBeCompleted()`                        | Asserts that the future has completed. |
| `future.shouldBeCompletedExceptionally()`           | Asserts that the the future has completed with an exception. |
| `future.shouldCompleteExceptionallyWith(throwable)` | Asserts that the the future will complete with given exception. |

| Threads                       ||
|-------------------------------| ---- |
| `thread.shouldBeBlocked()`    | Asserts that the thread is currently blocked. |
| `thread.shouldBeDaemon()`     | Asserts that the thread is a daemon thread. |
| `thread.shouldBeAlive()`      | Asserts that the thread is alive. |
| `thread.shouldBeTerminated()` | Asserts that the thread has been terminated. |

| Throwables / Exceptions                    |                                                                                 |
|--------------------------------------------|---------------------------------------------------------------------------------|
| `throwable.shouldHaveMessage(message)`     | Asserts that the throwable message is the same of the given one.                |
| `throwable.shouldHaveCause()`              | Asserts that the throwable have a cause.                                        |
| `throwable.shouldHaveCause { block }`      | Asserts that the throwable have a cause, and pass it as parameter to the block  |
| `throwable.shouldHaveCauseInstanceOf<T>()` | Asserts that the throwable have a cause and it is of type T or a subclass of T. |
| `throwable.shouldHaveCauseOfType<T>()`     | Asserts that the throwable have a cause and it is **exactly** of type T.        |

| Result                                             |                                                                                                   |
|----------------------------------------------------|---------------------------------------------------------------------------------------------------|
| `result.shouldBeSuccess()`                         | Asserts that the result is success                                                                |
| `result.shouldBeSuccess(value)`                    | Asserts that the result is a success and the value is the same of the given one.                  |
| `result.shouldBeSuccess(block)`                    | Asserts that the result is success and then, runs the block with the result value.                |
| `result.shouldBeFailure()`                         | Asserts that the result is failure                                                                |
| `result.shouldBeFailureOfType<Type : Throwable>()` | Asserts that the result is a failure and the exception class is equals the same of the given one. |
| `result.shouldBeFailure(block)`                    | Asserts that the result is failure and then, runs the block with the exception.                   |

| Optional                                   |                                                                           |
|--------------------------------------------|---------------------------------------------------------------------------|
| `optional.shouldBePresent()`               | Asserts that this Optional is present                                     |
| `optional.shouldBePresent { value -> .. }` | Asserts that this Optional is present , then execute block with the value |
| `optional.shouldBeEmpty()`                 | Asserts that this optional is empty                                       |

| Reflection                                                     |                                                                                                                           |
|----------------------------------------------------------------|---------------------------------------------------------------------------------------------------------------------------|
| `kclass.shouldHaveAnnotations()`                               | Asserts that the class has some annotation                                                                                |
| `kclass.shouldHaveAnnotations(n)`                              | Asserts that the class has exactly N annotation                                                                           |
| `kclass.shouldBeAnnotatedWith<T>()`                            | Asserts that the class is annotated with the given type                                                                   |
| `kclass.shouldBeAnnotatedWith<T> { block }`                    | Asserts that the class is annotated with the given type, and then, runs the block with the annotation                     |
| `kclass.shouldHaveFunction(name)`                              | Asserts that the class have a function with the given name                                                                |
| `kclass.shouldHaveFunction(name) { block }`                    | Asserts that the class have a function with the given name, and then, runs the block with the function                    |
| `kclass.shouldHaveMemberProperty(name)`                        | Asserts that the class have a member property with the given name                                                         |
| `kclass.shouldHaveMemberProperty(name) { block }`              | Asserts that the class have a member property with the given name, and then, runs the block with the function             |
| `kclass.shouldBeSubtypeOf<T>()`                                | Asserts that the class is a subtype of T                                                                                  |
| `kclass.shouldBeSupertypeOf<T>()`                              | Asserts that the class is a supertype of T                                                                                |
| `kclass.shouldBeData()`                                        | Asserts that the class is a data class                                                                                    |
| `kclass.shouldBeSealed()`                                      | Asserts that the class is a sealed class                                                                                  |
| `kclass.shouldBeCompanion()`                                   | Asserts that the class is a companion object                                                                              |
| `kclass.shouldHavePrimaryConstructor()`                        | Asserts that the class has a primary constructor                                                                          |
| `kclass.shouldHaveVisibility(visibility)`                      | Asserts that the class has the given visibility                                                                           |
| `kfunction.shouldHaveAnnotations()`                            | Asserts that the function has some annotation                                                                             |
| `kfunction.shouldHaveAnnotations(n)`                           | Asserts that the function has exactly N annotation                                                                        |
| `kfunction.shouldBeAnnotatedWith<T>()`                         | Asserts that the function is annotated with the given type                                                                |
| `kfunction.shouldBeAnnotatedWith<T> { block }`                 | Asserts that the function is annotated with the given type, and then, runs the block with the annotation                  |
| `kfunction.shouldHaveReturnType<T>()`                          | Asserts that the function returns the given type                                                                          |
| `kfunction.shouldBeInline()`                                   | Asserts that the function is inline                                                                                       |
| `kfunction.shouldBeInfix()`                                    | Asserts that the function is infix                                                                                        |
| `kproperty.shouldBeOfType<T>()`                                | Asserts that the property is of the given type                                                                            |
| `kproperty.shouldBeConst()`                                    | Asserts that the property is a const                                                                                      |
| `kproperty.shouldBeLateInit()`                                 | Asserts that the property is a late init var                                                                              |
| `kcallable.shouldHaveVisibility(visibility)`                   | Asserts that the member have the given visibility                                                                         |
| `kcallable.shouldBeFinal()`                                    | Asserts that the member is final                                                                                          |
| `kcallable.shouldBeOpen()`                                     | Asserts that the member is open                                                                                           |
| `kcallable.shouldBeAbstract()`                                 | Asserts that the member is abstract                                                                                       |
| `kcallable.shouldBeSuspendable()`                              | Asserts that the member is suspendable                                                                                    |
| `kcallable.shouldAcceptParameters(parameters)`                 | Asserts that the member can be called with the parameters (check the types)                                               |
| `kcallable.shouldAcceptParameters(parameters) { block }`       | Asserts that the member can be called with the parameters (check the types), and then, runs the block with the annotation |
| `kcallable.shouldHaveParametersWithName(parameters)`           | Asserts that the member has the parameters with the given name                                                            |
| `kcallable.shouldHaveParametersWithName(parameters) { block }` | Asserts that the member has the parameters with the given name, and then, runs the block with the annotation              |
| `ktype.shouldBeOfType<T>()`                                    | Asserts that the KType has the type T                                                                                     |


| Statistic                                                 ||
|-----------------------------------------------------------| --- |
| `collection.shouldHaveMean(mean)`                         | Asserts that collection has specific mean with default precision = 4 |
| `collection.shouldHaveMean(mean, precision)`              | Asserts that collection has specific mean with specific precision |
| `collection.shouldHaveVariance(mean)`                     | Asserts that collection has specific variance with default precision = 4 |
| `collection.shouldHaveVariance(mean, precision)`          | Asserts that collection has specific variance with specific precision |
| `collection.shouldHaveStandardDeviation(mean)`            | Asserts that collection has specific standard deviation with default precision = 4 |
| `collection.shouldHaveStandardDeviation(mean, precision)` | Asserts that collection has specific standard deviation with specific precision |



| Regex                                             |                                                                                         |
|---------------------------------------------------|-----------------------------------------------------------------------------------------|
| `regex.shouldBeRegex(anotherRegex)`               | Asserts that regex is equal to anotherRegex by comparing their pattern and regexOptions |
| `regex.shouldHavePattern(regexPattern)`           | Asserts that regex have given regexPattern                                              |
| `regex.shouldHaveExactRegexOptions(regexOptions)` | Asserts that regex have exactly the given regexOptions                                  |
| `regex.shouldIncludeRegexOption(regexOption)`     | Asserts that regex include the given regexOption                                        |
| `regex.shouldIncludeRegexOptions(regexOptions)`   | Asserts that regex include of the given regexOptions                                    |

| Selective Matchers                                                                                                         |                                                                                                                                                                                                                                                                                                                      |
|----------------------------------------------------------------------------------------------------------------------------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `any<T>.shouldBeEqualToUsingFields(other: T, vararg properties: KProperty<*>)`                                             | Asserts that the any, of type T, is equal to other, of type T, considering only given properties. See [Example](https://github.com/kotest/kotest/blob/1f4069d78faead65a0d7e8c7f1b689b417a655d2/kotest-assertions/kotest-assertions-core/src/jvmMain/kotlin/io/kotest/matchers/equality/reflection.kt#L20)            |
| `any<T>.shouldBeEqualToDifferentTypeUsingFields(other: V, vararg properties: KProperty1<T, *>)`                            | Asserts that the any, of type T, is equal to other, of type V, considering only given properties, of type T. See [Example](https://github.com/kotest/kotest/blob/1f4069d78faead65a0d7e8c7f1b689b417a655d2/kotest-assertions/kotest-assertions-core/src/jvmMain/kotlin/io/kotest/matchers/equality/reflection.kt#L51) |
| `any<T>.shouldBeEqualToIgnoringFields(other: T, property: KProperty<*>, vararg others: KProperty<*>)`                      | Asserts that the any, of type T, is equal to other, of type T, ignoring the given properties. See [Example](https://github.com/kotest/kotest/blob/1f4069d78faead65a0d7e8c7f1b689b417a655d2/kotest-assertions/kotest-assertions-core/src/jvmMain/kotlin/io/kotest/matchers/equality/reflection.kt#L127)               |
| `any<T>.shouldBeEqualToDifferentTypeIgnoringFields(other: V, property: KProperty1<T, *>, vararg others: KProperty1<T, *>)` | Asserts that the any, of type T, is equal to other, of type V, ignoring the given properties, of type T. See [Example](https://github.com/kotest/kotest/blob/1f4069d78faead65a0d7e8c7f1b689b417a655d2/kotest-assertions/kotest-assertions-core/src/jvmMain/kotlin/io/kotest/matchers/equality/reflection.kt#L184)    |

| Field by Field Comparison Matchers                                                                                                                          |                                                                                                                                                                                                                                                                                                                                         |
|-------------------------------------------------------------------------------------------------------------------------------------------------------------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `any.shouldBeEqualToComparingFields(other: T)`                                                                                                              | Asserts that the any is equal to other considering their fields(ignoring private fields) instead of `equals` method.                                                                                                                                                                                                                    |
| `any.shouldBeEqualToComparingFields(other: T, ignorePrivateFields: Boolean)`                                                                                | Asserts that the any is equal to other considering their fields and private fields(if `ignorePrivateFields` is false) instead of `equals` method.                                                                                                                                                                                       |
| ~~`any.shouldBeEqualToComparingFieldsExcept(other: T, ignoreProperty: KProperty<*>, vararg ignoreProperties: KProperty<*>)`~~                               | ~~Asserts that the any is equal to other considering their public fields ignoring private fields and other fields mentioned by `ignoreProperty` and `ignoreProperties` instead of `equals` method.~~ deprecated. shouldBeEqualToComparingFields and shouldBeEqualToIgnoringFields are alternative.                                      |
| ~~`any.shouldBeEqualToComparingFieldsExcept(other: T, ignorePrivateFields: Boolean, ignoreProperty: KProperty<*>, vararg ignoreProperties: KProperty<*>)`~~ | ~~Asserts that the any is equal to other considering all their fields including private fields(if `ignorePrivateFields` is false) but ignoring fields mentioned by `ignoreProperty` and `ignoreProperties` instead of `equals` method.~~  deprecated. shouldBeEqualToComparingFields and shouldBeEqualToIgnoringFields are alternative. |



| Resource Matchers                                                                   |                                                                                                                                                          |
|-------------------------------------------------------------------------------------|----------------------------------------------------------------------------------------------------------------------------------------------------------|
| `str shouldMatchResource "/path/to/test_resource.txt"`                              | Asserts that the string is equal to the given resource (as String). This matcher will ignore differences in line separators.                             |
| `str shouldNotMatchResource "/path/to/test_resource.txt"`                           | Asserts that the string is **not** equal to the given resource (as String). This matcher will ignore differences in line separators.                     |
| `str.shouldMatchResource("/path/to/test_resource.txt", ::providedMatcher)`          | Asserts that the string matches to the given resource (as String) using `providedMatcher`. Differences in line separators is ignored by default.         |
| `str.shouldNotMatchResource("/path/to/test_resource.txt", ::providedMatcher)`       | Asserts that the string **not** matches to the given resource (as String) using `providedMatcher`. Differences in line separators is ignored by default. |
| `byteArray shouldMatchResource "/path/to/test_resource.bin"`                        | Asserts that the byteArray is equal to the given resource.                                                                                               |
| `byteArray shouldNotMatchResource "/path/to/test_resource.bin"`                     | Asserts that the byteArray is **not** equal to the given resource.                                                                                       |
| `byteArray.shouldMatchResource("/path/to/test_resource.bin", ::providedMatcher)`    | Asserts that the byteArray matches to the given resource using `providedMatcher`.                                                                        |
| `byteArray.shouldNotMatchResource("/path/to/test_resource.bin", ::providedMatcher)` | Asserts that the byteArray **not** matches to the given resource using `providedMatcher`.                                                                |

