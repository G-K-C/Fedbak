package com.questioner.service.impl;

import com.huaban.analysis.jieba.SegToken;
import com.questioner.entity.Account;
import com.questioner.entity.Question;
import com.questioner.entity.QuestionType;
import com.questioner.entity.SimilarQuestion;
import com.questioner.repository.AccountRepository;
import com.questioner.repository.QuestionRepository;
import com.questioner.repository.QuestionTypeRepository;
import com.questioner.service.abs.QuestionService;
import com.questioner.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

@Service
public class QuestionServiceImpl implements QuestionService{

    @Autowired
    private QuestionRepository questionRepository;
    @Autowired
    private QuestionTypeRepository questionTypeRepository;
    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private XpathCollectorService xpathCollectorService;

    @Override
    public boolean saveQuestion(Question question) {
        return questionRepository.save(question) != null;
    }

    @Override
    public Long getAnswerNumber(Long questionId) {
        return questionRepository.getTotalAnswerOfQuestion(questionId);
    }

    @Override
    public Question getQuestion(Long questionId) {
        return questionRepository.findOne(questionId);
    }

    @Override
    public Page<Question> getAllQuestionByPage(int pageSize, int currentPage, String sortParam){
        Pageable pageable = new PageableBuilder().setCurrentPage(currentPage)
                .setPageSize(pageSize).setSortParam(sortParam)
                .setDirection(Sort.Direction.DESC).buildPage();
        return questionRepository.getAllQuestionByPage(pageable);
    }

    @Override
    public Page<Question> getQuestionTitleLike(String questionTitle, int pageSize, int currentPage, String sortParam) {
        Pageable pageable = new PageableBuilder().setCurrentPage(currentPage)
                .setPageSize(pageSize).setSortParam(sortParam)
                .setDirection(Sort.Direction.DESC).buildPage();
        return questionRepository.getQuestionTitleLike(questionTitle,pageable);
    }

    @Override
    public Page<Question> getHiddenQuestionByPage(int pageSize, int currentPage, String sortParam){
        Pageable pageable = new PageableBuilder().setCurrentPage(currentPage)
                .setPageSize(pageSize).setSortParam(sortParam)
                .setDirection(Sort.Direction.DESC).buildPage();
        return questionRepository.getHiddenQuestionByPage(pageable);
    }

    @Override
    public Page<Question> getHiddenQuestionTitleLike(String questionTitle, int pageSize, int currentPage, String sortParam) {
        Pageable pageable = new PageableBuilder().setCurrentPage(currentPage)
                .setPageSize(pageSize).setSortParam(sortParam)
                .setDirection(Sort.Direction.DESC).buildPage();
        return questionRepository.getHiddenQuestionTitleLike(questionTitle,pageable);
    }

    @Override
    public Page<Question> getQuestionByPageAndType(Long typeId, int pageSize, int currentPage, String sortParam) {
        Pageable pageable = new PageableBuilder().setCurrentPage(currentPage)
                .setPageSize(pageSize).setSortParam(sortParam)
                .setDirection(Sort.Direction.DESC).buildPage();
        return questionRepository.getQuestionByPageAndType(typeId, pageable);
    }

    @Override
    public Page<Question> getQuestionTitleLikeByType(Long questionTypeId, String questionTitle,
                                                     int pageSize, int currentPage, String sortParam) {
        Pageable pageable = new PageableBuilder().setCurrentPage(currentPage)
                .setPageSize(pageSize).setSortParam(sortParam)
                .setDirection(Sort.Direction.DESC).buildPage();
        return questionRepository.getQuestionTitleLikeByType(questionTypeId, questionTitle, pageable);
    }

    @Override
    public boolean userFollowQuestion(Long questionId, Long userId) {
        Account account = accountRepository.findOne(userId);
        if(account !=null){
            Question question = questionRepository.findOne(questionId);
            if(question!=null)
            {
                account.getFollowQuestion().add(question);
                return accountRepository.save(account) != null;
            }
        }
        return false;
    }

    @Override
    public boolean userUnFollowQuestion(Long questionId, Long userId) {
        Account account = accountRepository.findOne(userId);
        List<Question> followQuestions = account.getFollowQuestion();
        Question removedQuestion = null;
        for (Question question: account.getFollowQuestion()){
            if (question.getId().equals(questionId)){
                removedQuestion = question;
            }
        }
        if(removedQuestion != null){
            followQuestions.remove(removedQuestion);
        }
        return accountRepository.save(account) != null;
    }

