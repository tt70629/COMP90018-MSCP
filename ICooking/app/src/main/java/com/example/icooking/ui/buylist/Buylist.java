package com.example.icooking.ui.buylist;

import com.google.firebase.database.Exclude;

public class Buylist {


    private String BuyName;
    @Exclude
    private String key;
    private boolean ischecked;

    public boolean isIschecked() {
        return ischecked;
    }

    public Buylist() { }

    public void setIschecked(boolean ischecked) {
        this.ischecked = ischecked;
    }

    //private String count;
    //private String key;



    public Buylist(String BuyName) {
        this.BuyName = BuyName;


    }

    public void setBuyName(String BuyName) {
        this.BuyName = BuyName;
    }

    public String getBuyName() {
        return BuyName;
    }

    



    @Override
    public String toString() {
        return "Buylist{" +
                "BuyName='" + BuyName + '\'' +
                //", createday='" + count + '\'' +
                '}';
    }


    public void setKey(String key) {
        this.key = key;
    }
    public String getKey() {
        return key;
    }

}
