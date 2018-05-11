Matchers
==========

This page lists all current matchers in Kotlintest. Matchers can be used in two styles:

 * Infix functions like `a shouldBe b` or `a should startWith("foo")`
 * Extension functions like `a.shouldBe(b)` or `a.shouldStartWith("foo")`

Both styles are supported. The advantage of the extension function style is that the IDE can autocomplete for you,
 but some people may prefer the infix style as it is slightly cleaner.

Matchers can be negated by using `shouldNot` instead of `should` for the infix style. For example, `a shouldNot startWith("boo")`.
For the extension function style, each function has an equivalent negated version, for example, `a.shouldNotStartWith("boo")`.

The following table shows the matchers available in the `kotlintest-assertions` module, which is usually added to the build
when you add a KotlinTest test runner to your build (eg, `kotlintest-runner-junit5`). Of course, you could always add
this to your build explicitly.

| General |
| -------- |
| `obj.shouldBe(other)`<br/>General purpose assertion that the given obj and other are both equal |
| `expr.shouldBeTrue()`<br/>Convenience assertion that the expression is true. Equivalent to `expr.shouldBe(true)` |
| `expr.shouldBeFalse()`<br/>Convenience assertion that the expression is false. Equivalent to `expr.shouldBe(false)` |
| `shouldThrow<E> { block }`<br/>General purpose construct that asserts that the block throws an exception of E or a subtype of E |
| `shouldThrowExactly<E> { block }`<br/>General purpose construct that asserts that the block throws an exception of exactly E |
| `shouldThrowAny<E> { block }`<br/>General purpose construct that asserts that the block throws an exception of any type (including Throwable) |

