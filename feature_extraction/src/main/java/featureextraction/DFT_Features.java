package featureextraction;

import java.util.ArrayList;
import java.util.Arrays;

import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;
import org.apache.commons.math3.transform.DftNormalization;
import org.apache.commons.math3.transform.FastFourierTransformer;
import org.apache.commons.math3.transform.TransformType;

public class DFT_Features {
   
    /*
     * Fast Fourier Transform for given signal - this signal is a 1D array of doubles
    */
    public static double[] fft(double[] signal) {
        FastFourierTransformer fft = new FastFourierTransformer(DftNormalization.STANDARD);
        Complex[] result = fft.transform(signal, TransformType.FORWARD); // Forward FFT
        double[] amplitudes = new double[result.length]; // Keep the amplitudes of the DFT of the signal
        for (int i = 0; i < result.length; i++) {
            double real = result[i].getReal(); // get the real part of the complex number
            double imaginary = result[i].getImaginary(); // get the imaginary part of the complex number
            amplitudes[i] = Math.sqrt((real * real) + (imaginary * imaginary)); // calculate the amplitude
        }
        return amplitudes;
    }

    /*
     * Get the standard deviation of the amplitudes of the DFT of the signal
     */
    public static double getStandardDeviation(double[] amplitudes) {
        StandardDeviation sd = new StandardDeviation();
        return sd.evaluate(amplitudes);
    }

    public static long getDFTBandwith(double[] signal) {
        // if signal.length is not a power of 2, then pad the signal with zeros
        int length = signal.length;
        int powerOfTwo = 1;
        while (powerOfTwo < length) {
            powerOfTwo *= 2;
        }
        if (powerOfTwo > length) {
            double[] paddedSignal = new double[powerOfTwo];
            for (int i = 0; i < length; i++) {
                paddedSignal[i] = signal[i];
            }
            signal = paddedSignal;
        }
        double [] amplitudes = fft(signal);
        double sd = getStandardDeviation(amplitudes);
        ArrayList<Integer >indexOfMaxAmplitudes = new ArrayList<>();
        for (int i = 0; i < amplitudes.length; i++) {
            if (amplitudes[i] > sd) { // to handle the cases where only zero contains in the amplitudes array
                indexOfMaxAmplitudes.add(i);
            }
        }
       
        double frequency [] = new double [indexOfMaxAmplitudes.size()];
        if (indexOfMaxAmplitudes.size() == 0) {
            frequency = new double[1];
            frequency[0] = 0;
        } 

        for (int i = 0; i <indexOfMaxAmplitudes.size(); i++) {
            // frequency = bin_index * sampling_rate / #bins
            // sampling_rate = 10
            // #bins = amplitudes.length
            // bin_index = indexOfMaxAmplitude
            frequency[i] = (indexOfMaxAmplitudes.get(i) * 10.0) / amplitudes.length; 
        }

        // get the maximum frequency
        Arrays.sort(frequency);
        double maxFrequency = frequency[frequency.length - 1];
        return Math.round(2 * maxFrequency + 1); // return the bandwidth
        
    }
}
