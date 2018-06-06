package com.example.shaga.boardgameshelper;

import android.content.Intent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by shaga on 10/04/2018.
 */



public class StatsTemplate {
    public Map<String,String> valuesText;
    public Map<String,Integer> valuesNumber;
    public Map<String,Boolean> valuesBool;

    public StatsTemplate(){}

    public void Initialize()
    {
        if(valuesText == null)
            valuesText = new HashMap<String, String>();
        if(valuesNumber == null)
            valuesNumber = new HashMap<String, Integer>();
        if(valuesBool == null)
            valuesBool = new HashMap<String, Boolean>();
    }

}
