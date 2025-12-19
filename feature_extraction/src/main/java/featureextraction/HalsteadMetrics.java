package featureextraction;

/*
 * This class computes Halstead Metrics - program length, program vocabulary, program volume
 * Reference : http://www.virtualmachinery.com/sidebar2.htm
 */
public class HalsteadMetrics {
    
    private Features features;

    public HalsteadMetrics(Features features) {
        this.features = features;
    }

    public int getProgramLength() {
        // #operators = #arithmetic operators + #boolean operators + #comparisons 
        //              + #assignment expressions
        int n1 = features.getArithmeticOperators() + features.getBooleanOperators() + 
        features.getComparisons() + features.getAssignExprs();

        // #operands = #boolean literals + #char literals + #int literals + #long literals 
        //             + #null literals + #string literals + #textblock literals + #float literals 
        //             + #identifiers
        int n2 = features.getLiterals() + features.getIdentifiers();

        // program length = #operators + #operands
        return n1 + n2;
    }

    public int getProgramVocabulary() {
        // program vocabulary = #unique operators + #unique operands
        return features.getOperators().size() + features.getOperands().size();
    }

    public double getProgramVolume() {
        // program volume = program length * log2(program vocabulary)
        return getProgramLength() * (Math.log(getProgramVocabulary()) / Math.log(2));
    }

}
