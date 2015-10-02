/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vecto;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import preprocess.util.FileName;

/**
 *
 * @author OnlyOne
 */
public class VectoDoc {

    // Hàm đọc file dữ liệu
    public static ArrayList<Word> readFile(String fileName) throws IOException {
        ArrayList<Word> words = new ArrayList<Word>();

        try {

            BufferedReader br = new BufferedReader(new FileReader(fileName));
            String str = null;
            while ((str = br.readLine()) != null) {
                String[] a = str.split(" ");
                words.add(new Word(a[0], Double.parseDouble(a[1])));
            }

            br.close();

        } catch (FileNotFoundException e) {
            System.err.println(e.getMessage());
            System.exit(-1);
        }

        return words;
    }

    //Hàm đọc file từ phủ định
    public static String[][] readFileReverse(String fileName) throws IOException {
        String[][] reverse = new String[3][];

        try {

            BufferedReader br = new BufferedReader(new FileReader(fileName));
            String str = null;
            int k = 0;
            while ((str = br.readLine()) != null) {
                String[] a = str.split(",");
                reverse[k] = new String[a.length];
                for (int i = 0; i < a.length; i++) {
                    reverse[k][i] = a[i];

                }
                k++;

            }

            br.close();

        } catch (FileNotFoundException e) {
            System.err.println(e.getMessage());
            System.exit(-1);
        }

        return reverse;
    }

    //Hàm so sánh từ, trả về điểm số của từ cảm xúc
    public static double compareWords(String word1, ArrayList<Word> words) {
        for (int i = 0; i < words.size(); i++) {
            if (word1.trim().equalsIgnoreCase(words.get(i).getContent().trim())) {
                return words.get(i).getScore();
            }
        }
        return 0;
    }

    //Hàm tạo vector từ cảm xúc
    public static void createVecto(String fileName) {
        String vecto = "";

        ArrayList<Word> posWords = null;
        ArrayList<Word> negWords = null;
        try {
            posWords = readFile("pos.txt");
            negWords = readFile("neg.txt");

        } catch (IOException ex) {
            Logger.getLogger(VectoDoc.class.getName()).log(Level.SEVERE, null, ex);
        }

        try {
            Scanner textFile = new Scanner(new File(fileName));

            PrintWriter outFile = new PrintWriter(new File(FileName.VECTO_FNAME));

            double score = 1;//điểm của từ cảm xúc
            String line = "";//mỗi dòng trong file vector hóa
            String text = "";//mỗi câu bình luận
            int countLine = 1;

            while (textFile.hasNextLine()) {
                text = textFile.nextLine();

                Scanner textLine = new Scanner(text);
                int index = 1;//vị trí của từ cảm xúc

                while (textLine.hasNext()) {
                    String[] split = textLine.next().split("/");
                    if ((score = compareWords(split[0], negWords)) != 0) {
                        line += index + ":" + score + " ";
                    } else if ((score = compareWords(split[0], posWords)) != 0) {
                        line += index + ":" + score + " ";

                    }
                    index++;
                }
                outFile.print(line);
                outFile.print("\n");
                System.out.println(countLine + ":" + line);
                line = "";
                textLine.close();

                countLine++;

            }

            outFile.println();

            textFile.close();
            outFile.close();
        } catch (FileNotFoundException e) {
            System.err.println(e.getMessage());
            System.exit(-1);
        }

    }

    
    //Hàm tạo vector hóa bổ sung từ phủ định
    public static void createVectoReverse2(String fileName) throws IOException {
        String vecto = "";

        ArrayList<Word> posWords = null;
        ArrayList<Word> negWords = null;
        try {
            posWords = readFile("pos.txt");
            negWords = readFile("neg.txt");

        } catch (IOException ex) {
            Logger.getLogger(VectoDoc.class.getName()).log(Level.SEVERE, null, ex);
        }

        try {
            Scanner textFile = new Scanner(new File(fileName));

            PrintWriter outFile = new PrintWriter(new File(FileName.VECTO_FNAME));

            double score = 1;//điểm của từ cảm xúc
            String line = "";//mỗi dòng trong file vector hóa
            String text = "";//mỗi câu bình luận
            int countLine = 1;

            while (textFile.hasNextLine()) {
                text = textFile.nextLine();

                Scanner textLine = new Scanner(text);
                int index = 1;//vị trí của từ cảm xúc

                while (textLine.hasNext()) {

                    String[] split = textLine.next().split("/");
                    //Nếu từ đó là từ tiêu cực
                    if ((score = compareWords(split[0], negWords)) != 0) {
                        score = score * getReverse2(text, split[0]);
                        line += index + ":" + score + " ";
                    } //Nếu từ đó là từ tích cực
                    else if ((score = compareWords(split[0], posWords)) != 0) {
                        score = score * getReverse2(text, split[0]);
                        line += index + ":" + score + " ";

                    }
                    index++;

                }
                outFile.print(line);
                outFile.print("\n");
                System.out.println(countLine + ":" + line);
                line = "";
                textLine.close();

                countLine++;

            }

            outFile.println();

            textFile.close();
            outFile.close();
        } catch (FileNotFoundException e) {
            System.err.println(e.getMessage());
            System.exit(-1);
        }

    }

