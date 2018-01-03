Matchers
==========

This page lists all current matchers in Kotlintest.

| General Matchers |
| -------- |
| `obj shouldBe other`<br/>General purpose assertion that the given obj and other are both equal |
| `shouldThrow<E> { block }`<br/>General purpose construct that asserts that the block throws an exception exactly of type E |
| `obj should beTheSameInstanceAs(other)`<br/>Compares objects by identity, that is, they are the same exact reference. |
| `obj should beOfType<T>`<br/>Asserts that the given reference is exactly of type T. Subclass will fail. Ie, `1 should beOfType<Number>` would fail because although 1 _is_ a Number, the runtime type is not Number. |

| Maps |
| -------- |
| `map should haveKey("key")`<br/>Asserts that the map contains a key called "key" with any value |
| `map should haveValue("value")`<br/>Asserts that the map contains at least one mapping where the value is "value". |
| `map should contain("key", "value")`<br/>Asserts that the map contains the mapping "key" to "value" |

| Strings |
| -------- |
| `str should endWith("suffix")`<br/>Asserts that the string ends with the given suffix. The suffix can be equal to the string. This matcher is case sensitive. To make this case insensitive call `toLowerCase()` on the value before the matcher. |
| `str should startWith("prefix")`<br/>Asserts that the string starts with the given prefix. The prefix can be equal to the string. This matcher is case sensitive. To make this case insensitive call `toLowerCase()` on the value before the matcher. |
| `str should include("substr")`<br/>Asserts that the string includes the given substring. The substring can be equal to the string. This matcher is case sensitive. To make this case insensitive call `toLowerCase()` on the value before the matcher. |
| `str should match(regex)`<br/>Asserts that the string fully matches the given regex. |
| `str should haveLength(length)`<br/>Asserts that the string has the given length. |

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

| Doubles |
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
