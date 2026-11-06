---
title: YAML
slug: yaml-matchers.html
sidebar_label: YAML
---

To use these matchers add `testImplementation("io.kotest:kotest-assertions-yaml:<version>")` to your build.

## Basic matchers

| Matcher                | Description                                           | Targets       |
|------------------------|-------------------------------------------------------|:--------------|
| `shouldBeValidYaml`    | verifies that a given string parses to valid YAML     | Multiplatform |
| `shouldNotBeValidYaml` | verifies that a given not string parses to valid YAML | Multiplatform |

## Content-based matching

| Matcher                                                                      | Description                                                                             | Targets       |
|------------------------------------------------------------------------------|-----------------------------------------------------------------------------------------|:--------------|
| `shouldEqualYaml`                                                            | Verifies that a String matches a given YAML.                                            | Multiplatform |
| `shouldNotEqualYaml`                                                         | Verifies that a String not matches a given YAML.                             | Multiplatform |

### yamlCompareOptions

`shouldEqualYaml` supports an additional parameter of type `CompareYamlOptions` which supports the following flags to
toggle behaviour of the YAML comparison:

#### Usage:

Options can be specified inline, like:

```kotlin
a.shouldEqualYaml(b, yamlCompareOptions { arrayOrder = ArrayOrder.Strict })
```

Or by using the builder-style overload, which receives the options and the expected value:

```kotlin
a shouldEqualYaml {
  arrayOrder = ArrayOrder.Lenient
  b
}
```

Another option is to define a compare function which suits your desires, like:

```kotlin
val myOptions = yamlCompareOptions {
  arrayOrder = ArrayOrder.Lenient
}

infix fun String.lenientShouldEqualYaml(other: String) = this.shouldEqualYaml(other, myOptions)

"""items:
  - 1
  - 2""" lenientShouldEqualYaml """items:
  - 2
  - 1""" // This will pass
```

#### Parameters

| Name              | Purpose                                                                                                                            | Possible values                                     | Default value                                                         |
|-------------------|------------------------------------------------------------------------------------------------------------------------------------|-----------------------------------------------------|-----------------------------------------------------------------------|
| `PropertyOrder`   | Determines if the order of properties in YAML mappings are considered when comparing                                                | `PropertyOrder.Strict`, `PropertyOrder.Lenient`     | `PropertyOrder.Lenient`, i.e. order of properties *DON'T* matter      |
| `ArrayOrder`      | Determines if the order of elements in YAML arrays are considered when comparing                                                    | `ArrayOrder.Strict`, `ArrayOrder.Lenient`           | `ArrayOrder.Strict`, i.e. order of elements *DO* matter               |
| `FieldComparison` | Determines if comparison will fail if YAML mappings in `actual` contain extra properties, when compared to `expected`               | `FieldComparison.Strict`, `FieldComparison.Lenient` | `FieldComparison.Strict`, i.e. extra properties will cause inequality |
| `NumberFormat`    | Determines if comparison of numbers are strict with regards to number format. For instance, if 100.0 and 100 are considered equal. | `NumberFormat.Strict`, `NumberFormat.Lenient`       | `NumberFormat.Lenient`, i.e. number formats *DON'T* matter            |

Targets: **Multiplatform**

Note: unlike the JSON matchers there is no `TypeCoercion` option, because the YAML parser retains only the content of a
scalar, not whether it was quoted, so a scalar such as `"1"` and `1` cannot be distinguished after parsing. YAML-specific
numeric forms that are not valid JSON numbers, such as `0x1F` or `1_000`, are always compared literally.
