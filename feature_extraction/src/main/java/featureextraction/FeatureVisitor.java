package featureextraction;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.comments.Comment;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.expr.BinaryExpr.Operator;
import com.github.javaparser.ast.expr.BooleanLiteralExpr;
import com.github.javaparser.ast.expr.CharLiteralExpr;
import com.github.javaparser.ast.expr.ConditionalExpr;
import com.github.javaparser.ast.expr.DoubleLiteralExpr;
import com.github.javaparser.ast.expr.InstanceOfExpr;
import com.github.javaparser.ast.expr.IntegerLiteralExpr;
import com.github.javaparser.ast.expr.LongLiteralExpr;
import com.github.javaparser.ast.expr.NullLiteralExpr;
import com.github.javaparser.ast.expr.SimpleName;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.expr.TextBlockLiteralExpr;
import com.github.javaparser.ast.expr.UnaryExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.stmt.AssertStmt;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.BreakStmt;
import com.github.javaparser.ast.stmt.ContinueStmt;
import com.github.javaparser.ast.stmt.DoStmt;
import com.github.javaparser.ast.stmt.EmptyStmt;
import com.github.javaparser.ast.stmt.ExplicitConstructorInvocationStmt;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.stmt.ForEachStmt;
import com.github.javaparser.ast.stmt.ForStmt;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.LabeledStmt;
import com.github.javaparser.ast.stmt.LocalClassDeclarationStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.stmt.SwitchStmt;
import com.github.javaparser.ast.stmt.SynchronizedStmt;
import com.github.javaparser.ast.stmt.ThrowStmt;
import com.github.javaparser.ast.stmt.TryStmt;
import com.github.javaparser.ast.stmt.WhileStmt;
import com.github.javaparser.ast.type.Type;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * This class to extract features from a java file Input : Java file with a Single Method (Note: if
 * want to use all the features) Output: Features
 */
public class FeatureVisitor extends VoidVisitorAdapter<Void> {

  private Features features = new Features();

  /**
   * This method creates a map of line number and count of given feature value
   */
  private Map<String, Double> constructLineNumberFeatureMap (Map<String, Double> map, String lineNumber, boolean incrementExistingValue) {
    if (map.containsKey(lineNumber)) {
      double numOfValues = map.get(lineNumber);
      if (incrementExistingValue) {
        map.put(lineNumber, numOfValues + 1.0);
      } else {
        map.put(lineNumber, 1.0);
      }
    } else {
      map.put(lineNumber, 1.0);
    }
    return map;
  }

  /**
   * This method to compute #parameters of a java method
   */
  @Override
  public void visit(MethodDeclaration md, Void arg) {
    super.visit(md, arg);
    features.setNumOfParameters(md.getParameters().size());
  
    List<String> elements = new ArrayList<>();
    if (md.getParameters().size() > 0) {
        elements.addAll(Arrays.asList(md.getParameters().toString().split(",")));
    }
    if (md.getThrownExceptions().size() > 0) {
        elements.addAll(Arrays.asList(md.getThrownExceptions().toString().split(",")));
    }

    String lineNumber = md.getRange().get().begin.line + "";
    List<String> param_return_throws_List = features.getLineNumber_param_return_throws_Map().getOrDefault(lineNumber, new ArrayList<>());
    param_return_throws_List.addAll(elements);
    features.getLineNumber_param_return_throws_Map().put(lineNumber, param_return_throws_List);


    // check whether there are any class names inside a method.
    // This inclues both method sinature and method body
    for (Type type: md.findAll(Type.class)){
      features.getClassNames().add(type.asString());
    }
  }

  /**
   * This method to compute #parameters of a java method
   */
  @Override
  public void visit(ConstructorDeclaration cd, Void arg) {
    super.visit(cd, arg);
    features.setNumOfParameters(cd.getParameters().size());
  }

