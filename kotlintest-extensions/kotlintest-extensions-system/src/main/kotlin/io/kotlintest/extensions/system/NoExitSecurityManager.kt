package io.kotlintest.extensions.system


import java.io.FileDescriptor
import java.net.InetAddress
import java.security.Permission

class SystemExitException(val exitCode: Int) : RuntimeException()

/**
 * An implementation of [SecurityManager] that throws a [SystemExitException]
 * exception whenever System.exit(int) is invoked.
 *
 * Implemenation inspired from
 * https://github.com/stefanbirkner/system-rules/blob/72250d0451f9f1a5c5852502b5e9b0c874aeab42/src/main/java/org/junit/contrib/java/lang/system/internal/NoExitSecurityManager.java
 * Apache Licensed.
 */
@Suppress("OverridingDeprecatedMember", "DEPRECATION")
class NoExitSecurityManager(private val originalSecurityManager: SecurityManager?) : SecurityManager() {

  override fun checkExit(status: Int) = throw SystemExitException(status)

  override fun getInCheck(): Boolean {
    return originalSecurityManager != null && originalSecurityManager.inCheck
  }

  override fun getSecurityContext(): Any {
    return if (originalSecurityManager == null)
      super.getSecurityContext()
    else
      originalSecurityManager.securityContext
  }

  override fun checkPermission(perm: Permission) {
    originalSecurityManager?.checkPermission(perm)
  }

  override fun checkPermission(perm: Permission, context: Any) {
    originalSecurityManager?.checkPermission(perm, context)
  }

  override fun checkCreateClassLoader() {
    originalSecurityManager?.checkCreateClassLoader()
  }

  override fun checkAccess(t: Thread) {
    originalSecurityManager?.checkAccess(t)
  }

  override fun checkAccess(g: ThreadGroup) {
    originalSecurityManager?.checkAccess(g)
  }

  override fun checkExec(cmd: String) {
    originalSecurityManager?.checkExec(cmd)
  }

  override fun checkLink(lib: String) {
    originalSecurityManager?.checkLink(lib)
  }

  override fun checkRead(fd: FileDescriptor) {
    originalSecurityManager?.checkRead(fd)
  }

  override fun checkRead(file: String) {
    originalSecurityManager?.checkRead(file)
  }

  override fun checkRead(file: String, context: Any) {
    originalSecurityManager?.checkRead(file, context)
  }

  override fun checkWrite(fd: FileDescriptor) {
    originalSecurityManager?.checkWrite(fd)
  }

  override fun checkWrite(file: String) {
    originalSecurityManager?.checkWrite(file)
  }

  override fun checkDelete(file: String) {
    originalSecurityManager?.checkDelete(file)
  }

  override fun checkConnect(host: String, port: Int) {
    originalSecurityManager?.checkConnect(host, port)
  }

  override fun checkConnect(host: String, port: Int, context: Any) {
    originalSecurityManager?.checkConnect(host, port, context)
  }

  override fun checkListen(port: Int) {
    originalSecurityManager?.checkListen(port)
  }

  override fun checkAccept(host: String, port: Int) {
    originalSecurityManager?.checkAccept(host, port)
  }

  override fun checkMulticast(maddr: InetAddress) {
    originalSecurityManager?.checkMulticast(maddr)
  }

  override fun checkMulticast(maddr: InetAddress, ttl: Byte) {
    originalSecurityManager?.checkMulticast(maddr, ttl)
  }

  override fun checkPropertiesAccess() {
    originalSecurityManager?.checkPropertiesAccess()
  }

  override fun checkPropertyAccess(key: String) {
    originalSecurityManager?.checkPropertyAccess(key)
  }

  override fun checkTopLevelWindow(window: Any): Boolean {
    return originalSecurityManager?.checkTopLevelWindow(window) ?: super.checkTopLevelWindow(window)
  }

  override fun checkPrintJobAccess() {
    originalSecurityManager?.checkPrintJobAccess()
  }

  override fun checkSystemClipboardAccess() {
    originalSecurityManager?.checkSystemClipboardAccess()
  }

  override fun checkAwtEventQueueAccess() {
    originalSecurityManager?.checkAwtEventQueueAccess()
  }

  override fun checkPackageAccess(pkg: String) {
    originalSecurityManager?.checkPackageAccess(pkg)
  }

  override fun checkPackageDefinition(pkg: String) {
    originalSecurityManager?.checkPackageDefinition(pkg)
  }

  override fun checkSetFactory() {
    originalSecurityManager?.checkSetFactory()
  }

  override fun checkMemberAccess(clazz: Class<*>, which: Int) {
    originalSecurityManager?.checkMemberAccess(clazz, which)
  }

  override fun checkSecurityAccess(target: String) {
    originalSecurityManager?.checkSecurityAccess(target)
  }

  override fun getThreadGroup(): ThreadGroup {
    return if (originalSecurityManager == null)
      super.getThreadGroup()
    else
      originalSecurityManager.threadGroup
  }
}