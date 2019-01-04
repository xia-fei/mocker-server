package org.wing.mocker.core;

public class MockSettings {
    private String randomStringSource;
    private  int listSize;
    private  int depth;

    public MockSettings(String randomStringSource, int listSize, int depth) {
        this.randomStringSource = randomStringSource;
        this.listSize = listSize;
        this.depth = depth;
    }

    public String getRandomStringSource() {
        return randomStringSource;
    }

    public void setRandomStringSource(String randomStringSource) {
        this.randomStringSource = randomStringSource;
    }

    public int getListSize() {
        return listSize;
    }

    public void setListSize(int listSize) {
        this.listSize = listSize;
    }

    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }
}
