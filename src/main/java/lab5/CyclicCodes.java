package lab5;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

// POL: 10001000000100001 CRC-16-CCITT
// message <32751

// Test 1
// Pol: 10011
// Input: 11 0101 1011
// Reminder: 1110

// Test 2
// Pol: 10011
// Input: 1110 0101
// Reminder: 1100

// Test 3
// Pol: 10011
// Input: 111 0101 1011
// Reminder: 0111

// Test 4
// Pol: 10011
// Input: 11 0101 1011
// Reminder: 1110

// Test 5 (Syndrome)
// Pol: 10011
// Invalid CRC code: 11 0101 1010 1110
// Reminder 0011


public class CyclicCodes {
    private static final int CRC_CODE_LENGTH = 15;
    private static final int CRC_REMINDER_LENGTH_16 = 16;
    private static final int CRC_REMINDER_LENGTH_4 = 4;
    private static final int POLYNOMIAL_CRC_16_CCITT = 0b10001000000100001;
    private static final int POLYNOMIAL_CRC_4_ITU = 0b10011;

    private static final Random random = new Random();

    public static void main(String[] args) {
        runTests(POLYNOMIAL_CRC_4_ITU, CRC_CODE_LENGTH - CRC_REMINDER_LENGTH_4, CRC_REMINDER_LENGTH_4);
    }

    private static void runTests(int polynomial, int messageLength, int crcReminderLength) {
        String polynomialStr = Integer.toBinaryString(polynomial);
        test1(polynomialStr, crcReminderLength, messageLength);
        test2(polynomialStr, crcReminderLength, messageLength + crcReminderLength);
        test3(polynomialStr, crcReminderLength, messageLength + crcReminderLength);
    }

    private static void test3(String polynomial, int reminderLength, int crcCodeLength) {
        System.out.println("\nTEST. Generate not CRC code and get error syndrome");

        String number;
        do {
            number = generateBits(crcCodeLength);
        } while (isValid(number, polynomial, reminderLength));

        System.out.println("Invalid CRC code: \n" + getFormattedCrc(number, reminderLength));
        System.out.println("Is valid: " + isValid(number, polynomial, reminderLength));
        String errorSyndrome = getErrorSyndrome(number, polynomial, reminderLength);
        System.out.println("Syndrome: " + errorSyndrome);
        Map<String, Integer> syndromeMap = getSyndromeMap(polynomial, reminderLength, crcCodeLength);
        Integer errorPlace = syndromeMap.get(errorSyndrome);
        if (errorPlace == null) {
            System.out.println("Can not detect error place ;(");
            return;
        }
        System.out.println("\nError place: " + errorPlace);
        String validCrc = switchDigitOnPlace(errorPlace, number);
        System.out.println("Valid Crc Code:\n" + getFormattedCrc(validCrc, reminderLength));
        System.out.println("Is valid: " + isValid(validCrc, polynomial, reminderLength) + "\n");

    }

    private static void test2(String polynomial, int reminderLength, int crcCodeLength) {
        System.out.println("\nTEST. Generate random number until number is CRC code");
        String number;
        int iter = 0;
        do {
            number = generateBits(crcCodeLength);
            iter++;
        } while (!isValid(number, polynomial, reminderLength));

        System.out.println("Iter: " + iter);
        System.out.println("CRC code: \n" + getFormattedCrc(number, reminderLength));
        System.out.println("Is valid: " + isValid(number, polynomial, reminderLength) + "\n");
    }

    private static void test1(String polynomial, int reminderLength, int crcCodeLength) {
        System.out.println("\nTEST. Generating CRC Code and validation");
        String crcCode = generateMessageAndCrcCode(crcCodeLength, polynomial, reminderLength);
        System.out.println("Generated CRC code: \n" + getFormattedCrc(crcCode, reminderLength));
        System.out.println("Is valid: " + isValid(crcCode, polynomial, reminderLength) + "\n");
    }

    private static Map<String, Integer> getSyndromeMap(String polynomialStr, int reminderLength, int crcCodeLength) {
        Map<String, Integer> syndromeMap = new HashMap<>();
        for (int i = 0; i < CRC_CODE_LENGTH; i++) {
            String errorCrc = generateCodeWithSingleDigit(i, crcCodeLength);
            String syndrome = getErrorSyndrome(errorCrc, polynomialStr, reminderLength);
            syndromeMap.put(syndrome, i);
        }

        return syndromeMap;
    }

    private static String switchDigitOnPlace(int place, String crcCode) {
        StringBuilder stringBuilder = new StringBuilder(crcCode);
        stringBuilder.reverse();
        char c = stringBuilder.charAt(place);
        if (c == '1') {
            stringBuilder.setCharAt(place, '0');
        } else {
            stringBuilder.setCharAt(place, '1');
        }
        return stringBuilder.reverse().toString();
    }