  /**
   * This method to compute #if statements of a java method
   */
  @Override
  public void visit(IfStmt ifStmt, Void arg) {
    super.visit(ifStmt, arg);
    features.incrementNumOfIfStatements();
    features.incrementNumOfConditionals();
    features.incrementNumOfStatements();
    
    if (ifStmt.getParentNode().isPresent() && (ifStmt.getParentNode().get() instanceof BlockStmt || ifStmt.getParentNode().get() instanceof ForStmt || ifStmt.getParentNode().get() instanceof ForEachStmt || ifStmt.getParentNode().get() instanceof WhileStmt || ifStmt.getParentNode().get() instanceof DoStmt || ifStmt.getParentNode().get() instanceof IfStmt || ifStmt.getParentNode().get() instanceof SwitchStmt || ifStmt.getParentNode().get() instanceof TryStmt || ifStmt.getParentNode().get() instanceof SynchronizedStmt)) {
      String lineNumber = ifStmt.getRange().get().begin.line+"";
      boolean incrementExistingValue = false;
      features.setLineNestedBlockMap(constructLineNumberFeatureMap(features.getLineNestedBlockMap(), lineNumber, incrementExistingValue));
      features.incrementNumOfNestedBlocks();
    }

    String lineNumber = ifStmt.getRange().get().begin.line+"";
    boolean incrementExistingValue = true;
    features.setLineConditionalMap(constructLineNumberFeatureMap(features.getLineConditionalMap(), lineNumber, incrementExistingValue));    
  }

  /**
   * This method to compute # switch statements of a java method (not entries)
   */
  @Override
  public void visit(SwitchStmt swStmt, Void arg) {
    super.visit(swStmt, arg);
    features.incrementNumOfConditionals();
    features.incrementNumOfStatements();

    if (swStmt.getParentNode().isPresent() && (!(swStmt.getParentNode().get() instanceof BlockStmt) && (swStmt.getParentNode().get() instanceof ForStmt || swStmt.getParentNode().get() instanceof ForEachStmt || swStmt.getParentNode().get() instanceof WhileStmt || swStmt.getParentNode().get() instanceof DoStmt || swStmt.getParentNode().get() instanceof IfStmt || swStmt.getParentNode().get() instanceof SwitchStmt || swStmt.getParentNode().get() instanceof TryStmt || swStmt.getParentNode().get() instanceof SynchronizedStmt))) {
      features.incrementNumOfNestedBlocks();
    }

    String lineNumber = swStmt.getRange().get().begin.line+"";
    boolean incrementExistingValue = true;
    features.setLineConditionalMap(constructLineNumberFeatureMap(features.getLineConditionalMap(), lineNumber, incrementExistingValue));
  }

  /**
   * This method to compute # for loops of a java method
   */
  @Override
  public void visit(ForStmt forStmt, Void arg) {
    super.visit(forStmt, arg);
    features.incrementNumOfLoops();
    features.incrementNumOfStatements();

    if (forStmt.getParentNode().isPresent() && (forStmt.getParentNode().get() instanceof BlockStmt || forStmt.getParentNode().get() instanceof ForStmt || forStmt.getParentNode().get() instanceof ForEachStmt || forStmt.getParentNode().get() instanceof WhileStmt || forStmt.getParentNode().get() instanceof DoStmt || forStmt.getParentNode().get() instanceof IfStmt || forStmt.getParentNode().get() instanceof SwitchStmt || forStmt.getParentNode().get() instanceof TryStmt || forStmt.getParentNode().get() instanceof SynchronizedStmt)) {
      String lineNumber = forStmt.getRange().get().begin.line+"";
      boolean incrementExistingValue = false;
      features.setLineNestedBlockMap(constructLineNumberFeatureMap(features.getLineNestedBlockMap(), lineNumber, incrementExistingValue));
      features.incrementNumOfNestedBlocks();
    }


    String lineNumber = forStmt.getRange().get().begin.line+"";
    boolean incrementExistingValue = true;
    features.setLineLoopMap(constructLineNumberFeatureMap(features.getLineLoopMap(), lineNumber, incrementExistingValue));
  }

  /**
   * This method to compute # while loops of a java method
   */
  @Override
  public void visit(WhileStmt whileStmt, Void arg) {
    super.visit(whileStmt, arg);
    features.incrementNumOfLoops();
    features.incrementNumOfStatements();

    if (whileStmt.getParentNode().isPresent() && (whileStmt.getParentNode().get() instanceof BlockStmt || whileStmt.getParentNode().get() instanceof ForStmt || whileStmt.getParentNode().get() instanceof ForEachStmt || whileStmt.getParentNode().get() instanceof WhileStmt || whileStmt.getParentNode().get() instanceof DoStmt || whileStmt.getParentNode().get() instanceof IfStmt || whileStmt.getParentNode().get() instanceof SwitchStmt || whileStmt.getParentNode().get() instanceof TryStmt || whileStmt.getParentNode().get() instanceof SynchronizedStmt)) {
      String lineNumber = whileStmt.getRange().get().begin.line+"";
      boolean incrementExistingValue = false;
      features.setLineNestedBlockMap(constructLineNumberFeatureMap(features.getLineNestedBlockMap(), lineNumber, incrementExistingValue));
      features.incrementNumOfNestedBlocks();
    }

    String lineNumber = whileStmt.getRange().get().begin.line+"";
    boolean incrementExistingValue = true;
    features.setLineLoopMap(constructLineNumberFeatureMap(features.getLineLoopMap(), lineNumber, incrementExistingValue));
  }

