package com.example.shaga.boardgameshelper;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * Created by shaga on 24/04/2018.
 */

public class DatabaseOperations {

    private static String playersStr = "players";
    private static String gamesStr = "games";
    private static String templatesStr = "templates";
    private static String sessionsStr = "sessions";

    /*************************************************/



    public static void addPlayer(FirebaseDatabase fd, String id, final String name)
    {
        final DatabaseReference ref = fd.getReference(playersStr).child(id);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists())
                {
                    Player player = new Player();
                    player.Initialize();
                    player.name = name;
                    ref.setValue(player);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public static void updatePlayer(FirebaseDatabase fd, String id, Player player)
    {
        DatabaseReference ref = fd.getReference(playersStr).child(id);
        ref.setValue(player);
    }

    public static void addTemplateAsync(final FirebaseDatabase fd, final String userId, final GameTemplate gt)
    {
        DatabaseReference ref = fd.getReference(playersStr).child(userId);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Player player = dataSnapshot.getValue(Player.class);
                String tempId = addTemplate(fd,gt);
                addTemplateToPlayer(fd, userId, player, tempId);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public static void addSessionAsync(final FirebaseDatabase fd, final String userId, final String tempId, final GameSession gs)
    {
        DatabaseReference ref = fd.getReference(playersStr).child(userId);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Player player = dataSnapshot.getValue(Player.class);
                String sessId = addSession(fd,tempId,gs);
                addSessionToPlayer(fd,userId,player,sessId);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public static void createTemplate(FirebaseDatabase fd, String id, GameTemplate gt)
    {
        String key = addTemplate(fd, gt);
        addTemplateToPlayerDirectly(fd,id,key);
    }

    public static void updateTemplate(FirebaseDatabase fd, String tempID, final GameTemplate gt)
    {
        DatabaseReference ref = fd.getReference(gamesStr).child(tempID);
        ref.setValue(gt);
        /*********************************************************************/
        ref = ref.child(sessionsStr);
        for(String key : gt.sessions.keySet())
        {
            final DatabaseReference ref2 = ref.child(key);
            ref2.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    GameSession gs = dataSnapshot.getValue(GameSession.class);
                    for(String val : gt.sharedValueNamesText)
                    {
                        if(!dataSnapshot.hasChild("sharedStats/valuesText/" + val))
                        {
                            ref2.child("sharedStats/valuesText/" + val).setValue("");
                        }
                    }
                    for(String val : gt.sharedValueNamesNumber)
                    {
                        if(!dataSnapshot.hasChild("sharedStats/valuesNumber/" + val))
                        {
                            ref2.child("sharedStats/valuesNumber/" + val).setValue(0);
                        }
                    }
                    for(String val : gt.sharedValueNamesBool)
                    {
                        if(!dataSnapshot.hasChild("sharedStats/valuesBool/" + val))
                        {
                            ref2.child("sharedStats/valuesBool/" + val).setValue(false);
                        }
                    }
                    gs.Initialize();
                    for(String pl : gs.playerStats.keySet())
                    {
                        for(String val : gt.playerValueNamesText)
                        {
                            if(!dataSnapshot.hasChild("playerStats/" + pl + "/valuesText/" + val))
                            {
                                ref2.child("playerStats/" + pl + "/valuesText/" + val).setValue("");
                            }
                        }
                        for(String val : gt.playerValueNamesNumber)
                        {
                            if(!dataSnapshot.hasChild("playerStats/" + pl + "/valuesNumber/" + val))
                            {
                                ref2.child("playerStats/" + pl + "/valuesNumber/" + val).setValue(0);
                            }
                        }
                        for(String val : gt.playerValueNamesBool)
                        {
                            if(!dataSnapshot.hasChild("playerStats/" + pl + "/valuesBool/" + val))
                            {
                                ref2.child("playerStats/" + pl + "/valuesBool/" + val).setValue(false);
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {}
            });
        }
    }

    public static String createSession(FirebaseDatabase fd, String userId, String tempId, GameSession gs)
    {
        String key = addSession(fd,tempId,gs);
        addSessionToPlayerDirectly(fd,userId,key);
        return key;
    }

    public static void updateSessionValueText(FirebaseDatabase fd, String tempId, String sessId, String valueName, String newValue, boolean playerV, String playerId)
    {
        DatabaseReference ref = fd.getReference(gamesStr).child(tempId).child(sessionsStr).child(sessId);
        if(playerV)
        {
            ref = ref.child("playerStats").child(playerId).child("valuesText").child(valueName);
        }
        else
        {
            ref = ref.child("sharedStats").child("valuesText").child(valueName);
        }
        ref.setValue(newValue);
    }

    public static void updateSessionValueNumber(FirebaseDatabase fd, String tempId, String sessId, String valueName, Integer newValue, boolean playerV, String playerId)
    {
        DatabaseReference ref = fd.getReference(gamesStr).child(tempId).child(sessionsStr).child(sessId);
        if(playerV)
        {
            ref = ref.child("playerStats").child(playerId).child("valuesNumber").child(valueName);
        }
        else
        {
            ref = ref.child("sharedStats").child("valuesNumber").child(valueName);
        }
        ref.setValue(newValue);
    }

    public static void updateSessionValueBool(FirebaseDatabase fd, String tempId, String sessId, String valueName, Boolean newValue, boolean playerV, String playerId)
    {
        DatabaseReference ref = fd.getReference(gamesStr).child(tempId).child(sessionsStr).child(sessId);
        if(playerV)
        {
            ref = ref.child("playerStats").child(playerId).child("valuesBool").child(valueName);
        }
        else
        {
            ref = ref.child("sharedStats").child("valuesBool").child(valueName);
        }
        ref.setValue(newValue);
    }

    public static StatsTemplate getStats(GameTemplate gt, boolean playerSpecific)
    {
        StatsTemplate ret = new StatsTemplate();
        ret.Initialize();
        if(gt == null)
            return ret;
        String empty = "";
        Integer zero = 0;
        Boolean fals = false;
        if(playerSpecific) {
            if(gt.playerValueNamesText == null)
                gt.playerValueNamesText = new ArrayList<>();
            if(gt.playerValueNamesNumber == null)
                gt.playerValueNamesNumber = new ArrayList<>();
            if(gt.playerValueNamesBool == null)
                gt.playerValueNamesBool = new ArrayList<>();
            for (String str : gt.playerValueNamesText) {
                ret.valuesText.put(str, empty);
            }
            for (String str : gt.playerValueNamesNumber) {
                ret.valuesNumber.put(str, zero);
            }
            for (String str : gt.playerValueNamesBool) {
                ret.valuesBool.put(str, fals);
            }
        } else {
            if(gt.sharedValueNamesText == null)
                gt.sharedValueNamesText = new ArrayList<>();
            if(gt.sharedValueNamesNumber == null)
                gt.sharedValueNamesNumber = new ArrayList<>();
            if(gt.sharedValueNamesBool == null)
                gt.sharedValueNamesBool = new ArrayList<>();
            for (String str : gt.sharedValueNamesText) {
                ret.valuesText.put(str, empty);
            }
            for (String str : gt.sharedValueNamesNumber) {
                ret.valuesNumber.put(str, zero);
            }
            for (String str : gt.sharedValueNamesBool) {
                ret.valuesBool.put(str, fals);
            }
        }
        return ret;
    }

    /**********************************/

    private static void addTemplateToPlayer(FirebaseDatabase fd, String id, Player player, String tempId)
    {
        player.templates.put(tempId,true);
        updatePlayer(fd,id,player);
    }

    private static void addSessionToPlayer(FirebaseDatabase fd, String id, Player player, String sessId)
    {
        player.sessions.put(sessId, true);
        updatePlayer(fd,id,player);
    }

    public static void addTemplateToPlayerDirectly(FirebaseDatabase fd, String id, String tempId)
    {
        DatabaseReference ref = fd.getReference(playersStr).child(id).child(templatesStr).child(tempId);
        ref.setValue(true);
    }

    public static void addSessionToPlayerDirectly(FirebaseDatabase fd, String id, String sessId)
    {
        DatabaseReference ref = fd.getReference(playersStr).child(id).child(sessionsStr).child(sessId);
        ref.setValue(true);
    }

    public static void addSessionToPlayerDirectly(FirebaseDatabase fd, String id, String sessId, String tempID, GameTemplate gt)
    {
        DatabaseReference ref = fd.getReference(playersStr).child(id).child(sessionsStr).child(sessId);
        ref.setValue(true);
        addPlayerToSession(fd,id,sessId,tempID,gt);
    }

    private static void addPlayerToSession(FirebaseDatabase fd, String userID, String sessID, String tempID, GameTemplate gt)
    {
        DatabaseReference ref = fd.getReference(gamesStr).child(tempID).child(sessionsStr).child(sessID).child("playerStats").child(userID);
        gt.Initialize();
        for(String str : gt.playerValueNamesText)
        {
            ref.child("valuesText").child(str).setValue("");
        }
        for(String str : gt.playerValueNamesNumber)
        {
            ref.child("valuesNumber").child(str).setValue(0);
        }
        for(String str : gt.playerValueNamesBool)
        {
            ref.child("valuesBool").child(str).setValue(false);
        }
    }

    private static String addTemplate(FirebaseDatabase fd, GameTemplate gt)
    {
        DatabaseReference ref = fd.getReference(gamesStr).push();
        ref.setValue(gt);
        return ref.getKey();
    }

    private static String addSession(FirebaseDatabase fd, String tempId, GameSession gs)
    {
        DatabaseReference ref = fd.getReference(gamesStr).child(tempId).child(sessionsStr).push();
        ref.setValue(gs);
        return ref.getKey();
    }
}
