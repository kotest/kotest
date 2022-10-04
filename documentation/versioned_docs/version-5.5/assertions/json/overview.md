---
title: JSON
slug: json-overview.html
sidebar_label: Overview
---

## Basic matchers

| Matcher              | Description                                        | Targets       |
|----------------------|----------------------------------------------------|:--------------|
| `shouldBeValidJson`  | verifies that a given string parses to valid json  | Multiplatform |
| `shouldBeJsonObject` | asserts that a string is a valid JSON **_object_** | Multiplatform |
| `shouldBeJsonArray`  | asserts that a string is a valid JSON **_array_**  | Multiplatform |

## Content-based matching

For more details, see [here](content-json-matchers.html) or follow matcher-specific links below

| Matcher                                                                         | Description                                                                                          | Targets       |
|---------------------------------------------------------------------------------|------------------------------------------------------------------------------------------------------|:--------------|
| [shouldEqualJson](content-json-matchers.html#shouldequaljson)                   | Verifies that a String matches a given JSON structure.                                               | Multiplatform |
| [shouldEqualSpecifiedJson](content-json-matchers.html#shouldequalspecifiedjson) | Verifies that a String matches a given JSON structure, but allows additional unspecified properties. | Multiplatform |
| [shouldContainJsonKey](content-json-matchers.html#shouldcontainjsonkey)         | Verifies that a String is JSON, and contains a given JSON path                                       | JVM           |
| [shouldContainJsonKeyValue](content-json-matchers.html#shouldcontainjsonkey)    | Verifies that a String is JSON, and contains a given JSON path with the specified value              | JVM           |
| [shouldMatchJsonResource](content-json-matchers.html#shouldcontainjsonkey)      | Verifies that a String is matches the JSON content of a given test resource                          | JVM           |

## Schema validation
| Matcher                                        | Description                                                                                                                                         | Targets       |
|------------------------------------------------|-----------------------------------------------------------------------------------------------------------------------------------------------------|:--------------|
| [shouldMatchSchema](json-schema-matchers.html) | Validates that a `String` or `kotlinx.serialization.JsonElement` matches a `JsonSchema`. See description below for details on constructing schemas. | Multiplatform |
