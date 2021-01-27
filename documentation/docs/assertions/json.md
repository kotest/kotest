---
title: Json Matchers
slug: json-matchers.html
---




Matchers for JSON are provided by the `kotest-assertions-json` module.


| JSON | For convenience, JSONs are simply strings |
| -------- | ---- |
| `str?.shouldMatchJson(json?)` | Asserts that the JSON is equal to another JSON ignoring properties' order and formatting. |
| `str?.shouldContainJsonKey("$.json.path")` | Asserts that the JSON contains a JSON path. |
| `str?.shouldContainJsonKeyValue("$.json.path", value)` | Asserts that the JSON contains a JSON path with a specific `value`. |
| `str?.shouldMatchJsonResource("/file.json")` | Asserts that the JSON is equal to the existing `/file.json` ignoring properties' order and formatting. |


:::note
JSON matchers are built using the Jackson library
:::
