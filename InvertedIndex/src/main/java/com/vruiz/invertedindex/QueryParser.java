package com.vruiz.invertedindex;
import com.vruiz.invertedindex.index.*;
import com.vruiz.invertedindex.util.Benchmark;
import java.io.*;
import java.util.*;

public class QueryParser {
    public static void main(String[] args) {
        String term1 = "Democratic";
        String term2 = "disparities";
        String term3 = "office";

        String[] termList = {term1, term2, term3};

		try {
			Searcher searcher = new Searcher();
			searcher.openIndexReader();
            
            for (int i=0; i < termList.length; i++){
                List<String> results = searcher.search(termList[i]);
                if (results.size() == 0){
                    System.out.println(String.format("No Tokens found matching the term %s", termList[i]));
                } 
                else {
                    System.out.println(String.format("Query result for term: %s", termList[i]));
                    for (int j=0; j < results.size(); j++){
                        System.out.println(String.format("%d - %s \n", j, results.get(j)));
                    }
                }
                
            }

		} catch (IOException e) {
			e.printStackTrace();
		} catch (CorruptIndexException e) {
			e.printStackTrace();
		}
    }
}
