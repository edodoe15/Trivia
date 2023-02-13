package com.ptf.rs.trivia.triviaUser;


import java.util.Objects;

public class TriviaUser {

    private String name;
    private int result;

    public TriviaUser(String name) {
        this.name = name;
        this.result = 0;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public void povecajRezultat(){
        this.result = this.result + 1;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TriviaUser that = (TriviaUser) o;
        return result == that.result && Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, result);
    }

    @Override
    public String toString() {
        return name + " - rezultat: " + result;
    }
}
