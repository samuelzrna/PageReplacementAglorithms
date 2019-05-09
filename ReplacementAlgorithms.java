import java.io.File;
import java.io.IOException;
import java.util.*;
import static java.lang.Integer.parseInt;

public class ReplacementAlgorithms {

    public static void main(String [] args){

        if(args.length < 1) return;

        /*
         *  Initialization
         * 
         *  Calculation variables:
         *  numFrames  	- will store the second command line argument as an integer value
         *  refStrings 	- will store each reference string as a list of lists
         * 
         *  Output Variables:
         *  yAxis	- stores the strings for the y axis
         *  xAxis	- stores integers starting from 1 up until numFrames for the x axis
         *  algData	- will store fault/frame average for each algorithm
         */
        int numFrames = Integer.parseInt(args[1]);
        ArrayList<ArrayList<Integer>> refStrings = new ArrayList<>();
        String[] yAxis = {"FIFO", "LRU", "Random", "Optimal"};
        int[] xAxis = new int[numFrames];
        for(int i = 0; i < xAxis.length; i++) xAxis[i] = i+1;
        int[][] algData = new int[4][numFrames];
        
        // Retrieve data from the .dat file and store it in refStrings
        getRefStringsFromFile(args[0], refStrings);

        // Output for the specific frame acquired from the second command line argument
        System.out.println("Average number of page faults with " + numFrames + " frames");
        System.out.println("-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*");
        System.out.println("Algorithm  |  Average page faults across reference strings");
        System.out.println("---------  |  --------------------------------------------");

        // Calculation and output for FIFO
        int total = 0;
        for(ArrayList<Integer> refString : refStrings)
            total += FIFO(refString, numFrames);
        System.out.println("FIFO       |  " + total/refStrings.size());

        // Calculation and output for LRU
        total = 0;
        for(ArrayList<Integer> refString : refStrings)
            total += LRU(refString, numFrames);
        System.out.println("LRU        |  " + total/refStrings.size());

        // Calculation and output for Random
        total = 0;
        for(ArrayList<Integer> refString : refStrings)
            total += Random(refString, numFrames);
        System.out.println("Random     |  " + total/refStrings.size());

        // Calculation and output for Optimal
        total = 0;
        for(ArrayList<Integer> refString : refStrings)
            total += Optimal(refString, numFrames);
        System.out.println("Optimal    |  " + total/refStrings.size());
        System.out.println("\nGetting graph data from 1-" + numFrames + " frames...");
        
        // Calculation and output for ALL FOUR algorithms from 1 to (number of frames)
        // (We did this to speed up the process of collecting data for the graph)
        for(int frames = 1; frames <= numFrames; frames++){
            for(ArrayList<Integer> refString : refStrings) {
                algData[0][frames - 1] += FIFO(refString, frames);
                algData[1][frames - 1] += LRU(refString, frames);
                algData[2][frames - 1] += Random(refString, frames);
                algData[3][frames - 1] += Optimal(refString, frames);
            }
            algData[0][frames-1] /= refStrings.size();
            algData[1][frames-1] /= refStrings.size();
            algData[2][frames-1] /= refStrings.size();
            algData[3][frames-1] /= refStrings.size();
        }
        
        System.out.println("-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*");
        System.out.println("Algorithm  |  Average page faults vs. the number of available frames across reference strings");
        System.out.println("---------  |  -------------------------------------------------------------------------------");
        
        // Output formatting
        for(int i = 0; i < algData.length; i++){
        	System.out.print(yAxis[i] + "\t   |  ");
        	for(int j = 0; j < numFrames ; j++) {
            	System.out.print(algData[i][j] + " | ");
            }
            System.out.println();	
        }
        System.out.print("Frame #\t   |  ");
        for(int frame : xAxis) 
        	System.out.print((frame < 10) ? frame + "    | " : frame + "   | ");
        	
    } // end of main

    /**** METHODS ****/

    /*
    * 	Method: FIFO
    * 	Parameters: ArrayList<Integer> refString, int frameCount
    * 	Return: int faultCount
    * 	Design: This method is supposed to count the number of page
    * 	faults of a given reference string using the 
    * 	first-in-first-out (FIFO) algorithm. The FIFO algorithm is 
    * 	supposed to compare each value of it's reference string to 
    * 	what's currently in memory. If there are no matches, a page 
    *  	fault will occur (which this algorithm should catch) and the  
    *  	contents of the frame containing the reference string which
    * 	caused the earliest page fault.
    */
    public static int FIFO(ArrayList<Integer> refString, int frameCount){
        int faultCount = 0;
        Queue<Integer> frames = new LinkedList<>();
        HashSet<Integer> withinFrame = new HashSet<>();
        for(Integer page: refString){
            if(withinFrame.contains(page))
               continue;

            faultCount++;
            if(frames.size() == frameCount)
                withinFrame.remove(frames.remove());

            frames.add(page);
            withinFrame.add(page);
        }
        return faultCount;
    }

    /*
    * 	Method: LRU
    * 	Parameters: ArrayList<Integer> refString, int frameCount
    * 	Return: int faultCount
    * 	Design: This method is supposed to count the number of page
    * 	faults of a given reference string using the 
    * 	least-recently-used (LRU) algorithm. The LRU algorithm is 
    * 	supposed to take in a reference string and for each value
    * 	of the reference string and compare it to what's currently
    * 	in memory. If there are no matches, a page fault will occur
    * 	(which this algorithm should catch) and it will replace the
    * 	contents of the frame containing the reference string that
    * 	was least recently used.
    */
    public static int LRU(ArrayList<Integer> refString, int frameCount){
        int faultCount = 0;
        Deque<Integer> frames = new LinkedList<>();
        HashSet<Integer> withinFrame = new HashSet<>();
        
        for(Integer page: refString){
            if(withinFrame.contains(page))
                frames.remove(page);
            else {
                faultCount++;
                if (frames.size() == frameCount) {
                    int leastRecentlyUsed = frames.removeLast();
                    withinFrame.remove(leastRecentlyUsed);
                }
            }
            withinFrame.add(page);
            frames.push(page);
        }

        return faultCount;
    }

