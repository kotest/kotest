package io.kotest.provided

import com.sksamuel.kotest.CIServerTagExtension
import io.kotest.AbstractProjectConfig
import io.kotest.extensions.ProjectLevelExtension

class ProjectConfig : AbstractProjectConfig() {
  override fun extensions(): List<ProjectLevelExtension> =
      listOf(CIServerTagExtension)
}