  /**
   * This method to compute # for each loops of a java method
   */
  @Override
  public void visit(ForEachStmt forEachStmt, Void arg) {
    super.visit(forEachStmt, arg);
    features.incrementNumOfLoops();
    features.incrementNumOfStatements();

    if (forEachStmt.getParentNode().isPresent() && (forEachStmt.getParentNode().get() instanceof BlockStmt || forEachStmt.getParentNode().get() instanceof ForStmt || forEachStmt.getParentNode().get() instanceof ForEachStmt || forEachStmt.getParentNode().get() instanceof WhileStmt || forEachStmt.getParentNode().get() instanceof DoStmt || forEachStmt.getParentNode().get() instanceof IfStmt || forEachStmt.getParentNode().get() instanceof SwitchStmt || forEachStmt.getParentNode().get() instanceof TryStmt || forEachStmt.getParentNode().get() instanceof SynchronizedStmt)) {
      String lineNumber = forEachStmt.getRange().get().begin.line+"";
      boolean incrementExistingValue = false;
      features.setLineNestedBlockMap(constructLineNumberFeatureMap(features.getLineNestedBlockMap(), lineNumber, incrementExistingValue));
      features.incrementNumOfNestedBlocks();
    }
    
    String lineNumber = forEachStmt.getRange().get().begin.line+"";
    boolean incrementExistingValue = true;
    features.setLineLoopMap(constructLineNumberFeatureMap(features.getLineLoopMap(), lineNumber, incrementExistingValue));
  }

  /**
   * This method computes # assignment expressions in a java method
   * Does not include declaration statements
   */
  @Override
  public void visit(AssignExpr assignExpr, Void arg) {
    super.visit(assignExpr, arg);
    features.setAssignExprs(features.getAssignExprs() + 1);
    String lineNumber = assignExpr.getRange().get().begin.line+"";
    boolean incrementExistingValue = true;
    features.setLineAssignmentExpressionMap(constructLineNumberFeatureMap(features.getLineAssignmentExpressionMap(), lineNumber, incrementExistingValue));
  }

  /*
   * This method computes # assignment expressions in a java method
   * Includes declaration statements
   */
  @Override
  public void visit(VariableDeclarationExpr vdExpr, Void arg) {
    super.visit(vdExpr, arg);
    features.setAssignExprs(features.getAssignExprs() + 1);
    String lineNumber = vdExpr.getRange().get().begin.line+"";
    boolean incrementExistingValue = true;
    features.setLineAssignmentExpressionMap(constructLineNumberFeatureMap(features.getLineAssignmentExpressionMap(), lineNumber, incrementExistingValue));
  }

  /**
   * This method computes # comparisons and arithmetic operators in a java method
   */
  @Override
  public void visit(BinaryExpr n, Void arg) {
    super.visit(n, arg);
    Operator operator = n.getOperator();
    String lineNumber = n.getRange().get().begin.line+"";
    boolean incrementExistingValue = true;
    if (operator == Operator.EQUALS
        || operator == Operator.NOT_EQUALS
        || operator == Operator.LESS
        || operator == Operator.LESS_EQUALS
        || operator == Operator.GREATER
        || operator == Operator.GREATER_EQUALS) {
      features.setComparisons(features.getComparisons() + 1);

      features.setLineComparisonMap(constructLineNumberFeatureMap(features.getLineComparisonMap(), lineNumber, incrementExistingValue));
      
      features.setLineOperatorMap(constructLineNumberFeatureMap(features.getLineOperatorMap(), lineNumber, incrementExistingValue));
      
      features.getOperators().add(operator.asString()); // add operators

    } else if (operator == Operator.PLUS
        || operator == Operator.MINUS
        || operator == Operator.MULTIPLY
        || operator == Operator.DIVIDE
        || operator == Operator.REMAINDER) {
      features.incrementNumOfArithmeticOperators();

      features.setLineOperatorMap(constructLineNumberFeatureMap(features.getLineOperatorMap(), lineNumber, incrementExistingValue));

      features.getOperators().add(operator.asString()); // add operators

    } else if (operator == Operator.AND
        || operator == Operator.OR
        || operator == Operator.BINARY_AND
        || operator == Operator.BINARY_OR
        || operator == Operator.XOR
        || operator == Operator.LEFT_SHIFT 
        || operator == Operator.SIGNED_RIGHT_SHIFT 
        || operator == Operator.UNSIGNED_RIGHT_SHIFT) {
      features.incrementNumOfLogicalOperators();

      features.setLineOperatorMap(constructLineNumberFeatureMap(features.getLineOperatorMap(), lineNumber, incrementExistingValue));

      features.getOperators().add(operator.asString()); // add operators
    }
  }

