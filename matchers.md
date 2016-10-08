Matchers
==========

This page lists all current matchers in Kotlintest.

| Matcher | Brief Description |
|----|-----------|
| `obj shouldBe other` | General purpose assertion that the given obj and other are both equal |
| `shouldThrow<E> { block } | General purpose construct that asserts that the block throws an exception exactly of type E |
| `map should haveKey("key")` | Asserts that the map contains a key called "key" with any value |
| `map should haveValue("value")` | Asserts that the map contains at least one mapping where the value is "value". |
| `map should contain("key", "value")` | Asserts that the map contains the mapping "key" to "value" |
| `str should endWith("suffix")` | Asserts that the string ends with the given suffix. The suffix can be equal to the string. This matcher is case sensitive. To make this case insensitive call `toLowerCase()` on the value before the matcher. |
| `str should startWith("prefix")` | Asserts that the string starts with the given prefix. The prefix can be equal to the string. This matcher is case sensitive. To make this case insensitive call `toLowerCase()` on the value before the matcher. |
| `str should include("substr")` | Asserts that the string includes the given substring. The substring can be equal to the string. This matcher is case sensitive. To make this case insensitive call `toLowerCase()` on the value before the matcher. |
| `str should match(regex)` | Asserts that the string fully matches the given regex. |
| `str should haveLength(length)` | Asserts that the string has the given length. |
| `int shouldBe between(x, y)` | Asserts that the integer is between x and y, inclusive on both x and y |
| `int shouldBe lt(n)` | Asserts that the integer is less than the given value n |
| `int shouldBe lte(n)` | Asserts that the integer is less or equal to than the given value n |
| `int shouldBe gt(n)` | Asserts that the integer is greater than the given value n |
| `int shouldBe gte(n)` | Asserts that the integer is greater than or equal to the given value n |
| `long shouldBe between(x, y)` | Asserts that the long is between x and y, inclusive on both x and y |
| `long shouldBe lt(n)` | Asserts that the long is less than the given value n |
| `long shouldBe lte(n)` | Asserts that the long is less or equal to than the given value n |
| `long shouldBe gt(n)` | Asserts that the long is greater than the given value n |
| `long shouldBe gte(n)` | Asserts that the long is greater than or equal to the given value n |
| `double shouldBe exactly(value)` | Asserts that the double is exactly equal to the given value. Exactly equal means the same representation. |
| `double shouldBe (value plusOrMinus(tolerance)) | Asserts that the double is equal to the given value within a tolerance range. This is the recommended way of testing for double equality. |
| `collection should beEmpty()` | Asserts that the collections has zero elements. |
| `collection should contain(element)` | Asserts that the collection contains the given element. |
| `collection should containInAnyOrder(e1, e2, ..., en)` | Asserts that the collection contains all the elements listed, where order is not important. Ie, element 2 can be in the collection before element 1.  |
| `collection should haveSize(length)` | Asserts that the collection is exactly the given length. |
| `collection shouldBe singleElement(element)` | Asserts that the collection only contains a single element and that that element is the given one. |
| `collection shouldBe sorted()` | Asserts that the collection is in sorted order. |
| `obj should beTheSameInstanceAs(other)` | Compares objects by identity, that is, they are the same exact reference. |
| `obj should beOfType<T>` | Asserts that the given reference is exactly of type T. Subclass will fail. Ie, `1 should beOfType<Number>` would fail because although 1 _is_ a Number, the runtime type is not Number. |