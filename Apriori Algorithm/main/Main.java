//implementation of apriori algorithm
import java.util.*;
public class Main {
	static HashMap <String,HashSet<String>> dataSet1,dataSet2;
	static int min_support1 = 2;
	static double min_confidence1 = 0.7;
	static int min_support2 = 3;
	static double min_confidence2 = 0.8;

	static {
		dataSet1 = new HashMap <String,HashSet<String>>();
		dataSet2 = new HashMap <String,HashSet<String>>();
		String transactions1[] = {"t100","t200","t300","t400","t500","t600","t700","t800","t900"};
		String itemSets1[][] = {
								{"i1","i2","i5"},
								{"i2","i4"},
								{"i2","i3"},
								{"i1","i2","i4"},
								{"i1","i3"},
								{"i2","i3"},
								{"i1","i3"},
								{"i1","i2","i3","i5"},
								{"i1","i2","i3"}
							};
		String transactions2[] = {"0001","0024","0012","0031","0015","0022","0029","0040","0033","0038"};
		String itemSets2[][] = {
								{"a","d","e"},
								{"a","b","c","e"},
								{"a","b","d","e"},
								{"a","c","d","e"},
								{"b","c","e"},
								{"b","d","e"},
								{"c","d"},
								{"a","b","c"},
								{"a","d","e"},
								{"a","b","e"}
							};
		for(int i = 0; i < transactions1.length; i++) {	//populate first data set
			HashSet <String> s = new HashSet <String>();
			Collections.addAll(s,itemSets1[i]);
			dataSet1.put(transactions1[i],s);
		}

		for(int i = 0; i < transactions2.length; i++) {	//populate second data set
			HashSet <String> s = new HashSet <String>();
			Collections.addAll(s,itemSets2[i]);
			dataSet2.put(transactions2[i],s);
		}
	}

	public static void main(String[] args) {
		System.out.println("Data set 1:");
		displayDataset(dataSet1);
		System.out.println();
		System.out.println("Running apriori algorithm on first data set...");
		apriori(dataSet1,min_support1,min_confidence1);

		System.out.println("\n\n\nData set 2:");
		displayDataset(dataSet2);
		System.out.println();
		System.out.println("Running apriori algorithm on second data set...");
		apriori(dataSet2,min_support2,min_confidence2);
	}

	static void apriori(HashMap <String,HashSet<String>> dataSet, int min_support, double min_confidence) {
		ArrayList <HashMap<HashSet<String>,Integer>> candidates = new ArrayList <HashMap<HashSet<String>,Integer>>(); 	//list of all candidates
		getFrequent1Items(dataSet,candidates);	//get set of unique items and support count
		System.out.println("List of frequent 1 item set candidates:");
		displayCandidates(candidates.get(0));
		removeNonFrequentCandidates(candidates.get(0),min_support);	//remove candidates that dont satisfy min support
		System.out.println("\nList of frequent 1 item sets:");
		displayCandidates(candidates.get(0));

		for(int k = 1; candidates.get(k-1).isEmpty() == false; k++) {
			generateFrequentItemSets(k-1,candidates);	//generate supersets using the k-1 frequent items with all subsets satisfying minimum support
			getSupportCount(dataSet,candidates); //get support count of candidates from data set
			System.out.println("\n\nList of frequent "+(k+1)+" item set candidates:");
			if(candidates.get(k).isEmpty() == false) {
				displayCandidates(candidates.get(k));	//display all newly generated candidates
				removeNonFrequentCandidates(candidates.get(k),min_support);	//remove all candidates that dont satisfy minimum support
				System.out.println("\nList of frequent "+(k+1)+" item sets:");
				displayCandidates(candidates.get(k));
			}
			else
				System.out.println("empty");
		}
		if(candidates.size() > 2)
			generateAssociationRules(candidates,min_confidence);
		else
			System.out.println("\nCannot generate association rules");
	}

	static void generateAssociationRules(ArrayList <HashMap<HashSet<String>,Integer>> candidates, double min_confidence) {
		HashMap <HashSet<String>,Integer> allCandidates = new HashMap <HashSet<String>,Integer>();
		for(int i = 0; i < candidates.size() - 2; i++) {
			for(HashSet <String> items : candidates.get(i).keySet()) {
				allCandidates.put(items,candidates.get(i).get(items));
			}
		}
		HashMap <HashSet<String>,Integer> finalCandidates = candidates.get(candidates.size() - 2); //put kth candidates in a new hashmap
		double confidence;
		System.out.println("\nAssociation rules are:");
		for(HashSet <String> c : finalCandidates.keySet()) {
			for(HashSet <String> lhs : allCandidates.keySet()) {	//getting possible lhs for set
				if(c.containsAll(lhs)) {	//if lhs is a subset
					confidence = finalCandidates.get(c) / (double)allCandidates.get(lhs);	//calculate confidence of rule
					if(confidence >= min_confidence) {
						HashSet <String> rhs = copySet(c);
						rhs.removeAll(lhs);
						System.out.println(lhs+" --> "+rhs+"\tconfidence = "+confidence);
					}
				}
			}
		}
	}

