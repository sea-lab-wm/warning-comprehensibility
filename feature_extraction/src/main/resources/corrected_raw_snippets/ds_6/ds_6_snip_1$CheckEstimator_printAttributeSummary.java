package snippet_splitter_out.ds_6;
public class ds_6_snip_1$CheckEstimator_printAttributeSummary {
    /**
    * Print out a short summary string for the dataset characteristics
    * 
    * @param attrTypes the attribute types used (NUMERIC, NOMINAL, etc.)
    * @param classType the class type (NUMERIC, NOMINAL, etc.)
    */
    protected void printAttributeSummary(AttrTypes attrTypes, int classType) {

        String str = "";

        if (attrTypes.numeric) {
        str += " numeric";
        }

        if (attrTypes.nominal) {
        if (str.length() > 0) {
            str += " &";
        }
        str += " nominal";
        }

        if (attrTypes.string) {
        if (str.length() > 0) {
            str += " &";
        }
        str += " string";
        }

        if (attrTypes.date) {
        if (str.length() > 0) {
            str += " &";
        }
        str += " date";
        }

        if (attrTypes.relational) {
        if (str.length() > 0) {
            str += " &";
        }
        str += " relational";
        }

        str += " attributes)";

        switch (classType) {
        case Attribute.NUMERIC:
        str = " (numeric class," + str;
        break;
        case Attribute.NOMINAL:
        str = " (nominal class," + str;
        break;
        case Attribute.STRING:
        str = " (string class," + str;
        break;
        case Attribute.DATE:
        str = " (date class," + str;
        break;
        case Attribute.RELATIONAL:
        str = " (relational class," + str;
        break;
        }

        print(str);
    }
}