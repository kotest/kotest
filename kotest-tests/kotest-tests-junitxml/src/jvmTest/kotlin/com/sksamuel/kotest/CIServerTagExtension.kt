package com.sksamuel.kotest

import io.kotest.core.Tags
import io.kotest.core.extensions.TagExtension

/**
 * If we are not running on CI then exclude any test tagged with [GithubActionsTag].
 */
object CIServerTagExtension : TagExtension {
   override fun tags(): Tags = if (isCI()) Tags.Empty else Tags.exclude(GithubActionsTag)
}
