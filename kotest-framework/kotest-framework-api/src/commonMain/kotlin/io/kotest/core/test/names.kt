package io.kotest.core.test

import io.kotest.core.names.TestName

@Suppress("DEPRECATION") // Internal deprecation usage. Remove when deprecated functions are removed.
@Deprecated("use TestName directly, eg TestName(name). Deprecated in 5.0", ReplaceWith("TestName(name)"))
fun createTestName(name: String): TestName = createTestName(null, name, false)

@Deprecated(
   "use TestName directly, eg TestName(prefix, name, defaultIncludeAffix). Deprecated in 5.0",
   ReplaceWith("TestName(prefix, name, defaultIncludeAffix)")
)
fun createTestName(prefix: String?, name: String, defaultIncludeAffix: Boolean): TestName =
   TestName(
      prefix,
      name,
      defaultIncludeAffix
   )

@Suppress("DEPRECATION") // Internal deprecation usage. Remove when deprecated functions are removed.
@Deprecated(
   "use TestName directly, eg TestName(prefix, name, includeAffixesInDisplayName). Deprecated in 5.0",
   ReplaceWith("TestName(prefix, name, includeAffixesInDisplayName)")
)
fun createTestName(
   prefix: String?,
   name: String,
   testNameCase: TestNameCase,
   includeAffixesInDisplayName: Boolean,
) = TestName(prefix, name, includeAffixesInDisplayName)
