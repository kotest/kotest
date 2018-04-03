package io.kotlintest.provided

import io.kotlintest.AbstractProjectConfig
import io.kotlintest.extensions.Extension
import io.kotlintest.spring.SpringAutowireConstructorExtension

class ProjectConfig : AbstractProjectConfig() {
  override fun extensions(): List<Extension> = listOf(SpringAutowireConstructorExtension)
}