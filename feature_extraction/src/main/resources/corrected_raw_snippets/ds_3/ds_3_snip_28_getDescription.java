package snippet_splitter_out.ds_3;
public class ds_3_snip_28_getDescription {
// Added to allow compilation
// Snippet s63
// @Override // Removed to allow compilation
// SNIPPET_STARTS
public Description getDescription() {
    Description spec = Description.createSuiteDescription(getName());
    List<Method> testMethods = fTestMethods;
    for (Method method : testMethods) spec.addChild(methodDescription(method));
    // Added to allow compilation
    return null;
}
}