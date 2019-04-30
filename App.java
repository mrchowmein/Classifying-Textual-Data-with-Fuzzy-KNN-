package com.knntextdata.knntextdata;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;




/**
 * KNN Textual Data
 * Jason Chan
 *
 */
public class App 
{
	public static void main( String[] args ) throws IOException
	{

		String testFilePath = "src/test/java/com/knntextdata/knntextdata/unknown/";
		//String testFileName = null;
		int kValue;
		String[] directoryList = {"src/test/java/com/knntextdata/knntextdata/C1/", "src/test/java/com/knntextdata/knntextdata/C4/", "src/test/java/com/knntextdata/knntextdata/C7/"};
		HashMap <String, String[]> folderTopicsMap;
		int [] documentTopicArray;
		String [] termColsArray;
		double [] idf;
		String [] docNameArray;
		int fuzzyKnn;



		Scanner sc = new Scanner(System.in);
		System.out.println("Welcome to Jason Chan's Document K Nearest Neighbor Classifer");
		System.out.println("Please enter in a number for the K value?");

		System.out.print("K Value: ");

		kValue = sc.nextInt();
		
		System.out.println();
		
		System.out.print("Input 1 for FuzzyKNN or 0 for Regular KNN: ");

		fuzzyKnn = sc.nextInt();
		System.out.println();

		System.out.println("\nCurrent test document directory: " +  testFilePath);


		//        
		//       while(!mode.equals("CS") && !mode.equals("EU")) {
		//    	   System.out.print("incorrect mode, please try again:");
		//    	    mode = sc.nextLine().toUpperCase();
		//  	     	
		//       } 

		//BasicConfigurator.configure();
		//Load documents that need to be preprocessed into a list
		//String[] directoryList = {"src/test/java/com/nlptools/corenlp/C1/"};


		ArrayList <Preprocess> ppList = loadAllFiles(directoryList);

		System.out.println("Preprocessing starting, please wait\n");

		//Preprocess each document
		int ppcount = 0;

		docNameArray = new String[ppList.size()];
		//ppList.size()
		for(int i = 0; i < ppList.size(); i++){
			ppList.get(i).processDocument();
			docNameArray[i] = ppList.get(i).getDocName();
			ppcount++;
			//System.out.println("\"" + ppList.get(i).getDocName() + "\" processed to index: " + i);

		}

		System.out.println("Text Files Preprocessed: " + ppcount);

		System.out.println("Preprocessing Complete");

		System.out.println("Total Unique Words: " + Preprocess.globalWordCountMap.size());

		System.out.println("-------------------------\n");

		//create document matrix
		System.out.println("Building Document-Word Matrix");
		documentMatrix docMatrix = new documentMatrix(Preprocess.globalWordCountMap, ppList, directoryList);


		folderTopicsMap = docMatrix.getFolderTopicMap();

		int[] actualDocumentTopic = docMatrix.getDocumentTopicArray();
		String [][] folderTopicArray = docMatrix.getFolderTopicArray();
		//String [] folderTopicString = docMatrix.getFolderTopicString();

		//print2DMatrix(folderTopicArray);
		//System.out.println(documentTopic);


		double [][] tfidfMatrix = docMatrix.getTFIDFMatrix();
		documentTopicArray = docMatrix.getDocumentTopicArray();
		termColsArray = docMatrix.getTermColsArray();
		idf = docMatrix.getIDF();


		//System.out.println("Building Document-Word Matrix Complete");
		System.out.println("-------------------------\n");

		//    	for(int i = 0; i < actualDocumentTopic.length; i++) {
		//    		
		//    		System.out.println("docmatrix index " + i + " topic: " +actualDocumentTopic[i]);
		//    	}

		if(fuzzyKnn == 1) {
			System.out.println("Starting Fuzzy KNN \n");
		} else {
			System.out.println("Starting KNN \n");	
		}
		

		//System.out.println("The following files area available to test with");

		File folder = new File(testFilePath);
		File[] listOfFiles = folder.listFiles();


		for (File file : listOfFiles) {
			if (file.isFile()) {
				String textFileName = file.getName();
				System.out.println("\nTest Document: " + textFileName);
				
				String testFilePathTemp = testFilePath + textFileName;
				
				//System.out.println(testFilePathTemp);
				KNN predict = new KNN(tfidfMatrix, documentTopicArray, folderTopicArray, termColsArray, idf, directoryList, docNameArray);

				predict.findNeighbor(kValue, testFilePathTemp, fuzzyKnn);

			}
		}
		






	}




	public static void print2DMatrix(double [][] inputMatrix){

		for (int i = 0; i < inputMatrix.length; i++){

			for(int j = 0; j < inputMatrix[0].length; j++){
				System.out.print(inputMatrix[i][j] + " ");
			}
			System.out.println();
		}

	} 

	public static void print2DMatrix(String [][] inputMatrix){

		for (int i = 0; i < inputMatrix.length; i++){

			for(int j = 0; j < inputMatrix[0].length; j++){
				System.out.print(inputMatrix[i][j] + " ");
			}
			System.out.println();
		}

	} 


	//load all the files of filepaths in array
	public static ArrayList <Preprocess> loadAllFiles (String [] directoryList) throws IOException{

		ArrayList <Preprocess> documentObjectsList = new ArrayList<Preprocess>();

		for(int i = 0; i < directoryList.length; i++){

			File folder = new File(directoryList[i]);
			File[] listOfFiles = folder.listFiles();

			for (File file : listOfFiles) {
				if (file.isFile()) {
					String textFileName = file.getName();
					//System.out.println(textFileName);
					documentObjectsList.add(new Preprocess(directoryList[i] + textFileName, i, directoryList[i]));
				}
			}

		}	

		return documentObjectsList;
	}


}
