package io.kotlintest.provided

import io.kotlintest.AbstractProjectConfig
import io.kotlintest.spring.SpringAutowireConstructorExtension

class ProjectConfig : AbstractProjectConfig() {
  override fun extensions() = listOf(SpringAutowireConstructorExtension)
}