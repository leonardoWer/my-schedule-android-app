package com.example.myschedule.lessons.items;

public class Discipline {

    private String name;
    private String assessmentType;
    private float ballsCnt;

    public Discipline(String name, String assessmentType, float ballsCnt) {
        this.name = name;
        this.assessmentType = assessmentType;
        this.ballsCnt = ballsCnt;
    }

    public void setName(String name) {
        this.name = name;
    }
    public String getName() {
        return name;
    }

    public void setAssessmentType(String assessmentType) {
        this.assessmentType = assessmentType;
    }
    public String getAssessmentType() {
        return assessmentType;
    }

    public void setBallsCnt(float ballsCnt) {
        this.ballsCnt = ballsCnt;
    }
    public float getBallsCnt() {
        return ballsCnt;
    }

}