    //Hàm tạo vector hóa có bổ sung thêm từ tăng cường
    public static void createVectoReverse3(String fileName) throws IOException {
        String vecto = "";

        ArrayList<Word> posWords = null;
        ArrayList<Word> negWords = null;
        try {
            posWords = readFile("pos.txt");//đọc file từ tích cực
            negWords = readFile("neg.txt");//đọc file từ tiêu cực

        } catch (IOException ex) {
            Logger.getLogger(VectoDoc.class.getName()).log(Level.SEVERE, null, ex);
        }

        try {
            Scanner textFile = new Scanner(new File(fileName));

            PrintWriter outFile = new PrintWriter(new File(FileName.VECTO_FNAME));

            double score = 1;//điểm số của từ cảm xúc
            String line = "";//mỗi dòng trong file vector hóa
            String text = "";//mỗi câu bình luận
            int countLine = 1;

            while (textFile.hasNextLine()) {
                text = textFile.nextLine();

                Scanner textLine = new Scanner(text);
                int index = 1;//vì trí của từ cảm xúc

                while (textLine.hasNext()) {

                    String[] split = textLine.next().split("/");
                    //nếu từ đó là từ tiêu cực
                    if ((score = compareWords(split[0], negWords)) != 0) {
                        score = score + getStrong(text, split[0], score);
                        score = score * getReverse2(text, split[0]);
                        line += index + ":" + score + " ";
                        
                    }//nếu từ đó là từ tích cực 
                    else if ((score = compareWords(split[0], posWords)) != 0) {
                        score = score + getStrong(text, split[0], score);
                        score = score * getReverse2(text, split[0]);
                        line += index + ":" + score + " ";

                    }
                    index++;

                }
                outFile.print(line);
                outFile.print("\n");
                System.out.println(countLine + ":" + line);
                line = "";
                textLine.close();

                countLine++;

            }

            outFile.println();

            textFile.close();
            outFile.close();
        } catch (FileNotFoundException e) {
            System.err.println(e.getMessage());
            System.exit(-1);
        }

    }
    //Hàm tạo vector cho việc test
    public static void createVectoReverseForTest(String fileName) throws IOException {
        String vecto = "";

        ArrayList<Word> posWords = null;
        ArrayList<Word> negWords = null;
        try {
            posWords = readFile("pos.txt");
            negWords = readFile("neg.txt");

        } catch (IOException ex) {
            Logger.getLogger(VectoDoc.class.getName()).log(Level.SEVERE, null, ex);
        }

        try {
            Scanner textFile = new Scanner(new File(fileName));

            PrintWriter outFile = new PrintWriter(new File(FileName.TEST_FNAME));

            double score = 1;
            int orentation = 0;
            String line = "0 ";
            String text = "";
            int countLine = 1;

            while (textFile.hasNextLine()) {
                text = textFile.nextLine();

                Scanner textLine = new Scanner(text);
                int index = 1;

                while (textLine.hasNext()) {

                    String[] split = textLine.next().split("/");
                    if ((score = compareWords(split[0], negWords)) != 0) {
                        score = score + getStrong(text, split[0], score);
                        score = score * getReverse2(text, split[0]);
                        line += index + ":" + score + " ";
                    } else if ((score = compareWords(split[0], posWords)) != 0) {
                        score = score + getStrong(text, split[0], score);
                        score = score * getReverse2(text, split[0]);
                        line += index + ":" + score + " ";

                    }
                    index++;

                }
                outFile.print(line);
                outFile.print("\n");
                System.out.println(countLine + ":" + line);
                line = "0 ";
                textLine.close();

                countLine++;

            }
            textFile.close();
            outFile.close();
        } catch (FileNotFoundException e) {
            System.err.println(e.getMessage());
            System.exit(-1);
        }

    }

    
    // Hàm kiểm tra từ phủ định trong khoảng cách 3 từ tính từ từ cảm xúc về trước
    public static int getReverse2(String strLine, String word) {
        Scanner pos = new Scanner(strLine);
        boolean check = true;
        int index = 0;
        int indexReverse = -500;
        while (pos.hasNext()) {

            String vecto = pos.next();
            String[] a = vecto.split("/");
            if (!a[0].equals(word) && check) {
                if (compareWordString(a[0], TYPE.START) == 1) {
                    indexReverse = index;
                }

            } else {
                check = false;
                if ((index - indexReverse) < 3) {
                    return -1;
                } else {
                    return 1;
                }
            }
            index++;
        }
        return 1;
    }

