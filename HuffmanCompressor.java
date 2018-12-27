import java.io.File;
import java.io.FileNotFoundException;
import java.io.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 *Class that encodes files using Huffman encoding
 * @author jallowm
 */
public class HuffmanCompressor {
    /*
    1. I used an ArrayList because of the fact that it was easier to use it for searching through a list as
    searching through an array list has a worst case runtime of O(1) while a linked List has a worst case runtime
    of O(N) which would take much longer. Array Lists were easier, at least for me, to sort based on the frequencies
    of the characters held by each node.
    2.First of all because we were representing the encodings using Strings instead of binary bits the output files arguments
    slightly larger in terms of relative size as Strings take up 32 bits of memory. The efficiency of compression was only slightly greater(1.2 to 1.1) when the Gadsby text was encoded using the Gadsby file
    instead of the dictionary file. When encoded using the dictionary file the compression ratio was slightly
    larger. This is most likely because the Gadsby text has no e's so while e is a common letter in the dictionary
    and thus it has a low encoding like 001, since it doesn't appear at all in Gadsby, longer encodings are used
    for letters that appear more often in the Gadsby text which is why when encoding with Gadsby file instead the amount
    of characters in a encoding will be more representative of the frequencies of the characters that appear in that text.

    */
    private static ArrayList<HuffmanNode> charList=new ArrayList<HuffmanNode>();// stores the nodes that represent characters that appear//
    private static ArrayList<HuffmanNode> charList2=new ArrayList<HuffmanNode>();//copy of the the original list beofre merging ndes//
    private static int[] frequencies=new int[charList2.size()];//stores the amount of times a character appears in a file//
    private static HuffmanNode root;//stores the root of the Huffman Tree made from the encoding file//
    private static PrintWriter writer ;// writes the encodings on to the ouput file//
    private static ArrayList<String> encodings=new ArrayList<String>();//stores the encodins created from the Huffman Tree//
    private static ArrayList<Character> characters=new ArrayList<Character>();//stores the characters found in a file in the same order as the encoding file//
    private static double savings;// stores the savings that would be made by the input file being compressed//
    private static double inPutSize;// size of the input file//
    private static double outPutSize;// size of the output file//


    /**
     *Encodes the input file and pastes that encoding on to an output file, methods within it are defined below
     * @param inputFileName name of the file
     * @param encodingFileName name of the encoding file
     * @param outputFileName name of the file created from encoding the input file
     * @return the Strings for the new file
     * @throws IOException if there is no input file
     */
    public static String huffmanEncoder(String inputFileName, String encodingFileName, String outputFileName)throws IOException{
        try{
            charList=scanFile(encodingFileName);

        }
        catch(FileNotFoundException e){
            throw new FileNotFoundException(e.getMessage());
        }
        HuffmanNode node=createHuffmanTree(charList);
          writer=new PrintWriter(outputFileName, "UTF-8");
          String s="";
          traverseTree(root,s);
          writer.close();
        printEncodedFile(inputFileName,encodingFileName ,outputFileName);
        inPutSize=savings + outPutSize/8;
       System.out.println("My savings were "+savings + " bits");
       System.out.println("My output file size was " + outPutSize + " bits");
       System.out.println("My input file size was " + inPutSize + " bits");
        return  "OK";
    }

    /**
     *Scans through a file and creates an arraylist of HuffmanNodes representing those characters
     * @param fileName the name of the input file
     * @return the array list of HuffmanNodes
     * @throws IOException if there is no input file
     */
    public static ArrayList<HuffmanNode> scanFile(String fileName) throws IOException{

        File file=new File(fileName);
        ArrayList<HuffmanNode> charList=new ArrayList<HuffmanNode>();

        Scanner sc=new Scanner(file);
        boolean firstScan=true;
        while (sc.hasNext()){
            String s=sc.nextLine();
            for (int i=0;i<s.length();i++){
                Character c=s.charAt(i);
                Character ct=s.charAt(i);
                boolean seenBefore=false;
                for(int j=0;j<charList.size();j++){
                    if(firstScan && Character.isLetter(ct)){
                        HuffmanNode ch=new HuffmanNode(s.charAt(i),1,null,null);
                        charList.add(ch);
                        firstScan=false;

                    }

                    else if (s.charAt(i)==charList.get(j).getInChar()){
                        charList.get(j).addFrequency();
                        seenBefore=true;
                    }

                }
                if(!(seenBefore)&&(Character.isLetter(c))){
                    HuffmanNode ch=new HuffmanNode(s.charAt(i),1,null,null);
                    charList.add(ch);
                }
            }

        }


        return charList;

    }

