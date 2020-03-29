package io.kotest.assertions.json

/**
 * JSON element representation via Kotlin String.
 *
 * It can be any JSON value, for example:
 * * JSON string: `""id""` – please note that it differs from a simple Kotlin string `"id"` which is not valid JSON representation.
 * * JSON number: `"42"`.
 * * JSON null: `"null"`.
 * * JSON object: `"{"id": 42}"`.
 * * JSON array: `"["id", 42, null]"`.
 */
typealias Json = String

/**
 * String representing a key for JSON value. For example, to access `42` in `"{"id": 42}"`, use `"id"` as a key.
 */
typealias JsonKey = String
