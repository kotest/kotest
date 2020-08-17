package io.kotest.core

import io.kotest.core.test.DescriptionName

class DuplicatedTestNameException(name: DescriptionName.TestName) : RuntimeException()
