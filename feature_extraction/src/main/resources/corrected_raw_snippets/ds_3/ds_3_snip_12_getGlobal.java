package snippet_splitter_out.ds_3;
public class ds_3_snip_12_getGlobal {
// Added to allow compilation
// Snippet s46
/**
 *     Get the top level namespace or this namespace if we are the top.
 *     Note: this method should probably return type bsh.This to be consistent
 *     with getThis();
 */
// SNIPPET_STARTS
public This getGlobal(Interpreter declaringInterpreter) {
    if (parent != null)
        return parent.getGlobal(declaringInterpreter);
    else
        return getThis(declaringInterpreter);
}
}