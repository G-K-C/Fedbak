package com.questioner.util.word.learn;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.questioner.util.word.domain.*;
import com.questioner.util.word.Huffman;
import com.questioner.util.word.MapCount;

public class Learn {
	 private Map<String, Neuron> wordMap = new HashMap<>(); 
	
	  public int EXP_TABLE_SIZE = 1000;

	  private int window=5;
	  private double sample = 1e-3;
	  private double alpha = 0.025;
	  private double startingAlpha = alpha;
	 
	  
	  private double[] expTable = new double[EXP_TABLE_SIZE];
	 
	  private int trainWordsCount = 0;

	  private int MAX_EXP = 6;
	  
	  private int layerSize=200;
	  
	public Learn() {
		createExpTable();
	}
	//创建sigmoid函数表
	 private void createExpTable() {
		    for (int i = 0; i < EXP_TABLE_SIZE; i++) {
		      expTable[i] = Math.exp(((i / (double) EXP_TABLE_SIZE * 2 - 1) * MAX_EXP));
		      expTable[i] = expTable[i] / (expTable[i] + 1);
		    }
	}
	public void learnFile(File file) throws IOException {
	    readVocab(file);
	    new Huffman(layerSize).make(wordMap.values());//Huffman排序

	    // 查找每个神经元
	    for (Neuron neuron : wordMap.values()) {
	      ((WordNeuron) neuron).makeNeurons();//构造关系节点
	    }

	    trainModel(file);
	}
	/**
	   * 统计词频
	   */
	  private void readVocab(File file) throws IOException {
	    MapCount<String> mc = new MapCount<>();
	    try (BufferedReader br = new BufferedReader(new InputStreamReader(
	        new FileInputStream(file)))) {
	      String temp = null;
	      while ((temp = br.readLine()) != null) {
	        String[] split = temp.split(" ");
	        trainWordsCount += split.length;
	        for (String string : split) {
	          mc.add(string);
	        }
	      }
	    }
	    for (Entry<String, Integer> element : mc.get().entrySet()) {
	      wordMap.put(element.getKey(), new WordNeuron(element.getKey(),
	          (double) element.getValue() / mc.size(), layerSize));
	    }
	  }
	private void trainModel(File file) throws FileNotFoundException, IOException {
		try (BufferedReader br = new BufferedReader(new InputStreamReader(
		        new FileInputStream(file)))) {
		      String temp = null;
		      long nextRandom = 5;
		      int wordCount = 0;
		      int lastWordCount = 0;
		      int wordCountActual = 0;
		      while ((temp = br.readLine()) != null) {
		        if (wordCount - lastWordCount > 10000) {
		          /*System.out.println("alpha:" + alpha + "\tProgress: "
		              + (int) (wordCountActual / (double) (trainWordsCount + 1) * 100)
		              + "%");
		          wordCountActual += wordCount - lastWordCount;
		          lastWordCount = wordCount;*///统计计数
		          alpha = startingAlpha
		              * (1 - wordCountActual / (double) (trainWordsCount + 1));//控制学习速度
		          if (alpha < startingAlpha * 0.0001) {
		            alpha = startingAlpha * 0.0001;
		          }
		        }
		        String[] strs = temp.split(" ");
		        wordCount += strs.length;
		        List<WordNeuron> sentence = new ArrayList<WordNeuron>();
		        for (int i = 0; i < strs.length; i++) {//获取每个词
		          Neuron entry = wordMap.get(strs[i]);
		          if (entry == null) {
		            continue;
		          }
		          if (sample > 0) {
		            double ran = (Math.sqrt(entry.freq / (sample * trainWordsCount)) + 1)
		                * (sample * trainWordsCount) / entry.freq;
		            nextRandom = nextRandom * 25214903917L + 11;
		            if (ran < (nextRandom & 0xFFFF) / (double) 65536) {
		              continue;
		            }
		          }
		          sentence.add((WordNeuron) entry);
		        }

		        for (int index = 0; index < sentence.size(); index++) {
		          nextRandom = nextRandom * 25214903917L + 11;
		          //skipGram(index, sentence, (int) nextRandom % window);

		        }

		      }
		      System.out.println("Vocab size: " + wordMap.size());
		      System.out.println("Words in train file: " + trainWordsCount);
		      System.out.println("sucess train over!");
		    }
	}
	 /**
	   * skip gram 模型训练
	   */
	  private void skipGram(int index, List<WordNeuron> sentence, int b) {
	    // TODO Auto-generated method stub
	    WordNeuron word = sentence.get(index);
	    int a, c = 0;
	    for (a = b; a < window * 2 + 1 - b; a++) {
	      if (a == window) {
	        continue;
	      }
	      c = index - window + a;
	      if (c < 0 || c >= sentence.size()) {
	        continue;
	      }

	      double[] neu1e = new double[layerSize];// 误差项
	      // HIERARCHICAL SOFTMAX
	      List<Neuron> neurons = word.neurons;
	      WordNeuron we = sentence.get(c);
	      for (int i = 0; i < neurons.size(); i++) {
	        HiddenNeuron out = (HiddenNeuron) neurons.get(i);
	        double f = 0;
	        // Propagate hidden -> output
	        for (int j = 0; j < layerSize; j++) {
	          f += we.syn0[j] * out.syn1[j];
	        }
	        if (f <= -MAX_EXP || f >= MAX_EXP) {
	          continue;
	        } else {
	          f = (f + MAX_EXP) * (EXP_TABLE_SIZE / MAX_EXP / 2);
	          f = expTable[(int) f];
	        }
	        // 'g' is the gradient multiplied by the learning rate
	        double g = (1 - word.codeArr[i] - f) * alpha;
	        // Propagate errors output -> hidden
	        for (c = 0; c < layerSize; c++) {
	          neu1e[c] += g * out.syn1[c];
	        }
	        // Learn weights hidden -> output
	        for (c = 0; c < layerSize; c++) {
	          out.syn1[c] += g * we.syn0[c];
	        }
	      }
	    }
	  }
	  public void saveModel(File file) throws IOException {
		    // TODO Auto-generated method stub

		    try (DataOutputStream dataOutputStream = new DataOutputStream(
		        new BufferedOutputStream(new FileOutputStream(file)))) {
		      dataOutputStream.writeInt(wordMap.size());
		      dataOutputStream.writeInt(layerSize);
		      double[] syn0 = null;
		      for (Entry<String, Neuron> element : wordMap.entrySet()) {
		        dataOutputStream.writeUTF(element.getKey());
		        syn0 = ((WordNeuron) element.getValue()).syn0;
		        for (double d : syn0) {
		          dataOutputStream.writeFloat(((Double) d).floatValue());
		        }
		      }
		    } catch (IOException e) {
		      // TODO Auto-generated catch block
		      e.printStackTrace();
		    }
		   
		  }
}
