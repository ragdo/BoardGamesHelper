package com.example.shaga.boardgameshelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by shaga on 12/04/2018.
 */

public class Player {
    public String name;

    public Map<String, Boolean> templates;
    public Map<String, Boolean> sessions;

    public Player(){}

    public void Initialize()
    {
        name = "";
        templates = new HashMap<String, Boolean>();
        sessions = new HashMap<String, Boolean>();
    }
}
