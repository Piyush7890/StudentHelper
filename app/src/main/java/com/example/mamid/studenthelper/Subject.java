package com.example.mamid.studenthelper;


import org.apache.commons.lang3.StringUtils;
import org.jsoup.helper.StringUtil;

public class Subject implements Comparable<Subject> {
    public boolean isvisible= false;
    private double attendance;
    private boolean isprl = false;
    private int present;
    private String subjectname;
    private int total;
    private  String category;

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
    private void split()
    {
        String[] splits= subjectname.split("-");
        subjectname= StringUtils.capitalize(splits[1]);
        char[] arr = splits[1].toCharArray();
        switch (splits[2]) {
            case "pr":
                category = "Practical";
                break;
            case "th":
                category = "Lecture";
                break;
            case "tut":
                category = "Tutorial";
                break;
            default:
                category = "Other";
                break;
        }

    }

    public boolean isprl() {
        return this.isprl;
    }

    public void setIsprl(boolean isprl) {
        this.isprl = isprl;
    }

    public int getPresent() {
        return this.present;
    }

    public void setPresent(int present) {
        this.present = present;
    }

    public int getTotal() {
        return this.total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public String getSubjectname() {
        return this.subjectname;
    }

    public void setSubjectname(String subjectname) {
        this.subjectname = subjectname;
        split();

    }

    public double getAttendance() {
        return this.attendance;
    }

    public void setAttendance(double attendance) {
        this.attendance = attendance;
    }

    public int compareTo(Subject another) {
        return getSubjectname().compareTo(another.getSubjectname());
    }
}