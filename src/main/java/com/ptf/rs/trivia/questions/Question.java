package com.ptf.rs.trivia.questions;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ptf.rs.trivia.answers.Answer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Question {
	
    private String timestamp;
    private String question;
    private List<Answer> answers;
    private int question_num;

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public List<Answer> getAnswers() {
        return answers;
    }

    public void setAnswers(List<Answer> answers) {
        this.answers = answers;
    }

    public int getQuestion_num() {
        return question_num;
    }

    public void setQuestion_num(int question_num) {
        this.question_num = question_num;
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder(question_num + " Question: " + question).append(System.lineSeparator());
        for (int i=0; i<answers.size(); i++){
            s.append(answers.get(i).getChoice()).append(answers.get(i)).append(System.lineSeparator());
        }
        return s.toString();
    }
	
	
}