  /**
   * This method identifies boolean literals in a java method and sums them up to the total number
   * of literals
   */
  @Override
  public void visit(BooleanLiteralExpr ble, Void arg) {
    super.visit(ble, arg);
    features.incrementNumOfLiterals();
    features.getOperands().add(ble.toString()); // add operands
  }

  /**
   * This method identifies char literals in a java method and sums them up to the total number of
   * literals
   */
  @Override
  public void visit(CharLiteralExpr cle, Void arg) {
    super.visit(cle, arg);
    features.incrementNumOfLiterals();
    features.getOperands().add(cle.getValue()); // add operands
  }

  /**
   * This method identifies integer literals in a java method and sums them up to the total number
   * of literals
   */
  @Override
  public void visit(IntegerLiteralExpr ile, Void arg) {
    super.visit(ile, arg);
    features.incrementNumOfLiterals();
    features.incrementNumOfNumbers();
    features.getOperands().add(ile.getValue()); // add operands
    String lineNumber = ile.getRange().get().begin.line+"";
    if(features.getLineNumberMap().containsKey(lineNumber)){
      List<Integer> list = features.getLineNumberMap().get(lineNumber);
      list.add(ile.asNumber().intValue());
      features.getLineNumberMap().put(lineNumber,list);
    }else{
      List<Integer> list = new ArrayList<>();
      list.add(ile.asNumber().intValue());
      features.getLineNumberMap().put(lineNumber,list);
    }
  }

  /**
   * This method identifies the long literals in a java method and sums them up to the total number
   * of literals
   */
  @Override
  public void visit(LongLiteralExpr lle, Void arg) {
    super.visit(lle, arg);
    features.incrementNumOfLiterals();
    features.incrementNumOfNumbers();
    features.getOperands().add(lle.getValue()); // add operands
  }

  /**
   * This method identifies null literals in a java method and sums them up to the total number of
   * literals
   */
  @Override
  public void visit(NullLiteralExpr nle, Void arg) {
    super.visit(nle, arg);
    features.incrementNumOfLiterals();
    features.getOperands().add(nle.toString()); // add operands
  }

  /**
   * This method identifies string literals in a java method and sums them up to the total number of
   * literals
   */
  @Override
  public void visit(StringLiteralExpr sle, Void arg) {
    super.visit(sle, arg);
    features.incrementNumOfLiterals();
    features.getOperands().add(sle.getValue()); // add operands
  }

  /**
   * This method identifies text block literals in a java method and sums them up to the total
   * number of literals
   */
  @Override
  public void visit(TextBlockLiteralExpr tble, Void arg) {
    super.visit(tble, arg);
    features.incrementNumOfLiterals();
    features.getOperands().add(tble.getValue()); // add operands
  }

  /**
   * This method identifies double literals in a java method and sums them up to the total number of
   * literals
   */
  @Override
  public void visit(DoubleLiteralExpr dle, Void arg) {
    super.visit(dle, arg);
    features.incrementNumOfLiterals();
    features.incrementNumOfNumbers();
    features.getOperands().add(dle.getValue()); // add operands
  }

  /**
   * This method identifies comments in a java method and sums them up to the total number of
   * comments. The ClassOrInterfaceDeclaration method getAllContainedComments() is used in order
   * to prevent orphan comments from being ignored by the Parser.
   */
  @Override
  public void visit(ClassOrInterfaceDeclaration cu, Void arg) {
    super.visit(cu, arg);
    features.setNumOfComments(cu.getAllContainedComments().size());

    for (Comment comment : cu.getAllContainedComments()) {
      String lineNumber = comment.getRange().get().begin.line+"";
      boolean incrementExistingValue = true;
      features.setLineCommentMap(constructLineNumberFeatureMap(features.getLineCommentMap(), lineNumber, incrementExistingValue));
    
      // add comments
      features.getComments().add(comment.getContent());
    }
  }
  

