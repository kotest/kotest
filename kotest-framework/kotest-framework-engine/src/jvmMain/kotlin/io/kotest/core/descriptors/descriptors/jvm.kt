package io.kotest.core.descriptors.descriptors

import io.kotest.core.descriptors.Descriptor
import io.kotest.core.descriptors.DescriptorId

fun Class<*>.toDescriptor(): Descriptor.SpecDescriptor = Descriptor.SpecDescriptor(DescriptorId(name))
