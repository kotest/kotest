package io.kotest.assertions.json

import com.jayway.jsonpath.JsonPath
import io.kotest.assertions.failure
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract
import kotlin.reflect.typeOf

/**
 * Returns encoded JSON.
 *
 * We need to distinct `null` and `"null"`. The first one means there is no JSON at all, the second is null in JSON.
 * So we can't do just `Json.toString()`.
 *
 * TODO: Should we leave error messages unchanged?
 *       It's more logical to just add quotes to non-nullable JSONs like this but it will "break" old tests:
 *         val Json?.representation get(): String? = when (this) {
 *             null -> null.toString()
 *             else -> "\"$this\""
 *         }
 */
val Json?.representation
    get(): String? = when (this) {
        "null" -> "'null'"

        else -> this
    }

internal fun Json.limitLength() = if (this.length < 50) this.trim() else this.substring(0, 50).trim() + "..."

@OptIn(ExperimentalContracts::class, ExperimentalStdlibApi::class)
inline fun <reified T> Json?.shouldBeOfType(): T {
    contract {
        returns() implies (this@shouldBeOfType != null)
    }

    return when (val decoded: Any? = JsonPath.read(this, "")) {
        is T -> decoded

        else -> throw failure(
            "bad type of ${this.representation} - " +
                    "expected: ${typeOf<T>()} but was: ${decoded?.javaClass?.name ?: "Nothing?"}"
        )
    }
}
