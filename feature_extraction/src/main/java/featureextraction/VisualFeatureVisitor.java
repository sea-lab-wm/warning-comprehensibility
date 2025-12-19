package featureextraction;

import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.EnumDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.comments.Comment;
import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.expr.BinaryExpr.Operator;
import com.github.javaparser.ast.expr.BooleanLiteralExpr;
import com.github.javaparser.ast.expr.CharLiteralExpr;
import com.github.javaparser.ast.expr.DoubleLiteralExpr;
import com.github.javaparser.ast.expr.InstanceOfExpr;
import com.github.javaparser.ast.expr.IntegerLiteralExpr;
import com.github.javaparser.ast.expr.LongLiteralExpr;
import com.github.javaparser.ast.expr.NullLiteralExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.SimpleName;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.expr.SuperExpr;
import com.github.javaparser.ast.expr.ThisExpr;
import com.github.javaparser.ast.stmt.AssertStmt;
import com.github.javaparser.ast.stmt.BreakStmt;
import com.github.javaparser.ast.stmt.CatchClause;
import com.github.javaparser.ast.stmt.ContinueStmt;
import com.github.javaparser.ast.stmt.DoStmt;
import com.github.javaparser.ast.stmt.ForEachStmt;
import com.github.javaparser.ast.stmt.ForStmt;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.stmt.SwitchEntry;
import com.github.javaparser.ast.stmt.SwitchStmt;
import com.github.javaparser.ast.stmt.ThrowStmt;
import com.github.javaparser.ast.stmt.TryStmt;
import com.github.javaparser.ast.stmt.WhileStmt;
import com.github.javaparser.ast.type.PrimitiveType;
import com.github.javaparser.ast.type.VoidType;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * This class to extract features from a java file Input : Java file with a Single Method (Note: if
 * want to use all the features) Output: Features
 */
public class VisualFeatureVisitor extends VoidVisitorAdapter<Void> {

  private VisualFeatures visualFeatures = new VisualFeatures();

  private static final int KEYWORDS_VISUAL_FEATURE_NUMBER = 1;
  private static final int IDENTIFIERS_VISUAL_FEATURE_NUMBER = 2;
  private static final int OPERATOR_VISUAL_FEATURE_NUMBER = 3;
  private static final int NUMBERS_VISUAL_FEATURE_NUMBER = 4;
  private static final int STRINGS_VISUAL_FEATURE_NUMBER = 5;
  private static final int OTHER_LITERALS_FEATURE_NUMBER = 6; //for example, integer or boolean literals
  private static final int COMMENTS_VISUAL_FEATURE_NUMBER = 7;

  /**
   * Locates all instances of the keywords "if" and "else" and adds them to the Visual Feature Matrix
   */
  @Override
  public void visit(IfStmt ifStmt, Void arg) {
    super.visit(ifStmt, arg);

    int lineNumber = ifStmt.getRange().get().begin.line - 1;
    int columnStart = ifStmt.getRange().get().begin.column - 1;
    int columnEnd = columnStart + 1;
    int visualFeatureNumber = KEYWORDS_VISUAL_FEATURE_NUMBER;
    addToVisualMatrix(lineNumber, columnStart, columnEnd, visualFeatureNumber);

    if (ifStmt.hasElseBranch()) {
      Statement elseStmt = ifStmt.getElseStmt().get();
      lineNumber = elseStmt.getRange().get().begin.line - 1;
      columnStart = elseStmt.getRange().get().begin.column - 6;
      columnEnd = columnStart + 3;
      addToVisualMatrix(lineNumber, columnStart, columnEnd, visualFeatureNumber);
    }
  }

