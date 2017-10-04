package io.kotlintest;

import java.util.Random;

// taken from the JDK
/*
 * Copyright (c) 1995, 2013, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
public class JavaRandoms {
  public static int internalNextInt(Random random, int origin, int bound) {
    if (origin < bound) {
      int n = bound - origin;
      if (n > 0) {
        return random.nextInt(n) + origin;
      } else {  // range not representable as int
        int r;
        do {
          r = random.nextInt();
        } while (r < origin || r >= bound);
        return r;
      }
    } else {
      return random.nextInt();
    }
  }

  public static long internalNextLong(Random random, long origin, long bound) {
    long r = random.nextLong();
    if (origin < bound) {
      long n = bound - origin, m = n - 1;
      if ((n & m) == 0L)  // power of two
        r = (r & m) + origin;
      else if (n > 0L) {  // reject over-represented candidates
        for (long u = r >>> 1;            // ensure nonnegative
             u + m - (r = u % n) < 0L;    // rejection check
             u = random.nextLong() >>> 1) // retry
          ;
        r += origin;
      } else {              // range not representable as long
        while (r < origin || r >= bound)
          r = random.nextLong();
      }
    }
    return r;
  }
}
