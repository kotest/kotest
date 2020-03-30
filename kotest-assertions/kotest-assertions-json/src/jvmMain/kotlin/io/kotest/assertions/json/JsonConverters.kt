package io.kotest.assertions.json

import io.kotest.assertions.failure
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract
import kotlin.reflect.typeOf

/**
 * Returns encoded JSON.
 *
 * We need to distinct `null` and `"null"`. The first one means there is no JSON at all, the second is null in JSON.
 * So we can't do just `Json.toString()`.
 */
val Json?.representation
    get(): String? = when (this) {
        null -> null.toString()

        else -> "\"$this\""
    }

internal fun Json.limitLength() = if (this.length < 50) this.trim() else this.substring(0, 50).trim() + "..."

@OptIn(ExperimentalContracts::class, ExperimentalStdlibApi::class)
inline fun <reified T : Any?> Json?.shouldBeJsonValueOfType(): T {
    contract {
        returns() implies (this@shouldBeJsonValueOfType != null)
    }

    try {
        if (this != null && typeOf<T>() == typeOf<String>()) {
            check(this.length >= 2 && this.first() == '"' && this.last() == '"') {
                "JSON string value should contain opening and closing double quotes"
            }
        }

        return mapper.readValue(this, T::class.java)
    } catch (thrown: Throwable) {
        throw failure("bad type of ${this.representation} - expected: ${typeOf<T>()}", cause = thrown)
    }
}
