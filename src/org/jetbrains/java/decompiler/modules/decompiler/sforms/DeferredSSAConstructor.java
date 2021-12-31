package org.jetbrains.java.decompiler.modules.decompiler.sforms;

import org.jetbrains.java.decompiler.modules.decompiler.stats.RootStatement;
import org.jetbrains.java.decompiler.struct.StructMethod;

public final class DeferredSSAConstructor {
  private final RootStatement root;
  private final StructMethod mt;

  private SSAConstructorSparseEx ssa;

  public DeferredSSAConstructor(RootStatement root, StructMethod mt) {
    this.root = root;
    this.mt = mt;
  }

  public SSAConstructorSparseEx get() {
    if (this.ssa == null) {
      this.ssa = new SSAConstructorSparseEx();
      this.ssa.splitVariables(this.root, this.mt);
    }

    return this.ssa;
  }

  public boolean exists() {
    return this.ssa != null;
  }

  public void invalidate() {
    this.ssa = null;
  }
}
