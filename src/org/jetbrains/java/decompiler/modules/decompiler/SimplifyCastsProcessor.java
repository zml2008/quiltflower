package org.jetbrains.java.decompiler.modules.decompiler;

import org.jetbrains.java.decompiler.modules.decompiler.exps.Exprent;
import org.jetbrains.java.decompiler.modules.decompiler.exps.FunctionExprent;
import org.jetbrains.java.decompiler.modules.decompiler.exps.InvocationExprent;
import org.jetbrains.java.decompiler.modules.decompiler.stats.RootStatement;
import org.jetbrains.java.decompiler.modules.decompiler.stats.Statement;
import org.jetbrains.java.decompiler.struct.gen.VarType;

import java.util.ArrayList;
import java.util.List;

/**
 * Applies simple transforms to remove implicit casts where they're not needed.
 *
 * @author SuperCoder79
 */
public final class SimplifyCastsProcessor {
  public static void simplifyCasts(RootStatement root) {
    simplifyCastsRec(root);
  }

  private static void simplifyCastsRec(Statement stat) {
    for (Object obj : stat.getSequentialObjects()) {
      if (obj instanceof Statement) {
        simplifyCastsRec((Statement) obj);
      }

      if (obj instanceof InvocationExprent) {
        simplifyInvocation((InvocationExprent) obj);
      }
    }

    if (stat.getExprents() != null) {
      List<Exprent> exps = new ArrayList<>();

      // Recursively get all exprents
      for (Exprent exprent : stat.getExprents()) {
        exps.add(exprent);
        exps.addAll(exprent.getAllExprents(true));
      }

      for (Exprent exprent : exps) {
        if (exprent.type == Exprent.EXPRENT_INVOCATION) {
          simplifyInvocation((InvocationExprent) exprent);
        }
      }
    }
  }

  private static void simplifyInvocation(InvocationExprent invoc) {
    // Simplify both int to double and float to double casts
    simplifyDoubleCast(invoc, FunctionExprent.FUNCTION_I2D);
    simplifyDoubleCast(invoc, FunctionExprent.FUNCTION_F2D);

    // TODO: int to float
  }

  private static void simplifyDoubleCast(InvocationExprent invoc, int targetType) {
    VarType[] params = invoc.getDescriptor().params;
    List<Exprent> exprents = invoc.getLstParameters();

    // Go through all the parameters
    for (int i = 0; i < params.length; i++) {
      VarType descriptor = params[i];
      Exprent expr = exprents.get(i);

      if (descriptor.equals(VarType.VARTYPE_DOUBLE)) {

        if (expr.type == Exprent.EXPRENT_FUNCTION) {
          int funcType = ((FunctionExprent)expr).getFuncType();

          // If it's a simple cast, then remove it
          if (funcType == targetType) {
            // TODO: should be combining the bytecode ranges here?

            invoc.replaceExprent(expr, ((FunctionExprent)expr).getLstOperands().get(0));
          }

          // It's only safe to handle addition and subtraction casts here
          if (funcType == FunctionExprent.FUNCTION_ADD || funcType == FunctionExprent.FUNCTION_SUB) {
            FunctionExprent wrapper = ((FunctionExprent)expr);

            // Handle both sides of the function
            Exprent left = wrapper.getLstOperands().get(0);
            if (left.type == Exprent.EXPRENT_FUNCTION && ((FunctionExprent)left).getFuncType() == targetType) {

              wrapper.replaceExprent(left, ((FunctionExprent)left).getLstOperands().get(0));
            }

            Exprent right = wrapper.getLstOperands().get(1);
            if (right.type == Exprent.EXPRENT_FUNCTION && ((FunctionExprent)right).getFuncType() == targetType) {

              wrapper.replaceExprent(right, ((FunctionExprent)right).getLstOperands().get(0));
            }
          }
        }
      }
    }
  }
}