  /**
   * This method identifies identifiers in a java method and sums them up to the total number of
   * operands
   */
  @Override
  public void visit(SimpleName sn, Void arg) {
    super.visit(sn, arg);

    // if identifier contain ds_[number]_snip_[number] then check regex and ignore
    // this will ignore the identifiers that are generated by the snippet splitter
    if(Pattern.compile("^ds_[a-zA-Z0-9]+[$_]").matcher(sn.getIdentifier()).find()){
      return;
    }
    

    features.incrementNumOfIdentifiers();
    features.getOperands().add(sn.getIdentifier()); // add operands
    
    String lineNumber = sn.getRange().get().begin.line+"";
    if(features.getLineNumber_Identifier_Map().containsKey(lineNumber)){
      List<String> identifierList = features.getLineNumber_Identifier_Map().get(lineNumber);
      
      identifierList.add(sn.getIdentifier());
      features.getLineNumber_Identifier_Map().put(lineNumber,identifierList);

    }else{
      List<String> identifierList = new ArrayList<String>();
      identifierList.add(sn.getIdentifier());
      features.getLineNumber_Identifier_Map().put(lineNumber,identifierList);

    }
  }

  /**
   * This method identifies Assert Statements in a java method to sum up the total number of
   * all statements
   */
  @Override
  public void visit(AssertStmt ast, Void arg) {
    super.visit(ast, arg);
    features.incrementNumOfStatements();
  }

  /**
   * This method identifies Block Statements in a java method to sum up the total number of 
   * all statements. It also counts the number of nested blocks.
   */
  @Override
  public void visit(BlockStmt bst, Void arg) {
    boolean labledStmtIsConditional = false;
    if (bst.getParentNode().isPresent() && bst.getParentNode().get() instanceof LabeledStmt) {
      // now the bst's parent is a labledStmt. Check whether it is a
      // for/foreach/while stmt/do stmt/switch stmt/if stmt/try stmt/synchronized stmt
      LabeledStmt ls = (LabeledStmt) bst.getParentNode().get();
      if (ls.getStatement() instanceof ForStmt || ls.getStatement() instanceof ForEachStmt
          || ls.getStatement() instanceof WhileStmt || ls.getStatement() instanceof DoStmt
          || ls.getStatement() instanceof IfStmt || ls.getStatement() instanceof SwitchStmt
          || ls.getStatement() instanceof TryStmt || ls.getStatement() instanceof SynchronizedStmt) {
        labledStmtIsConditional = true;
      }
    }

    if (bst.getParentNode().isPresent() && (bst.getParentNode().get() instanceof BlockStmt
        || bst.getParentNode().get() instanceof ForStmt || bst.getParentNode().get() instanceof ForEachStmt
        || bst.getParentNode().get() instanceof WhileStmt || bst.getParentNode().get() instanceof DoStmt
        || bst.getParentNode().get() instanceof IfStmt || bst.getParentNode().get() instanceof SwitchStmt
        || bst.getParentNode().get() instanceof TryStmt || bst.getParentNode().get() instanceof SynchronizedStmt
        || bst.getParentNode().get() instanceof LocalClassDeclarationStmt || labledStmtIsConditional)) {

          
      // check if the bst is a else block
      if (bst.getParentNode().get() instanceof IfStmt) {
        IfStmt ifStmt = (IfStmt) bst.getParentNode().get();
        if (ifStmt.getElseStmt().isPresent() && ifStmt.getElseStmt().get().equals(bst)) {
          // check the else block contains any IfStmt, ForStmt, ForEachStmt, WhileStmt,
          // DoStmt, SwitchStmt, TryStmt, SynchronizedStmt or BlockStmt
          if (bst.findAll(IfStmt.class).size() > 0 || bst.findAll(ForStmt.class).size() > 0
              || bst.findAll(ForEachStmt.class).size() > 0 || bst.findAll(WhileStmt.class).size() > 0
              || bst.findAll(DoStmt.class).size() > 0 || bst.findAll(SwitchStmt.class).size() > 0
              || bst.findAll(TryStmt.class).size() > 0 || bst.findAll(SynchronizedStmt.class).size() > 0
              || bst.findAll(BlockStmt.class).size() > 0) {
            // String lineNumber = bst.getRange().get().begin.line + "";
            // boolean incrementExistingValue = false;
            // features.setLineNestedBlockMap(
            //     constructLineNumberFeatureMap(features.getLineNestedBlockMap(), lineNumber, incrementExistingValue));
            super.visit(bst, arg);
          }
        } else {
          features.incrementNumOfNestedBlocks();
          String lineNumber = bst.getRange().get().begin.line + "";
          boolean incrementExistingValue = false;
          features.setLineNestedBlockMap(
              constructLineNumberFeatureMap(features.getLineNestedBlockMap(), lineNumber, incrementExistingValue));

        }

        
      }

    }
    super.visit(bst, arg);
  }


