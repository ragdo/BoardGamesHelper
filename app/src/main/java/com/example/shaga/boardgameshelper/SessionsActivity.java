package com.example.shaga.boardgameshelper;

import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.restfb.FacebookClient;

import java.util.ArrayList;
import java.util.List;

public class SessionsActivity extends AppCompatActivity {

    private static Toolbar toolbar;
    private static TabLayout tabLayout;
    private static FirebaseDatabase fd;
    private static List<String> sLKeys;
    private static List<GameSession> sL;
    private static String playerStr = "players";
    private static String gamesStr = "games";
    private static String tempStr = "templates";
    private static String sessStr = "sessions";
    private static List<String> tLKeys;
    private static List<GameTemplate> tL;
    private static String chosenTemplateId = null;
    private static GameTemplate chosenTemplate = null;
    private static String chosenSessionId = null;
    private static GameSession chosenSession = null;
    private static int clickedTemplatePos = -1;
    private static int clickedSessionPos = -1;
    private static int clickedMySessionPos = -1;
    private static AccessToken accessToken;
    private static String userID = null;
    private static List<String> mySLKeys;
    private static List<GameSession> mySL;
    private static String chosenMySessionId = null;
    private static GameSession chosenMySession = null;
    private static FloatingActionButton fab;
    private static EditText filterEditText;
    private static EditText createEditText;

    private static SectionsPagerAdapter mSectionsPagerAdapter;
    private static ViewPager mViewPager;

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
    public void onBackPressed()
    {
        deselectTemplateSessionMySession(0);
        deselectTemplateSessionMySession(1);
        deselectTemplateSessionMySession(2);
        super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sessions);

