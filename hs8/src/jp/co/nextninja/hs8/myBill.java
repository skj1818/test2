package jp.co.nextninja.hs8;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import jp.co.nextninja.billing.util.Bill;

public class myBill {
    private Bill bill;
    public String success;
    public String error;
    public myBill(Bill bill) {
        this.bill = bill;
    }
    public void bill(String item,String onsuccess,String onerror) {
        success = onsuccess;
        error = onerror;
        bill.billItem(item,"");
    }

    public String getItems() {
        Set<String> item = new HashSet<String>();
        if(bill.getBuyedItem(item) == Bill.ERROR_OK) {
            StringBuilder r = new StringBuilder();
            ArrayList<String> a = new ArrayList<String>();
            for(String i : item) {
                a.add(i);
            }
            r.append("[");
            for(int i=0;i<a.size();i++) {
                r.append("\'"+a.get(i)+"\'");
                if(i+1<a.size())r.append(",");
            }
            r.append("]");
            return r.toString();
        }
        return "";
    }

    public String getRecentItem() {
        return bill.getRecentItem();
    }
};