    private static String generateCodeWithSingleDigit(int place, int maxSize) {
        String zeroesCode = "0".repeat(Math.max(0, maxSize));
        StringBuilder stringBuilder = new StringBuilder(zeroesCode);
        stringBuilder.setCharAt(place, '1');
        return stringBuilder.reverse().toString();
    }

    public static String getErrorSyndrome(String crcCode, String polynomial, int reminderLength) {
        String syndrome = beforeCrcGeneration(crcCode, polynomial);
        if (syndrome.length() < reminderLength) {
            int difference = reminderLength - syndrome.length();
            syndrome = addStartZeroes(syndrome, difference);
        }
        return syndrome;
    }

    public static String getFormattedCrc(String crcCode, int reminderLength) {
        String reminder = getReminderFromCrcCode(crcCode, reminderLength);
        String message = getMessageFromCrcCode(crcCode, reminderLength);
        return message + ' ' + reminder;
    }

    public static String getMessageFromCrcCode(String crcCode, int reminderLength) {
        return crcCode.substring(0, crcCode.length() - reminderLength);
    }

    public static String getReminderFromCrcCode(String crcCode, int reminderLength) {
        int reminderIndex = crcCode.length() - reminderLength;
        return crcCode.substring(reminderIndex);
    }

    public static boolean isValid(String crcCode, String actualReminder, String polynomial) {
        String message = getMessageFromCrcCode(crcCode, actualReminder.length());
        String reminder = generateCrcReminder(message, polynomial, actualReminder.length());
        return reminder.equals(actualReminder);
    }

    public static boolean isValid(String crcCode, String polynomial, int reminderLength) {
        String actualReminder = getReminderFromCrcCode(crcCode, reminderLength);
        String message = getMessageFromCrcCode(crcCode, reminderLength);
        String reminder = generateCrcReminder(message, polynomial, reminderLength);
        return reminder.equals(actualReminder);
    }

    public static String generateMessageAndCrcCode(int messageSize, String polynomial, int reminderLength) {
        String inputData = generateBits(messageSize);
        return generateCrcCode(inputData, polynomial, reminderLength);
    }

    public static String generateCrcCode(String input, String polynomial, int reminderLength) {
        String reminder = generateCrcReminder(input, polynomial, reminderLength);
        return input + reminder;
    }

    private static String generateCrcReminder(String input, String polynomial, int reminderLength) {
        String crcInput = beforeCrcGeneration(input, polynomial);
        return generateCrc(crcInput, polynomial, reminderLength);
    }

    private static String generateCrc(String input, String polynomial, int reminderLength) {
        int tempReminderLength = reminderLength;
        while (tempReminderLength > 0) {
            int difference = calculateDifference(polynomial, input);
            if (difference > tempReminderLength) {
                input = addZeroes(input, tempReminderLength);
                break;
            }

            tempReminderLength -= difference;
            input = addZeroes(input, difference);
            input = binaryStringXor(input, polynomial);
            input = clearZeroes(input);
        }

        int difference = reminderLength - input.length();
        if (difference > 0) {
            input = addStartZeroes(input, difference);
        }

        return input;
    }

    private static String beforeCrcGeneration(String input, String polynomial) {
        int difference = calculateDifference(input, polynomial);
        while (difference >= 0) {
            String extendedPolynomial = addZeroes(polynomial, difference);
            input = binaryStringXor(input, extendedPolynomial);
            input = clearZeroes(input);
            difference = calculateDifference(input, polynomial);
        }
        return input;
    }

    private static String binaryStringXor(String binary1, String binary2) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < binary1.length(); i++) {
            result.append(charOf(bitOf(binary1.charAt(i)) ^ bitOf(binary2.charAt(i))));
        }
        return result.toString();
    }

    private static boolean bitOf(char in) {
        return (in == '1');
    }

    private static char charOf(boolean in) {
        return (in) ? '1' : '0';
    }

    private static int calculateDifference(String input1, String input2) {
        return input1.length() - input2.length();
    }

    private static String addZeroes(String input, int zeroes) {
        return input + "0".repeat(Math.max(0, zeroes));
    }

    private static String addStartZeroes(String input, int zeroes) {
        return "0".repeat(Math.max(0, zeroes)) + input;
    }

    public static String generateBits(int length) {
        String result;
        do {
            result = random.ints(0, 2)
                    .limit(length).mapToObj(Objects::toString).reduce("", String::concat);
            result = clearZeroes(result);
        } while (result.length() < length);
        return result;
    }

    private static String clearZeroes(String input) {
        if (input.isEmpty()) return input;
        StringBuilder result = new StringBuilder(input);
        while (result.charAt(0) == '0') {
            result.deleteCharAt(0);
            if (result.length() == 0) break;
        }
        return result.toString();
    }
}