        fd = FirebaseDatabase.getInstance();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        AppBarLayout.LayoutParams params = (AppBarLayout.LayoutParams) toolbar.getLayoutParams();
        params.setScrollFlags(0);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        tabLayout = (TabLayout) findViewById(R.id.tabsTS);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if(tab.getPosition() != 0 && (clickedTemplatePos == -1 || chosenTemplate == null || chosenTemplateId == null))
                {
                    mViewPager.setCurrentItem(0);
                    TabLayout.Tab tab1 = tabLayout.getTabAt(0);
                    tab1.select();
                    View view = findViewById(R.id.main_content);
                    Snackbar.make(view, "No template is chosen.", Snackbar.LENGTH_LONG).show();
                }
                else
                    mViewPager.setCurrentItem(tab.getPosition());
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) { }
            @Override
            public void onTabReselected(TabLayout.Tab tab) { }
        });

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));

        mViewPager.setOffscreenPageLimit(2);

        getUserID();

        if(mySL == null)
            mySL = new ArrayList<>();
        if(mySLKeys == null)
            mySLKeys = new ArrayList<>();

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                int a = mViewPager.getCurrentItem();
                String curPage = Integer.toString(a);
                if(curPage.equals("1"))
                {
                    if(clickedSessionPos == -1)
                        return;
                    DatabaseOperations.addSessionToPlayerDirectly(fd,userID,chosenSessionId, chosenTemplateId, chosenTemplate);
                    int i = sLKeys.indexOf(chosenSessionId);
                    sLKeys.remove(chosenSessionId);
                    sL.remove(i);
                    SessionFragmentList.mSessRecyclesViewAdapter.notifyDataSetChanged();

                    deselectTemplateSessionMySession(0);
                    deselectTemplateSessionMySession(1);
                    deselectTemplateSessionMySession(2);
                    MySessionFragmentList.attachSingleTimeUserSessionsListener(fd,userID,chosenTemplate);
                    mViewPager.setCurrentItem(0);
                    TabLayout.Tab tab1 = tabLayout.getTabAt(0);
                    tab1.select();
                }
                else if(curPage.equals("2"))
                {
                    if(clickedMySessionPos == -1 || chosenMySessionId == null || chosenTemplateId == null)
                        return;
                    Intent intent = new Intent(SessionsActivity.this, SingleSessionActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("sessionKey", chosenMySessionId);
                    bundle.putString("templateKey", chosenTemplateId);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            }
        });

    }

    public void onFilterSessionClick(View view) {
        if(chosenTemplate == null || filterEditText == null || clickedTemplatePos == -1)
            return;
        SessionFragmentList.attachSingleTimeUserSessionsListener(fd,userID,chosenTemplate);
    }

    private static void deselectTemplateSessionMySession(int pos)
    {
        if(pos == 0)
        {
            clickedTemplatePos = -1;
            chosenTemplate = null;
            chosenTemplateId = null;
        }
        else if(pos == 1) {
            clickedSessionPos = -1;
            chosenSessionId = null;
            chosenSession = null;
        }
        else if(pos == 2) {
            clickedMySessionPos = -1;
            chosenMySessionId = null;
            chosenMySession = null;
        }
    }


    public void onCreateSessionClick(View view) {
        if(clickedTemplatePos == -1 || createEditText == null || chosenTemplate == null)
            return;
        String newName = createEditText.getText().toString();
        if(newName == null)
            return;
        if(newName.isEmpty())
            return;
        GameSession gs = new GameSession();
        gs.Initialize();
        gs.name = newName;
        StatsTemplate st = DatabaseOperations.getStats(chosenTemplate,true);
        gs.playerStats.put(userID,st);
        StatsTemplate sts = DatabaseOperations.getStats(chosenTemplate, false);
        gs.sharedStats = sts;
        String key = DatabaseOperations.createSession(fd,userID,chosenTemplateId,gs);
        deselectTemplateSessionMySession(2);
        chosenTemplate.sessions.put(key,gs);
        MySessionFragmentList.attachSingleTimeUserSessionsListener(fd,userID,chosenTemplate);
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_sessions, menu);
//        return true;
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                deselectTemplateSessionMySession(0);
                deselectTemplateSessionMySession(1);
                deselectTemplateSessionMySession(2);
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class SessionFragmentTemplatesList extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";
        private static TempRecyclesViewAdapter mTempRecyclesViewAdapter;

        public SessionFragmentTemplatesList() {
        }

        private void setupRecyclerView(@NonNull RecyclerView recyclerView, List<GameTemplate> list) {
            mTempRecyclesViewAdapter = new TempRecyclesViewAdapter(this, list);
            recyclerView.setAdapter(mTempRecyclesViewAdapter);
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static SessionFragmentTemplatesList newInstance(int sectionNumber) {
            SessionFragmentTemplatesList fragment = new SessionFragmentTemplatesList();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        private void attachUserTemplatesListeners(final FirebaseDatabase fd, String userID)
        {
            if(fd == null || userID == null)
            {
                Log.d("sessions", "Non-initialized database or id.");
                return;
            }

            DatabaseReference drP = fd.getReference(playerStr).child(userID).child(tempStr);
            drP.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Log.d("attach", Long.toString(dataSnapshot.getChildrenCount()));
                    List<String> tempKeys = new ArrayList<>();
                    for(DataSnapshot dsnp : dataSnapshot.getChildren())
                    {
                        Log.d("attach", dsnp.getKey());
                        tempKeys.add(dsnp.getKey());
                    }
                    if(tLKeys != null)
                        tLKeys.clear();
                    tLKeys = tempKeys;
                    int totalSize = tempKeys.size();
                    if(tL != null)
                        tL.clear();
                    for(int i=0;i<totalSize;++i)
                    {
                        GameTemplate newgt = new GameTemplate();
                        tL.add(newgt);
                    }
                    int tS = 0;
                    DatabaseReference drT = fd.getReference(gamesStr);
                    for(String temp : tempKeys)
                    {
                        final int iterator = tS;
                        drT.child(temp).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                GameTemplate gt = new GameTemplate();
                                gt.Initialize();
                                gt = dataSnapshot.getValue(GameTemplate.class);
//                                if(gt.sessions == null)
//                                    gt.sessions = new HashMap<String, GameSession>();
                                gt.Initialize();
                                tL.set(iterator,gt);
                                mTempRecyclesViewAdapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Log.d("sessions", "Error.");
                            }
                        });
                        ++tS;
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.d("sessions", "Error.");
                }
            });
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_sessions_templates_list, container, false);
            View tempListView = rootView.findViewById(R.id.temp_list);
            assert tempListView != null;
            if(tL == null)
                tL = new ArrayList<GameTemplate>();
            if(tLKeys == null)
                tLKeys = new ArrayList<>();
            setupRecyclerView((RecyclerView) tempListView, tL);
            attachUserTemplatesListeners(fd, userID);
