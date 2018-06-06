package com.example.shaga.boardgameshelper;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class SearchTemplatesActivity extends AppCompatActivity {

    private static AccessToken accessToken;
    private static String userID = null;
    private static FirebaseDatabase fd;
    private static List<GameTemplate> tL;
    private static List<String> tLKeys;
    private static GameTemplate chosenTemplate;
    private static String chosenTemplateID;
    private static String playerStr = "players";
    private static String gamesStr = "games";
    private static String tempStr = "templates";
    private static TemplateRecyclesViewAdapter mItemsRecyclesViewAdapter;
    private static int clickedTemplatePos = -1;
    private int limit = 30;
    private EditText filterEditText = null;

    private boolean getUserID()
    {
        accessToken = AccessToken.getCurrentAccessToken();
        if(accessToken != null) {
            if(!accessToken.isExpired()) {
                userID = accessToken.getUserId();
                return true;
            }
        }
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_templates);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        fd = FirebaseDatabase.getInstance();
        tL = new ArrayList<GameTemplate>();
        tLKeys = new ArrayList<String>();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        AppBarLayout.LayoutParams params = (AppBarLayout.LayoutParams) toolbar.getLayoutParams();
        params.setScrollFlags(0);

        filterEditText = (EditText) findViewById(R.id.editTextFilter);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
                if(chosenTemplate == null || chosenTemplateID == null || clickedTemplatePos < 0)
                    return;
                DatabaseOperations.addTemplateToPlayerDirectly(fd,userID,tLKeys.get(clickedTemplatePos));
                attachSearchTemplateListeners(fd,userID,limit,filterEditText);
                setChosenTemplateAsNull();
            }
        });

        if(getUserID()) {
            View recyclerView = findViewById(R.id.temp_list_search);
            setupRecyclerView((RecyclerView) recyclerView, tL);
            attachSearchTemplateListeners(fd, userID, limit, filterEditText);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                chosenTemplateID = null;
                chosenTemplate = null;
                clickedTemplatePos = -1;
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private static void setChosenTemplateAsNull()
    {
        chosenTemplate = null;
        chosenTemplateID = null;
        clickedTemplatePos = -1;
    }

    private static void attachSearchTemplateListeners(final FirebaseDatabase fd, String userID, final int limit, final EditText filter)
    {
        if(fd == null || userID == null)
        {
            Log.d("templates", "Non-initialized database or id.");
            return;
        }
        DatabaseReference drP = fd.getReference(playerStr).child(userID).child(tempStr);
        drP.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final List<String> tempKeys = new ArrayList<>();
                for(DataSnapshot dsnp : dataSnapshot.getChildren())
                {
                    Log.d("attach", dsnp.getKey());
                    tempKeys.add(dsnp.getKey());
                }
                if(tL != null)
                    tL.clear();
                if(tLKeys != null)
                    tLKeys.clear();
//                for(int i=0;i<limit;++i)
//                {
//                    GameTemplate newgt = new GameTemplate();
//                    tL.add(newgt);
//                }
                DatabaseReference drT = fd.getReference(gamesStr);
                drT.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        int i = 0;
                        tL.clear();
                        tLKeys.clear();
                        String filterStr = filter.getText().toString();
                        for(DataSnapshot dsnpT: dataSnapshot.getChildren())
                        {
                            if(i >= limit)
                                break;
                            if(tempKeys.contains(dsnpT.getKey()))
                                continue;
                            GameTemplate newgt;
                            newgt = new GameTemplate();
                            newgt.Initialize();
                            newgt = dsnpT.getValue(GameTemplate.class);
                            if(filterStr != null && !filterStr.isEmpty())
                            {
                                if(!newgt.name.contains(filterStr))
                                    continue;
                            }
//                            tL.set(i,newgt);
                            tL.add(newgt);
                            tLKeys.add(dsnpT.getKey());
                            mItemsRecyclesViewAdapter.notifyDataSetChanged();
                            ++i;
                        }
                        setChosenTemplateAsNull();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("templates", "Error.");
            }
        });
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView, List<GameTemplate> list) {
        mItemsRecyclesViewAdapter = new TemplateRecyclesViewAdapter(this,list);
        recyclerView.setAdapter(mItemsRecyclesViewAdapter);
    }

    public void onFilterButtonClicked(View view)
    {
        attachSearchTemplateListeners(fd,userID,limit,filterEditText);
    }

    public static class TemplateRecyclesViewAdapter extends RecyclerView.Adapter<TemplateRecyclesViewAdapter.TemplateViewHolder>
    {

        private final SearchTemplatesActivity parent;
        private final List<GameTemplate> templates;
        //        private int row_index = -1;
//        private final List<DummyContent.DummyItem> mValues;
        private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                GameTemplate item = (GameTemplate) view.getTag();
//                int position = (int) view.getTag();
//                chosenTemplateID = position;
//                chosenTemplate = templates.get(position);
//                FragmentSingleTemplate.notifyDataSets();
//                Context context = view.getContext();
//                chosenTemplate = item;
//                Intent intent = new Intent(context, EventDetailActivity.class);
//                intent.putExtra(EventDetailFragment.ARG_ITEM_ID, item.id);

//                context.startActivity(intent);

            }
        };

        TemplateRecyclesViewAdapter(SearchTemplatesActivity parent, List<GameTemplate> templates)
        {
            this.parent = parent;
//            this.mValues = templates;
            this.templates = templates;
        }

        @Override
        public TemplateRecyclesViewAdapter.TemplateViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.template_item_content, parent, false);
            return new TemplateRecyclesViewAdapter.TemplateViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final TemplateRecyclesViewAdapter.TemplateViewHolder holder, final int position) {
            templates.get(position).Initialize();
            holder.templateNameView.setText(templates.get(position).name);
            if(templates.get(position).sessions != null)
                holder.sessionsInfoView.setText("Sessions: " + templates.get(position).sessions.size());

            holder.itemView.setTag(position);


            holder.mLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = (int) view.getTag();
                    chosenTemplateID = tLKeys.get(position);
                    chosenTemplate = templates.get(position);
                    clickedTemplatePos = position;
                    notifyDataSetChanged();
                }
            });
            if(clickedTemplatePos == position)
            {
                holder.mLayout.setBackgroundColor(Color.parseColor("#127712"));
            }
            else
            {
                holder.mLayout.setBackgroundColor(Color.parseColor("#ffffff"));
            }
        }

        @Override
        public int getItemCount() {return templates.size();}

        class TemplateViewHolder extends RecyclerView.ViewHolder {
            final TextView templateNameView;
            final TextView sessionsInfoView;
            final LinearLayout mLayout;

            TemplateViewHolder(View view) {
                super(view);
                templateNameView = (TextView) view.findViewById(R.id.temp_name);
                sessionsInfoView = (TextView) view.findViewById(R.id.sess_info);
                mLayout = (LinearLayout) view.findViewById(R.id.temp_item_content_layout);
            }
        }
    }

}