  /**
   * Locates all instances of the keyword "import" and adds them to the Visual Feature Matrix
   */
  @Override
  public void visit(ImportDeclaration id, Void arg) {
    super.visit(id, arg);

    int lineNumber = id.getRange().get().begin.line - 1;
    int columnStart = id.getRange().get().begin.column - 1;
    int columnEnd = columnStart + 5;
    int visualFeatureNumber = KEYWORDS_VISUAL_FEATURE_NUMBER;
    addToVisualMatrix(lineNumber, columnStart, columnEnd, visualFeatureNumber);
  }

  /**
   * Locates all instances of the keyword "instanceof" and adds them to the Visual Feature Matrix
   */
  @Override
  public void visit(InstanceOfExpr ioe, Void arg) {
    super.visit(ioe, arg);

    int lineNumber = ioe.getRange().get().begin.line - 1;
    int columnStart = ioe.getRange().get().begin.column - 1;
    int columnEnd = columnStart + 13;
    int visualFeatureNumber = KEYWORDS_VISUAL_FEATURE_NUMBER;
    addToVisualMatrix(lineNumber, columnStart, columnEnd, visualFeatureNumber);
  }

  /**
   * Locates all instances of the keyword "switch" and adds them to the Visual Feature Matrix
   */
  @Override
  public void visit(SwitchStmt swStmt, Void arg) {
    super.visit(swStmt, arg);

    int lineNumber = swStmt.getRange().get().begin.line - 1;
    int columnStart = swStmt.getRange().get().begin.column - 1;
    int columnEnd = columnStart + 5;
    int visualFeatureNumber = KEYWORDS_VISUAL_FEATURE_NUMBER;
    addToVisualMatrix(lineNumber, columnStart, columnEnd, visualFeatureNumber);
  }

  /**
   * Locates all instances of the keyword "for" (excluding for-each loops) and adds them to the Visual Feature Matrix
   */
  @Override
  public void visit(ForStmt forStmt, Void arg) {
    super.visit(forStmt, arg);

    int lineNumber = forStmt.getRange().get().begin.line - 1;
    int columnStart = forStmt.getRange().get().begin.column - 1;
    int columnEnd = columnStart + 2;
    int visualFeatureNumber = KEYWORDS_VISUAL_FEATURE_NUMBER;
    addToVisualMatrix(lineNumber, columnStart, columnEnd, visualFeatureNumber);
  }

  /**
   * Locates all instances of the keyword "while" (excluding do-while loops) and adds them to the Visual Feature Matrix
   */
  @Override
  public void visit(WhileStmt whileStmt, Void arg) {
    super.visit(whileStmt, arg);

    int lineNumber = whileStmt.getRange().get().begin.line - 1;
    int columnStart = whileStmt.getRange().get().begin.column - 1;
    int columnEnd = columnStart + 4;
    int visualFeatureNumber = KEYWORDS_VISUAL_FEATURE_NUMBER;
    addToVisualMatrix(lineNumber, columnStart, columnEnd, visualFeatureNumber);
  }

  /**
   * Locates all instances of the keyword "for" when used in for-each loops and adds them to the Visual Feature Matrix
   */
  @Override
  public void visit(ForEachStmt forEachStmt, Void arg) {
    super.visit(forEachStmt, arg);

    int lineNumber = forEachStmt.getRange().get().begin.line - 1;
    int columnStart = forEachStmt.getRange().get().begin.column - 1;
    int columnEnd = columnStart + 2;
    int visualFeatureNumber = KEYWORDS_VISUAL_FEATURE_NUMBER;
    addToVisualMatrix(lineNumber, columnStart, columnEnd, visualFeatureNumber);
  }

  /**
   * Locates all integer literals and adds them to the Visual Feature Matrix
   */
  @Override
  public void visit(IntegerLiteralExpr ile, Void arg) {
    super.visit(ile, arg);

    int lineNumber = ile.getRange().get().begin.line - 1;
    int columnStart = ile.getRange().get().begin.column - 1;
    int columnEnd = ile.getRange().get().end.column - 1;
    int visualFeatureNumber = NUMBERS_VISUAL_FEATURE_NUMBER;
    addToVisualMatrix(lineNumber, columnStart, columnEnd, visualFeatureNumber);
  }