    //Hàm trả về điểm sau khi xét thêm từ tăng cường ở trước từ cảm xúc
    public static double getStrong(String strLine, String wordSentiment, double score) {
        Scanner pos = new Scanner(strLine);
        boolean check = true;
        int index = 0;
        int indexReverse = -500;
        double realScore = 0;
        double realScore1 = 0;
        while (pos.hasNext()) {

            String vecto = pos.next();
            String[] a = vecto.split("/");

            if (!a[0].equals(wordSentiment) && check) {
                if ((realScore = compareWordString2(a[0], score)) != 0) {
                    if (Math.abs(realScore) > 0) {
                        realScore1 = realScore;
                    }
                    indexReverse = index;
                }

            } else {
                check = false;

                if ((index - indexReverse) < 3) {
                    return realScore1;
                } else {
                    return 0;
                }
            }
            index++;

        }

        return 0;

    }

    enum TYPE {

        START, END
    }

    //hàm  so sánh kiểm tra từ phủ định
    public static int compareWordString(String word, TYPE type) {

        int k = 0;
        try {
            if (type == TYPE.START) {
                k = 0;

            } else {
                k = 1;
            }
            String[][] reverse = readFileReverse("reverse.txt");
            for (int i = 0; i < reverse[k].length; i++) {
                if (reverse[k][i].equalsIgnoreCase(word)) {
                    return 1;
                }
            }

        } catch (IOException ex) {
            Logger.getLogger(VectoDoc.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;

    }

    //Hàm so sánh kiểm tra từ tăng cường, kết quả trả về là điểm số để cộng hoặc trừ đi
    public static double compareWordString2(String word, double score) {

        try {

            String[][] reverse = readFileReverse("increase.txt");
            for (int i = 0; i < reverse[0].length; i++) {
                if (reverse[0][i].equalsIgnoreCase(word)) {
                    if (score > 0) {
                        return 0.125;
                    } else {
                        return -0.125;
                    }
                }
            }
            for (int i = 0; i < reverse[1].length; i++) {
                if (reverse[1][i].equalsIgnoreCase(word)) {
                    if (score > 0) {
                        return -0.125;
                    } else {
                        return 0.125;
                    }
                }
            }

        } catch (IOException ex) {
            Logger.getLogger(VectoDoc.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;

    }

    public static void main(String[] args) throws IOException {

        //createVecto(FileName.POSTAG_FNAME);
        //createVectoReverse2(FileName.POSTAG_FNAME);
        createVectoReverse3(FileName.POSTAG_FNAME);

    }
}
