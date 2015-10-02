/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package preprocess.tachtugannhan;

/**
 *
 * @author OnlyOne
 */
import java.util.*;
import java.util.regex.Pattern;
import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import preprocess.util.FileName;

public class StopWord {

    //Trả về true nếu từ đó là stopword
    public static Boolean isStopWord(String word, String[] stopWords) {
        for (int i = 0; i < stopWords.length; i++) {
            if (compareWords(word, stopWords[i]) == 0) {
                return true;
            }
        }
        return false;
    }

    // Trả về 0 nếu giống nhau
    public static int compareWords(String word1, String word2) {
        if (word1.trim().equalsIgnoreCase(word2.trim())) {
            return 0;

        } else {
            return 1;
        }
    }

    //Hàm loại bỏ stopword
    public void removeStopword() {
        String[] stopWords;
        try {
            stopWords = readStopWords(FileName.VNSTOPWORD_FNAME);
            removeStopWords(FileName.SEPRATEWORD_FNAME, stopWords);
        } catch (IOException ex) {
            Logger.getLogger(StopWord.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public static void main(String[] arg) throws IOException {
        String[] stopWords = readStopWords(FileName.VNSTOPWORD_FNAME);
        removeStopWords(FileName.INPUT_FNAME, stopWords);
    }

    //Đọc file stopword
    public static String[] readStopWords(String stopWordsFilename) throws IOException {
        String[] stopWords = null;
        try {
            //Scanner stopWordsFile = new Scanner(new File(stopWordsFilename));
            BufferedReader br = new BufferedReader(new FileReader(stopWordsFilename));
            int numStopWords = 616;
            String a = null;
            stopWords = new String[numStopWords];
            for (int i = 0; i < numStopWords; i++) {
                stopWords[i] = br.readLine();
            }
            br.close();
        } catch (FileNotFoundException e) {
            System.err.println(e.getMessage());
            System.exit(-1);
        }
        return stopWords;
    }

    //Hàm loại bỏ stopword
    public static void removeStopWords(String textFilename, String[] stopWords) {
        String word;
        try {
            Scanner textFile = new Scanner(new File(textFilename));
            textFile.useDelimiter(Pattern.compile("[ \n\r\t,;:?!'\"]+"));
            PrintWriter outFile = new PrintWriter(new File(FileName.REMOVESTOPWORD_FNAME));

            System.out.println("\nRemoving:");
            int i = 0;
            while (textFile.hasNextLine()) {
                String strLine = textFile.nextLine();
                Scanner textLine = new Scanner(strLine);
                while (textLine.hasNext()) {
                    word = textLine.next();
                    if (isStopWord(word, stopWords)) {
                        System.out.print(word + " ");
                    } else {
                        outFile.print(word + " ");
                    }

                }
                textLine.close();
                outFile.print("\n");

            }

            System.out.println("\n\nText after removing stop words is in " + FileName.REMOVESTOPWORD_FNAME);
            outFile.println();

            textFile.close();
            outFile.close();
        } catch (FileNotFoundException e) {
            System.err.println(e.getMessage());
            System.exit(-1);
        }

    }
}
