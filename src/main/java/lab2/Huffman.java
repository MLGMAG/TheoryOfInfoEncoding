package lab2;

import utils.FileData;

import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;

import static lab1.FileEntropy.*;

public class Huffman {

    private static int currentPointer = 0;
    private static final byte EOF = 26;

    public static void main(String[] args) throws IOException {
        huffmanEncodingDecoding(FileData.FILE_MY_NAME, "encodedTest1", "decodedTest1");
        huffmanEncodingDecoding(FileData.FILE_EN, "encodedTest2", "decodedTest2");
    }

    private static void huffmanEncodingDecoding(FileData inputFile, String encodedOutputFile, String decodedOutputFile) throws IOException {
        fileDataStats(inputFile);
        Node treeRootNodeTest2 = generateHuffmanRootNode(inputFile);
        Map<Character, String> encodingsMap = new HashMap<>();
        generateEncodingMap(treeRootNodeTest2, "", encodingsMap);
        encodeFile(inputFile, encodedOutputFile, encodingsMap);
        writeTreeToFile(treeRootNodeTest2, encodedOutputFile);
        treeRootNodeTest2 = readTreeFromFile(encodedOutputFile);
        decodeFile(Paths.get(FileData.OUTPUT_FILEPATH.getFilePath(), encodedOutputFile).toString(), decodedOutputFile, treeRootNodeTest2);
    }

    public static Node generateHuffmanRootNode(FileData inputFile) throws IOException {
        Map<Character, Integer> testData = generateFileMap(inputFile);
        Node treeRootNodeTest2 = huffmanEncoding(testData);
        return treeRootNodeTest2;
    }

    public static Node huffmanEncoding(Map<Character, Integer> charAndFrequencyMap) {
        PriorityQueue<Node> minHeap = new PriorityQueue<>(charAndFrequencyMap.size(), Comparator.comparingInt(Node::getData));
        charAndFrequencyMap.entrySet().forEach(x -> generateNode(x, minHeap));
        Optional<Node> treeRootNode = buildCodeDictionaryTree(minHeap);
//        treeRootNode.ifPresent(x -> traverseCodeTree(x, ""));
        return treeRootNode.get();
    }

    private static Optional<Node> buildCodeDictionaryTree(PriorityQueue<Node> minHeap) {
        Optional<Node> root = Optional.empty();
        while (minHeap.size() > 1) {
            Node x = minHeap.peek();
            minHeap.poll();
            Node y = minHeap.peek();
            minHeap.poll();

            Node f = new Node();
            f.setData(x.getData() + y.getData());
            f.setCharacter('-');
            f.setLeftNode(x);
            f.setRightNode(y);
            root = Optional.of(f);
            minHeap.add(f);
        }
        return root;
    }

    private static void generateNode(Map.Entry<Character, Integer> dataForNode, PriorityQueue<Node> minHeap) {
        Node huffmanNode = new Node();

        huffmanNode.setCharacter(dataForNode.getKey());
        huffmanNode.setData(dataForNode.getValue());

        huffmanNode.setLeftNode(null);
        huffmanNode.setRightNode(null);

        minHeap.add(huffmanNode);
    }

    private static void traverseCodeTree(Node root, String currentOutput) {
        if (root.getLeftNode() == null && root.getRightNode() == null) {
            System.out.println(root.getCharacter() + ":" + currentOutput);
            return;
        }
        traverseCodeTree(root.getLeftNode(), currentOutput + "0");
        traverseCodeTree(root.getRightNode(), currentOutput + "1");
    }

    public static void generateEncodingMap(Node rootNode, String currentOutput, Map<Character, String> encodingsMap) {
        if (rootNode.getLeftNode() == null && rootNode.getRightNode() == null) {
            encodingsMap.put(rootNode.getCharacter(), currentOutput);
            return;
        }
        generateEncodingMap(rootNode.getLeftNode(), currentOutput + "0", encodingsMap);
        generateEncodingMap(rootNode.getRightNode(), currentOutput + "1", encodingsMap);
    }

    private static Map<Character, Integer> generateFileMap(FileData fileData) throws IOException {
        byte[] bytesFromFile = fileToBytes(fileData.getFilePath());
        HashMap<Character, Integer> charAndFrequencyMap = parseSymbols(bytesFromFile);
        return charAndFrequencyMap;
    }

