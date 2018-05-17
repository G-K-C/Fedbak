package com.questioner.util;

import com.huaban.analysis.jieba.JiebaSegmenter;
import com.huaban.analysis.jieba.SegToken;

import java.util.*;

public class AnalyzerUtils {
    public static List findFrequentWord(String content, String title) {
        JiebaSegmenter segmenter = new JiebaSegmenter();
        String r = " ~，[’!\"#$%&\'()*+,-./:;<=>?@[\\]^_`{|}~]+";
        HashMap<String, Integer> frequency = new HashMap<>();
        for(SegToken wordtoken : segmenter.process(content, JiebaSegmenter.SegMode.SEARCH)) {
            if(!wordtoken.nature.equals("v")&&!wordtoken.nature.equals("r")&&!wordtoken.nature.equals("m")&&!wordtoken.nature.equals("a")&&!wordtoken.nature.equals("p")&&!wordtoken.nature.equals("uj")&&!r.contains(wordtoken.word)&&wordtoken.word!=" "){
                if(frequency.containsKey(wordtoken.word)) {
                    frequency.put(wordtoken.word, frequency.get(wordtoken.word) + 1);
                }
                else
                {
                    frequency.put(wordtoken.word, 1);
                }

            }

        }
        List<WordFrequency> list = new ArrayList<>();
        for(Map.Entry<String, Integer> entry : frequency.entrySet()) {
            System.out.println(entry.getKey());
            System.out.println(entry.getValue());
            list.add(new WordFrequency(entry.getKey(), entry.getValue()));
        }
        list.sort(new Comparator<WordFrequency>() {
            @Override
            public int compare(WordFrequency o1, WordFrequency o2) {
                return o2.getFrequency() - o1.getFrequency();
            }
        });
        List<String> result = new ArrayList<>();
        int i = 0;
        for(WordFrequency wordFrequency:list) {
            if(title.toLowerCase().contains(wordFrequency.getWord().toLowerCase()) && i < 3) {
                result.add(wordFrequency.getWord());
                System.out.println(wordFrequency.getWord());
                i++;
            }
        }
        return result;
    }
}
