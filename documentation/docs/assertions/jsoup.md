---
title: Jsoup Matchers
slug: jsoup-matchers.html
sidebar_label: Jsoup
---




This page lists all current matchers in the KotlinTest jsoup matchers extension library. To use this library
 you need to add `kotlintest-assertions-jsoup` to your build.

| Element                                         |                                                                 |
|-------------------------------------------------|-----------------------------------------------------------------|
| `element.shouldHaveChildWithTag(tag)`           | Asserts that the element has a child with the given tag         |
| `element.shouldHaveText(text)`                  | Asserts that the element has the given text                     |
| `element.shouldHaveAttribute(name)`             | Asserts that the element has an attribute with the given name   |
| `element.shouldHaveAttributeValue(name, value)` | Asserts that the element have an attribute with the given value |

| Elements                      |                                                |
|-------------------------------|------------------------------------------------|
| `elements.shouldBePresent()`  | Asserts that the Elements object has some item |
| `elements.shouldBePresent(n)` | Asserts that the Elements object has N items   |
| `elements.shouldBePresent(n)` | Asserts that the Elements object has N items   |

| HTML                                   |                                                                     |
|----------------------------------------|---------------------------------------------------------------------|
| `element.shouldHaveId(id)`             | Asserts that the element has an attribute id with the given value   |
| `element.shouldHaveClass(class)`       | Asserts that the element has the specified class                    |
| `element.shouldHaveSrc(src)`           | Asserts that the element has an attribute src with the given value  |
| `element.shouldHaveHref(href)`         | Asserts that the element has an attribute href with the given value |
| `element.shouldHaveElementWithId(id)`  | Asserts that the element has a child with the given id              |
| `element.shouldHaveChildWithClass(id)` | Asserts that the element has a child with the given class           |
