package com.sksamuel.kotest.property;

public class JavaClassWithOnlyZeroArgConstructor {
   private final int bar;
   private final String baz;

   private JavaClassWithOnlyZeroArgConstructor(int bar, String baz) {
      // if this constructor was used, baz would be an arbitrary string.
      this.bar = bar;
      this.baz = baz;
   }

   public JavaClassWithOnlyZeroArgConstructor() {
      this(42, "baz");
   }

   public int getBar() {
      return bar;
   }

   public String getBaz() {
      return baz;
   }
}
