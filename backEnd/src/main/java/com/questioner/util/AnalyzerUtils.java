package com.questioner.util;

import com.huaban.analysis.jieba.JiebaSegmenter;

import java.util.*;

public class AnalyzerUtils {
    public static List findFrequentWord(String content, String title) {
        String r = " ~，[’!\"#$%&\'()*+,-./:;<=>?@[\\]^_`{|}~]+";
        JiebaSegmenter segmenter = new JiebaSegmenter();
        HashMap<String, Integer> frequency = new HashMap<>();
        for(String word : segmenter.sentenceProcess(content)) {
            if(frequency.containsKey(word)) {
                frequency.put(word, frequency.get(word) + 1);
            }
            else
            {
                frequency.put(word, 1);
            }
        }
        List<WordFrequency> list = new ArrayList<>();
        for(Map.Entry<String, Integer> entry : frequency.entrySet()) {
            if(r.contains(entry.getKey())) {
                continue;
            }
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
                i++;
            }
        }
        return result;
    }
}