//            TextView textView = (TextView) rootView.findViewById(R.id.section_label);
//            textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
            return rootView;
        }
    }

    public static class SessionFragmentList extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";
        private static SessRecyclesViewAdapter mSessRecyclesViewAdapter;

        public SessionFragmentList() {
        }

        private void setupRecyclerView(@NonNull RecyclerView recyclerView, List<GameSession> list) {
            mSessRecyclesViewAdapter = new SessRecyclesViewAdapter(this, list);
            recyclerView.setAdapter(mSessRecyclesViewAdapter);
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static SessionFragmentList newInstance(int sectionNumber) {
            SessionFragmentList fragment = new SessionFragmentList();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_sessions, container, false);
            View sessListView = rootView.findViewById(R.id.sess_list);
            filterEditText = (EditText) rootView.findViewById(R.id.editTextFilterSession);
            assert sessListView != null;
            if(sL == null)
                sL = new ArrayList<GameSession>();
            if(sLKeys == null)
                sLKeys = new ArrayList<>();
            setupRecyclerView((RecyclerView) sessListView, sL);
//            attachUserSessionsListeners(fd,userID,chosenTemplateId);
            return rootView;
        }

        private static void attachSingleTimeUserSessionsListener(final FirebaseDatabase fd, final String userID, final GameTemplate temp)
        {
            if(fd == null || userID == null || temp == null)
            {
                return;
            }

            DatabaseReference drP = fd.getReference(playerStr).child(userID).child(sessStr);
            drP.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    List<String> userSessKeys = new ArrayList<>();
                    for(DataSnapshot dsnp : dataSnapshot.getChildren())
                    {
                        Log.d("attach", dsnp.getKey());
                        userSessKeys.add(dsnp.getKey());
                    }

                    if(mySL != null)
                        mySL.clear();
                    if(mySLKeys != null)
                        mySLKeys.clear();

                    String filter = filterEditText.getText().toString();
                    boolean check = false;
                    if(filter != null && !filter.isEmpty()) {
                        check = true;
                    }

                    sL.clear();
                    sLKeys.clear();
                    for(String key : chosenTemplate.sessions.keySet())
                    {
                        if(userSessKeys.contains(key))
                            continue;
                        GameSession gs = chosenTemplate.sessions.get(key);
                        if(check)
                        {
                            if(!gs.name.contains(filter))
                            {
                                continue;
                            }
                        }
                        sL.add(gs);
                        sLKeys.add(key);
                    }
                    SessionFragmentList.mSessRecyclesViewAdapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.d("sessions", "Error.");
                }
            });
        }
    }

    public static class MySessionFragmentList extends Fragment {
        private static final String ARG_SECTION_NUMBER = "section_number";
        private static MySessRecyclesViewAdapter mMySessRecyclesViewAdapter;

        public MySessionFragmentList() {
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
        }

        private void setupRecyclerView(@NonNull RecyclerView recyclerView, List<GameSession> list) {
            mMySessRecyclesViewAdapter = new MySessRecyclesViewAdapter(this, list);
            recyclerView.setAdapter(mMySessRecyclesViewAdapter);
        }

        public static MySessionFragmentList newInstance(int sectionNumber) {
            MySessionFragmentList fragment = new MySessionFragmentList();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
        {
            View rootView = inflater.inflate(R.layout.fragment_my_sessions, container, false);
            View sessListView = rootView.findViewById(R.id.my_sess_list);
            assert sessListView != null;
            createEditText = (EditText) rootView.findViewById(R.id.editTextCreateSession);
            if(mySLKeys == null)
                mySLKeys = new ArrayList<>();
            if(mySL == null)
                mySL = new ArrayList<>();

            setupRecyclerView((RecyclerView) sessListView, mySL);
            return rootView;
        }

        private static void attachSingleTimeUserSessionsListener(final FirebaseDatabase fd, final String userID, final GameTemplate temp)
        {
            if(fd == null || userID == null || temp == null)
            {
                Log.d("sessions", "Non-initialized database or either of IDs.");
                return;
            }

            DatabaseReference drP = fd.getReference(playerStr).child(userID).child(sessStr);
            drP.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    List<String> userSessKeys = new ArrayList<>();
                    for(DataSnapshot dsnp : dataSnapshot.getChildren())
                    {
                        Log.d("attach", dsnp.getKey());
                        userSessKeys.add(dsnp.getKey());
                    }

                    if(mySL != null)
                        mySL.clear();
                    if(mySLKeys != null)
                        mySLKeys.clear();

                    for(String usk : userSessKeys)
                    {
                        if(temp.sessions.keySet().contains(usk))
                        {
                            mySLKeys.add(usk);
                            mySL.add(temp.sessions.get(usk));
                        }
                    }
                    mMySessRecyclesViewAdapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.d("sessions", "Error.");
                }
            });
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        private Fragment mCurrentFragment;
        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        public Fragment getCurrentFragment()
        {
            return mCurrentFragment;
        }

        @Override
        public void setPrimaryItem(ViewGroup container, int position, Object object) {
            if (getCurrentFragment() != object) {
                mCurrentFragment = ((Fragment) object);
            }
            super.setPrimaryItem(container, position, object);
            String title;
            if(position == 1) {
                title = "Search sessions";
                fab.show();
            }
            else if(position == 2) {
                title = "My sessions";
                fab.show();
            }
            else {
                title = "Choose template";
                fab.hide();
            }
            getSupportActionBar().setTitle(title);
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a SessionFragmentTemplatesList (defined as a static inner class below).
            if(position == 0)
                return SessionFragmentTemplatesList.newInstance(position + 1);
            else if(position == 1)
                return SessionFragmentList.newInstance(position + 1);
            else
                return MySessionFragmentList.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            return 3;
        }
    }

    public static class TempRecyclesViewAdapter extends RecyclerView.Adapter<TempRecyclesViewAdapter.TemplateViewHolder>
    {
        private final SessionFragmentTemplatesList parent;
        private final List<GameTemplate> templates;

        private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        };

        TempRecyclesViewAdapter(SessionFragmentTemplatesList parent, List<GameTemplate> templates)
        {
            this.parent = parent;
            this.templates = templates;
        }

        @Override
        public TemplateViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.template_item_content, parent, false);
            return new TemplateViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final TemplateViewHolder holder, final int position) {
            holder.templateNameView.setText(templates.get(position).name);
            if(templates.get(position).sessions != null)
                holder.sessionsInfoView.setText("Sessions: " + templates.get(position).sessions.size());

            holder.itemView.setTag(position);
            holder.mLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = (int) view.getTag();
                    chosenTemplateId = tLKeys.get(position);
                    chosenTemplate = templates.get(position);
                    clickedTemplatePos = position;

                    chosenMySessionId = chosenSessionId = null;
                    chosenMySession = chosenSession = null;
                    clickedMySessionPos = clickedSessionPos = -1;

                    notifyDataSetChanged();

                    SessionFragmentList.attachSingleTimeUserSessionsListener(fd,userID,chosenTemplate);
                    MySessionFragmentList.attachSingleTimeUserSessionsListener(fd,userID,chosenTemplate);
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
                templateNameView = view.findViewById(R.id.temp_name);
                sessionsInfoView = view.findViewById(R.id.sess_info);
                mLayout = view.findViewById(R.id.temp_item_content_layout);
            }
        }
    }

    public static class SessRecyclesViewAdapter extends RecyclerView.Adapter<SessRecyclesViewAdapter.SessionViewHolder>
    {
        private final SessionFragmentList parent;
        private final List<GameSession> sessions;

        SessRecyclesViewAdapter(SessionFragmentList parent, List<GameSession> sessions)
        {
            this.parent = parent;
            this.sessions = sessions;
        }

        @Override
        public void onBindViewHolder(final SessionViewHolder holder, final int position)
        {
            sessions.get(position).Initialize();
            holder.sessionNameView.setText(sessions.get(position).name);
            if(sessions.get(position).playerStats != null)
                holder.nrPlayersInfoView.setText("Players: " + sessions.get(position).playerStats.size());

            holder.itemView.setTag(position);
            holder.mLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = (int) view.getTag();
                    chosenSessionId = sLKeys.get(position);
                    chosenSession = sessions.get(position);
//                    updateChosenTemplate(chosenTemplate,chosenTemplateId);
//                    TemplatesActivity.FragmentSingleTemplate.notifyDataSets();
                    clickedSessionPos = position;
//                    mViewPager.disableScroll(false);
                    notifyDataSetChanged();
                }
            });
            if(clickedSessionPos == position)
            {
                holder.mLayout.setBackgroundColor(Color.parseColor("#127712"));
            }
            else
            {
                holder.mLayout.setBackgroundColor(Color.parseColor("#ffffff"));
            }
        }

        @Override
        public SessionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.session_item_content, parent, false);
            return new SessionViewHolder(view);
        }

        @Override
        public int getItemCount() {return sessions.size();}

        class SessionViewHolder extends RecyclerView.ViewHolder {
            final TextView sessionNameView;
            final TextView nrPlayersInfoView;
            final LinearLayout mLayout;

            SessionViewHolder(View view) {
                super(view);
                sessionNameView = view.findViewById(R.id.session_name_textView);
                nrPlayersInfoView = view.findViewById(R.id.nr_of_players_textView);
                mLayout = view.findViewById(R.id.sess_item_content_layout);
            }
        }
    }

    public static class MySessRecyclesViewAdapter extends RecyclerView.Adapter<MySessRecyclesViewAdapter.SessionViewHolder>
    {
        private final MySessionFragmentList parent;
        private final List<GameSession> sessions;

        MySessRecyclesViewAdapter(MySessionFragmentList parent, List<GameSession> sessions)
        {
            this.parent = parent;
            this.sessions = sessions;
        }

        @Override
        public void onBindViewHolder(final SessionViewHolder holder, final int position)
        {
            sessions.get(position).Initialize();
            holder.sessionNameView.setText(sessions.get(position).name);
            if(sessions.get(position).playerStats != null)
                holder.nrPlayersInfoView.setText("Players: " + sessions.get(position).playerStats.size());

            holder.itemView.setTag(position);
            holder.mLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = (int) view.getTag();
                    chosenMySessionId = mySLKeys.get(position);
                    chosenMySession = sessions.get(position);

                    clickedMySessionPos = position;
                    notifyDataSetChanged();
                }
            });
            if(clickedMySessionPos == position)
            {
                holder.mLayout.setBackgroundColor(Color.parseColor("#127712"));
            }
            else
            {
                holder.mLayout.setBackgroundColor(Color.parseColor("#ffffff"));
            }
        }

        @Override
        public SessionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.session_item_content, parent, false);
            return new SessionViewHolder(view);
        }

        @Override
        public int getItemCount() {return sessions.size();}

        class SessionViewHolder extends RecyclerView.ViewHolder {
            final TextView sessionNameView;
            final TextView nrPlayersInfoView;
            final LinearLayout mLayout;

            SessionViewHolder(View view) {
                super(view);
                sessionNameView = view.findViewById(R.id.session_name_textView);
                nrPlayersInfoView = view.findViewById(R.id.nr_of_players_textView);
                mLayout = view.findViewById(R.id.sess_item_content_layout);
            }
        }
    }
}
