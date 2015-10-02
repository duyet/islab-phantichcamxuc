/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package preprocess.tachtugannhan;

/**
 *
 * @author Thinh
 */
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import preprocess.jvntagger.MaxentTagger;
import preprocess.jvntagger.POSTagger;
import preprocess.util.FileName;
import vn.hus.nlp.tokenizer.VietTokenizer;

public class TachTuGanNhan {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        VietTokenizer token = new VietTokenizer();
        //Tách từ
        token.tokenize(FileName.INPUT_FNAME, FileName.SEPRATEWORD_FNAME);
        //Loại bỏ stopword
        StopWord stopword = new StopWord();
        stopword.removeStopword();
        //Gán nhãn từ loại
        String modelDir = "model\\maxent";
        POSTagger tagger = null;
        tagger = new MaxentTagger(modelDir);
        String out = tagger.tagging(new File(FileName.REMOVESTOPWORD_FNAME));
        try {
            PrintWriter outFile = new PrintWriter(new File(FileName.POSTAG_FNAME));
            outFile.print(out);
            outFile.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(TachTuGanNhan.class.getName()).log(Level.SEVERE, null, ex);
        }

        System.out.println(out);

    }

}
