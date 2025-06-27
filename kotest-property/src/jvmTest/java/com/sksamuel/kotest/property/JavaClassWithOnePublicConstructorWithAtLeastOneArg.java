package com.sksamuel.kotest.property;

public class JavaClassWithOnePublicConstructorWithAtLeastOneArg {
   private final int bar;
   private final String baz;

   private JavaClassWithOnePublicConstructorWithAtLeastOneArg(int bar, String baz) {
      // if this constructor was used, baz would be an arbitrary string.
      this.bar = bar;
      this.baz = baz;
   }

   public JavaClassWithOnePublicConstructorWithAtLeastOneArg() {
      throw new IllegalArgumentException("Should not be called, since there is no zero-arg constructor");
   }

   public JavaClassWithOnePublicConstructorWithAtLeastOneArg(int bar) {
      this(bar, "baz");
   }

   public int getBar() {
      return bar;
   }

   public String getBaz() {
      return baz;
   }
}