    @Override
    public boolean adminHiddenQuestion(Long questionId) {
        Question question = questionRepository.findOne(questionId);
        if(question != null) {
            if(question.getHidden() == null || !question.getHidden()) {
                question.setHidden(true);
                questionRepository.save(question);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean adminUnhiddenQuestion(Long questionId) {
        Question question = questionRepository.findOne(questionId);
        if(question != null) {
            if(question.getHidden() == true) {
                question.setHidden(false);
                questionRepository.save(question);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean hasFollowQuestion(Long questionId, Long userId) {
        return questionRepository.hasFollowQuestion(userId,questionId) > 0;
    }

    @Override
    public boolean hasAnswerQuestion(Long questionId, Long userId) {
        return questionRepository.hasAnswerQuestion(userId,questionId) > 0;
    }

    @Override
    public boolean getHidden(Long questionId) {
        return questionRepository.getHidden(questionId) > 0;
    }

    @Override
    public Page<Question> getUserFollowQuestions(Long userId, int currentPage, int pageSize) {
        Pageable pageable = new PageableBuilder().setCurrentPage(currentPage)
                .setPageSize(pageSize).setSortParam("id").setDirection(Sort.Direction.DESC).buildPage();
        return questionRepository.getUserFollowQuestions(userId, pageable);
    }

    @Override
    public Page<Question> getUserQuestionByViews(Long userId, int currentPage, int pageSize) {
        Pageable pageable = new PageableBuilder().setCurrentPage(currentPage)
                .setPageSize(pageSize).setSortParam("views")
                .setDirection(Sort.Direction.DESC).buildPage();
        return questionRepository.getUserQuestions(userId, pageable);
    }

    @Override
    public Page<Question> getUserQuestionByDateTime(Long userId, int currentPage, int pageSize) {
        Pageable pageable = new PageableBuilder().setCurrentPage(currentPage)
                .setPageSize(pageSize).setSortParam("id")
                .setDirection(Sort.Direction.DESC).buildPage();
        // even sort by dateTime, but maybe id is ok for id is incremental by dateTime
        // and sort by id is more efficient
        return questionRepository.getUserQuestions(userId, pageable);
    }

    @Override
    public Question getQuestionByAnswerId(Long answerId) {
        return questionRepository.getQuestionByAnswerId(answerId);
    }

    @Override
    public Long getUserQuestionCount(Long userId) {
        return questionRepository.countByPublisherId(userId);
    }

    @Override
    public List<SimilarQuestion> getSimilarQuestionsByDB(String originquestiontitle, Long questiontype, String originquestioncontent) {
        List<SimilarQuestion> similarQuestions = new ArrayList<>();
        List<String> frequentWords = AnalyzerUtils.findFrequentWord(originquestioncontent, originquestiontitle);
        for(Question question:questionRepository.getAllQuestionsByQuestionTypeId(questiontype)) {
            String similartitle = question.getQuestionTitle();
            String contenttxt = question.getQuestionContentTxt();
            String similaroverview = contenttxt.substring(0,contenttxt.length()>30?30:contenttxt.length());
            similaroverview = similaroverview+"...";
            String url = "/questionDetail/"+question.getId();
            int kindofkeyword = 0;
            int numofkeyword = 0;
            for(String keyword : frequentWords) {
                if(contenttxt.toLowerCase().contains(keyword.toLowerCase())) {
                    kindofkeyword++;
                    numofkeyword = numofkeyword + appearNumber(contenttxt, keyword);
                }
            }
            SimilarQuestion similarQuestion = new SimilarQuestion(similartitle,url,similaroverview,kindofkeyword,numofkeyword);
            similarQuestions.add(similarQuestion);
        }
        similarQuestions.sort(new Comparator<SimilarQuestion>() {
            @Override
            public int compare(SimilarQuestion o1, SimilarQuestion o2) {
                if(o1.getKindofkeyword()!=o2.getKindofkeyword()){
                    return o2.getKindofkeyword()-o1.getKindofkeyword();
                }
                else {
                    return o2.getNumofkeyword()-o1.getNumofkeyword();
                }

            }
        });
        return similarQuestions;
    }
    @Override
    public List<SimilarQuestion> getSimilarQuestionsBySpider(String originquestiontitle, Long questiontype, String originquestioncontent) {
        List<SimilarQuestion> similarQuestions = new ArrayList<>();
        String url = "https://www.baidu.com/s?wd="+originquestiontitle.replaceAll(" ","");
        List<String> urls = new ArrayList<String>();
        urls.add(url);

        xpathCollectorService.crawlSingleData(urls,new String[]{"//*[@id=1]/h3/a[1]/allText()", "//*[@id=1]/h3/a[1]/@href", "//*[@id=1]/div[1]/allText()", "//*[@id=2]/h3/a[1]/allText()", "//*[@id=2]/h3/a[1]/@href", "//*[@id=2]/div[1]/allText()", "//*[@id=3]/h3/a[1]/allText()", "//*[@id=3]/h3/a[1]/@href", "//*[@id=3]/div[1]/allText()"}, Useragnets.getuseragent(), "id");
        while(!xpathCollectorService.getiscompleted())
        {
            try {
                Thread.sleep(5);
            }catch (Exception e) {
                e.printStackTrace();
            }
        }
        if(!FilePipline.error) {
            similarQuestions = FilePipline.tempResult.get("id");
            for(int j = 0; j < similarQuestions.size(); j++){
                System.out.println(similarQuestions.get(j).getQuestionTitle());
                if(similarQuestions.get(j).getQuestionTitle()==null){
                    similarQuestions.remove(j);
                }
            }
        }
        return similarQuestions;

    }
    public  int appearNumber(String srcText, String findText) {
        int count = 0;
        int index = 0;
        while ((index = srcText.indexOf(findText, index)) != -1) {
            index = index + findText.length();
            count++;
        }
        return count;
    }
}