  /**
   * This method identifies Break Statements in a java method to sum up the total number of 
   * all statements
   */
  @Override
  public void visit(BreakStmt brst, Void arg) {
    super.visit(brst, arg);
    features.incrementNumOfStatements();
  }

  /**
   * This method identifies Catch Clauses in a java method to sum up the total number of 
   * all statements
   */
  // @Override
  // public void visit(CatchClause cc, Void arg) {
  //   super.visit(cc, arg);
  //   features.incrementNumOfStatements();

  //   // print line number and statement type
  //   System.out.println("Line Number: " + cc.getRange().get().begin.line + " Statement Type: " + cc.getClass().getSimpleName());
  // }

  /**
   * This method identifies Continue Statements in a java method to sum up the total number of 
   * all statements
   */
  @Override
  public void visit(ContinueStmt cs, Void arg) {
    super.visit(cs, arg);
    features.incrementNumOfStatements();

    // print line number and statement type
    // System.out.println("Line Number: " + cs.getRange().get().begin.line + " Statement Type: " + cs.getClass().getSimpleName());
  }

  /**
   * This method identifies Do Statements in a java method to sum up the total number of 
   * all statements
   */
  @Override
  public void visit(DoStmt ds, Void arg) {
    super.visit(ds, arg);
    features.incrementNumOfStatements();
    features.incrementNumOfLoops();

    if (ds.getParentNode().isPresent() && (ds.getParentNode().get() instanceof BlockStmt || ds.getParentNode().get() instanceof ForStmt || ds.getParentNode().get() instanceof ForEachStmt || ds.getParentNode().get() instanceof WhileStmt || ds.getParentNode().get() instanceof DoStmt || ds.getParentNode().get() instanceof IfStmt || ds.getParentNode().get() instanceof SwitchStmt || ds.getParentNode().get() instanceof TryStmt || ds.getParentNode().get() instanceof SynchronizedStmt)) {
      String lineNumber = ds.getRange().get().begin.line+"";
      boolean incrementExistingValue = false;
      features.setLineNestedBlockMap(constructLineNumberFeatureMap(features.getLineNestedBlockMap(), lineNumber, incrementExistingValue));
      features.incrementNumOfNestedBlocks();
    }
    
  }

  /**
   * This method identifies Empty Statements in a java method to sum up the total number of 
   * all statements.
   * eg. ;
   */
  @Override
  public void visit(EmptyStmt es, Void arg) {
    super.visit(es, arg);
    features.incrementNumOfStatements();

  }

  /**
   * This method identifies Explicit Constructor Invocation Statements in a java method to sum up the total number of 
   * all statements.
   */
  @Override
  public void visit(ExplicitConstructorInvocationStmt ecis, Void arg) {
    super.visit(ecis, arg);
    features.incrementNumOfStatements();

  }

  /**
   * This method identifies Expression Statements in a java method to sum up the total number of 
   * all statements.
   */
  @Override
  public void visit(ExpressionStmt es, Void arg) {
    super.visit(es, arg);
    features.incrementNumOfStatements();

  }

  /**
   * This method identifies Labeled Statements in a java method to sum up the total number of 
   * all statements.
   */
  @Override
  public void visit(LabeledStmt ls, Void arg) {
    super.visit(ls, arg);
    features.incrementNumOfStatements();

    // check the labeledstmt is a for stmt/foreach/while stmt/do stmt/switch stmt/if stmt/try stmt/synchronized stmt
    if (ls.getStatement() instanceof ForStmt || ls.getStatement() instanceof ForEachStmt || ls.getStatement() instanceof WhileStmt || ls.getStatement() instanceof DoStmt || ls.getStatement() instanceof IfStmt || ls.getStatement() instanceof SwitchStmt || ls.getStatement() instanceof TryStmt || ls.getStatement() instanceof SynchronizedStmt) {
      if (ls.getParentNode().isPresent() && (ls.getParentNode().get() instanceof BlockStmt || ls.getParentNode().get() instanceof ForStmt || ls.getParentNode().get() instanceof ForEachStmt || ls.getParentNode().get() instanceof WhileStmt || ls.getParentNode().get() instanceof DoStmt || ls.getParentNode().get() instanceof IfStmt || ls.getParentNode().get() instanceof SwitchStmt || ls.getParentNode().get() instanceof TryStmt || ls.getParentNode().get() instanceof SynchronizedStmt)) {
        String lineNumber = ls.getRange().get().begin.line+"";
        boolean incrementExistingValue = false;
        features.setLineNestedBlockMap(constructLineNumberFeatureMap(features.getLineNestedBlockMap(), lineNumber, incrementExistingValue));
        features.incrementNumOfNestedBlocks();
      }
    }

  }