  /**
   * Locates all long literals and adds them to the Visual Feature Matrix
   */
  @Override
  public void visit(LongLiteralExpr lle, Void arg) {
    super.visit(lle, arg);
    
    int lineNumber = lle.getRange().get().begin.line - 1;
    int columnStart = lle.getRange().get().begin.column - 1;
    int columnEnd = lle.getRange().get().end.column - 1;
    int visualFeatureNumber = NUMBERS_VISUAL_FEATURE_NUMBER;
    addToVisualMatrix(lineNumber, columnStart, columnEnd, visualFeatureNumber);
  }

  /**
   * Locates all instances of the keywords "extends" and "throws" and adds them to the Visual Feature Matrix
   */
  @Override
  public void visit(MethodDeclaration md, Void arg) {
    super.visit(md, arg);

    if (md.getType().toString().contains("extends")) {
      int lineNumber = md.getType().getChildNodes().get(1).getChildNodes().get(0).getRange().get().begin.line - 1;
      int columnStart = md.getType().getChildNodes().get(1).getChildNodes().get(0).getRange().get().begin.column - 1;
      int columnEnd = columnStart + 6;
      int visualFeatureNumber = KEYWORDS_VISUAL_FEATURE_NUMBER;
      addToVisualMatrix(lineNumber, columnStart, columnEnd, visualFeatureNumber);
    }

    if (md.getThrownExceptions().size() != 0) {
      int lineNumber = md.getThrownException(0).getRange().get().begin.line - 1;
      int columnStart = md.getThrownException(0).getRange().get().begin.column - 8;
      int columnEnd = columnStart + 5;
      int visualFeatureNumber = KEYWORDS_VISUAL_FEATURE_NUMBER;
      addToVisualMatrix(lineNumber, columnStart, columnEnd, visualFeatureNumber);
    }
  }

  /**
   * Locates all instances of the keyword "new" and adds them to the Visual Feature Matrix
   */
  @Override
  public void visit(ObjectCreationExpr oce, Void arg) {
    super.visit(oce, arg);

    int lineNumber = oce.getRange().get().begin.line - 1;
    int columnStart = oce.getRange().get().begin.column - 1;
    int columnEnd = columnStart + 2;
    int visualFeatureNumber = KEYWORDS_VISUAL_FEATURE_NUMBER;
    addToVisualMatrix(lineNumber, columnStart, columnEnd, visualFeatureNumber);
  }

  /**
   * Locates all instances of the keyword "package" and adds them to the Visual Feature Matrix
   */
  @Override
  public void visit(PackageDeclaration pd, Void arg) {
    super.visit(pd, arg);

    int lineNumber = pd.getRange().get().begin.line - 1;
    int columnStart = pd.getRange().get().begin.column - 1;
    int columnEnd = columnStart + 6;
    int visualFeatureNumber = KEYWORDS_VISUAL_FEATURE_NUMBER;
    addToVisualMatrix(lineNumber, columnStart, columnEnd, visualFeatureNumber);
  }

  /**
   * Locates all String literals and adds them to the Visual Feature Matrix
   */
  @Override
  public void visit(StringLiteralExpr sle, Void arg) {
    super.visit(sle, arg);

    int lineNumber = sle.getRange().get().begin.line - 1;
    int columnStart = sle.getRange().get().begin.column;
    int columnEnd = sle.getRange().get().end.column - 2;
    int visualFeatureNumber = STRINGS_VISUAL_FEATURE_NUMBER;
    addToVisualMatrix(lineNumber, columnStart, columnEnd, visualFeatureNumber);
  }

