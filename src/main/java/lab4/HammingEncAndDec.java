package lab4;

import lab1.FileEntropy;
import lab2.Huffman;
import lab2.Node;

import java.util.HashMap;
import java.util.Map;

public class HammingEncAndDec {
    private static final Map<Character, String> encodingMap = new HashMap<>();
    private static Node rootNode;

    public static void main(String[] args) {
        String testData = "Akhmedov Mahomed Shamilievich 2001 Kyiv DA-91 SH89";
        System.out.println("Original massage: \n" + testData);
        System.out.println();

        String encodedData = huffmanEncoding(testData);
        System.out.println("Huffman encoded data: \n" + encodedData);
        System.out.println();

        int[] hammingEncodedData = hammingEncoding(encodedData);
        System.out.println();

        String hummingDecodedMessage = hammingDecoding(hammingEncodedData);
        System.out.println("Hamming Decoded Message: \n" + hummingDecodedMessage);
        System.out.println();

        String decodedData = Huffman.huffmanDecoding(encodedData, rootNode);
        System.out.println("Huffman decoded data:\n" + decodedData);
    }

    public static int[] hammingEncoding(String message) {
        int messageLength = message.length();
        int hammingPower = calculateParity(0, messageLength);
        System.out.println("Number of parity bits needed: " + hammingPower);

        int hammingMessageLength = messageLength + hammingPower;
        int[] hammingMessageDataArray = generateEmptyHammingCode(hammingMessageLength, message);

        calculateParityCells(hammingPower, hammingMessageLength, hammingMessageDataArray);

        System.out.print("Hamming Encoded Message: \n");
        printArray(hammingMessageDataArray);
        System.out.println("Is code Valid? " + HammingCodes.isValid(hammingMessageDataArray));

        return hammingMessageDataArray;
    }

    public static String hammingDecoding (int[] encodedMessage) {
        int temp = 0;
        int temp2;
        StringBuilder hummingMessage = new StringBuilder();

        for (int i = 1; i <= encodedMessage.length - 1; i++) {
            temp2 = (int) Math.pow(2, temp);
            if (i % temp2 != 0) {
                hummingMessage.append(encodedMessage[i]);
            } else {
                temp++;
            }
        }
        return hummingMessage.toString();
    }

    public static String huffmanEncoding(String data) {
        byte[] dataBytes = data.getBytes();
        Map<Character, Integer> statistic = FileEntropy.parseSymbols(dataBytes);
        rootNode = Huffman.huffmanEncoding(statistic);
        Huffman.generateEncodingMap(rootNode, "", encodingMap);
        return Huffman.encodeData(dataBytes, encodingMap);
    }

    private static void calculateParityCells(int hammingPower, int hammingMessageLength, int[] hammingMessageDataArray) {
        for (int i = 0; i < hammingPower; i++) {
            int smallStep = (int) Math.pow(2, i);
            int bigStep = smallStep * 2;
            int start = smallStep, checkPos = start;
//            System.out.println("Calculating Parity bit for Position : " + smallStep);
//            System.out.print("Bits to be checked : ");
            while (true) {
                for (int k = start; k <= start + smallStep - 1; k++) {
                    checkPos = k;
//                    System.out.print(checkPos + " ");
                    if (k > hammingMessageLength) {
                        break;
                    }
                    hammingMessageDataArray[smallStep] ^= hammingMessageDataArray[checkPos];
                }
                if (checkPos > hammingMessageLength) {
                    break;
                } else {
                    start = start + bigStep;
                }
            }
//            System.out.println();
        }
    }

    private static void printArray(int[] hammingMessageDataArray) {
        StringBuilder hummingMessage = new StringBuilder();
        for (int i : hammingMessageDataArray) {
            hummingMessage.append(i);
        }
        hummingMessage.deleteCharAt(0);
        System.out.println(hummingMessage);
    }

    //m+r+1<=2^r
    private static int calculateParity(int r, int m) {
        while (!(m + r + 1 <= Math.pow(2, r))) {
            r++;
        }
        return r;
    }

    private static int[] generateEmptyHammingCode(int hammingMessageLength, String message) {
        int temp = 0;
        int temp2;
        int j = 0;

        int[] hammingMessageDataArray = new int[hammingMessageLength + 1]; //+1 because starts with 1
        for (int i = 1; i <= hammingMessageLength; i++) {
            temp2 = (int) Math.pow(2, temp);
            if (i % temp2 != 0) {
                hammingMessageDataArray[i] = Integer.parseInt(Character.toString(message.charAt(j)));
                j++;
            } else {
                temp++;
            }
        }
        return hammingMessageDataArray;
    }

}