| Types |
| ------- |
| `obj.shouldBeSameInstanceAs(other)`<br/>Compares objects by identity, that is, they are the same exact reference. |
| `obj.shouldBeTypeOf<T>()`<br/>Asserts that the given reference is exactly of type T. Subclass will fail. Ie, `1 should beOfType<Number>` would fail because although 1 _is_ a Number, the runtime type is not Number. |
| `obj.shouldBeInstanceOf<T>`<br/>Asserts that the given reference is of type T or a subclass of T. |
| `obj.shouldHaveAnnotation(annotationClass)<br/>Asserts that the object has an annotation of the given type. |

| Maps |
| -------- |
| `map.shouldContainKey(key)`<br/>Asserts that the map contains a key called `key` with any value |
| `map.shouldContainKeys(keys)`<br/>Asserts that the map contains mappings for all the given keys. |
| `map.shouldContainValue(value)`<br/>Asserts that the map contains at least one mapping where the value is `value`. |
| `map.shouldContainValues(values)`<br/>Asserts that the map contains all the given values. |
| `map.shouldContain("key", "value")`<br/>Asserts that the map contains the mapping "key" to "value" |
| `map.shouldContainAll(other)`<br/>Asserts that the map contains all the pairs from the given map. |
| `map.shouldContainExactly(other)`<br/>Asserts that the map contains exactly the pairs from given map, and no extra. |

| Strings |
| -------- |
| `str.shouldEndWith("suffix")`<br/>Asserts that the string ends with the given suffix. The suffix can be equal to the string. This matcher is case sensitive. To make this case insensitive call `toLowerCase()` on the value before the matcher. |
| `str.shouldStartWith("prefix")`<br/>Asserts that the string starts with the given prefix. The prefix can be equal to the string. This matcher is case sensitive. To make this case insensitive call `toLowerCase()` on the value before the matcher. |
| `str.shouldContain("substr")`<br/>Asserts that the string includes the given substring. The substring can be equal to the string. This matcher is case sensitive. To make this case insensitive call `toLowerCase()` on the value before the matcher. |
| `str.shouldContainIgnoringCase(substring)`<br/>Asserts that the string contains the substring ignoring case. |
| `str.shouldContainOnlyDigits()`<br/>Asserts that the string contains only digits, or is empty. |
| `str.shouldContainADigit()`<br/>Asserts that the string contains at least one digit. |
| `str.shouldContainOnlyOnce(substring)`<br/>Asserts that the string contains the substring exactly once. |
| `str.shouldMatch(regex)`<br/>Asserts that the string fully matches the given regex. |
| `str.shouldHaveLength(length)`<br/>Asserts that the string has the given length. |
| `str.shouldHaveSameLengthAs(length)`<br/>Asserts that the string has the same length as another string. |
| `str.shouldBeLowerCase()`<br/>Asserts that the string is all in lower case. |
| `str.shouldBeUpperCase()`<br/>Asserts that the string is all in upper case. |
| `str.shouldBeEmpty()`<br/>Asserts that the string has length zero. |
| `str.shouldBeBlank()`<br/>Asserts that the string contains only whitespace, or is empty. |
| `str.shouldHaveLineCount(count)`<br/>Asserts that the string contains the given number of lines. Similar to `str.split("\n").length.shouldBe(n)` |

| Integers |
| -------- |
| `int.shouldBeBetween(x, y)`<br/>Asserts that the integer is between x and y, inclusive on both x and y |
| `int.shouldBeLessThan(n)`<br/>Asserts that the integer is less than the given value n |
| `int.shouldBeLessThanOrEqual(n)`<br/>Asserts that the integer is less or equal to than the given value n |
| `int.shouldBeGreaterThan(n)`<br/>Asserts that the integer is greater than the given value n |
| `int.shouldBeGreaterThanOrEqual(n)`<br/>Asserts that the integer is greater than or equal to the given value n |
| `int.shouldBeEven()`<br/>Asserts that the integer is even. |
| `int.shouldBeOdd()`<br/>Asserts that the integer is odd. |
| `int.shouldBeInRange(range)`<br/>Asserts that the integer is included in the given range. |

| Longs |
| -------- |
| `long.shouldBeBetween(x, y)`<br/>Asserts that the long is between x and y, inclusive on both x and y |
| `long.shouldBeLessThan(n)`<br/>Asserts that the long is less than the given value n |
| `long.shouldBeLessThanOrEqual(n)`<br/>Asserts that the long is less or equal to than the given value n |
| `long.shouldBeGreaterThan(n)`<br/>Asserts that the long is greater than the given value n |
| `long.shouldBeGreaterThanOrEqual(n)`<br/>Asserts that the long is greater than or equal to the given value n |
| `long.shouldBeInRange(range)`<br/>Asserts that the long is included in the given range. |
| `long.shouldBeEven()`<br/>Asserts that the long is even. |
| `long.shouldBeOdd()`<br/>Asserts that the long is odd. |

| Doubles or Floats |
| -------- |
| `double.shouldBeExactly(value)`<br/>Asserts that the double is exactly equal to the given value. Exactly equal means the same representation. |
| `double.shouldBe(value plusOrMinus(tolerance))`<br/>Asserts that the double is equal to the given value within a tolerance range. This is the recommended way of testing for double equality. |
| `double.shouldBeBetween(x, y)`<br/>Asserts that the double is between x and y, inclusive on both x and y |
| `double.shouldBeLessThan(n)`<br/>Asserts that the double is less than the given value n |
| `double.shouldBeLessThanOrEqual(n)`<br/>Asserts that the double is less or equal to than the given value n |
| `double.shouldBeGreaterThan(n)`<br/>Asserts that the double is greater than the given value n |
| `double.shouldBeGreaterThanOrEqual(n)`<br/>Asserts that the double is greater than or equal to the given value n |

| Collections |
| -------- |
| `collection.shouldBeEmpty()`<br/>Asserts that the collections has zero elements. |
| `collection.shouldContain(element)`<br/>Asserts that the collection contains the given element. |
| `collection.shouldContainAll(e1, e2, ..., en)`<br/>Asserts that the collection contains all the elements listed, where order is not important. Ie, element 2 can be in the collection before element 1.  |
| `collection.shouldHaveSize(length)`<br/>Asserts that the collection is exactly the given length. |
| `collection.shouldHaveSingleElement(element)`<br/>Asserts that the collection only contains a single element and that that element is the given one. |
| `collection.shouldBeSorted()`<br/>Asserts that the collection is in sorted order. |
| `collection.shouldContainNoNulls()`<br/>Asserts that the collection contains no null elements, or is empty. |
| `collection.shouldContainOnlyNulls()`<br/>Asserts that the collection contains only null elements, or is empty. |
| `collection.shouldContainNull()`<br/>Asserts that the collection contains at least one null element. |
| `collection.shouldContainDuplicates()`<br/>Asserts that the collection contains at least one duplicate element. |

| `list.shouldContainInOrder(other)`<br/>Asserts that this list contains the given list in order. Other elements may appear either side of the given list. |
| `list.shouldContainElementAt()`<br/>Asserts that this list contains the given element at the given position. |

| URIs |
| -------- |
| `uri.shouldHaveScheme(scheme)`<br/>Asserts that the uri has the given scheme. |
| `uri.shouldHaveHost(scheme)`<br/>Asserts that the uri has the given hostname. |
| `uri.shouldHavePort(scheme)`<br/>Asserts that the uri has the given port. |
| `uri.shouldHavePath(scheme)`<br/>Asserts that the uri has the given path. |
| `uri.shouldHaveParameter(scheme)`<br/>Asserts that the uri's query string contains the given parameter. |
| `uri.shouldHaveFragemnt(fragment)`<br/>Asserts that the uri has the given fragment. |

| Files |
| -------- |
| `file.shouldExist()`<br/>Asserts that the file exists on disk, either a directory or as a file. |
| `file.shouldBeAbsolute()`<br/>Asserts that the file represents an absolute path. |
| `file.shouldBeADirectory()`<br/>Asserts that the file denotes a directory. |
| `file.shouldBeAFile()`<br/>Asserts that the file denotes a file. |
| `file.shouldBeCanonical()`<br/>Asserts that the file is in canonical format. |
| `file.shouldBeEmpty()`<br/>Asserts that the file exists but is empty. |
| `file.shouldBeExecutable()`<br/>Asserts that the file is executable by the current process. |
| `file.shouldBeHidden()`<br/>Asserts that the file exists on disk and is a hidden file. |
| `file.shouldBeReadable()`<br/>Asserts that the file is readable by the current process. |
| `file.shouldBeRelative()`<br/>Asserts that the file represents a relative path. |
| `file.shouldBeWriteable()`<br/>Asserts that the file is writeable by the current process. |
| `file.shouldHaveExtension(ext)`<br/>Asserts that the file ends with the given extension. |
| `file.shouldHaveFileSize(size)`<br/>Asserts that the file has the given file size. |
| `file.shouldHaveName(name)`<br/>Asserts that the file's name matches the given name. |
| `file.shouldHavePath(path)`<br/>Asserts that the file's path matches the given path. |
| `file.shouldStartWithPath(prefix)`<br/>Asserts that the file's path starts with the given prefix. |

| Dates |
| -------- |
| `date.shouldHaveSameYearAs(otherDate)`<br/>Asserts that the date has the same year as the given date. |
| `date.shouldHaveSameMonthAs(otherDate)`<br/>Asserts that the date has the same month as the given date. |
| `date.shouldHaveSameDayAs(otherDate)`<br/>Asserts that the date has the same day of the month as the given date. |
| `date.shouldBeBefore(otherDate)`<br/>Asserts that the date is before the given date. |
| `date.shouldBeAfter(otherDate)`<br/>Asserts that the date is after the given date. |
| `date.shouldBeWithin(period, otherDate)`<br/>Asserts that the date is within the period of the given date. |

| Concurrent |
| -------- |
| `shouldCompleteWithin(timeout, unit, function)`<br/>Asserts that the given function completes within the given duration. |
| `shouldTimeout(timeout, unit, function)`<br/>Asserts that given function does not complete within the given duration. |