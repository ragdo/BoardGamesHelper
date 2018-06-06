package com.example.shaga.boardgameshelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by shaga on 12/04/2018.
 */

public class GameSession {

    public String name;
    public StatsTemplate sharedStats;
    public Map<String, StatsTemplate> playerStats;

    public GameSession(){}

    public void Initialize()
    {
        if(name == null)
            name = "";
        if(sharedStats == null) {
            sharedStats = new StatsTemplate();
            sharedStats.Initialize();
        }
        if(playerStats == null)
            playerStats = new HashMap<String, StatsTemplate>();
    }

}