    /*
    * 	Method: Random
    * 	Parameters: ArrayList<Integer> refString, int frameCount
    * 	Return: int faultCount
    * 	Design: This method is supposed to count the number of page
    * 	faults of a given reference string using an algorithm that
    *  randomly selects the a frame and replaces the reference 
    *  string value with the value of the current reference string
    *  that just caused a page fault. 
    */
    public static int Random(ArrayList<Integer> refString, int frameCount) {
        int faultCount = 0;
        int[] frames = new int[frameCount];
        Random rand = new Random();
        HashSet<Integer> withinFrame = new HashSet<>();

        int i = 0;
        for (Integer page : refString) {
            if (withinFrame.contains(page))
                continue;

            faultCount++;
            withinFrame.add(page);

            if (i == frameCount -1) {
                int toRemoveIndex = rand.nextInt(frameCount);
                int toRemove = frames[toRemoveIndex];
                frames[toRemoveIndex] = page;
                withinFrame.remove(toRemove);
            } else {
                frames[i] = page;
                i++;
            }
        }
        return faultCount;
    }

    /*
    *	Method: Optimal
    *	Parameters: ArrayList<Integer> refString, int frameCount
    *	Return: int faultCount
    *	Design: This method is supposed to count the number of page
    * 	faults of a given reference string using the Optimal
    * 	algorithm. The Optimal algorithm is supposed to compare each
    *  	value of it's reference string to what's currently in memory. 
    *  	If there are no matches, a fault will occur (which this 
    *  	algorithm should catch) and the contents of the frame 
    * 	containing the value that matches the last used value of the	
    * 	reference string will be replaced with the current value of
    * 	the reference string.
    */
    public static int Optimal(ArrayList<Integer> refString, int frameCount){

        int faultCount = 0;
        PriorityQueue<OptimalNode> frames = new PriorityQueue<>(Collections.reverseOrder());
        HashSet<Integer> withinFrame = new HashSet<>();
        HashMap<Integer, ArrayList<Integer>> allIndices = new HashMap<>();
        ArrayList<Integer> indices;

        
        for(int i = 0; i< refString.size(); i++) {
            for(int j = i; j< refString.size(); j++) {
                if (refString.get(i) == refString.get(j) && i != j) {
                    if (allIndices.containsKey(refString.get(i)))
                        indices = allIndices.get(refString.get(i));
                    else
                        indices = new ArrayList<>();
                    indices.add(j);
                    allIndices.put(refString.get(i), indices);
                }
            }
        }

        int index;
        for(Integer page : refString){
            if (allIndices.containsKey(page)) { 	// need to find the next index at which this page appears next
                indices = allIndices.get(page);
                index = indices.remove(0); 		// we will use this to set the value for the priority queue
                if(indices.isEmpty())
                    allIndices.remove(page); 		// if there are no more appearances of this value, take it out of the hash map!
            } else
                index = Integer.MAX_VALUE; 		// we want it to always lose against any other possible value

            if (withinFrame.contains(page)) { 		// if the page is already in the frame, update the next index!
                for(OptimalNode frame : frames) {
                    if (frame.getPage() == page)
                        frame.setNextIndex(index);
                }
                continue;
            }

            // if the page is in the frame
            faultCount++; // increment fault counter

            if (frames.size() == frameCount) { // need to remove a page!
                int toRemove = frames.remove().getPage(); // remove the OptimalNode from the priority queue and return the page with the lowest priority!

                // remove from frame and hash set
                frames.remove(toRemove);
                withinFrame.remove(toRemove);
            }

            // finally we can add the frame
            frames.add(new OptimalNode(page, index));
            withinFrame.add(page);

        }
        return faultCount;
    }

    /*
     * 	Method: getRefStringsFromFile
     * 	Parameters: String datFile, ArrayList<ArrayList<Integer>> refStrings
     * 	Design: This method is designed to parse through the .dat file and
     * 	retrieve each reference string and store it in the refStrings as a
     * 	list of lists.
     */
    public static void getRefStringsFromFile(String datFile, ArrayList<ArrayList<Integer>> refStrings) {
    	try
        {
            File file = new File(datFile);
            Scanner scan = new Scanner(file);

            String line;
            String[] splitLine;
            ArrayList<Integer> refString;

            while(scan.hasNextLine()){
                line =  scan.nextLine();
                splitLine = line.split(" ");
                refString = new ArrayList<>();
                for(String s: splitLine)
                    refString.add(parseInt(s));
                refStrings.add(refString);
            }
            scan.close();
        } catch (IOException e) { System.out.println("Error accessing input file!"); }
    }  
}

// Class to compare nodes 
class OptimalNode implements Comparable<OptimalNode>{

    private int page;
    private Integer nextIndex;

    public OptimalNode(int page, Integer nextIndex){
        this.page = page;
        this.nextIndex = nextIndex;
    }

    public Integer getNextIndex() {
        return nextIndex;
    }

    public int getPage() {
        return page;
    }

    public void setNextIndex(Integer nextIndex) {
        this.nextIndex = nextIndex;
    }

    @Override
    public int compareTo(OptimalNode o) {
        return this.getNextIndex().compareTo(o.getNextIndex());
    }
}
