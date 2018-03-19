package io.kotlintest.runner.junit5

import org.junit.platform.engine.TestDescriptor
import org.junit.platform.engine.TestTag
import org.junit.platform.engine.UniqueId
import java.util.*

abstract class LeafDescriptor : TestDescriptor {

  // the parent of this container which should never be
  // null once it has been set
  private var parent: TestDescriptor? = null

  override fun setParent(parent: TestDescriptor?) {
    this.parent = parent
  }

  override fun getParent(): Optional<TestDescriptor> = Optional.of(parent!!)

  override fun getType(): TestDescriptor.Type = TestDescriptor.Type.TEST

  // leaf descriptors do not have children
  override fun getChildren(): MutableSet<out TestDescriptor> = mutableSetOf()

  override fun removeFromHierarchy() {}
  override fun removeChild(descriptor: TestDescriptor?) = throw UnsupportedOperationException()
  override fun addChild(descriptor: TestDescriptor?) = throw UnsupportedOperationException()
  override fun getTags(): MutableSet<TestTag> = mutableSetOf()

  override fun findByUniqueId(uniqueId: UniqueId?): Optional<out TestDescriptor> =
      when (uniqueId) {
        getUniqueId() -> Optional.of(this)
        else -> Optional.empty()
      }
}

abstract class BranchDescriptor : TestDescriptor {

  // nested containers
  private val children = mutableListOf<TestDescriptor>()

  // the parent of this container, which would be empty if this is the root descriptor
  // we don't set this in the constructor because junit likes to set it using the set method
  private var parent: Optional<TestDescriptor> = Optional.empty()

  override fun setParent(parent: TestDescriptor) {
    this.parent = Optional.ofNullable(parent)
  }

  override fun getParent(): Optional<TestDescriptor> = parent

  override fun getChildren(): MutableSet<out TestDescriptor> = children.toMutableSet()

  override fun getType(): TestDescriptor.Type = TestDescriptor.Type.CONTAINER

  override fun getTags(): MutableSet<TestTag> = mutableSetOf()

  override fun findByUniqueId(uniqueId: UniqueId): Optional<out TestDescriptor> =
      when {
        uniqueId == getUniqueId() -> Optional.of(this)
        children.isEmpty() -> Optional.empty()
        else -> {
          children.forEach {
            val found = it.findByUniqueId(uniqueId)
            if (found.isPresent)
              return Optional.of(found.get())
          }
          Optional.empty()
        }
      }

  override fun removeFromHierarchy() {}
  override fun removeChild(descriptor: TestDescriptor?) {
    throw UnsupportedOperationException()
  }

  override fun addChild(descriptor: TestDescriptor) {
    descriptor.setParent(this)
    this.children.add(descriptor)
    this.children.sortBy { it.displayName.trim().toLowerCase() }
  }
}