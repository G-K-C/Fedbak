package com.questioner.entity;

public class SimilarQuestion {
    private String questionTitle;
    private String questionHref;
    private String questionOverview;
    private int kindofkeyword;
    private int numofkeyword;

    public SimilarQuestion(String questionTitle, String questionHref, String questionOverview, int kindofkeyword, int numofkeyword) {
        this.questionHref = questionHref;
        this.questionTitle = questionTitle;
        this.questionOverview = questionOverview;
        this.kindofkeyword = kindofkeyword;
        this.numofkeyword = numofkeyword;
    }

    public String getQuestionBrief() {
        return questionOverview;
    }

    public String getQuestionHref() {
        return questionHref;
    }

    public String getQuestionTitle() {
        return questionTitle;
    }


    public void setQuestionHref(String questionHref) {
        this.questionHref = questionHref;
    }

    public void setQuestionTitle(String questionTitle) {
        this.questionTitle = questionTitle;
    }

    public String getQuestionOverview() {
        return questionOverview;
    }

    public void setQuestionOverview(String questionOverview) {
        this.questionOverview = questionOverview;
    }

    public int getKindofkeyword() {
        return kindofkeyword;
    }

    public int getNumofkeyword() {
        return numofkeyword;
    }

    public void setKindofkeyword(int kindofkeyword) {
        this.kindofkeyword = kindofkeyword;
    }

    public void setNumofkeyword(int numofkeyword) {
        this.numofkeyword = numofkeyword;
    }
}
