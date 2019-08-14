package com.chatengine.wordEntity;

public class Words {

    String id;

    String word;

    String postag;

    String head;

    String deprel;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getPostag() {
        return postag;
    }

    public void setPostag(String postag) {
        this.postag = postag;
    }

    public String getHead() {
        return head;
    }

    public void setHead(String head) {
        this.head = head;
    }

    public String getDeprel() {
        return deprel;
    }

    public void setDeprel(String deprel) {
        this.deprel = deprel;
    }

    @Override
    public String toString() {
        return "Words{" +
                "id='" + id + '\'' +
                ", word='" + word + '\'' +
                ", postag='" + postag + '\'' +
                ", head='" + head + '\'' +
                ", deprel='" + deprel + '\'' +
                '}';
    }
}
