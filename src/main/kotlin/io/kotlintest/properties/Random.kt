package io.kotlintest.properties

class Random(val random: java.util.Random) {

  constructor() : this(java.util.Random())

  constructor(seed: Long) : this(java.util.Random(seed))

  constructor(seed: Int) : this(java.util.Random(seed.toLong()))

  /** Returns the next pseudorandom, uniformly distributed boolean value
   *  from this random number generator's sequence.
   */
  fun nextBoolean(): Boolean = random.nextBoolean()

  /** Generates random bytes and places them into a user-supplied byte
   *  array.
   */
  fun nextBytes(bytes: ByteArray) {
    random.nextBytes(bytes)
  }

  /** Returns the next pseudorandom, uniformly distributed double value
   *  between 0.0 and 1.0 from this random number generator's sequence.
   */
  fun nextDouble(): Double = random.nextDouble()

  /** Returns the next pseudorandom, uniformly distributed float value
   *  between 0.0 and 1.0 from this random number generator's sequence.
   */
  fun nextFloat(): Float = random.nextFloat()

  /** Returns the next pseudorandom, Gaussian ("normally") distributed
   *  double value with mean 0.0 and standard deviation 1.0 from this
   *  random number generator's sequence.
   */
  fun nextGaussian(): Double = random.nextGaussian()

  /** Returns the next pseudorandom, uniformly distributed int value
   *  from this random number generator's sequence.
   */
  fun nextInt(): Int = random.nextInt()

  /** Returns a pseudorandom, uniformly distributed int value between 0
   *  (inclusive) and the specified value (exclusive), drawn from this
   *  random number generator's sequence.
   */
  fun nextInt(n: Int): Int = random.nextInt(n)

  /** Returns the next pseudorandom, uniformly distributed long value
   *  from this random number generator's sequence.
   */
  fun nextLong(): Long = random.nextLong()

  /** Returns a pseudorandomly generated String.  This routine does
   *  not take any measures to preserve the randomness of the distribution
   *  in the face of factors like unicode's variable-length encoding,
   *  so please don't use this for anything important.  It's primarily
   *  intended for generating test data.
   *
   *  @param  length    the desired length of the String
   *  @return           the String
   */
  fun nextString(length: Int) = {
    fun safeChar() = {
      val surrogateStart: Int = 0xD800
      val res = nextInt(surrogateStart - 1) + 1
      res.toChar()
    }
    (0..length).map { safeChar() }.joinToString()
  }

  /** Returns the next pseudorandom, uniformly distributed value
   *  from the ASCII range 33-126.
   */
  fun nextPrintableChar(): Char {
    val low = 33
    val high = 127
    return (random.nextInt(high - low) + low).toChar()
  }

  fun setSeed(seed: Long): Unit {
    random.setSeed(seed)
  }

  /** Returns a new collection of the same type in a randomly chosen order.
   *
   *  @return         the shuffled collection
   */
  fun <T> shuffle(array: Array<T>): Array<T> {
    fun swap(i1: Int, i2: Int) {
      val tmp = array[i1]
      array[i1] = array[i2]
      array[i2] = tmp
    }
    for (n in array.size..2) {
      val k = nextInt(n)
      swap(n - 1, k)
    }
    return array
  }

  fun <T> shuffle(list: List<T>): List<T> {
    val result = list.toMutableList()
    fun swap(i1: Int, i2: Int) {
      val tmp = result[i1]
      result[i1] = result[i2]
      result[i2] = tmp
    }
    for (n in result.size..2) {
      val k = nextInt(n)
      swap(n - 1, k)
    }
    return result
  }
}