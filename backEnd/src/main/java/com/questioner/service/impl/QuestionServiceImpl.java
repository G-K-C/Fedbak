package com.questioner.service.impl;

import com.questioner.entity.Account;
import com.questioner.entity.Question;
import com.questioner.entity.QuestionType;
import com.questioner.entity.SimilarQuestion;
import com.questioner.repository.AccountRepository;
import com.questioner.repository.QuestionRepository;
import com.questioner.repository.QuestionTypeRepository;
import com.questioner.service.abs.QuestionService;
import com.questioner.util.PageableBuilder;
import com.questioner.util.SimilarDistance;
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
    public List<SimilarQuestion> getSimilarQuestions(String originquestion, Long questiontype) {
        List<SimilarQuestion> similarQuestions = new ArrayList<>();
        for(Question question:questionRepository.getAllQuestionsByQuestionTypeId(questiontype)) {
            String similartitle = question.getQuestionTitle();
            String content = question.getQuestionContentTxt();
            String similaroverview = content.substring(0,content.length()>30?30:content.length());
            similaroverview = similaroverview+"...";
            String url = "/questionDetail/"+question.getId();
            int distance = SimilarDistance.getStringDistance(originquestion.toCharArray(), similartitle.toCharArray());
            SimilarQuestion similarQuestion = new SimilarQuestion(similartitle,url,similaroverview,distance);
            similarQuestions.add(similarQuestion);
        }
        similarQuestions.sort(new Comparator<SimilarQuestion>() {
            @Override
            public int compare(SimilarQuestion o1, SimilarQuestion o2) {
                return o1.getDistance()-o2.getDistance();
            }
        });
        return similarQuestions;
    }
}
