Matchers
==========

This page lists all current matchers in Kotlintest.

| General Matchers |
| -------- |
| `obj shouldBe other`<br/>General purpose assertion that the given obj and other are both equal |
| `shouldThrow<E> { block }`<br/>General purpose construct that asserts that the block throws an exception of E or a subtype of E |
| `shouldThrowExactly<E> { block }`<br/>General purpose construct that asserts that the block throws an exception of exactly E |
| `shouldThrowAny<E> { block }`<br/>General purpose construct that asserts that the block throws an exception of any type (including Throwable) |
| `obj should beTheSameInstanceAs(other)`<br/>Compares objects by identity, that is, they are the same exact reference. |
| `obj should beOfType<T>`<br/>Asserts that the given reference is exactly of type T. Subclass will fail. Ie, `1 should beOfType<Number>` would fail because although 1 _is_ a Number, the runtime type is not Number. |
| `obj should beInstanceOf<T>`<br/>Asserts that the given reference is of type T or a subclass of T. |

| Maps |
| -------- |
| `map should haveKey(key)`<br/>Asserts that the map contains a key called `key` with any value |
| `map should haveKeys(keys)`<br/>Asserts that the map contains mappings for all the given keys. |
| `map should haveValue(value)`<br/>Asserts that the map contains at least one mapping where the value is `value`. |
| `map should haveValues(values)`<br/>Asserts that the map contains all the given values. |
| `map should contain("key", "value")`<br/>Asserts that the map contains the mapping "key" to "value" |
| `map should containAll(other)`<br/>Asserts that the map contains all the pairs from the given map. |
| `map should containExactly(other)`<br/>Asserts that the map contains exactly the pairs from given map, and no extra. |

| Strings |
| -------- |
| `str should endWith("suffix")`<br/>Asserts that the string ends with the given suffix. The suffix can be equal to the string. This matcher is case sensitive. To make this case insensitive call `toLowerCase()` on the value before the matcher. |
| `str should startWith("prefix")`<br/>Asserts that the string starts with the given prefix. The prefix can be equal to the string. This matcher is case sensitive. To make this case insensitive call `toLowerCase()` on the value before the matcher. |
| `str should include("substr")`<br/>Asserts that the string includes the given substring. The substring can be equal to the string. This matcher is case sensitive. To make this case insensitive call `toLowerCase()` on the value before the matcher. |
| `str should match(regex)`<br/>Asserts that the string fully matches the given regex. |
| `str should haveLength(length)`<br/>Asserts that the string has the given length. |
| `str should haveSameLengthAs(length)`<br/>Asserts that the string has the same length as another string. |
| `str should beLowerCase()`<br/>Asserts that the string is all in lower case. |
| `str should beUpperCase()`<br/>Asserts that the string is all in upper case. |
| `str should beEmpty()`<br/>Asserts that the string has length zero. |
| `str should beBlank()`<br/>Asserts that the string contains only whitespace, or is empty. |
| `str should containIgnoringCase(substring)`<br/>Asserts that the string contains the substring ignoring case. |
| `str should containOnlyDigits()`<br/>Asserts that the string contains only digits, or is empty. |
| `str should containADigit()`<br/>Asserts that the string contains at least one digit. |
| `str should containOnlyOnce(substring)`<br/>Asserts that the string contains the substring exactly once. |

| Integers |
| -------- |
| `int shouldBe between(x, y)`<br/>Asserts that the integer is between x and y, inclusive on both x and y |
| `int shouldBe lt(n)`<br/>Asserts that the integer is less than the given value n |
| `int shouldBe lte(n)`<br/>Asserts that the integer is less or equal to than the given value n |
| `int shouldBe gt(n)`<br/>Asserts that the integer is greater than the given value n |
| `int shouldBe gte(n)`<br/>Asserts that the integer is greater than or equal to the given value n |

| Longs |
| -------- |
| `long shouldBe between(x, y)`<br/>Asserts that the long is between x and y, inclusive on both x and y |
| `long shouldBe lt(n)`<br/>Asserts that the long is less than the given value n |
| `long shouldBe lte(n)`<br/>Asserts that the long is less or equal to than the given value n |
| `long shouldBe gt(n)`<br/>Asserts that the long is greater than the given value n |
| `long shouldBe gte(n)`<br/>Asserts that the long is greater than or equal to the given value n |

| Doubles or Floats |
| -------- |
| `double shouldBe exactly(value)`<br/>Asserts that the double is exactly equal to the given value. Exactly equal means the same representation. |
| `double shouldBe (value plusOrMinus(tolerance))`<br/>Asserts that the double is equal to the given value within a tolerance range. This is the recommended way of testing for double equality. |
| `double shouldBe lt(n)`<br/>Asserts that the double is less than the given value n |
| `double shouldBe lte(n)`<br/>Asserts that the double is less or equal to than the given value n |
| `double shouldBe gt(n)`<br/>Asserts that the double is greater than the given value n |
| `double shouldBe gte(n)`<br/>Asserts that the double is greater than or equal to the given value n |

| Collections |
| -------- |
| `collection should beEmpty()`<br/>Asserts that the collections has zero elements. |
| `collection should contain(element)`<br/>Asserts that the collection contains the given element. |
| `collection should containsAll(e1, e2, ..., en)`<br/>Asserts that the collection contains all the elements listed, where order is not important. Ie, element 2 can be in the collection before element 1.  |
| `collection should haveSize(length)`<br/>Asserts that the collection is exactly the given length. |
| `collection shouldBe singleElement(element)`<br/>Asserts that the collection only contains a single element and that that element is the given one. |
| `collection shouldBe sorted()`<br/>Asserts that the collection is in sorted order. |
| `collection shouldBe containNoNulls()`<br/>Asserts that the collection contains no null elements, or is empty. |
| `collection shouldBe containOnlyNulls()`<br/>Asserts that the collection contains only null elements, or is empty. |

| URIs |
| -------- |
| `uri should haveScheme(scheme)`<br/>Asserts that the uri has the given scheme. |
| `uri should havePort(scheme)`<br/>Asserts that the uri has the given port. |
| `uri should haveHost(scheme)`<br/>Asserts that the uri has the given hostname. |

| Files |
| -------- |
| `file shouldBe aDirectory()`<br/>Asserts that the file denotes a directory. |
| `file shouldBe aFile()`<br/>Asserts that the file denotes a file. |
| `file should haveExtension(ext)`<br/>Asserts that the file's name ends with the given extension. |
| `file should exist()`<br/>Asserts that the file exists on disk, either a directory or as a file. |