  /**
   * Locates all double literals and adds them to the Visual Feature Matrix
   */
  @Override
  public void visit(DoubleLiteralExpr dle, Void arg) {
    super.visit(dle, arg);

    int lineNumber = dle.getRange().get().begin.line - 1;
    int columnStart = dle.getRange().get().begin.column - 1;
    int columnEnd = dle.getRange().get().end.column - 1;
    int visualFeatureNumber = NUMBERS_VISUAL_FEATURE_NUMBER;
    addToVisualMatrix(lineNumber, columnStart, columnEnd, visualFeatureNumber);
  }

  /**
   * Locates all instances of the keywords "class", "interface", and "implements" and adds them to the Visual Feature Matrix
   * Additionally, locates all line, block, and javadoc comments and adds them to the Visual Feature Matrix
   */
  @Override
  public void visit(ClassOrInterfaceDeclaration cu, Void arg) {
    super.visit(cu, arg);

    int lineNumber = cu.getRange().get().begin.line - 1;
    int columnStart;
    int columnEnd;
    if (cu.isInterface()) {
      columnStart = cu.toString().indexOf("interface");
      columnEnd = columnStart + 8;
    } else {
      columnStart = cu.toString().indexOf("class");
      columnEnd = columnStart + 4;
    }
    int visualFeatureNumber = KEYWORDS_VISUAL_FEATURE_NUMBER;
    addToVisualMatrix(lineNumber, columnStart, columnEnd, visualFeatureNumber);
    
    if (cu.getImplementedTypes().size() != 0) {
      columnStart = 6 + cu.getName().toString().length();
      columnEnd = columnStart + 9;
      addToVisualMatrix(lineNumber, columnStart, columnEnd, visualFeatureNumber);
    }

    visualFeatureNumber = COMMENTS_VISUAL_FEATURE_NUMBER;
    List<Comment> comments = cu.getAllContainedComments();
    for (int i = 0; i < comments.size(); i++) {
      lineNumber = comments.get(i).getRange().get().begin.line - 1;
      int endLineNumber = comments.get(i).getRange().get().end.line - 1;
      columnStart = comments.get(i).getRange().get().begin.column - 1;
      columnEnd = comments.get(i).getRange().get().end.column - 1;
      while (lineNumber < endLineNumber) {
        addToVisualMatrix(lineNumber, columnStart, visualFeatures.getVisualFeaturesMatrix().get((lineNumber)).size() - 1, visualFeatureNumber);
        columnStart = 0;
        lineNumber++;
      }
      addToVisualMatrix(lineNumber, columnStart, columnEnd, visualFeatureNumber);
    }
  }

  /**
   * Locates all instances of the keyword "assert" and adds them to the Visual Feature Matrix
   */
  @Override
  public void visit(AssertStmt ast, Void arg) {
    super.visit(ast, arg);
    
    int lineNumber = ast.getRange().get().begin.line - 1;
    int columnStart = ast.getRange().get().begin.column - 1;
    int columnEnd = ast.getRange().get().end.column - 1;
    int visualFeatureNumber = KEYWORDS_VISUAL_FEATURE_NUMBER;
    addToVisualMatrix(lineNumber, columnStart, columnEnd, visualFeatureNumber);
  }

  /**
   * Locates all instances of boolean and arithmetic operators and adds them to the Visual Feature Matrix
   */
  @Override
  public void visit(BinaryExpr n, Void arg) {
    super.visit(n, arg);
    Operator operator = n.getOperator();
    int lineNumber = n.getChildNodes().get(1).getRange().get().begin.line - 1;
    int columnStart = n.getChildNodes().get(1).getRange().get().begin.column - operator.asString().length() - 2;
    int columnEnd = columnStart + operator.asString().length() - 1;
    addToVisualMatrix(lineNumber, columnStart, columnEnd, OPERATOR_VISUAL_FEATURE_NUMBER);
  }