    private static void encodeFile(FileData inputFile, String outputFileName, Map<Character, String> encodingsMap) {
        try (FileWriter fileWriter = new FileWriter(Paths.get(System.getProperty("user.dir"), FileData.OUTPUT_FILEPATH.getFilePath(), outputFileName).toString())) {
            byte[] bytes = fileToBytes(inputFile.getFilePath());
            for (byte byteSymbol : bytes) {
                char charSymbol = (char) byteSymbol;
                String code = encodingsMap.get(charSymbol);
                fileWriter.write(code);
            }
            fileWriter.write((char) EOF);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String encodeData(byte[] data, Map<Character, String> encodingMap) {
        StringBuilder output = new StringBuilder();
        for (byte byteSymbol : data) {
            char charSymbol = (char) byteSymbol;
            String code = encodingMap.get(charSymbol);
            output.append(code);
        }
        return output.toString();
    }

    public static String huffmanDecoding(String encodedData, Node rootNode) {
        StringBuilder output = new StringBuilder();
        byte[] dataBytes = encodedData.getBytes();
        while (currentPointer != dataBytes.length) {
            char c = Huffman.decodeSymbol(rootNode, dataBytes);
            output.append(c);
        }
        currentPointer = 0;
        return output.toString();
    }

    private static void decodeFile(String inputFile, String outputFileName, Node rootNode) {
        try (FileWriter fileWriter = new FileWriter(Paths.get(System.getProperty("user.dir"), FileData.OUTPUT_FILEPATH.getFilePath(), outputFileName).toString())) {
            byte[] bytes = fileToBytes(inputFile);
            while (currentPointer != bytes.length) {
                if (bytes[currentPointer] == EOF) break;
                char c = decodeSymbol(rootNode, bytes);
                fileWriter.write(c);
            }
            currentPointer = 0;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static char decodeSymbol(Node currentNode, byte[] array) {
        if (currentNode.getLeftNode() == null && currentNode.getRightNode() == null) {
            return currentNode.getCharacter();
        }

        if ((char) array[currentPointer] == '1') {
            currentPointer++;
            return decodeSymbol(currentNode.getRightNode(), array);
        } else {
            currentPointer++;
            return decodeSymbol(currentNode.getLeftNode(), array);
        }
    }

    private static String serializeTree(Node rootNode, String currentString, String currentCode) {
        if (rootNode != null) {
            if (rootNode.getRightNode() == null && rootNode.getLeftNode() == null) {
                currentString += currentCode + "~" + rootNode.getCharacter() + "_";
            }
            currentString = serializeTree(rootNode.getLeftNode(), currentString, currentCode + "0");
            currentString = serializeTree(rootNode.getRightNode(), currentString, currentCode + "1");
        }
        return currentString;
    }

    private static Node deserializeTree(String encodedTree) {
        Node rootNode = new Node();
        Scanner scanner = new Scanner(encodedTree);
        scanner.useDelimiter("_");
        while (scanner.hasNext()) {
            String entry = scanner.next();
            String[] data = entry.split("~");
            System.out.println(Arrays.toString(data));
            String symbol = data[1];
            String code = data[0];
            Node lastNode = rootNode;
            for (int i = 0; i < code.length(); i++) {
                if (code.charAt(i) == '0') {
                    if (lastNode.getLeftNode() == null) {
                        Node leftNode = new Node();
                        lastNode.setLeftNode(leftNode);
                        lastNode = leftNode;
                    } else {
                        lastNode = lastNode.getLeftNode();
                    }
                } else {
                    if (lastNode.getRightNode() == null) {
                        Node rightNode = new Node();
                        lastNode.setRightNode(rightNode);
                        lastNode = rightNode;
                    } else {
                        lastNode = lastNode.getRightNode();
                    }
                }
            }
            lastNode.setCharacter(symbol.charAt(0));
        }
        return rootNode;
    }

    private static void writeTreeToFile(Node rootNode, String outputFile) {
        try (FileWriter fileWriter = new FileWriter(Paths.get(FileData.OUTPUT_FILEPATH.getFilePath(), outputFile).toString(), true)) {
            String tree = serializeTree(rootNode, "", "");
            System.out.println(tree);
            fileWriter.write(tree);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static Node readTreeFromFile(String fileWithTree) {
        try (Scanner scanner = new Scanner(new FileInputStream(Paths.get(FileData.OUTPUT_FILEPATH.getFilePath(), fileWithTree).toString()))) {
            scanner.useDelimiter(String.valueOf((char) EOF));
            scanner.next();
            String nextTreeFromFile = scanner.next();
            return deserializeTree(nextTreeFromFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}