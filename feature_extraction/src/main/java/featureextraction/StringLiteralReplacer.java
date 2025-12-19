package featureextraction;

import com.github.javaparser.ast.expr.CharLiteralExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.visitor.ModifierVisitor;
import com.github.javaparser.ast.visitor.Visitable;

/** Replace all string and character literals with empty string */
public class StringLiteralReplacer extends ModifierVisitor<Void> {

  private final String sReplacement = "";
  private final char cReplacement = Character.MIN_VALUE;

  @Override
  public Visitable visit(StringLiteralExpr sle, Void arg) {
    return new StringLiteralExpr(sReplacement);
  }

  @Override
  public Visitable visit(CharLiteralExpr cle, Void arg) {
    return new CharLiteralExpr(cReplacement);
  }
}