  /**
   * This method identifies Local Class Declaration Statements in a java method to sum up the total number of 
   * all statements.
   */
  @Override
  public void visit(LocalClassDeclarationStmt lcds, Void arg) {
    super.visit(lcds, arg);
    features.incrementNumOfStatements();

    if (lcds.getParentNode().isPresent() && lcds.getParentNode().get() instanceof BlockStmt) {
        String lineNumber = lcds.getRange().get().begin.line+"";
        boolean incrementExistingValue = false;
        features.setLineNestedBlockMap(constructLineNumberFeatureMap(features.getLineNestedBlockMap(), lineNumber, incrementExistingValue));
        features.incrementNumOfNestedBlocks();
    }


    }

  /**
   * This method identifies Local Record Declaration Statements in a java method to sum up the total number of 
   * all statements. // can't use since introduced in Java 14
   */
  // @Override
  // public void visit(LocalRecordDeclarationStmt lrds, Void arg) {
  //   super.visit(lrds, arg);
  //   features.incrementNumOfStatements();

  //   // print line number and statement type
  //   // System.out.println("Line Number: " + lrds.getRange().get().begin.line + " Statement Type: " + lrds.getClass().getSimpleName());
  // }

  /**
   * This method identifies Return Statements in a java method to sum up the total number of 
   * all statements.
   */
  @Override
  public void visit(ReturnStmt rs, Void arg) {
    super.visit(rs, arg);
    features.incrementNumOfStatements();

    // print line number and statement type
    // System.out.println("Line Number: " + rs.getRange().get().begin.line + " Statement Type: " + rs.getClass().getSimpleName());

    String lineNumber = rs.getRange().get().begin.line+"";
    if(features.getLineNumber_param_return_throws_Map().containsKey(lineNumber)){
      List<String> param_return_throws_List = features.getLineNumber_param_return_throws_Map().get(lineNumber);
      param_return_throws_List.add(rs.getExpression().toString());
      features.getLineNumber_param_return_throws_Map().put(lineNumber,param_return_throws_List);
    }else{
      List<String> param_return_throws_List = new ArrayList<String>();
      param_return_throws_List.add(rs.getExpression().toString());
      features.getLineNumber_param_return_throws_Map().put(lineNumber,param_return_throws_List);
    }

  }

  /**
   * This method identifies Synchronized Statements in a java method to sum up the total number of 
   * all statements.
   */
  @Override
  public void visit(SynchronizedStmt ss, Void arg) {
    super.visit(ss, arg);
    features.incrementNumOfStatements();

    if (ss.getParentNode().isPresent() && ((ss.getParentNode().get() instanceof BlockStmt) || (ss.getParentNode().get() instanceof ForStmt || ss.getParentNode().get() instanceof ForEachStmt || ss.getParentNode().get() instanceof WhileStmt || ss.getParentNode().get() instanceof DoStmt || ss.getParentNode().get() instanceof IfStmt || ss.getParentNode().get() instanceof SwitchStmt || ss.getParentNode().get() instanceof TryStmt || ss.getParentNode().get() instanceof SynchronizedStmt))) {
        String lineNumber = ss.getRange().get().begin.line+"";
        boolean incrementExistingValue = false;
        features.setLineNestedBlockMap(constructLineNumberFeatureMap(features.getLineNestedBlockMap(), lineNumber, incrementExistingValue));
        features.incrementNumOfNestedBlocks();
    }

  }

  /**
   * This method identifies Throw Statements in a java method to sum up the total number of 
   * all statements.
   */
  @Override
  public void visit(ThrowStmt ts, Void arg) {
    super.visit(ts, arg);
    features.incrementNumOfStatements();

    String lineNumber = ts.getRange().get().begin.line+"";
    if(features.getLineNumber_param_return_throws_Map().containsKey(lineNumber)){
      List<String> param_return_throws_List = features.getLineNumber_param_return_throws_Map().get(lineNumber);
      param_return_throws_List.add(ts.getExpression().toString());
      features.getLineNumber_param_return_throws_Map().put(lineNumber,param_return_throws_List);
    }else{
      List<String> param_return_throws_List = new ArrayList<String>();
      param_return_throws_List.add(ts.getExpression().toString());
      features.getLineNumber_param_return_throws_Map().put(lineNumber,param_return_throws_List);
    }

  }

