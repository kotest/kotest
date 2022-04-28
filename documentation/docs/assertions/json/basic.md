---
title: Basic JSON Matchers
slug: basic-json-matchers.html
sidebar_label: Basic matchers
---

Kotest provides powerful JSON assertions in the `kotest-assertions-json` module.
These allow flexible testing of json strings without the need to worry about formatting or ordering.
They provide precise error messages when comparing json so that the error can be easily found in a large json structure.

This module is available for multiplatform targets, but some assertions are restricted to JVM.

There are a few matchers that simply validate that JSON is valid and optionally of a certain type.

### shouldBeValidJson
`shouldBeValidJson` simply verifies that a given string parses to valid json. The inverse is `shouldNotBeValidJson` which will error if the string is valid json.

Targets: **Multiplatform**

### shouldBeJsonObject
`shouldBeJsonObject` asserts that a string is a valid JSON **_object_**. The inverse is `shouldNotBeJsonObject` which will error if the string is an object.

Targets: **Multiplatform**

### shouldBeJsonArray
`shouldBeJsonArray` asserts that a string is a valid JSON **_array_**. The inverse is `shouldNotBeJsonArray` which will error if the string is an array.

Targets: **Multiplatform**
