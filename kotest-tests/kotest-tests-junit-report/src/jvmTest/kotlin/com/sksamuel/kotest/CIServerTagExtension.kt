package com.sksamuel.kotest

import io.kotest.Tags
import io.kotest.extensions.TagExtension

object CIServerTagExtension : TagExtension {
  override fun tags(): Tags = if (isCI()) Tags.Empty else Tags.exclude(AppveyorTag, TravisTag)
}