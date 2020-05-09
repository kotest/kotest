package io.kotest.core.spec.style.scopes

import io.kotest.core.spec.style.KotestDsl
import io.kotest.core.test.TestContext

/**
 * A scope that allows no further test or callback methods to be registered.
 */
@KotestDsl
class TerminalScope(val context: TestContext)
