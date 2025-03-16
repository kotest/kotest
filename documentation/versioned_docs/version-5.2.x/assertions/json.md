---
title: Json Matchers
slug: json-matchers.html
sidebar_label: Json
---

Kotest provides powerful JSON assertions in the `kotest-assertions-json` module.
These allow flexible testing of json strings without the need to worry about formatting or ordering.
They provide precise error messages when comparing json so that the error can be easily found in a large json structure.

This module is available for JVM and JS targets.

## shouldEqualJson

`json.shouldEqualJson(other)` asserts that the left-hand side represents the same
JSON structure as the right-hand side.

The matcher allows for different formatting, and for different order of keys.

For example, the following two JSON strings would be considered equal:

```json
{
   "name": "sam",
   "location": "chicago",
   "age" : 41
}
```

and

```json
{ "age" : 41, "name": "sam", "location": "chicago" }
```

The inverse of this matcher is `shouldNotEqualJson` which will error if two JSON strings
_are_ considered equal.

### compareJsonOptions
`shouldEqualJson` supports an additional parameter of type `CompareJsonOptions` which supports the following flags to toggle behaviour of the JSON comparison:

#### Usage:

Options can be specified inline, like:
```kotlin
a.shouldEqualJson(b, compareJsonOptions { arrayOrder = ArrayOrder.Strict })
```

Another option is to define a compare function which suits your desires, like:
```kotlin
val myOptions = compareJsonOptions {
   typeCoercion = TypeCoercion.Enabled
   arrayOrder = ArrayOrder.Lenient
}

infix fun String.lenientShouldEqualJson(other: String) = this.shouldEqualJson(other, myOptions)

"[1, 2]" lenientShouldEqualJson "[2, 1]" // This will pass
```

#### Parameters
| Name  | Purpose  | Possible values | Default value |
|---|---|---|---|
| `PropertyOrder`  | Determines if the order of properties in JSON objects are considered when comparing | `PropertyOrder.Strict`, `PropertyOrder.Lenient`  |  `PropertyOrder.Lenient`, i.e. order of properties *DON'T* matter  |
| `ArrayOrder`  | Determines if the order of elements in JSON arrays are considered when comparing | `ArrayOrder.Strict`, `ArrayOrder.Lenient`  | `ArrayOrder.Strict`, i.e. order of elements *DO* matter |
| `FieldComparison`  | Determines if comparison will fail if JSON objects `actual` contain extra properties, when compared to `expected`  | `FieldComparison.Strict`, `FieldComparison.Lenient` | `FieldComparison.Strict`, i.e. extra properties will cause inequality |
| `NumberFormat`  | Determines if comparison of numbers are strict with regards to number format. For instance, if 100.0 and 100 are considered equal.  | `NumberFormat.Strict`, `NumberFormat.Lenient`  | `NumberFormat.Lenient`, i.e. number formats *DON'T* matter  |
| `TypeCoercion` | Determines if types will try to be coerced, for instance when a string contains a number or boolean value  | `TypeCoercion.Enabled`, `TypeCoercion.Disabled`  | `TypeCoercion.Disabled`, i.e. types will *NOT* be coerced  |

Targets: **JVM**, **JS**

### shouldEqualSpecifiedJson
Alias for `shouldEqualJson`, with default options except `FieldComparison` which is set to `FieldComparison.Lenient` instead.

```kotlin
val a = """ { "a": true, "date": "2019-11-03" } """
val b = """ { "a": true } """

// this would pass
a shouldEqualSpecifiedJson b

// this would fail
a shouldEqualJson b
```

Targets: **JVM**, **JS**

## shouldContainJsonKey

`json.shouldContainJsonKey("$.json.path")` asserts that a JSON string contains the given JSON path.

The inverse of this matcher is `shouldNotContainJsonKey` which will error if a JSON string _does_ contain the given JSON path.

Targets: **JVM**

## shouldContainJsonKeyValue

`str.shouldContainJsonKeyValue("$.json.path", value)` asserts that a JSON string contains a JSON path with a specific `value`.

The inverse of this matcher is `shouldNotContainJsonKeyValue` which will error if a JSON string _does_ contain the given value at the given JSON path.

Targets: **JVM**

## shouldMatchJsonResource

`json.shouldMatchJsonResource("/file.json")` asserts that the JSON is equal to the existing test reosource `/file.json`, ignoring properties' order and formatting.

Targets: **JVM**

## Basic JSON Validation
There are a few matchers that simply validate that JSON is valid and optionally of a certain type.

### shouldBeValidJson
`shouldBeValidJson` simply verifies that a given string parses to valid json. The inverse is `shouldNotBeValidJson` which will error if the string is valid json.

Targets: **JVM** Since: **5.2**

### shouldBeJsonObject
`shouldBeJsonObject` asserts that a string is a valid JSON **_object_**. The inverse is `shouldNotBeJsonObject` which will error if the string is an object.

Targets: **JVM** Since: **5.2**

### shouldBeJsonArray
`shouldBeJsonArray` asserts that a string is a valid JSON **_array_**. The inverse is `shouldNotBeJsonArray` which will error if the string is an array.

Targets: **JVM** Since: **5.2**
