package org.wing.mocker.core;

public class MockSettings {
    private String randomStringSource;
    private  int listLimit;
    private  int depth;

    public MockSettings(String randomStringSource, int listLimit, int depth) {
        this.randomStringSource = randomStringSource;
        this.listLimit = listLimit;
        this.depth = depth;
    }

    public String getRandomStringSource() {
        return randomStringSource;
    }

    public void setRandomStringSource(String randomStringSource) {
        this.randomStringSource = randomStringSource;
    }

    public int getListLimit() {
        return listLimit;
    }

    public void setListLimit(int listLimit) {
        this.listLimit = listLimit;
    }

    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }
}
