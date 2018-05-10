package io.kotlintest.provided

import com.sksamuel.kotlintest.CITagExtension
import io.kotlintest.AbstractProjectConfig
import io.kotlintest.extensions.ProjectLevelExtension

class ProjectConfig : AbstractProjectConfig() {
  override fun extensions(): List<ProjectLevelExtension> =
      listOf(CITagExtension)
}