  /**
   * Locates all instances of boolean literals and adds them to the Visual Feature Matrix
   */
  @Override
  public void visit(BooleanLiteralExpr ble, Void arg) {
    super.visit(ble, arg);

    int lineNumber = ble.getRange().get().begin.line - 1;
    int columnStart = ble.getRange().get().begin.column - 1;
    int columnEnd = ble.getRange().get().end.column - 1;
    int visualFeatureNumber = OTHER_LITERALS_FEATURE_NUMBER;
    addToVisualMatrix(lineNumber, columnStart, columnEnd, visualFeatureNumber);
  }

  /**
   * Locates all instances of the keyword "break" and adds them to the Visual Feature Matrix
   */
  @Override
  public void visit(BreakStmt brst, Void arg) {
    super.visit(brst, arg);

    int lineNumber = brst.getRange().get().begin.line - 1;
    int columnStart = brst.getRange().get().begin.column - 1;
    int columnEnd = columnStart + 4;
    int visualFeatureNumber = KEYWORDS_VISUAL_FEATURE_NUMBER;
    addToVisualMatrix(lineNumber, columnStart, columnEnd, visualFeatureNumber);
  }

  /**
   * Locates all instances of the keyword "catch" and adds them to the Visual Feature Matrix
   */
  @Override
  public void visit(CatchClause cc, Void arg) {
    super.visit(cc, arg);

    int lineNumber = cc.getRange().get().begin.line - 1;
    int columnStart = cc.getRange().get().begin.column - 1;
    int columnEnd = columnStart + 4;
    int visualFeatureNumber = KEYWORDS_VISUAL_FEATURE_NUMBER;
    addToVisualMatrix(lineNumber, columnStart, columnEnd, visualFeatureNumber);
  }

  /**
   * Locates all instances of char literals and adds them to the Visual Feature Matrix
   */
  @Override
  public void visit(CharLiteralExpr cle, Void arg) {
    super.visit(cle, arg);

    int lineNumber = cle.getRange().get().begin.line - 1;
    int columnStart = cle.getRange().get().begin.column - 1;
    int columnEnd = cle.getRange().get().end.column - 1;
    int visualFeatureNumber = OTHER_LITERALS_FEATURE_NUMBER;
    addToVisualMatrix(lineNumber, columnStart, columnEnd, visualFeatureNumber);
  }

  /**
   * Locates all instances of the keyword "continue" and adds them to the Visual Feature Matrix
   */
  @Override
  public void visit(ContinueStmt cs, Void arg) {
    super.visit(cs, arg);

    int lineNumber = cs.getRange().get().begin.line - 1;
    int columnStart = cs.getRange().get().begin.column - 1;
    int columnEnd = cs.getRange().get().end.column - 1;
    int visualFeatureNumber = KEYWORDS_VISUAL_FEATURE_NUMBER;
    addToVisualMatrix(lineNumber, columnStart, columnEnd, visualFeatureNumber);
  }

  /**
   * Locates all instances of the keyword "do" and the instances of the keyword "while" attatched to them and adds them to the Visual Feature Matrix
   */
  @Override
  public void visit(DoStmt ds, Void arg) {
    super.visit(ds, arg);

    int lineNumber = ds.getRange().get().begin.line - 1;
    int columnStart = ds.getRange().get().begin.column - 1;
    int columnEnd = columnStart + 1;
    int visualFeatureNumber = KEYWORDS_VISUAL_FEATURE_NUMBER;
    addToVisualMatrix(lineNumber, columnStart, columnEnd, visualFeatureNumber);

    lineNumber = ds.getCondition().getRange().get().begin.line - 1;
    columnStart = ds.getCondition().getRange().get().begin.column - 8;
    columnEnd = columnStart + 4;
    addToVisualMatrix(lineNumber, columnStart, columnEnd, visualFeatureNumber);
  }

  /**
   * Locates all modifiers, or instances of the keywords "abstract", "default", "final", "native",
   * "non_sealed", "static", "strictfp", "synchronized", "transient", "transitive", and "volatile",
   *  and adds them to the Visual Feature Matrix.
   */
  @Override
  public void visit(Modifier m, Void arg) {
    super.visit(m, arg);
    int lineNumber = m.getRange().get().begin.line -1;
    int columnStart = m.getRange().get().begin.column - 1;
    int columnEnd = m.getRange().get().end.column - 1;
    addToVisualMatrix(lineNumber, columnStart, columnEnd, KEYWORDS_VISUAL_FEATURE_NUMBER);
  }

