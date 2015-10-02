/*
    Copyright (C) 2009, Cam-Tu Nguyen, Xuan-Hieu Phan
    
    Email:	ncamtu@gmail.com; pxhieu@gmail.com
    URL:	http://www.hori.ecei.tohoku.ac.jp/~hieuxuan
    
    Graduate School of Information Sciences,
    Tohoku University
*/

package preprocess.jvntagger;

import java.io.File;
import java.util.List;

import preprocess.jmaxent.Classification;
import preprocess.jvntagger.data.DataReader;
import preprocess.jvntagger.data.DataWriter;
import preprocess.jvntagger.data.Sentence;
import preprocess.jvntagger.data.TaggingData;

public class MaxentTagger implements POSTagger {
	DataReader reader = new POSDataReader();
	DataWriter writer = new POSDataWriter();
	TaggingData dataTagger = new TaggingData();
	
	Classification classifier = null;
	
	public MaxentTagger(String modelDir){
		init(modelDir);
	}
	public void init(String modeldir) {
		// TODO Auto-generated method stub
		dataTagger.addContextGenerator(new POSContextGenerator(modeldir + File.separator + "featuretemplate.xml"));
		classifier = new Classification(modeldir);	
	}

	public String tagging(String instr) {
		// TODO Auto-generated method stub
		System.out.println("tagging ....");
		List<Sentence> data = reader.readString(instr);
		for (int i = 0; i < data.size(); ++i){
        	
    		Sentence sent = data.get(i);
    		for (int j = 0; j < sent.size(); ++j){
    			String [] cps = dataTagger.getContext(sent, j);
    			String label = classifier.classify(cps);
    			
    			if (label.equalsIgnoreCase("Mrk"))
    				label = sent.getWordAt(j);
    			
    			sent.getTWordAt(j).setTag(label);
    		}
    	}
		
		return writer.writeString(data);
	}

	
	public String tagging(File file) {
		// TODO Auto-generated method stub
		List<Sentence> data = reader.readFile(file.getPath());
		for (int i = 0; i < data.size(); ++i){
        	
    		Sentence sent = data.get(i);
    		for (int j = 0; j < sent.size(); ++j){
    			String [] cps = dataTagger.getContext(sent, j);
    			String label = classifier.classify(cps);
    			
    			if (label.equalsIgnoreCase("Mrk"))
    				label = sent.getWordAt(j);
    			
    			sent.getTWordAt(j).setTag(label);    
    			//System.out.println(sent.getTagAt(j));
    		}
    	}
		
		return writer.writeString(data);
	}

	public void setDataReader(DataReader reader){
		this.reader = reader;
	}
	
	public void setDataWriter(DataWriter writer){
		this.writer = writer;
	}
}