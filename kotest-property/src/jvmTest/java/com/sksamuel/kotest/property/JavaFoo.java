package com.sksamuel.kotest.property;

public class JavaFoo {
   private final int bar;
   private final String baz;

   private JavaFoo(int bar, String baz) {
      // if this constructor was used, baz would be an arbitrary string.
      this.bar = bar;
      this.baz = baz;
   }

   public JavaFoo(int bar) {
      this(bar, "baz");
   }

   public int getBar() {
      return bar;
   }

   public String getBaz() {
      return baz;
   }
}
