package snippet_splitter_out.ds_3;
public class ds_3_snip_67_s99 {
// Added to allow compilation
// Snippet s99
// SNIPPET_STARTS
public Object s99() {
    if (expression.exprType != VALUE && expression.exprType != COLUMN && expression.exprType != FUNCTION && expression.exprType != ALTERNATIVE && expression.exprType != CASEWHEN && expression.exprType != CONVERT) {
        StringBuffer temp = new StringBuffer();
        ddl = temp.append('(').append(ddl).append(')').toString();
    }
    return ddl;
}
}