    /**
     * Creates a Huffman Tree using the array list of nodes made from the scanned file
     * @param hc an array list of HuffmanNodes
     * @return the last node after merging the nodes in the tree
     */
    public static HuffmanNode createHuffmanTree(ArrayList<HuffmanNode> hc){

        while(hc.size()>1){
            Collections.sort(hc);
            HuffmanNode mergeNode= new HuffmanNode(null,(hc.get(0).getFrequency()+hc.get(1).getFrequency()),hc.get(1),hc.get(0));
            hc.remove(0);
            hc.remove(0);
            hc.add(mergeNode);
            hc.trimToSize();
        }
        root=hc.get(0);
        return root;
    }

    /**
     *Traverses down the tree in order to create a table of characters in the tree, how frequently they appear and their encoding
     * @param traverseNode the node that the traversal begins from
     * @param encoding the encoding that represents the character
     */
    public static void traverseTree(HuffmanNode traverseNode,String encoding) throws IOException{


        if(traverseNode.getLeft()==null && traverseNode.getRight()==null ) {
            System.out.println(traverseNode.getInChar()+":"+traverseNode.getFrequency()+":"+ encoding);
            writer.println(traverseNode.getInChar()+":"+traverseNode.getFrequency()+":"+ encoding);
            encodings.add(encoding);
            characters.add(traverseNode.getInChar());
        }
        else {
            traverseTree(traverseNode.getLeft(),encoding+"0");
            traverseTree(traverseNode.getRight(),encoding+"1");
            }

    }
    /**
     *Scans the input file and writes the encodings of its characters onto the ouput file
     * @param inputFileName name of the file
     * @param encodingFileName name of the encoding file
     * @param outputFileName name of the file created from encoding the input file
     */
  public static void printEncodedFile(String inputFileName, String encodingFileName ,String outputFileName)throws IOException{
    Scanner sc=new Scanner(new File(inputFileName));
     PrintWriter writer= new PrintWriter(outputFileName, "UTF-8");
    while (sc.hasNext()){
        String s=sc.nextLine();
        for (int i=0;i<s.length();i++){
          Character c=s.charAt(i);
            for(int j=0;j<characters.size();j++){
               if(characters.get(j)==c){
                 writer.print(encodings.get(j));
                 savings=savings+(8-encodings.get(j).length());
                 outPutSize=outPutSize+(encodings.get(j).length());



               }

             }
           }
         }


          writer.close();

  }


    /**
     * Node class to represent each character along with their frequencies
     */
    private static class HuffmanNode implements Comparable<HuffmanNode> {
        Character inChar;
        int frequency;
        HuffmanNode left;
        HuffmanNode right;

        HuffmanNode(Character inChar, int frequency,  HuffmanNode left,  HuffmanNode right) {
            this.inChar = inChar;// the character the node represents//
            this.frequency = frequency;// how frequently the character appears//
            this.left = left;// the node to the left//
            this.right = right;// the node to the right//
        }
        /**
         * Gets the character represented by the node
         * @return the character
         */
        public Character getInChar(){
            return this.inChar;
        }
        /**
         * Gets the frequency of the character represented by the node
         * @return the frequency of the character
         */
        public int getFrequency(){
            return this.frequency;
        }
        /**
         * Increases the frequency of the character when it appears
         * @return the character
         */
        public void addFrequency(){
            frequency++;
        }
        /**
         * Gets the node to the left
         * @return the left node
         */
        public HuffmanNode getLeft(){
            return left;
        }
        /**
         * Gets the node to the right
         * @return the right node
         */
        public HuffmanNode getRight(){
            return right;
        }

        @Override
        /**
         * compares the frequency of two nodes in order to enable sorting
         * @return which node has a higher frequency
         */
        public int compareTo(HuffmanNode n) {
            Integer compareFrequencies=compare(this.getFrequency(), n.getFrequency());
            if(compareFrequencies!=0){
                return compareFrequencies;
            }

            else
                return 0;
        }


        /**
         * compares the frequency of two nodes in order to enable sorting
         * @return which node has a higher frequency
         */
        public  int compare(int node1Frequency, int node2Frequency) {
            return node1Frequency - node2Frequency;
        }
    }




    /**
     * @param args the command line arguments
     */
     public static void main(String[] args) throws IOException {
        huffmanEncoder(args[0], args[1], args[2]);

   }

}
