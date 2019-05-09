import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import java.util.Scanner;

public class ReplacementAlgorithms {

	private static Scanner scnr;

	public static void main(String[] args) {
		
		if (args.length > 0) {
			
			List<Integer> pages = new ArrayList<Integer>();
			List<Integer> LRUTotal = new ArrayList<Integer>();
			List<Integer> RandTotal = new ArrayList<Integer>();
			List<Integer> FIFOTotal = new ArrayList<Integer>();
			List<Integer> OptimalTotal = new ArrayList<Integer>();
			
			try
			{
				File file = new File(args[0]);
				scnr = new Scanner(file);
				while(scnr.hasNextLine()){
					String line =  scnr.nextLine();
					String data = "";
					for (int i = 0; i < line.length(); i++){
						char c = line.charAt(i);
						if(c == ' ') {
							pages.add(Integer.parseInt(data));
							data = "";
						} else
							data += c;
					}
				}
				 
			} catch (IOException e) {
				System.out.println("Error accessing input file!");
			}
			 
			for(int i = 1; i <= Integer.parseInt(args[1]); i++) {
				LRUTotal.add(LRU(pages, i));
				RandTotal.add(RandomReplacement(pages, i));
				FIFOTotal.add(FIFO(pages, i));
				OptimalTotal.add(Optimal(pages, i));
				
				System.out.println(i + ") LRU Average: \t" + DisplayAverage(LRUTotal));
				System.out.println(i + ") Rand Average: \t" + DisplayAverage(RandTotal));
				System.out.println(i + ") FIFO Average: \t" + DisplayAverage(FIFOTotal));
				System.out.println(i + ") Optimal Avg: \t" + DisplayAverage(OptimalTotal));
				
				System.out.println();
			}
		}
	}
	
	public static int LRU(List<Integer> pages, int frameCount) {
		int faultCount = 0;
		Queue<Integer> frames = new LinkedList<Integer>();
		boolean faultOccurs = true;
		
		for(int i = 0; i < pages.size(); i++) {
			if(i < frameCount) {
				frames.add(pages.get(i));
				faultCount++;
			} else {
				for(Integer frameContents : frames)
					if(frameContents == pages.get(i)) faultOccurs = false;
				
				if(faultOccurs) faultCount++;
				
				frames.remove();
				frames.add(pages.get(i));
				faultOccurs = true;
			}
		}
		return faultCount;
	}
	
	public static int RandomReplacement(List<Integer> pages, int frameCount) {
		int faultCount = 0;
		int[] frames = new int[frameCount];
		boolean faultOccurs = true;
		Random rand = new Random();
		
		for(int i = 0; i < pages.size(); i++) {
			if(i < frameCount) {
				frames[i] = pages.get(i);
				faultCount++;
			} else {
				for(Integer frameContents : frames)
					if(frameContents == pages.get(i)) faultOccurs = false;
				
				if(faultOccurs) faultCount++;
				
				frames[rand.nextInt(frameCount)] = pages.get(i);
				faultOccurs = true;
			}
		}
		return faultCount;
	}
	
	public static int FIFO(List<Integer> pages, int frameCount) {
		int faultCount = 0;
		Queue<Integer> frames = new LinkedList<Integer>();
		boolean faultOccurs = true;
		
		for(int i = 0; i < pages.size(); i++) {
			if(i < frameCount) {
				frames.add(pages.get(i));
				faultCount++;
			} else {
				for(Integer frameContents : frames)
					if(frameContents == pages.get(i)) faultOccurs = false;
				
				if(faultOccurs) {
					faultCount++;
					frames.remove();
					frames.add(pages.get(i));
				}
				faultOccurs = true;
			}
		}
		return faultCount;
	}
	
	public static int Optimal(List<Integer> pages, int frameCount) {
		int faultCount = 0;
		int[] frames = new int[frameCount];
		List<Integer> copyFrames = new ArrayList<Integer>();
		boolean faultOccurs = true;
		
		for(int i = 0; i < pages.size(); i++) {
			if(i < frameCount) {
				frames[i] = pages.get(i);
				copyFrames.add(pages.get(i));
				faultCount++;
			} else {
				for(Integer frameContents : frames)
					if(frameContents == pages.get(i)) faultOccurs = false;
				
				if(faultOccurs) {
					faultCount++;
					int j = i;
					while(copyFrames.size() != 1 && j < pages.size()) {
						for (int k = 0; k < copyFrames.size(); k++) 
							if (pages.get(j) == copyFrames.get(k)) 
								copyFrames.remove(k);
						j++;
					}
					for(int k = 0; k < frames.length; k++)
						if(copyFrames.get(0) == frames[k])
							frames[k] = pages.get(i);
				}
				faultOccurs = true;
			}
		}
		return faultCount;
	}
	
	public static int DisplayAverage(List<Integer> total) {
		int sum = 0;
		for(Integer x : total)
			sum += x;
		
		return sum/total.size();
	}
}
