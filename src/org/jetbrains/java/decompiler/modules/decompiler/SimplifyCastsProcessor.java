package org.jetbrains.java.decompiler.modules.decompiler;

import org.jetbrains.java.decompiler.modules.decompiler.exps.Exprent;
import org.jetbrains.java.decompiler.modules.decompiler.exps.FunctionExprent;
import org.jetbrains.java.decompiler.modules.decompiler.exps.InvocationExprent;
import org.jetbrains.java.decompiler.modules.decompiler.stats.RootStatement;
import org.jetbrains.java.decompiler.modules.decompiler.stats.Statement;
import org.jetbrains.java.decompiler.struct.gen.VarType;

import java.util.List;

public final class SimplifyCastsProcessor {
  public static void simplifyCasts(RootStatement root) {
    // TODO: recursive
    simplifyCastsRec(root);
  }

  private static boolean simplifyCastsRec(Statement stat) {
    boolean ret = false;

    for (Object obj : stat.getSequentialObjects()) {
      if (obj instanceof Statement) {
        ret |= simplifyCastsRec((Statement) obj);
      }

      if (obj instanceof InvocationExprent) {
        ret |= simplifyInvocation((InvocationExprent) obj);
      }
    }

    if (stat.getExprents() != null) {
      for (Exprent exprent : stat.getExprents()) {
        if (exprent.type == Exprent.EXPRENT_INVOCATION) {
          ret |= simplifyInvocation((InvocationExprent) exprent);
        }
      }
    }

    return ret;
  }

  private static boolean simplifyInvocation(InvocationExprent invoc) {
    boolean ret = false;
    ret |= simplifyDoubleCast(invoc);

    return ret;
  }

  private static boolean simplifyDoubleCast(InvocationExprent invoc) {
    VarType[] params = invoc.getDescriptor().params;
    List<Exprent> exprents = invoc.getLstParameters();

    for (int i = 0; i < params.length; i++) {
      VarType descriptor = params[i];
      Exprent expr = exprents.get(i);

      if (descriptor.equals(VarType.VARTYPE_DOUBLE)) {

        if (expr.type == Exprent.EXPRENT_FUNCTION) {
          if (((FunctionExprent)expr).getFuncType() == FunctionExprent.FUNCTION_I2D) {
            // TODO: should be combining the bytecode ranges here?

            invoc.replaceExprent(expr, ((FunctionExprent)expr).getLstOperands().get(0));
          }
        }
      }
    }

    return false;
  }
}
