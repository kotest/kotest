package com.sksamuel.kotlintest

import io.kotlintest.Tag
import io.kotlintest.Tags
import io.kotlintest.extensions.TagExtension

object CITag : Tag()

class CITagExtension : TagExtension {
  override fun tags(): Tags = if (isCI()) Tags.include(CITag) else Tags.Empty
}