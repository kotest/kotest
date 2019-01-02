fun currentThreadWithoutCoroutine(): String {
  val name = Thread.currentThread().name
  return name.take(name.indexOf("@coroutine"))
}