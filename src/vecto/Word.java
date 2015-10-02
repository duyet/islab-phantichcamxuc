/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vecto;

/**
 *
 * @author OnlyOne
 */
public class Word {
    private String content;//từ đặc trưng
    private double score;//điểm số của từ đặc trưng

    public Word(String content, double score) {
        this.content = content;
        this.score = score;
    }

    /**
     * @return the content
     */
    public String getContent() {
        return content;
    }

    /**
     * @param content the content to set
     */
    public void setContent(String content) {
        this.content = content;
    }

    /**
     * @return the score
     */
    public double getScore() {
        return score;
    }

    /**
     * @param score the score to set
     */
    public void setScore(double score) {
        this.score = score;
    }

}
