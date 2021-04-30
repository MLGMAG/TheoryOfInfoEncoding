package lab1;

import org.apache.commons.io.FileUtils;
import utils.FileData;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;


public class FileEntropy {
	public static void main(String[] args) throws IOException {
		fileDataStats(FileData.FILE_EN);
		fileDataStats(FileData.FILE_AR);
		fileDataStats(FileData.FILE_RU);
		fileDataStats(FileData.FILE_UA);
		fileDataStats(FileData.FILE_CH);
		fileDataStats(FileData.FILE_MY_NAME);
	}

	public static void fileDataStats(FileData fileData) throws IOException {
		byte[] bytesFromFile = fileToBytes(fileData.getFilePath());
		HashMap<Character, Integer> symbols = parseSymbols(bytesFromFile);
		double symbol_count = bytesFromFile.length;
		double entropy = calculateEntropy(symbols, symbol_count);
		double probability_sum = calculateProbability(symbols, symbol_count);
		int unique_symbols = symbols.keySet().size();

		System.out.println("\n================================");
		System.out.printf("File path: %s\n", fileData.getFilePath());
		System.out.printf("Symbols count: %s\n", symbol_count);
		System.out.printf("Unique symbols: %s\n", unique_symbols);
		System.out.printf("Entropy: %s\n", entropy);
		System.out.printf("Max entropy: %s\n", Math.log(unique_symbols) / Math.log(2));
		System.out.printf("Probability sum: %s\n", probability_sum);
		System.out.println("================================\n");
	}

	private static double calculateProbability(HashMap<Character, Integer> data, double symbol_count) {
		double probability_sum = 0;

		for (Character element : data.keySet()) {
			double probability = data.get(element) / symbol_count;
			probability_sum += probability;
		}

		return probability_sum;
	}

	private static double calculateEntropy(HashMap<Character, Integer> data, double symbol_count) {
		double entropy = 0;
		double temp;

		for (Character element : data.keySet()) {
			temp = data.get(element) / symbol_count;
			entropy += -1 * temp * ((int) (Math.log(temp) / Math.log(2)));
		}

		return entropy;
	}

	public static HashMap<Character, Integer> parseSymbols(byte[] bytesFromFile) {
		HashMap<Character, Integer> symbols = new HashMap<>();

		for (byte byteSymbol : bytesFromFile) {
			char charSymbol = (char) byteSymbol;
			if (symbols.containsKey(charSymbol)) {
				Integer integer = symbols.get(charSymbol);
				symbols.put(charSymbol, ++integer);
			} else {
				symbols.put(charSymbol, 1);
			}
		}

		return symbols;
	}

	public static byte[] fileToBytes(String filepath) throws IOException {
		File file = new File(System.getProperty("user.dir") + File.separator + filepath);
		return FileUtils.readFileToByteArray(file);
	}
}