---
title: JSON Schema Matchers
slug: json-schema-matchers.html
sidebar_label: Schema matchers
---

| Matcher             | Description                                                                                                                                         | Targets       |
|---------------------|-----------------------------------------------------------------------------------------------------------------------------------------------------|:--------------|
| `shouldMatchSchema` | Validates that a `String` or `kotlinx.serialization.JsonElement` matches a `JsonSchema`. See description below for details on constructing schemas. | Multiplatform |

## Defining Schemas

A subset of [JSON Schemas](https://json-schema.org/) can be defined either by parsing a textual schema. Example:

```kotlin
val parsedSchema = parseSchema(
  """
  {
  "$id": "https://example.com/geographical-location.schema.json",  // will be ignored
  "$schema": "https://json-schema.org/draft/2020-12/schema",       // will be ignored
  "title": "Longitude and Latitude Values",                        // will be ignored
  "description": "A geographical coordinate.",                     // will be ignored
  "required": [ "latitude", "longitude" ],
  "type": "object",
  "properties": {
    "latitude": {
      "type": "number",
      "minimum": -90,
      "maximum": 90
    },
    "longitude": {
      "type": "number",
      "minimum": -180,
      "maximum": 180
    }
  }
}
  """
)
```

or using Kotest's built-in DSL:

```kotlin
val addressSchema = jsonSchema {
  obj {   // object is reserved, obj was chosen over jsonObject for brevity but could be changed ofc, or jsonObject could be added as alternative.
    withProperty("street", required = true) { string() }
    withProperty("zipCode", required = true) {
      integer {
        beEven() and beInRange(10000..99999)   // supports constructing a matcher that will be used to test values
      }
    }
    additionalProperties = false   // triggers failure if other properties are defined in actual
  }
}

val personSchema = jsonSchema {
  obj {
    withProperty("name", required = true) { string() }
    withProperty("address") { addressSchema() } // Schemas can re-use other schemas 🎉
  }
}
```

⚠️ Note that Kotest only supports a subset of JSON schema currently. Currently missing support for:

* $defs and $refs
* Recursive schemas
* Parsing of schema composition
* string.format
* array.prefixItems,
* array.contains,
* array.items = false
* array.maxContains
* array.minContains
* array.uniqueItems
* enum

## Validating

Once a schema has been defined, you can validate `String` and `kotlinx.serialization.JsonElement` against it:

```kotlin
"{}" shouldMatchSchema personSchema

// fails with:
// $.name => Expected string, but was undefined

""" { "name": "Emil", "age": 34 } """
// Passes, since address isn't required and `additionalProperties` are allowed
```

