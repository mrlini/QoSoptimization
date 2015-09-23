package org.utcluj.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

class Index {
	
	public int i;
	public int j;
	
	public Index() {
		
		i = 0;
		j = 0;
	}
	
	public Index(int i, int j) {
		
		this.i = i;
		this.j = j;
	}
}

public class RefactorTimes {

	public static void main(String [] args) {
		
		int [][] times = new int[5][];
		
		try {
			BufferedReader reader = new BufferedReader(new FileReader("times"));
			
			String line = null;
			
			int i = 0;
			
			while ((line = reader.readLine()) != null) {
				
				String [] cells = line.split("\t");
				times[i] = new int[cells.length - 1];
				
				for (int j = 0; j < times[i].length; j++) {
					
					times[i][j] = Integer.parseInt(cells[j+1].trim());
				}
				i++;
			}
			
			final int [][] scenarios = new int[5][5];
			List<Index> indexes = new ArrayList<Index>();
			
			for (i = 1; i <= 5; i ++) 
				for (int j = 1; j <=5; j ++) {
					scenarios[i-1][j-1] = i * j;
					indexes.add(new Index(i - 1, j - 1));
					System.out.print(i*10 + "/" + j*10 + "\t");
				}
			
			Collections.sort(indexes, new Comparator<Index>() {

				@Override
				public int compare(Index a, Index b) {
					
					return new Integer(scenarios[a.i][a.j]).compareTo(new Integer(scenarios[b.i][b.j]));
				}
			});
			
			for (Index index : indexes) {
				
				//System.out.print("" + ((index.i + 1) * 10) + "/" + ((index.j + 1) * 10) + "\t");
				//System.out.print("" + ((index.i + 1) * 10) + "/" + ((index.j + 1) * 10) + "=" + times[1][index.i * 5 + index.j]);
			}
			
//			for (i = 0; i < 5; i++) {
//				System.out.println();
//			for (Index index : indexes) {
//				
//				System.out.print("" + times[i][index.i * 5 + index.j] + "\t");
//			}
//			}
			
//			for (i = 0; i < 5; i ++)
//				for ()
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