  /**
   * Locates all instances of the literal "null" and adds them to the Visual Feature Matrix
   */
  @Override
  public void visit(NullLiteralExpr nle, Void arg) {
    super.visit(nle, arg);

    int lineNumber = nle.getRange().get().begin.line - 1;
    int columnStart = nle.getRange().get().begin.column - 1;
    int columnEnd = nle.getRange().get().end.column - 1;
    int visualFeatureNumber = OTHER_LITERALS_FEATURE_NUMBER;
    addToVisualMatrix(lineNumber, columnStart, columnEnd, visualFeatureNumber);
  }

  /**
   * Locates all instances of the keyword "throw" and adds them to the Visual Feature Matrix
   */
  @Override
  public void visit(ReturnStmt rs, Void arg) {
    super.visit(rs, arg);

    int lineNumber = rs.getRange().get().begin.line - 1;
    int columnStart = rs.getRange().get().begin.column - 1;
    int columnEnd = columnStart + 5;
    int visualFeatureNumber = KEYWORDS_VISUAL_FEATURE_NUMBER;
    addToVisualMatrix(lineNumber, columnStart, columnEnd, visualFeatureNumber);
  }

  /**
   * Locates all identifiers and adds them to the Visual Feature Matrix
   */
  @Override
  public void visit(SimpleName sn, Void arg) {
    super.visit(sn, arg);

    int lineNumber = sn.getRange().get().begin.line - 1;
    int columnStart = sn.getRange().get().begin.column - 1;
    int columnEnd = sn.getRange().get().end.column - 1;
    int visualFeatureNumber = IDENTIFIERS_VISUAL_FEATURE_NUMBER;
    addToVisualMatrix(lineNumber, columnStart, columnEnd, visualFeatureNumber);
  }

  /**
   * Locates all instances of the keyword "super" and adds them to the Visual Feature Matrix
   */
  @Override
  public void visit(SuperExpr se, Void arg) {
    super.visit(se, arg);

    int lineNumber = se.getRange().get().begin.line - 1;
    int columnStart = se.getRange().get().begin.column - 1;
    int columnEnd = columnStart + 4;
    int visualFeatureNumber = KEYWORDS_VISUAL_FEATURE_NUMBER;
    addToVisualMatrix(lineNumber, columnStart, columnEnd, visualFeatureNumber);
  }

  /**
   * Locates all instances of the keyword "throw" and adds them to the Visual Feature Matrix
   */
  @Override
  public void visit(ThrowStmt ts, Void arg) {
    super.visit(ts, arg);

    int lineNumber = ts.getRange().get().begin.line - 1;
    int columnStart = ts.getRange().get().begin.column - 1;
    int columnEnd = columnStart + 4;
    int visualFeatureNumber = KEYWORDS_VISUAL_FEATURE_NUMBER;
    addToVisualMatrix(lineNumber, columnStart, columnEnd, visualFeatureNumber);
  }

  /**
   * Locates all instances of the keywords "try" and "finally" and adds them to the Visual Feature Matrix
   */
  @Override
  public void visit(TryStmt ts, Void arg) {
    super.visit(ts, arg);

    int lineNumber = ts.getRange().get().begin.line -1;
    int columnStart = ts.getRange().get().begin.column - 1;
    int columnEnd = columnStart + 2;
    addToVisualMatrix(lineNumber, columnStart, columnEnd, KEYWORDS_VISUAL_FEATURE_NUMBER);

    // if (!ts.getFinallyBlock().isEmpty()) {
    if (ts.getFinallyBlock().isPresent()) {
      lineNumber = ts.getFinallyBlock().get().getRange().get().begin.line - 1;
      columnStart = ts.getFinallyBlock().get().getRange().get().begin.column - 9;
      columnEnd = columnStart + 6;
      addToVisualMatrix(lineNumber, columnStart, columnEnd, KEYWORDS_VISUAL_FEATURE_NUMBER);
    }
  }

