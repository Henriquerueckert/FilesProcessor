package application;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class ProcessFile extends Thread {

	public ProcessFile() {
		start();
	}

	public void run() {

		try {
			
			String directoryIn = System.getenv("HOMEPATH") + "\\data\\in\\";
			String directoryOut = System.getenv("HOMEPATH") + "\\data\\out\\";
			
			while (true) {

				File[] files = new File(directoryIn).listFiles();

				for (File file : files) {

					String path = file.toString();
					String nameFileIn = file.getName();
					String nameFileOut = directoryOut + nameFileIn.substring(0, nameFileIn.indexOf(".")) + ".done.dat";
					String extension = getFileExtension(nameFileIn);
					
					if (extension.equals("dat")) {

						int qtdClient = 0;
						int qtdSalesman = 0;
						int idSale = 0;
						double totalAmount = 0;
						double biggestSale = 0;
						double lowestSale = Double.MAX_VALUE;
						String nameSalesman = null;
						
						List<String> validLines = getValidLines(path);

						for (String line : validLines) {

							String lines[] = line.split("ç");
							int id = Integer.parseInt(lines[0]);

							switch (id) {

							case 1:
								qtdClient++;
								break;

							case 2:
								qtdSalesman++;
								break;

							case 3:
								
								String sales[] = getAllSales(lines[2]);
								totalAmount = getTotalAmount(sales);

								if (totalAmount > biggestSale) {
									biggestSale = totalAmount;
									idSale = Integer.parseInt(lines[1]);
								}

								if (lowestSale > totalAmount) {
									lowestSale = totalAmount;
									nameSalesman = lines[3];
								}

								break;
							}
							produceOutput(qtdClient, qtdSalesman, idSale, nameSalesman, nameFileOut);
						}
					}
				}
				System.out.println("Arquivos .dat processados!");
				Thread.sleep(1000);
			}

		} catch (InterruptedException | IOException e) {
			System.out.println(e.getMessage());
		}
	}

	private static double getTotalAmount(String[] sales) {
		
		double totalAmount = 0;
		
		for (String sale : sales) {
			String[] dataSale = sale.split("-");
			totalAmount = totalAmount + (Integer.parseInt(dataSale[1]) * Double.parseDouble(dataSale[2]));
		}
		return totalAmount;
	}

	private static List<String> getValidLines(String path) throws IOException {
		
		List<String> validLines = new ArrayList<String>();
		
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(path), "UTF-8"));

		String dataLine = br.readLine();
		
		while (dataLine != null) {
			if (!dataLine.trim().equals("") && (dataLine.contains("ç"))) {
				validLines.add(dataLine);
			}
			dataLine = br.readLine();
		}
		
		br.close();
		
		return validLines;
	}

	private static String[] getAllSales(String lines) {
		
		String allSales = lines.substring(lines.indexOf("[") + 1,lines.lastIndexOf("]"));
		String[] sales = allSales.split(",");
		
		return sales;
	}

	private static String getFileExtension(String filename) {
		
		if (filename.contains(".")) {
			return filename.substring(filename.lastIndexOf(".") + 1);
		} else {
			return "";
		}
	}
	
	private static void produceOutput(int qtdClient, int qtdSalesman, int idSale, String nameSalesman,String nameFileOut) throws IOException {

		FileWriter newFile = new FileWriter(nameFileOut);
		PrintWriter saveFile = new PrintWriter(newFile);

		saveFile.printf("Quantidade de clientes no arquivo de entrada: " + qtdClient + "\n");
		saveFile.printf("Quantidade de vendedor no arquivo de entrada: " + qtdSalesman + "\n");
		saveFile.printf("ID da venda mais cara: " + idSale + "\n");
		saveFile.printf("O pior vendedor:" + nameSalesman);

		newFile.close();
	}
}
