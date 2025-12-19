package snippet_splitter_out.ds_3;
public class ds_3_snip_69_s1 {
// Snippet s1
// SNIPPET_STARTS
public static Object s1() throws ScriptException {
    Object ret = body.eval(callstack, interpreter);
    boolean breakout = false;
    if (ret instanceof ReturnControl) {
        switch(((ReturnControl) ret).kind) {
            case RETURN:
                return ret;
        }
        // had to be added to allow compilation
    }
    // had to be added to allow compilation
    // had to be added to allow compilation
    return ret;
}
}