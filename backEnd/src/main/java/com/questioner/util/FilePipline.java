package com.questioner.util;

import com.questioner.entity.SimilarQuestion;
import org.springframework.beans.factory.annotation.Value;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;

import java.io.File;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FilePipline implements us.codecraft.webmagic.pipeline.Pipeline {
    public static HashMap<String, List<SimilarQuestion>> tempResult = new HashMap<>();
    public static boolean error = false;

    @Value("${generate.spider.result}")
    private String generate_spider_result;

    @Override
    public void process(ResultItems resultItems, Task task) {

        Map<String,Object> infos=resultItems.getAll();
        String result="";
        if(infos==null||infos.size()==0) {
            error = true;
        }
        else {
            for (Map.Entry<String, Object> info : infos.entrySet()) {
                tempResult.put(info.getKey(),(List<SimilarQuestion>)info.getValue());
            }

        }
        /*if(news!=null) {
            System.out.println("insert in news");
            Dao.getInstance().inertNews(news.getTitle(),news.getBody(),news.getPublish_time(),news.getWriter(),news.getDatasource(),news.getUrl(),news.isYangzhou(),news.isZhenjiang(),news.isBeijing(),news.isShanghai(),news.isHangzhou(),news.isChengdu(),news.isChangsha(),news.isZhangbei(),news.isZhoushan(),news.isCaomei(),news.isMidi(),news.isNanjing());
        }*/

    }
}
