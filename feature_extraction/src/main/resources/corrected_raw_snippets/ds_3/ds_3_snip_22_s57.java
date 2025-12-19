package snippet_splitter_out.ds_3;
public class ds_3_snip_22_s57 {
// Added to allow compilation
// Snippet s57
// SNIPPET_STARTS
public void s57() {
    String classname = (String) in.readObject();
    String devname = (String) in.readObject();
    DeviceIf dev = DriverFactory.getInstance().createDevice(classname, devname);
}
}