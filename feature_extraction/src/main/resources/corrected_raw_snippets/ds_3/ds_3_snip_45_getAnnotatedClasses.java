package snippet_splitter_out.ds_3;
public class ds_3_snip_45_getAnnotatedClasses {
// Added to allow compilation
// Snippet s78                                                                                      /*ORIGINALLY COMMENTED OUT*/
// SNIPPET_STARTS
private static Class<?>[] getAnnotatedClasses(Class<?> klass) throws InitializationError {
    SuiteClasses annotation = klass.getAnnotation(SuiteClasses.class);
    if (annotation == null)
        throw new Tasks_3("message").new InitializationError(String.format("class '%s' must have a SuiteClasses annotation", klass.getName()));
    return annotation.value();
}
}