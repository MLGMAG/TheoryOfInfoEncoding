package lab5;

import lab2.Huffman;
import lab2.Node;
import org.apache.commons.io.FileUtils;
import utils.FileData;

import java.io.File;
import java.nio.file.Paths;

import static lab4.HammingEncAndDec.huffmanEncoding;

public class CyclicCodesEncAndDec {

    private static final int CRC_REMINDER_LENGTH = 16;
    private static final int POLYNOMIAL = 0b10001000000100001;

    private static final String POLYNOMIAL_STR = Integer.toBinaryString(POLYNOMIAL);
    private final static String filePath = FileData.FILE_MY_NAME_AND_INFO.getFilePath();

    public static void main(String[] args) throws Exception {
        System.out.println("Generation pol: " + POLYNOMIAL_STR);
        System.out.println("CRC reminder size: " + CRC_REMINDER_LENGTH + "\n");
        Node huffmanRootNode = Huffman.generateHuffmanRootNode(FileData.FILE_MY_NAME_AND_INFO);
        File dataInputFile = Paths.get(filePath).toFile();
        String inputData = FileUtils.readFileToString(dataInputFile);
        System.out.println("Message to encode:\n" + "\"" + inputData + "\"");

        String huffmanEncodedData = huffmanEncoding(inputData);
        System.out.println("Huffman encoded data: \n" + huffmanEncodedData + "\n");

        int inputDataSize = huffmanEncodedData.length();
        System.out.println("Data size: " + inputDataSize + "\n");

        String crcCode = CyclicCodes.generateCrcCode(huffmanEncodedData, POLYNOMIAL_STR, CRC_REMINDER_LENGTH);
        System.out.println("Crc code: \n" + CyclicCodes.getFormattedCrc(crcCode, CRC_REMINDER_LENGTH));
        System.out.println("Is valid: " + CyclicCodes.isValid(crcCode,POLYNOMIAL_STR, CRC_REMINDER_LENGTH) + "\n");

        String decodedCrcCode = CyclicCodes.getMessageFromCrcCode(crcCode, CRC_REMINDER_LENGTH);

        String huffmanDecodedData = Huffman.huffmanDecoding(decodedCrcCode, huffmanRootNode);
        System.out.println("Huffman decoded data: \n" + huffmanDecodedData + "\n");
    }
}
