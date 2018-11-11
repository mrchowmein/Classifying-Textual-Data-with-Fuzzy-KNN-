package com.knntextdata.knntextdata;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map.Entry;

public class KNN {
	
	
	private String testDocFilePath;
	private double [][] tfidfMatrix;
	private String [][] folderTopicArray;
	private int [] documentTopic;
	private String [] termColsArray;
	private Preprocess testDocumentObj;
	private double [] idf;
	private String [] filePathArray;
	private String [] docNameArray;
	
	
	
	
	KNN(double [][] tfidfMatrix, int [] documentTopic, String [][]folderTopicArray, 
			String [] termColsArray, double [] idf, String [] filePathArray, String [] docNameArray){
		
		this.termColsArray = termColsArray;
		this.tfidfMatrix = tfidfMatrix;
		this.folderTopicArray = folderTopicArray;
		this.documentTopic = documentTopic;
		this.idf = idf;
		this.filePathArray = filePathArray;
		this.docNameArray = docNameArray;
		
		
	}
	
	public void findNeighbor(int kValue, String testDocFilePath, int fuzzy) throws IOException {
		
		HashMap <Integer, Integer> kNeighborMap = new HashMap();
		
		int [] similarDocIndex = new int[kValue];
		
		double [] similarityMeasures = new double [tfidfMatrix.length];
		this.testDocFilePath = testDocFilePath;
		
		//HashMap<String, Integer> vectorWordMap = new HashMap<String, Integer>();
		
		testDocumentObj = createDocumentVector(testDocFilePath);
		
		
		double [] docTFIDFVector = buildTFVector(testDocumentObj);
		
		
		for(int row = 0; row < tfidfMatrix.length; row++) {
			
			double[] tfidfVector = tfidfMatrix[row];
			similarityMeasures[row] = cosineSimilarity(tfidfVector, docTFIDFVector);	
			
		}
		
		
		
		//find the k neighbors with the highest cosinesimilarity
		for(int k = 0; k < kValue; k++) {
			double maxSim = 0;
			int maxIndex = -1;
			for(int j = 0; j < similarityMeasures.length; j++) {
				
				if(similarityMeasures[j]> maxSim) {
					maxSim = similarityMeasures[j];
					maxIndex = j;
				}
				
			}
			
			int topic = documentTopic[maxIndex];
			
			similarDocIndex[k] = maxIndex;
			kNeighborMap.merge(topic, 1, Integer::sum);
			similarityMeasures[maxIndex] = 0;
			
		}
		
		System.out.println(kNeighborMap);
		
		
		//run fuzzy knn or regularKnn
		if(fuzzy == 1) {
			fuzzyKNN(kNeighborMap, kValue, similarDocIndex);
		} else {
			regKNN(kNeighborMap, kValue, similarDocIndex);
		}
		
		
		
		}
	
	private Preprocess createDocumentVector(String testFilePath) throws IOException {
		Preprocess docVector = new Preprocess(testFilePath);
		docVector.processDocument();

		return docVector;
		
	}
	
	private double [] buildTFVector(Preprocess testDocumentObj) {
		
		double [] docVector = new double[termColsArray.length];
		
		double wordCount = 0;
		
		HashMap<String, Integer> documentWordCount = testDocumentObj.getDocumentWordMap();
		
		for (int value : documentWordCount.values()) {
			wordCount += value;
		}
		
		//System.out.println("Word count of test doc: "+ wordCount);
		
		for(int i = 0; i < termColsArray.length; i++) {
			if(documentWordCount.containsKey(termColsArray[i])){
				//System.out.println(termColsArray[i]);
				double TF = (documentWordCount.get(termColsArray[i])/wordCount);
				docVector[i] = TF*idf[i];	
			} 
			
		
		}
		
		return docVector;
		
		
	}
	
	private static double cosineSimilarity(double[] vectorA, double[] vectorB) {
        double dotProduct = 0.0;
        double normA = 0.0;
        double normB = 0.0;
        for (int i = 0; i < vectorA.length; i++) {
            dotProduct += vectorA[i] * vectorB[i];
            normA += Math.pow(vectorA[i], 2);
            normB += Math.pow(vectorB[i], 2);
        }   
        return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
    }
	
	private void regKNN(HashMap <Integer, Integer> kNeighborMap, int kValue, int []similarDocIndex) {
		int index = 0;
		int indexFreq = 0;
		for (Entry<Integer, Integer> entry : kNeighborMap.entrySet()) {
		    int key = entry.getKey();
		    int value = entry.getValue();
		    if(value > indexFreq) {
		    	indexFreq = value;
		    	index = key;
		    }
		}
		
		
		//System.out.println("Most similar document: " + docMatrixIndex);
		System.out.println("Predicted Class of test document: " + filePathArray[index] );
		
		
		System.out.print("Topics for Class/Folder: ");
		for(int j = 0; j < 3; j++) {
			System.out.print(folderTopicArray[index][j] +" ");
		}
		
		System.out.println("\nSimilar Documents: ");
		for(int l = 0; l < kValue; l++) {
			System.out.print(docNameArray[similarDocIndex[l]] + ", ");
		}
		
		System.out.println();
	}
	
	private void fuzzyKNN(HashMap <Integer, Integer> kNeighborMap, int kValue, int []similarDocIndex) {
		for (Entry<Integer, Integer> entry : kNeighborMap.entrySet()) {
		    int key = entry.getKey();
		    double value = entry.getValue();
		  
		    	int percent = (int) ((value/kValue)*100);
		    	int index = key;
		    	System.out.println("Predicted Class of test document: " + percent + "% " + filePathArray[index] );
		    
		}
		
		
		System.out.println("Similar Documents: ");
		for(int l = 0; l < kValue; l++) {
			System.out.print(docNameArray[similarDocIndex[l]] + ", ");
		}
		
		System.out.println("--------------------------------------");
	}
	
	
	
	

}
