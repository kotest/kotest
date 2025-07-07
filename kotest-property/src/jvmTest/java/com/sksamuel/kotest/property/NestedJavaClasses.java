package com.sksamuel.kotest.property;

public class NestedJavaClasses {
   private final JavaClassWithOnePublicConstructorWithAtLeastOneArg oneArg;
   private final JavaClassWithOnlyZeroArgConstructor zeroArg;

   public NestedJavaClasses(
      JavaClassWithOnlyZeroArgConstructor zeroArg,
      JavaClassWithOnePublicConstructorWithAtLeastOneArg oneArg
   ) {
      // if this constructor was used, baz would be an arbitrary string.
      this.zeroArg = zeroArg;
      this.oneArg = oneArg;
   }

   public JavaClassWithOnePublicConstructorWithAtLeastOneArg getOneArg() {
      return oneArg;
   }

   public JavaClassWithOnlyZeroArgConstructor getZeroArg() {
      return zeroArg;
   }
}
