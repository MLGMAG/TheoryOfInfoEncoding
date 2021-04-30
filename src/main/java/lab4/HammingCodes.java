package lab4;

import java.util.Arrays;
import java.util.Random;

public class HammingCodes {

    private static final Random random = new Random();
    private static final int MAX_BITS = 16;
    private static int ERROR_POSITION = 0;

    public static void main(String[] args) {
        System.out.println("GENERATE UNTIL VALID APPEARS");
        generateCodesByValid();
        System.out.println("\nGENERATE AND REPAIR");
        generateCodeAndRepairIfNotValid();
    }

    private static void generateCodeAndRepairIfNotValid() {
        int[] bits = generateBits(MAX_BITS);
        System.out.println(Arrays.toString(bits));
        System.out.println("Is code Valid? " + isValid(bits));
        if (!isValid(bits)) {
            System.out.println("Error at position: " + ERROR_POSITION);
            System.out.println("Repairing....");
            if (bits[ERROR_POSITION] == 1) {
                bits[ERROR_POSITION] = 0;
            } else {
                bits[ERROR_POSITION] = 1;
            }
            System.out.println("Checking....");
            System.out.println(Arrays.toString(bits));
            System.out.println("Is code Valid? " + isValid(bits));
        }
    }

    private static void generateCodesByValid() {
        int[] bits = generateBits(MAX_BITS);
        while (!isValid(bits)) {
            System.out.println(Arrays.toString(bits));
            System.out.println("Is code Valid? " + isValid(bits));
            if (!isValid(bits)) System.out.println("Error at position " + ERROR_POSITION + "\n");
            bits = generateBits(MAX_BITS);
        }
        System.out.println("Is code Valid? " + isValid(bits));
        System.out.println(Arrays.toString(bits));
    }

    public static int[] generateBits(int length) {
        return random.ints(0, 2).limit(length).toArray();
    }

    public static boolean isValid(int[] data) {
        int errorPosition = -1;
        for (int i = 0; i < data.length; i++) {
            if (data[i] == 1) {
                if (errorPosition == -1) {
                    errorPosition = i;
                } else {
                    errorPosition = errorPosition ^ i;
                }
            }
        }
        ERROR_POSITION = errorPosition;
        return errorPosition == 0;
    }
}
