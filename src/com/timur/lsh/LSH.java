package com.timur.lsh;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Random;
import java.util.Scanner;

/**
 * 
 * This class performs Locality Sensitive Hashing, or Advanced Data Mining
 * Assignment 2 Part 1. Locality Sensitive Hashing is useful for grouping
 * similar items together. In this example, the user provides as many text files
 * as they wish into a certain folder. The program then decides which text files
 * are similar based on their contexts.
 * 
 * @author Timur
 *
 */
public class LSH {

	private static LinkedList<ArrayList<String>> files;
	private static ArrayList<String> fileNames;
	private static HashSet<String> uniqueWords;
	private static LinkedList<ArrayList<String>> shuffles;
	private static LinkedList<LinkedList<Integer>> sequences;
	private static LinkedList<ArrayList<String>> allTopRanks;
	private static Hashtable<String, LinkedList<ArrayList<String>>> allHBLabels;

	/**
	 * Performs Locality Sensitive Hashing.
	 * 
	 * @param folderpath
	 *            Where all the text files to be compared are contained.
	 */
	public static void lsh(String folderpath) {
		extractAllTextFileData(folderpath);
		shuffleWords(5);
		generateRandomNumbers(6, 3, 5);
		findTopRank();
		findHashBucketLabels();
	}

	/**
	 * Prints out the HashBucket labels showing similar files in a readable format.
	 */
	public static void print(){
		for(String s : allHBLabels.keySet()){
			for(ArrayList<String> hashBucket : allHBLabels.get(s)){
				System.out.println(s + "\t" + hashBucket);
			}
			System.out.println();
		}
	}
	
	/**
	 * Goes through every {@link topRank} to find and store HashBucket Labels.
	 * After finding every HashBucket Label, moves on and does the same process
	 * for every text file in the folder.
	 */
	private static void findHashBucketLabels() {
		allHBLabels = new Hashtable<String, LinkedList<ArrayList<String>>>();
		int count = 0;
		for (ArrayList<String> topRank : allTopRanks) {
			LinkedList<ArrayList<String>> hbLabels = new LinkedList<ArrayList<String>>();
			for (LinkedList<Integer> sequence : sequences) {
				ArrayList<String> hashBucket = new ArrayList<String>();
				for (int i : sequence) {
					hashBucket.add(topRank.get(i));
				}
				hbLabels.add(hashBucket);
			}
			allHBLabels.put(fileNames.get(count), hbLabels);
			count++;
		}
	}

	/**
	 * Finds the top-ranking words from each individual text file based on
	 * random sequences and shuffles.
	 */
	private static void findTopRank() {
		allTopRanks = new LinkedList<ArrayList<String>>();
		for (ArrayList<String> textFile : files) {
			ArrayList<String> topRank = new ArrayList<String>();
			for (ArrayList<String> shuf : shuffles) {
				for (String s : shuf) {
					if (textFile.contains(s)) {
						topRank.add(s);
						break;
					} else {
						continue;
					}
				}
			}
			allTopRanks.add(topRank);
		}
	}

	/**
	 * Generates lists full of random numbers to be used for LSH, and stores
	 * them in LinkedLists.
	 * 
	 * @param numOfRands
	 *            The amount of random number sequences being used in the
	 *            algorithm, also ultimately the amount of HashBuckets we are
	 *            creating.
	 */
	private static void generateRandomNumbers(int numOfRands, int seqLength,
			int numOfShuffles) {
		sequences = new LinkedList<LinkedList<Integer>>();
		Random rand = new Random();
		for (int x = 0; x < numOfRands; x++) {
			LinkedList<Integer> sequence = new LinkedList<Integer>();
			for (int y = 0; y < seqLength; y++) {
				sequence.add(rand.nextInt(numOfShuffles));
			}
			sequences.add(sequence);
		}
	}

	/**
	 * Creates and stores a LinkedList of ArrayLists of Strings, each ArrayList
	 * stores a random shuffle of the original {@link uniqueWords}.
	 * 
	 * @param numOfShuffles
	 *            How many shuffles(and ArrayLists) do you want to be made,=.
	 */
	private static void shuffleWords(int numOfShuffles) {
		shuffles = new LinkedList<ArrayList<String>>();
		for (int i = 0; i < numOfShuffles; i++) {
			ArrayList<String> shuf = new ArrayList<String>();
			shuf.addAll(uniqueWords);
			Collections.shuffle(shuf);
			shuffles.add(shuf);
		}

	}

	/**
	 * Extracts and stores all text data in a HashSet {@link uniqueWords}. This
	 * way, there are no duplicates and it can be used as a "dictionary" of
	 * every single word from every text file.
	 * 
	 * @param folderPath
	 *            The location of the folder you wish to use.
	 */
	private static void extractAllTextFileData(String folderPath) {
		File folder = new File(folderPath);
		files = new LinkedList<ArrayList<String>>();
		uniqueWords = new HashSet<String>();
		fileNames = new ArrayList<String>();
		for (File f : folder.listFiles()) {
			if (f.getName().endsWith(".txt")) {
				fileNames.add(f.getName());
				try {
					ArrayList<String> words = new ArrayList<String>();
					Scanner scan = new Scanner(f);
					while (scan.hasNextLine()) {
						String[] split = scan.nextLine().toUpperCase()
								.split("[\\W\\d\\s]+");
						uniqueWords.addAll(Arrays.asList(split));
						words.addAll(Arrays.asList(split));
						files.add(words);
					}
					scan.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				continue;
			}
		}
	}

}