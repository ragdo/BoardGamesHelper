package com.example.shaga.boardgameshelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by shaga on 12/04/2018.
 */

public class GameTemplate {
    public List<String> sharedValueNamesText;
    public List<String> sharedValueNamesNumber;
    public List<String> sharedValueNamesBool;

    public List<String> playerValueNamesText;
    public List<String> playerValueNamesNumber;
    public List<String> playerValueNamesBool;

    public Integer minPlayers;
    public Integer maxPlayers;

    public String name;

    public Map<String,GameSession> sessions;

    public GameTemplate(){}

    public void Initialize()
    {
        if(sharedValueNamesText == null)
            sharedValueNamesText = new ArrayList<String>();
        if(sharedValueNamesNumber == null)
            sharedValueNamesNumber = new ArrayList<String>();
        if(sharedValueNamesBool == null)
            sharedValueNamesBool = new ArrayList<String>();

        if(playerValueNamesText == null)
            playerValueNamesText = new ArrayList<String>();
        if(playerValueNamesNumber == null)
            playerValueNamesNumber = new ArrayList<String>();
        if(playerValueNamesBool == null)
            playerValueNamesBool = new ArrayList<String>();

        if(minPlayers == null)
            minPlayers = new Integer(0);
        if(maxPlayers == null)
            maxPlayers = new Integer(0);

        if(name == null)
            name = "";

        if(sessions == null)
            sessions = new HashMap<String, GameSession>();
    }
}
