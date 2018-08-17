package com.example.mamid.studenthelper;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class Payment {

@org.greenrobot.greendao.annotation.Id (autoincrement = true)
    private Long Id;

    private String category;
    private String description;
    private String date;
    private String amount;
    private String title;

    public Long getId() {
        return Id;
    }

    public void setId(Long id) {
        Id = id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }



    @Generated(hash = 1395833465)
    public Payment(Long Id, String category, String description, String date,
            String amount, String title) {
        this.Id = Id;
        this.category = category;
        this.description = description;
        this.date = date;
        this.amount = amount;
        this.title = title;
    }

    @Generated(hash = 1565471489)
    public Payment() {
    }



}
