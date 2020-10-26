package io.kotest.core

import io.kotest.core.test.DescriptionName

class DuplicatedTestNameException(name: DescriptionName.TestName) :
   RuntimeException("Cannot create test with duplicated name ${name.displayName}")
