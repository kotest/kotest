Matchers
==========

This page lists all current matchers in Kotlintest. Matchers can be used in two styles:

 * Extension functions like `a.shouldBe(b)` or `a.shouldStartWith("foo")`
 * Infix functions like `a shouldBe b` or `a should startWith("foo")`

Both styles are supported. The advantage of the extension function style is that the IDE can autocomplete for you,
 but some people may prefer the infix style as it is slightly cleaner.

Matchers can be negated by using `shouldNot` instead of `should` for the infix style. For example, `a shouldNot startWith("boo")`.
For the extension function style, each function has an equivalent negated version, for example, `a.shouldNotStartWith("boo")`.

| General | |
| -------- | ---- |
| `obj.shouldBe(other)` | General purpose assertion that the given obj and other are both equal |
| `expr.shouldBeTrue()` | Convenience assertion that the expression is true. Equivalent to `expr.shouldBe(true)` |
| `expr.shouldBeFalse()` | Convenience assertion that the expression is false. Equivalent to `expr.shouldBe(false)` |
| `shouldThrow<E> { block }` | General purpose construct that asserts that the block throws an exception of E or a subtype of E |
| `shouldThrowExactly<E> { block }` | General purpose construct that asserts that the block throws an exception of exactly E |
| `shouldThrowAny<E> { block }` | General purpose construct that asserts that the block throws an exception of any type (including Throwable) |

| Types ||
| ------- | ---- |
| `obj.shouldBeSameInstanceAs(other)` | Compares objects by identity, that is, they are the same exact reference. |
| `obj.shouldBeTypeOf<T>()` | Asserts that the given reference is exactly of type T. Subclass will fail. Ie, `1 should beOfType<Number>` would fail because although 1 _is_ a Number, the runtime type is not Number. |
| `obj.shouldBeInstanceOf<T>` | Asserts that the given reference is of type T or a subclass of T. |
| `obj.shouldHaveAnnotation(annotationClass)` | Asserts that the object has an annotation of the given type. |

| Maps ||
| -------- | ---- |
| `map.shouldContainKey(key)` | Asserts that the map contains a key called `key` with any value |
| `map.shouldContainKeys(keys)` | Asserts that the map contains mappings for all the given keys. |
| `map.shouldContainValue(value)` | Asserts that the map contains at least one mapping where the value is `value`. |
| `map.shouldContainValues(values)` | Asserts that the map contains all the given values. |
| `map.shouldContain("key", "value")` | Asserts that the map contains the mapping "key" to "value" |
| `map.shouldContainAll(other)` | Asserts that the map contains all the pairs from the given map. |
| `map.shouldContainExactly(other)` | Asserts that the map contains exactly the pairs from given map, and no extra. |

| Strings ||
| -------- | ---- |
| `str.shouldEndWith("suffix")` | Asserts that the string ends with the given suffix. The suffix can be equal to the string. This matcher is case sensitive. To make this case insensitive call `toLowerCase()` on the value before the matcher. |
| `str.shouldStartWith("prefix")` | Asserts that the string starts with the given prefix. The prefix can be equal to the string. This matcher is case sensitive. To make this case insensitive call `toLowerCase()` on the value before the matcher. |
| `str.shouldContain("substr")` | Asserts that the string includes the given substring. The substring can be equal to the string. This matcher is case sensitive. To make this case insensitive call `toLowerCase()` on the value before the matcher. |
| `str.shouldContainIgnoringCase(substring)` | Asserts that the string contains the substring ignoring case. |
| `str.shouldContainOnlyDigits()` | Asserts that the string contains only digits, or is empty. |
| `str.shouldContainADigit()` | Asserts that the string contains at least one digit. |
| `str.shouldContainOnlyOnce(substring)` | Asserts that the string contains the substring exactly once. |
| `str.shouldMatch(regex)` | Asserts that the string fully matches the given regex. |
| `str.shouldHaveLength(length)` | Asserts that the string has the given length. |
| `str.shouldHaveSameLengthAs(length)` | Asserts that the string has the same length as another string. |
| `str.shouldBeLowerCase()` | Asserts that the string is all in lower case. |
| `str.shouldBeUpperCase()` | Asserts that the string is all in upper case. |
| `str.shouldBeEmpty()` | Asserts that the string has length zero. |
| `str.shouldBeBlank()` | Asserts that the string contains only whitespace, or is empty. |
| `str.shouldHaveLineCount(count)` | Asserts that the string contains the given number of lines. Similar to `str.split("\n").length.shouldBe(n)` |

| Integers ||
| -------- | ---- |
| `int.shouldBeBetween(x, y)` | Asserts that the integer is between x and y, inclusive on both x and y |
| `int.shouldBeLessThan(n)` | Asserts that the integer is less than the given value n |
| `int.shouldBeLessThanOrEqual(n)` | Asserts that the integer is less or equal to than the given value n |
| `int.shouldBeGreaterThan(n)` | Asserts that the integer is greater than the given value n |
| `int.shouldBeGreaterThanOrEqual(n)` | Asserts that the integer is greater than or equal to the given value n |
| `int.shouldBeEven()` | Asserts that the integer is even. |
| `int.shouldBeOdd()` | Asserts that the integer is odd. |
| `int.shouldBeInRange(range)` | Asserts that the integer is included in the given range. |