  /**
   * This method identifies Try Statements in a java method to sum up the total number of 
   * all statements.
   */
  @Override
  public void visit(TryStmt ts, Void arg) {
    super.visit(ts, arg);
    features.incrementNumOfStatements();

    if (ts.getParentNode().isPresent() && ((ts.getParentNode().get() instanceof BlockStmt) || (ts.getParentNode().get() instanceof ForStmt || ts.getParentNode().get() instanceof ForEachStmt || ts.getParentNode().get() instanceof WhileStmt || ts.getParentNode().get() instanceof DoStmt || ts.getParentNode().get() instanceof IfStmt || ts.getParentNode().get() instanceof SwitchStmt || ts.getParentNode().get() instanceof TryStmt || ts.getParentNode().get() instanceof SynchronizedStmt))) {
        String lineNumber = ts.getRange().get().begin.line+"";
        boolean incrementExistingValue = false;
        features.setLineNestedBlockMap(constructLineNumberFeatureMap(features.getLineNestedBlockMap(), lineNumber, incrementExistingValue));
        features.incrementNumOfNestedBlocks();
    }

    // print line number and statement type
    // System.out.println("Line Number: " + ts.getRange().get().begin.line + " Statement Type: " + ts.getClass().getSimpleName());
  }

  /**
   * This method identifies Yield Statements in a java method to sum up the total number of 
   * all statements. // Can't use since introduced in Java 14
   */
  // @Override
  // public void visit(YieldStmt ys, Void arg) {
  //   super.visit(ys, arg);
  //   features.incrementNumOfStatements();

  //   // print line number and statement type
  //   System.out.println("Line Number: " + ys.getRange().get().begin.line + " Statement Type: " + ys.getClass().getSimpleName());
  // }

  @Override
  public void visit(UnaryExpr ue, Void arg) {
    super.visit(ue, arg);
    
    com.github.javaparser.ast.expr.UnaryExpr.Operator operator = ue.getOperator();
    String lineNumber = ue.getRange().get().begin.line+"";
    boolean incrementExistingValue = true;
    if (operator == com.github.javaparser.ast.expr.UnaryExpr.Operator.POSTFIX_INCREMENT ||
        operator == com.github.javaparser.ast.expr.UnaryExpr.Operator.PREFIX_INCREMENT ||
        operator == com.github.javaparser.ast.expr.UnaryExpr.Operator.POSTFIX_DECREMENT ||
        operator == com.github.javaparser.ast.expr.UnaryExpr.Operator.PREFIX_DECREMENT ||
        operator == com.github.javaparser.ast.expr.UnaryExpr.Operator.LOGICAL_COMPLEMENT
    ) {
      
      features.setLineOperatorMap(constructLineNumberFeatureMap(features.getLineOperatorMap(), lineNumber, incrementExistingValue));
      
      features.getOperators().add(operator.asString()); // add operators

    }
  }

  @Override
  public void visit(ConditionalExpr ce, Void arg) {
    super.visit(ce, arg);
    // check ternary operator
    features.incrementNumOfConditionals();

    String lineNumber = ce.getRange().get().begin.line+"";
    boolean incrementExistingValue = true;
    features.setLineOperatorMap(constructLineNumberFeatureMap(features.getLineOperatorMap(), lineNumber, incrementExistingValue));
        
    features.getOperators().add("?"); // add operators
    features.getOperators().add(":"); // add operators

  }

  @Override
  public void visit(InstanceOfExpr ioe, Void arg) {
    super.visit(ioe, arg);
    String lineNumber = ioe.getRange().get().begin.line+"";
    boolean incrementExistingValue = true;
    features.setComparisons(features.getComparisons() + 1);
    features.setLineComparisonMap(constructLineNumberFeatureMap(features.getLineComparisonMap(), lineNumber, incrementExistingValue));
  
    features.setLineOperatorMap(constructLineNumberFeatureMap(features.getLineOperatorMap(), lineNumber, incrementExistingValue));  
    features.getOperators().add("instanceof"); // add operators

  }

  /**
   * This method is to get the computed features After one/more visit method is/are called, the
   * features will be updated and then use this method to get the updated features
   */
  public Features getFeatures() {
    return features;
  }
}
