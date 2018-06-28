package com.questioner.repository;

import com.questioner.entity.Account;
import com.questioner.entity.Question;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface QuestionRepository extends CrudRepository<Question,Long>{
    @Query("select q from Question as q")
    Page<Question>  getAllQuestionByPage(Pageable pageable);

    @Query("select q from Question as q where q.hidden = true")
    Page<Question>  getHiddenQuestionByPage(Pageable pageable);

    @Query("select q from Question as q where q.questionType.id = :questionTypeId")
    Page<Question> getQuestionByPageAndType(@Param("questionTypeId") Long questionTypeId,
                                             Pageable pageable);

    @Query("select q from Question as q where q.hidden = true and q.questionTitle like %:questionTitle%")
    Page<Question> getHiddenQuestionTitleLike(@Param("questionTitle") String questionTitle,
                                        Pageable pageable);

    @Query("select q from Question as q where q.questionTitle like %:questionTitle% or q.questionContentTxt like %:questionTitle%")
    Page<Question> getQuestionTitleLike(@Param("questionTitle") String questionTitle,
                                        Pageable pageable);

    @Query("select q from Question as q where q.questionType.id = :questionTypeId and q.questionTitle like %:questionTitle%")
    Page<Question> getQuestionTitleLikeByType(@Param("questionTypeId") Long questionTypeId,
                                              @Param("questionTitle") String questionTitle,
                                              Pageable pageable);

    @Query("select count(a.id) from Answer a where a.question.id = :questionId")
    Long getTotalAnswerOfQuestion(@Param("questionId") Long questionId);

    @Query("select count(a.id) from Account a join a.followQuestion q where a.id = :userId and q.id = :questionId")
  //  @Query(value = "select count(account_id) from account_follow_question WHERE account_id = :userId AND question_id = :questionId", nativeQuery = true)
    int hasFollowQuestion(@Param("userId") Long userId, @Param("questionId") Long questionId);

    @Query("select count(a.id) from Answer as a where a.account.id = :userId and a.question.id = :questionId")
    int hasAnswerQuestion(@Param("userId") Long userId, @Param("questionId") Long questionId);

    @Query("select count(q.id) from Question as q where q.id = :questionId and q.hidden = true")
    int getHidden(@Param("questionId") Long questionId);

    @Query("select q from Question q join q.followers u where u.id = ?1")
    Page<Question> getUserFollowQuestions(Long userId, Pageable pageable);

    @Query("select q from Question q where q.publisher.id = ?1")
    Page<Question> getUserQuestions(Long userId, Pageable pageable);

    @Query("select q from Answer a join a.question q where a.id = ?1")
    Question getQuestionByAnswerId(Long answerId);

    @Query("select u from Question q join q.followers u  where q.id = ?1")
    List<Account> getQuestionFollowers(Long questionId);

    Long countByPublisherId(Long publisherId);

    @Query("select q.id from Question q where q.questionType.id = ?1")
    List<Long> getAllQuestionIdByQuestionTypeId(Long typeId);

    @Query("select q from Question q where q.questionType.id = ?1")
    List<Question> getAllQuestionsByQuestionTypeId(Long typeId);


}
