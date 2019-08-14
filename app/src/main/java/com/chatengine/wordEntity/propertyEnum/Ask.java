package com.chatengine.wordEntity.propertyEnum;

public enum Ask {

    WHAT(1,"什么","神马"),
    WHATABOUT(2,"怎样","怎么样","好不好"),
    HOW(3,"如何");

    private int index;
    private String word1;
    private String word2;
    private String word3;

    Ask(int index){
        this.index = index;
    }

    Ask(int index,String word1){
        this.index = index;
        this.word1 = word1;
    }

    Ask(int index,String word1, String word2){
        this.index = index;
        this.word1 = word1;
        this.word2 = word2;
    }

    Ask(int index,String word1, String word2, String word3){
        this.index = index;
        this.word1 = word1;
        this.word2 = word2;
        this.word3 = word3;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getWord1() {
        return word1;
    }

    public void setWord1(String word1) {
        this.word1 = word1;
    }

    public String getWord2() {
        return word2;
    }

    public void setWord2(String word2) {
        this.word2 = word2;
    }

    public String getWord3() {
        return word3;
    }

    public void setWord3(String word3) {
        this.word3 = word3;
    }
}
