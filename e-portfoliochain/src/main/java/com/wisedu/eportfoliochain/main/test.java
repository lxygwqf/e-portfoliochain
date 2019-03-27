package com.wisedu.eportfoliochain.main;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class test {
    public static void main(String[] args) {
;

        HashMap<String,String> map = new LinkedHashMap<>();
        map.put("hashvalue","sdsdfsdf");
        map.put("t","sdfsdf");

        System.out.println("maps: " + map.values());
        for (Map.Entry<String, String> entry : map.entrySet()){
            System.out.println("key= " + entry.getKey() + " and value= " + entry.getValue());
        }
        String certcontentHash = map.get("hashvalue");
        System.out.println(certcontentHash);
    }
} 