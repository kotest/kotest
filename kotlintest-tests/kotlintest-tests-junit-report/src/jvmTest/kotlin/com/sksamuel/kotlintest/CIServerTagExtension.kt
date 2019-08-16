package com.sksamuel.kotlintest

import io.kotlintest.Tags
import io.kotlintest.extensions.TagExtension

object CIServerTagExtension : TagExtension {
  override fun tags(): Tags = if (isCI()) Tags.Empty else Tags.exclude(AppveyorTag, TravisTag)
}