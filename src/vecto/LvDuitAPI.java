/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vecto;

import java.io.PrintStream;
import java.net.InetSocketAddress;
import java.net.SocketAddress;


import org.apache.log4j.Logger;
import org.json.simple.ItemList;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;


import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.simpleframework.http.Query;
import org.simpleframework.http.core.Container;
import org.simpleframework.http.core.ContainerServer;
import org.simpleframework.transport.Server;
import org.simpleframework.transport.connect.Connection;
import org.simpleframework.transport.connect.SocketConnection;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import javax.swing.JFileChooser;
import preprocess.jvntagger.MaxentTagger;
import preprocess.jvntagger.POSTagger;
import preprocess.tachtugannhan.StopWord;
import preprocess.tachtugannhan.TachTuGanNhan;
import preprocess.util.FileName;
import svm.svm_predict;
import svm.svm_train;
import vn.hus.nlp.tokenizer.VietTokenizer;

/**
 * Main REST API
 * 
 * @author Van-Duyet Le (lvduit)
 */
public class LvDuitAPI implements Container {
   static Logger log = Logger.getLogger("LvDuit_API_Main");
   VietTokenizer token = new VietTokenizer();;
   
   public void renderResponseMessage(Request request, Response response, String errorMessage) {
       this.renderResponse(request, response, "'"+ errorMessage +"'");
   }
   
   public void renderResponse(Request request, Response response, String errorMessage) {
       try {
           PrintStream bodyStream = response.getPrintStream();
            response.set("Content-Type", "application/json");
            bodyStream.println("{\"message\": "+ errorMessage +"}");
            bodyStream.close();
            return;
       } catch(Exception e) {
         e.printStackTrace();
      }
   }
   
   public void handle(Request request, Response response) {
        log.debug("Handling POST: " + request.getPath());
        try {
            String data = "[]";
            
            String reqContent = request.getContent();
            if (reqContent.length() == 0) {
               this.renderResponse(request, response, "Request empty.");
               return;
            }
            
            if (reqContent.startsWith("feedback:")) {
                String s = "feedback:";
                String Userlabel = reqContent.substring(s.length(), s.length()+1);
                String Classifylabel = reqContent.substring(s.length() + 2, s.length()+3);
                String quote = reqContent.substring(s.length() + 3).replace("\n", "").replace("\r", "");
                
                // Fix 
                if (Classifylabel.equals("2")) {
                    Classifylabel = "-1";
                }
                
                try {
                    PrintWriter outFile = new PrintWriter(new FileOutputStream(new File("lvduit_feedback.txt"), true));
                    outFile.print("["+ Userlabel +"] => ["+ Classifylabel +"] => [" + quote + "]\n");
                    outFile.close();
                    this.renderResponseMessage(request, response, "ok");
                    return;
                } catch (FileNotFoundException ex) {
                    java.util.logging.Logger.getLogger(GUI.class.getName()).log(Level.SEVERE, null, ex);
                }
              
            }
            
            try {
                PrintWriter outFile = new PrintWriter(new File("th.txt"));
                outFile.print(reqContent);
                outFile.close();
            } catch (FileNotFoundException ex) {
                java.util.logging.Logger.getLogger(GUI.class.getName()).log(Level.SEVERE, null, ex);
            }
            token.tokenize("th.txt", FileName.SEPRATEWORD_FNAME);
            
            StopWord stopword = new StopWord();
            stopword.removeStopword();
            
            String modelDir = "model/maxent";
            
            POSTagger tagger = null;
            tagger = new MaxentTagger(modelDir);
            String out = tagger.tagging(new File(FileName.REMOVESTOPWORD_FNAME));
            try {
                PrintWriter outFile = new PrintWriter(new File(FileName.POSTAG_FNAME));
                outFile.print(out);
                outFile.close();
            } catch (FileNotFoundException ex) {
                java.util.logging.Logger.getLogger(TachTuGanNhan.class.getName()).log(Level.SEVERE, null, ex);
            }

            System.out.println(out);
            try {
                VectoDoc.createVectoReverseForTest(FileName.POSTAG_FNAME);
            } catch (IOException ex) {
                java.util.logging.Logger.getLogger(GUI.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            String[] argv = new String[]{"-b", "0", "test_file.txt", "vectoresult.txt.model", "output_file.txt"};
            try {
                svm_predict.main(argv);

            } catch (IOException ex) {
                java.util.logging.Logger.getLogger(GUI.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            String txtResult = showResult("output_file.txt");
            
            // ===============================================
            
            this.renderResponse(request, response, txtResult);
          
      } catch(Exception e) {
         e.printStackTrace();
      }
   }
   
    public String showResult(String name) {

        String str = null;
        String result = "";
        ItemList resultList = new ItemList();
        int countPos = 0;
        int countNeg = 0;
        int dem = 1;
        //ArrayList<Integer> rowSub = null;
        try {

            BufferedReader br = new BufferedReader(new FileReader(name));
            while ((str = br.readLine()) != null) {
                if (isUnDefinite(dem)) {
                    resultList.add("{\""+ dem +"\":0}");
                    result += "Câu " + dem + " không xác định\n";
                } else if (Double.parseDouble(str) > 0) {
                    resultList.add("{\""+ dem +"\":1}");
                    result += "Câu " + dem + " tích cực\n";
                    countPos++;
                } else {
                    resultList.add("{\""+ dem +"\":-1}");
                    result += "Câu " + dem + " tiêu cực\n";
                    countNeg++;
                }

                dem++;
            }
            br.close();
        } catch (IOException ex) {
            java.util.logging.Logger.getLogger(GUI.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "[" + resultList.toString() + "]";
    }

    public boolean isUnDefinite(int rowcurrent) {

        try {
            ArrayList<Integer> rowSub = getRowSub("test_file.txt");
            for (int i = 0; i < rowSub.size(); i++) {
                if (rowcurrent == rowSub.get(i)) {
                    return true;
                }
            }
        } catch (IOException ex) {
            java.util.logging.Logger.getLogger(GUI.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;

    }
    
    public ArrayList<Integer> getRowSub(String nameVector) throws IOException {

        ArrayList<Integer> listRow = new ArrayList<Integer>();
        BufferedReader br = new BufferedReader(new FileReader(nameVector));
        String str = null;
        int dem = 1;
        while ((str = br.readLine()) != null) {
            String[] a = str.split(" ");
            if (a.length < 2) {
                listRow.add(dem);
                //  System.out.println(dem);
            }
            dem++;

        }

        br.close();

        return listRow;

    }
    
   public static void main(String[] list) throws Exception {
      Container container = new LvDuitAPI();
      Server server = new ContainerServer(container);
      Connection connection = new SocketConnection(server);
      SocketAddress address = new InetSocketAddress(9999);

      connection.connect(address);
   }
}