| Longs ||
| -------- | ---- |
| `long.shouldBeBetween(x, y)` | Asserts that the long is between x and y, inclusive on both x and y |
| `long.shouldBeLessThan(n)` | Asserts that the long is less than the given value n |
| `long.shouldBeLessThanOrEqual(n)` | Asserts that the long is less or equal to than the given value n |
| `long.shouldBeGreaterThan(n)` | Asserts that the long is greater than the given value n |
| `long.shouldBeGreaterThanOrEqual(n)` | Asserts that the long is greater than or equal to the given value n |
| `long.shouldBeInRange(range)` | Asserts that the long is included in the given range. |
| `long.shouldBeEven()` | Asserts that the long is even. |
| `long.shouldBeOdd()` | Asserts that the long is odd. |

| Doubles or Floats ||
| -------- | ---- |
| `double.shouldBeExactly(value)` | Asserts that the double is exactly equal to the given value. Exactly equal means the same representation. |
| `double.shouldBe(value plusOrMinus(tolerance))` | Asserts that the double is equal to the given value within a tolerance range. This is the recommended way of testing for double equality. |
| `double.shouldBeBetween(x, y)` | Asserts that the double is between x and y, inclusive on both x and y |
| `double.shouldBeLessThan(n)` | Asserts that the double is less than the given value n |
| `double.shouldBeLessThanOrEqual(n)` | Asserts that the double is less or equal to than the given value n |
| `double.shouldBeGreaterThan(n)` | Asserts that the double is greater than the given value n |
| `double.shouldBeGreaterThanOrEqual(n)` | Asserts that the double is greater than or equal to the given value n |

| Collections ||
| -------- | ---- |
| `collection.shouldBeEmpty()` | Asserts that the collections has zero elements. |
| `collection.shouldContain(element)` | Asserts that the collection contains the given element. |
| `collection.shouldContainAll(e1, e2, ..., en)` | Asserts that the collection contains all the elements listed, where order is not important. Ie, element 2 can be in the collection before element 1.  |
| `collection.shouldHaveSize(length)` | Asserts that the collection is exactly the given length. |
| `collection.shouldHaveSingleElement(element)` | Asserts that the collection only contains a single element and that that element is the given one. |
| `collection.shouldBeSorted()` | Asserts that the collection is in sorted order. |
| `collection.shouldContainNoNulls()` | Asserts that the collection contains no null elements, or is empty. |
| `collection.shouldContainOnlyNulls()` | Asserts that the collection contains only null elements, or is empty. |
| `collection.shouldContainNull()` | Asserts that the collection contains at least one null element. |
| `collection.shouldContainDuplicates()` | Asserts that the collection contains at least one duplicate element. |
| `list.shouldContainInOrder(other)` | Asserts that this list contains the given list in order. Other elements may appear either side of the given list. |
| `list.shouldContainElementAt()` | Asserts that this list contains the given element at the given position. |

| URIs ||
| -------- | ---- |
| `uri.shouldHaveScheme(scheme)` | Asserts that the uri has the given scheme. |
| `uri.shouldHaveHost(scheme)` | Asserts that the uri has the given hostname. |
| `uri.shouldHavePort(scheme)` | Asserts that the uri has the given port. |
| `uri.shouldHavePath(scheme)` | Asserts that the uri has the given path. |
| `uri.shouldHaveParameter(scheme)` | Asserts that the uri's query string contains the given parameter. |
| `uri.shouldHaveFragemnt(fragment)` | Asserts that the uri has the given fragment. |

| Files ||
| -------- | ---- |
| `file.shouldExist()` | Asserts that the file exists on disk, either a directory or as a file. |
| `file.shouldBeAbsolute()` | Asserts that the file represents an absolute path. |
| `file.shouldBeADirectory()` | Asserts that the file denotes a directory. |
| `file.shouldBeAFile()` | Asserts that the file denotes a file. |
| `file.shouldBeCanonical()` | Asserts that the file is in canonical format. |
| `file.shouldBeEmpty()` | Asserts that the file exists but is empty. |
| `file.shouldBeExecutable()` | Asserts that the file is executable by the current process. |
| `file.shouldBeHidden()` | Asserts that the file exists on disk and is a hidden file. |
| `file.shouldBeReadable()` | Asserts that the file is readable by the current process. |
| `file.shouldBeRelative()` | Asserts that the file represents a relative path. |
| `file.shouldBeWriteable()` | Asserts that the file is writeable by the current process. |
| `file.shouldHaveExtension(ext)` | Asserts that the file ends with the given extension. |
| `file.shouldHaveFileSize(size)` | Asserts that the file has the given file size. |
| `file.shouldHaveName(name)` | Asserts that the file's name matches the given name. |
| `file.shouldHavePath(path)` | Asserts that the file's path matches the given path. |
| `file.shouldStartWithPath(prefix)` | Asserts that the file's path starts with the given prefix. |

| Dates ||
| -------- | ---- |
| `date.shouldHaveSameYearAs(otherDate)` | Asserts that the date has the same year as the given date. |
| `date.shouldHaveSameMonthAs(otherDate)` | Asserts that the date has the same month as the given date. |
| `date.shouldHaveSameDayAs(otherDate)` | Asserts that the date has the same day of the month as the given date. |
| `date.shouldBeBefore(otherDate)` | Asserts that the date is before the given date. |
| `date.shouldBeAfter(otherDate)` | Asserts that the date is after the given date. |
| `date.shouldBeWithin(period, otherDate)` | Asserts that the date is within the period of the given date. |

| Concurrent ||
| -------- | ---- |
| `shouldCompleteWithin(timeout, unit, function)` | Asserts that the given function completes within the given duration. |
| `shouldTimeout(timeout, unit, function)` | Asserts that given function does not complete within the given duration. |

| Futures ||
| -------- | ---- |
| `shouldBeCancelled()` | Asserts that the future has been cancelled. |
| `shouldBeCompleted()` | Asserts that the future has completed. |
| `shouldBeCompletedExceptionally()` | Asserts that the the future has completed with an exception. |