  /**
   * Locates all instances of the keyword "this" and adds them to the Visual Feature Matrix
   */
  @Override
  public void visit(ThisExpr te, Void arg) {
    super.visit(te, arg);

    int lineNumber = te.getRange().get().begin.line - 1;
    int columnStart = te.getRange().get().begin.column - 1;
    int columnEnd = columnStart + 3;
    int visualFeatureNumber = KEYWORDS_VISUAL_FEATURE_NUMBER;
    addToVisualMatrix(lineNumber, columnStart, columnEnd, visualFeatureNumber);
  }

  /**
   * Locates all instances of the keyword "void" and adds them to the Visual Feature Matrix
   */
  @Override
  public void visit(VoidType vt, Void arg) {
    super.visit(vt, arg);

    int lineNumber = vt.getRange().get().begin.line - 1;
    int columnStart = vt.getRange().get().begin.column - 1;
    int columnEnd = columnStart + 3;
    int visualFeatureNumber = KEYWORDS_VISUAL_FEATURE_NUMBER;
    addToVisualMatrix(lineNumber, columnStart, columnEnd, visualFeatureNumber);
  }

  /**
   * Locates all instances of primitive type keywords, or the keywords
   * "boolean", "byte", "char", "double", "float", "int", "long", and "short",
   * and adds them to the Visual Feature Matrix
   */
  @Override
  public void visit(PrimitiveType pt, Void arg) {
    super.visit(pt, arg);

    int lineNumber = pt.getRange().get().begin.line - 1;
    int columnStart = pt.getRange().get().begin.column - 1;
    int columnEnd = pt.getRange().get().end.column - 1;
    int visualFeatureNumber = KEYWORDS_VISUAL_FEATURE_NUMBER;
    addToVisualMatrix(lineNumber, columnStart, columnEnd, visualFeatureNumber);
  }

  /**
   * Locates all instances of the keyword "case" and adds them to the Visual Feature Matrix
   */
  @Override
  public void visit(SwitchEntry se, Void arg) {
    super.visit(se, arg);

    int lineNumber = se.getRange().get().begin.line - 1;
    int columnStart = se.getRange().get().begin.column - 1;
    int columnEnd = columnStart + 3;
    int visualFeatureNumber = KEYWORDS_VISUAL_FEATURE_NUMBER;
    addToVisualMatrix(lineNumber, columnStart, columnEnd, visualFeatureNumber);
  }

  /**
   * Locates all instances of the keyword "enum" and adds them to the Visual Feature Matrix
   */
  public void visit(EnumDeclaration ed, Void arg) {
    super.visit(ed, arg);

    int lineNumber = ed.getRange().get().begin.line - 1;
    int columnStart = ed.getRange().get().begin.column - 1;
    int columnEnd = columnStart + 3;
    int visualFeatureNumber = KEYWORDS_VISUAL_FEATURE_NUMBER;
    addToVisualMatrix(lineNumber, columnStart, columnEnd, visualFeatureNumber);
  }

  /**
   * Sets a series of values in the Visual Features Matrix to the approporiate feature number as defined by
   * the location and type of feature visited by one of the above methods
   */
  private void addToVisualMatrix(int lineNumber, int columnStart, int columnEnd, int FEATURE_NUMBER) {
    ArrayList<Integer> line = visualFeatures.getVisualFeaturesMatrix().get(lineNumber);
    for (int i = columnStart; i <= columnEnd; i++) {
      line.set(i, FEATURE_NUMBER);
    }
  }
  
  public VisualFeatures getVisualFeatures() {
    return visualFeatures;
  }
}
