---
title: Matching JSON content
slug: content-json-matchers.html
sidebar_label: Matching content
---

This module is available for all targets.

## shouldEqualJson

`json.shouldEqualJson(other)` asserts that the left-hand side represents the same
JSON structure as the right-hand side.

The matcher allows for different formatting, and for different order of keys.

For example, the following two JSON strings would be considered equal:

```json
{
  "name":     "sam",
  "location": "chicago",
  "age":      41
}
```

and

```json
{ "age": 41, "name": "sam", "location": "chicago" }
```

The inverse of this matcher is `shouldNotEqualJson` which will error if two JSON strings _are_ considered equal.

### compareJsonOptions

`shouldEqualJson` supports an additional parameter of type `CompareJsonOptions` which supports the following flags to
toggle behaviour of the JSON comparison:

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

| Name              | Purpose                                                                                                                            | Possible values                                     | Default value                                                         |
|-------------------|------------------------------------------------------------------------------------------------------------------------------------|-----------------------------------------------------|-----------------------------------------------------------------------|
| `PropertyOrder`   | Determines if the order of properties in JSON objects are considered when comparing                                                | `PropertyOrder.Strict`, `PropertyOrder.Lenient`     | `PropertyOrder.Lenient`, i.e. order of properties *DON'T* matter      |
| `ArrayOrder`      | Determines if the order of elements in JSON arrays are considered when comparing                                                   | `ArrayOrder.Strict`, `ArrayOrder.Lenient`           | `ArrayOrder.Strict`, i.e. order of elements *DO* matter               |
| `FieldComparison` | Determines if comparison will fail if JSON objects `actual` contain extra properties, when compared to `expected`                  | `FieldComparison.Strict`, `FieldComparison.Lenient` | `FieldComparison.Strict`, i.e. extra properties will cause inequality |
| `NumberFormat`    | Determines if comparison of numbers are strict with regards to number format. For instance, if 100.0 and 100 are considered equal. | `NumberFormat.Strict`, `NumberFormat.Lenient`       | `NumberFormat.Lenient`, i.e. number formats *DON'T* matter            |
| `TypeCoercion`    | Determines if types will try to be coerced, for instance when a string contains a number or boolean value                          | `TypeCoercion.Enabled`, `TypeCoercion.Disabled`     | `TypeCoercion.Disabled`, i.e. types will *NOT* be coerced             |

Targets: **Multiplatform**

## shouldEqualSpecifiedJson

Alias for `shouldEqualJson`, with default options except `FieldComparison` which is set to `FieldComparison.Lenient`
instead.

```kotlin
val a = """ { "a": true, "date": "2019-11-03" } """
val b = """ { "a": true } """

// this would pass
a shouldEqualSpecifiedJson b

// this would fail
a shouldEqualJson b
```

The inverse of this matcher is `shouldNotEqualSpecifiedJson` which will error if two JSON strings _are_ considered
equal.

Targets: **Multiplatform**

## shouldEqualSpecifiedJsonIgnoringOrder

Alias for `shouldEqualJson`, with default options except

- `FieldComparison` which is set to `FieldComparison.Lenient`
- `ArrayOrder` which is set to `ArrayOrder.Lenient`

Targets: **Multiplatform**

## shouldBeEmptyJsonArray

`json.shouldBeEmptyJsonArray()` asserts that the JSON is an empty array (`[]`).

Targets: **Multiplatform**

## shouldBeEmptyJsonObject

`json.shouldBeEmptyJsonObject()` asserts that the JSON is an empty array (`{}`).

Targets: **Multiplatform**

## shouldBeJsonArray

`json.shouldBeJsonArray()` asserts that the JSON is an array.

The inverse of this matcher is `shouldNotBeJsonArray` which will error if the JSON string _is_ an array.

Targets: **Multiplatform**

## shouldBeJsonObject

`json.shouldBeJsonObject()` asserts that the JSON is an object.

The inverse of this matcher is `shouldNotBeJsonObject` which will error if the JSON string _is_ an object.

Targets: **Multiplatform**

## shouldBeValidJson

`json.shouldBeValidJson()` asserts that the string is valid JSON.

The inverse of this matcher is `shouldNotBeValidJson` which will error if the string _is_ valid JSON.

Targets: **Multiplatform**

## shouldContainJsonKey

`json.shouldContainJsonKey("$.json.path")` asserts that a JSON string contains the given JSON path.

The inverse of this matcher is `shouldNotContainJsonKey` which will error if a JSON string _does_ contain the given JSON
path.

Targets: **JVM**

## shouldContainJsonKeyValue

`str.shouldContainJsonKeyValue("$.json.path", value)` asserts that a JSON string contains a JSON path with a specific
`value`.

The inverse of this matcher is `shouldNotContainJsonKeyValue` which will error if a JSON string _does_ contain the given
value at the given JSON path.

Targets: **JVM**

## shouldMatchJsonResource

`json.shouldMatchJsonResource("/file.json")` asserts that the JSON is equal to the existing test resource `/file.json`,
ignoring properties' order and formatting.

Targets: **JVM**
