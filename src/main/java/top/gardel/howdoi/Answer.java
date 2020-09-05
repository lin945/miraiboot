package top.gardel.howdoi;

import java.util.Objects;

public class Answer {
    private String title;
    private String question;
    private String answer;
    private String answerUrl;

    public Answer() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getAnswerUrl() {
        return answerUrl;
    }

    public void setAnswerUrl(String answerUrl) {
        this.answerUrl = answerUrl;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Answer)) return false;
        Answer answer1 = (Answer) o;
        return Objects.equals(getTitle(), answer1.getTitle()) &&
                Objects.equals(getQuestion(), answer1.getQuestion()) &&
                Objects.equals(getAnswer(), answer1.getAnswer()) &&
                Objects.equals(getAnswerUrl(), answer1.getAnswerUrl());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getTitle(), getQuestion(), getAnswer(), getAnswerUrl());
    }

    @Override
    public String toString() {
        return "Answer{" +
                "title='" + title + '\'' +
                ", question='" + question + '\'' +
                ", answer='" + answer + '\'' +
                ", answerUrl='" + answerUrl + '\'' +
                '}';
    }
}
