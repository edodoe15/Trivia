package com.ptf.rs.trivia.answers;

public class Answer {

	    private String text;
	    private boolean correct;
	    private String choice;

	    public String getText() {
	        return text;
	    }

	    public void setText(String text) {
	        this.text = text;
	    }

	    public boolean isCorrect() {
	        return correct;
	    }

	    public void setCorrect(boolean correct) {
	        this.correct = correct;
	    }

	    public String getChoice() {
	        return choice;
	    }

	    public void setChoice(String choice) {
	        this.choice = choice;
	    }

	    @Override
	    public String toString() {
	        return this.text;
	    }
		
}
