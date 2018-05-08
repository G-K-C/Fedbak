package com.questioner.entity;

public class SimilarQuestion {
    private String questionTitle;
    private String questionHref;
    private String questionOverview;
    private int distance;

    public SimilarQuestion(String questionTitle, String questionHref, String questionOverview, int distance) {
        this.questionHref = questionHref;
        this.questionTitle = questionTitle;
        this.questionOverview = questionOverview;
        this.distance = distance;
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

    public int getDistance() {
        return distance;
    }

    public String getQuestionOverview() {
        return questionOverview;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public void setQuestionOverview(String questionOverview) {
        this.questionOverview = questionOverview;
    }
}