	static void getSupportCount(HashMap <String,HashSet<String>> dataSet, ArrayList <HashMap<HashSet,Integer>> candidates) {
		int k = candidates.size() - 1;
		Set <HashSet> newCandidates = candidates.get(k).keySet();	//get kth candidate item sets
		for(String transactionID : dataSet.keySet()) {
			HashSet <String> items = dataSet.get(transactionID);	//get item set for each transaction
			for(HashSet c : newCandidates) {	//for each item set among candidates
				if(items.containsAll(c)) {	//if the candidate set is subset of item set of transaction
					candidates.get(k).put(c,candidates.get(k).get(c) + 1);	//increment count of candidate set
				}
			}
		}
	}

	static void generateFrequentItemSets(int matches_required, ArrayList <HashMap<HashSet<String>,Integer>> candidates) {
		HashMap <HashSet<String>,Integer> combinedItemSets = new HashMap <HashSet<String>,Integer>(); //store all unique combined item sets
		ArrayList <HashSet<String>> kMinusOneSets = new ArrayList <HashSet<String>>();
		kMinusOneSets.addAll(candidates.get(matches_required).keySet());	//store all k-1 item sets in an array list
		for(int i = 0; i < kMinusOneSets.size() - 1; i++) {
			HashSet <String> A = kMinusOneSets.get(i);	//get first item set
			for(int j = i + 1; j < kMinusOneSets.size(); j++) {
				HashSet <String> B = kMinusOneSets.get(j);	//get second item set
				if(hasRequiredMatches(A,B,matches_required)) {	//if both sets have required number of matches
					HashSet <String> c = addSets(A,B);	//add both sets
					if(hasNonFrequentSubsets(c,candidates) == false) {	//if all subsets of combined item set are frequent
						combinedItemSets.put(c,0);	//add the set to newly generated item set to candidates with count of 0
					}
				}
			}
		}
		candidates.add(combinedItemSets);	//add newly generated item sets to set k of candidates
	}
	
	static Boolean hasRequiredMatches(HashSet <String> A, HashSet <String> B, int required_matches) {
		if(required_matches == 0) {
			return true;
		}
		else {
			int matches_found = 0;
			for(String a : A) {	//for every item in set A
				if(B.contains(a)) {	//if item is also in set B
					matches_found++;	//we found a match
					if(matches_found == required_matches)	//if required number of matches were found
						return true;
				}
			}
			return false;	//required number of matches were not found
		}
	}

	static HashSet <String> addSets(HashSet <String> A, HashSet <String> B) {
		HashSet <String> result = new HashSet <String>();
		for(String s : A) {	//copying contents of first set into new set
			result.add(s);
		}
		for(String s : B) {	//copying contents of second set into new set
			result.add(s);
		}
		return result;
	}

	static Boolean hasNonFrequentSubsets(HashSet <String> c, ArrayList <HashMap<HashSet,Integer>> candidates) {
		int expected_matches = (int)Math.pow(2,c.size());
		expected_matches -= 2;	//get number of all possible proper subsets for the set in question (2^n - 2)
		int matches_found = 0;
		for(HashMap <HashSet,Integer> candidateSet : candidates) {	//for each set of candidates
			for(HashSet <String> itemSet : candidateSet.keySet()) {	//for each item set among the candidate set
				if(c.containsAll(itemSet)) {	//if one of the frequent item set is a subset of set in question
					matches_found++;
					if(matches_found == expected_matches) {	//if all subsets of set in question are frequent
						return false;
					}
				}
			}
		}
		return true;	//one of the subset was not frequent
	}

	static HashSet <String> copySet(HashSet <String> original) {
		HashSet <String> replica = new HashSet <String>();	//create a new set to hold the original item set
		for(String str : original) {
			replica.add(str);
		}
		return replica;
	}

	static void removeNonFrequentCandidates(HashMap <HashSet,Integer> candidates, int min_support) {
		ArrayList <HashSet> candidateItemSets = new ArrayList <HashSet>();
		//loop to create a copy of all the keys in candidate
		for(HashSet <String> original : candidates.keySet()) {	//get each candidate item set from set of candidates
			HashSet <String> replica = copySet(original);
			candidateItemSets.add(replica);
		}

		for(HashSet <String> frequentItems : candidateItemSets) {	//for every candidate frequent item set
			if(candidates.get(frequentItems) < min_support) {	//if item set does not satisfy minimum support
				candidates.remove(frequentItems);	//remove the item set
			}
		}
	}

	static void getFrequent1Items(HashMap <String,HashSet<String>> dataSet, ArrayList <HashMap<HashSet,Integer>> candidates) {
		HashMap <HashSet,Integer> c = new HashMap <HashSet,Integer>();	//set of uniqely identified candidate items set with support count
		for(String transactionID : dataSet.keySet()) {
			HashSet <String> items = dataSet.get(transactionID);	//get item set for each transaction
			for(String item : items) {	//for every item in the item set
				HashSet <String> i = new HashSet <String>();
				i.add(item);	//make a set containing the item
				if(c.containsKey(i)) {	//if item is already seen
					c.put(i,c.get(i) + 1); //increment count of item
				}
				else {	//item is seen for first time
					c.put(i,1);	//set count as 1 for the item
				}
			}
		}
		candidates.add(c);	//add candidates to list of candidates
	}

	static void displayCandidates(HashMap <HashSet,Integer> candidates) {
		for(HashSet s : candidates.keySet()) {
			System.out.println(s + " : " + candidates.get(s));
		}
	}

	static void displayDataset(HashMap <String,HashSet<String>> dataSet) {
		for(String key : dataSet.keySet()) {
			HashSet s = dataSet.get(key);
			System.out.print(key+" : ");
			System.out.print(s);
			System.out.println();
		}
	}
}
