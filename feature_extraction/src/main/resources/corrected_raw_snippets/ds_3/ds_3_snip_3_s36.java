package snippet_splitter_out.ds_3;
public class ds_3_snip_3_s36 {
// Snippet s36
// SNIPPET_STARTS
public Object s36() {
    Class clas = object.getClass();
    Field field = Reflect.resolveJavaField(clas, name, false);
    if (field != null)
        return new Variable(name, field.getType(), new LHS(object, field));
    return object;
    /*Altered return*/
    // return null; // Added to allow compilation
}
}