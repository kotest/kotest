---
id: collections
title: Collection Matchers
slug: collection-matchers.html
sidebar_label: Collections
---

This page describes the rich assertions (matchers) that are available for Collection, Iterable and Array types.

Also, see [inspectors](inspectors.md) which are useful ways to test multiple elements in a collection.


| Collections                                                   |                                                                                                                                                                   |
|---------------------------------------------------------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `collection.shouldBeEmpty()`                                  | Asserts that the collections has zero elements.                                                                                                                   |
| `collection.shouldBeUnique()`                                 | Asserts that all the elements of the collection are distinct using the natural equals of the elements.                                                            |
| `collection.shouldBeUnique(comparator)`                       | Asserts that all the elements of the collection are distinct by comparing elements using the given `comparator`.                                                  |
| `collection.shouldContain(element)`                           | Asserts that the collection contains the given element.                                                                                                           |
| `collection.shouldContainAll(e1, e2, ..., en)`                | Asserts that the collection contains all the elements listed, where order is not important. Ie, element 2 can be in the collection before element 1.              |
| `collection.shouldContainDuplicates()`                        | Asserts that the collection contains at least one duplicate element.                                                                                              |
| `collection.shouldContainExactly(e1, e2, ..., en)`            | Assert that a collection contains exactly the given elements and nothing else, in order.                                                                          |
| `collection.shouldContainExactlyInAnyOrder(e1, e2, ..., en)`  | Assert that a collection contains exactly the given elements and nothing else, in _any_ order.                                                                    |
| `collection.shouldContainAllInAnyOrder(e1, e2, ..., en)`      | Assert that a collection contains all the given elements and nothing else, in _any_ order.                                                                        |
| `collection.shouldContainNoNulls()`                           | Asserts that the collection contains no null elements, or is empty.                                                                                               |
| `collection.shouldContainNull()`                              | Asserts that the collection contains at least one null element.                                                                                                   |
| `collection.shouldContainOnlyNulls()`                         | Asserts that the collection contains only null elements, or is empty.                                                                                             |
| `collection.shouldContainAllIgnoringFields()`                 | Asserts that the collection contains all the elements listed ignoring one or more fields.                                                                         |
| `collection.shouldHaveSingleElement(element)`                 | Asserts that the collection only contains a single element and that that element is the given one.                                                                |
| `collection.shouldHaveSingleElement { block }`                | Asserts that the collection contains a single element by a given predicate.                                                                                       |
| `collection.shouldHaveSize(length)`                           | Asserts that the collection is exactly the given length.                                                                                                          |
| `collection.shouldBeSingleton()`                              | Asserts that the collection contains only one element.                                                                                                            |
| `collection.shouldBeSingleton { block }`                      | Asserts that the collection only one element, and then, runs the block with this element.                                                                         |
| `collection.shouldHaveLowerBound(element)`                    | Asserts that the given element is smaller or equal to every element of the collection. Works only for elements that implement Comparable.                         |
| `collection.shouldHaveUpperBound(element)`                    | Asserts that the given element is larger or equal to every element of the collection. Works only for elements that implement Comparable.                          |
| `collection.shouldBeSmallerThan(col)`                         | Asserts that the collection is smaller than the other collection.                                                                                                 |
| `collection.shouldBeLargerThan(col)`                          | Asserts that the collection is larger than the other collection.                                                                                                  |
| `collection.shouldBeSameSizeAs(col)`                          | Asserts that the collection has the same size as the other collection.                                                                                            |
| `collection.shouldHaveAtLeastSize(n)`                         | Asserts that the collection has at least size n.                                                                                                                  |
| `collection.shouldHaveAtMostSize(n)`                          | Asserts that the collection has at most size n.                                                                                                                   |
| `list.shouldBeSorted()`                                       | Asserts that the list is sorted.                                                                                                                                  |
| `list.shouldBeSortedBy { transform }`                         | Asserts that the list is sorted by the value after applying the transform.                                                                                        |
| `list.shouldContainInOrder(other)`                            | Asserts that this list contains the given list in order. Other elements may appear either side of the given list.                                                 |
| `list.shouldExistInOrder({ element }, ...)`                   | Asserts that this list contains elements matching the predicates in order. Other elements may appear around or between the elements matching the predicates.      |
| `list.shouldHaveElementAt(index, element)`                    | Asserts that this list contains the given element at the given position.                                                                                          |
| `list.shouldStartWith(lst)`                                   | Asserts that this list starts with the elements of the given list, in order.                                                                                      |
| `list.shouldEndWith(lst)`                                     | Asserts that this list ends with the elements of the given list, in order.                                                                                        |
| `iterable.shouldMatchEach(assertions)`                        | Iterates over this list and the assertions and asserts that each element of this list passes the associated assertion. Fails if size of the collections mismatch. |
| `iterable.shouldMatchInOrder(assertions)`                     | Asserts that there is a subsequence of this iterator that matches the assertions in order, with no gaps allowed.                                                  |
| `iterable.shouldMatchInOrderSubset(assertions)`               | Asserts that there is a subsequence (possibly with gaps) that matches the assertions in order.                                                                    |
| `value.shouldBeOneOf(collection)`                             | Asserts that a specific instance is contained in a collection.                                                                                                    |
| `collection.shouldContainAnyOf(collection)`                   | Asserts that the collection has at least one of the elements in `collection`                                                                                      |
| `value.shouldBeIn(collection)`                                | Asserts that an object is contained in collection, checking by value and not by reference.                                                                        |
