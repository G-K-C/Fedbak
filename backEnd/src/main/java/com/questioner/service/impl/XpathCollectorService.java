package com.questioner.service.impl;


import com.questioner.entity.SimilarQuestion;
import com.questioner.util.FilePipline;
import com.questioner.util.SimilarDistance;
import com.questioner.util.Useragnets;
import org.apache.http.HttpHost;
import org.springframework.stereotype.Service;
import us.codecraft.webmagic.*;
import us.codecraft.webmagic.pipeline.ConsolePipeline;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Html;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
@Service
public class XpathCollectorService {
    private String[] xpath;
    private List<String> urls;
    private PropertyChangeSupport propertySupport;
    private boolean isCompleted;
    private String proxy_id;
    private String starttime;
    private String endtime;
    private String header;
    private String id;
    public XpathCollectorService()
    {
        propertySupport = new PropertyChangeSupport(this);
        isCompleted=false;
    }
    public void addPropertyChangeListener(PropertyChangeListener listener)
    {
        propertySupport.addPropertyChangeListener(listener);
    }
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propertySupport.removePropertyChangeListener(listener);
    }
    public void crawlSingleData(List<String> urls, String[] xpath,  String header, String id)
    {

        this.xpath=xpath;
        this.urls=urls;
        this.proxy_id=proxy_id;
        this.starttime=starttime;
        this.endtime=endtime;
        this.header=header;
        this.id=id;
        if(this.header==null||this.header.equals(""))
        {

            this.header = Useragnets.getuseragent();
        }
        List<SpiderListener> spiderlisteners = new ArrayList<>();
        spiderlisteners.add(new SpiderListener() {
            @Override
            public void onSuccess(Request request) {
                isCompleted = true;

            }

            @Override
            public void onError(Request request) {
                isCompleted = true;
            }
        });

        Spider.create(new SingleCrawler()).setSpiderListeners(spiderlisteners).startUrls(urls).addPipeline(new FilePipline()).addPipeline(new ConsolePipeline()).thread(1).run();



    }
    class SingleCrawler implements PageProcessor{
        private Site site = Site.me().setRetryTimes(3).setSleepTime(1000)
                //.setDomain("www.douban.com")
                .setUserAgent(header)
                ;

        @Override
        public void process(Page page) {
            if(proxy_id!=null && proxy_id.length() > 0)
            {
                getSite().setHttpProxy(new HttpHost(proxy_id));
            }
            if(starttime!=null && !starttime.equals("")&&endtime!=null&& !endtime.equals(""))
            {
                double startT=Double.parseDouble(starttime);
                double endT=Double.parseDouble(endtime);
                double randomT=startT+new Random().nextDouble()*(endT-startT);
                System.out.println(randomT);
                try {
                    Thread.sleep((long)randomT * 1000);
                }catch(Exception e)
                {
                    e.printStackTrace();
                }

            }
            List<SimilarQuestion> list = new ArrayList<>();
            for(int i = 0; i < xpath.length; i++) {
                String title = page.getHtml().xpath(xpath[i]).toString();
                i++;
                String href = page.getHtml().xpath(xpath[i]).toString();
                i++;
                String overview = page.getHtml().xpath(xpath[i]).toString();
                SimilarQuestion similarQuestion = new SimilarQuestion(title, href, overview, -1, -1);
                list.add(similarQuestion);
            }
            page.putField(id, list);


        }

        @Override
        public Site getSite() {
            return site;
        }

    }
    public boolean getiscompleted() {
        return isCompleted;
    }
}
