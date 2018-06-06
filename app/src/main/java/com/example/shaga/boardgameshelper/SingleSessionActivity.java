package com.example.shaga.boardgameshelper;

import android.content.Context;
import android.content.Intent;
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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SingleSessionActivity extends AppCompatActivity {

    private static String playerStr = "players";
    private static String gamesStr = "games";
    private static String tempStr = "templates";
    private static String sessStr = "sessions";

    private static String chosenSessionKey;
    private static GameSession chosenSession;
    private static String chosenTemplateKey;
    private static GameTemplate chosenTemplate;
    private static AccessToken accessToken;
    private static String userID;
    private static FirebaseDatabase fd;


    private static Toolbar toolbar;
    private static SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;

    private static List<String> stringListKeysS;
    private static List<String> stringListKeysP;
    private static List<String> integerListKeysS;
    private static List<String> integerListKeysP;
    private static List<String> booleanListKeysS;
    private static List<String> booleanListKeysP;

    private static List<String> stringListValuesS;
    private static List<String> stringListValuesP;
    private static List<Integer> integerListValuesS;
    private static List<Integer> integerListValuesP;
    private static List<Boolean> booleanListValuesS;
    private static List<Boolean> booleanListValuesP;

    private static List<String> overviewList;

    DatabaseReference drs;
    ValueEventListener vel;

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

    private static void prepareValueLists()
    {
        if(stringListKeysS == null)
            stringListKeysS = new ArrayList<>();
        else
            stringListKeysS.clear();
        if(stringListKeysP == null)
            stringListKeysP = new ArrayList<>();
        else
            stringListKeysP.clear();
        if(integerListKeysS == null)
            integerListKeysS = new ArrayList<>();
        else
            integerListKeysS.clear();
        if(integerListKeysP == null)
            integerListKeysP = new ArrayList<>();
        else
            integerListKeysP.clear();
        if(booleanListKeysS == null)
            booleanListKeysS = new ArrayList<>();
        else
            booleanListKeysS.clear();
        if(booleanListKeysP == null)
            booleanListKeysP = new ArrayList<>();
        else
            booleanListKeysP.clear();
        if(stringListValuesS == null)
            stringListValuesS = new ArrayList<>();
        else
            stringListValuesS.clear();
        if(stringListValuesP == null)
            stringListValuesP = new ArrayList<>();
        else
            stringListValuesP.clear();
        if(integerListValuesS == null)
            integerListValuesS = new ArrayList<>();
        else
            integerListValuesS.clear();
        if(integerListValuesP == null)
            integerListValuesP = new ArrayList<>();
        else
            integerListValuesP.clear();
        if(booleanListValuesS == null)
            booleanListValuesS = new ArrayList<>();
        else
            booleanListValuesS.clear();
        if(booleanListValuesP == null)
            booleanListValuesP = new ArrayList<>();
        else
            booleanListValuesP.clear();
    }

    private static void updateValueLists()
    {
        if(chosenSession.sharedStats.valuesBool == null)
            chosenSession.sharedStats.valuesBool = new HashMap<>();
        for(String key : chosenSession.sharedStats.valuesBool.keySet())
        {
            booleanListKeysS.add(key);
            booleanListValuesS.add(chosenSession.sharedStats.valuesBool.get(key));
        }
        if(chosenSession.sharedStats.valuesNumber == null)
            chosenSession.sharedStats.valuesNumber = new HashMap<>();
        for(String key : chosenSession.sharedStats.valuesNumber.keySet())
        {
            integerListKeysS.add(key);
            integerListValuesS.add(chosenSession.sharedStats.valuesNumber.get(key));
        }
        if(chosenSession.sharedStats.valuesText == null)
            chosenSession.sharedStats.valuesText = new HashMap<>();
        for(String key : chosenSession.sharedStats.valuesText.keySet())
        {
            stringListKeysS.add(key);
            stringListValuesS.add(chosenSession.sharedStats.valuesText.get(key));
        }

        if(chosenSession.playerStats.get(userID).valuesBool == null)
            chosenSession.playerStats.get(userID).valuesBool = new HashMap<>();
        for(String key : chosenSession.playerStats.get(userID).valuesBool.keySet())
        {
            booleanListKeysP.add(key);
            booleanListValuesP.add(chosenSession.playerStats.get(userID).valuesBool.get(key));
        }
        if(chosenSession.playerStats.get(userID).valuesNumber == null)
            chosenSession.playerStats.get(userID).valuesNumber = new HashMap<>();
        for(String key : chosenSession.playerStats.get(userID).valuesNumber.keySet())
        {
            integerListKeysP.add(key);
            integerListValuesP.add(chosenSession.playerStats.get(userID).valuesNumber.get(key));
        }
        if(chosenSession.playerStats.get(userID).valuesText == null)
            chosenSession.playerStats.get(userID).valuesText = new HashMap<>();
        for(String key : chosenSession.playerStats.get(userID).valuesText.keySet())
        {
            stringListKeysP.add(key);
            stringListValuesP.add(chosenSession.playerStats.get(userID).valuesText.get(key));
        }
    }

    private void setChosenSessionAndTemplate(final String templateKey, final String sessionKey)
    {
        if(templateKey == null || sessionKey == null || fd == null)
            return;
        DatabaseReference dr = fd.getReference(gamesStr).child(templateKey);
        dr.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                GameTemplate gt = new GameTemplate();
                gt.Initialize();
                gt = dataSnapshot.getValue(GameTemplate.class);
                gt.Initialize();
                chosenTemplate = gt;
                drs = fd.getReference(gamesStr).child(templateKey).child(sessStr).child(sessionKey);
                vel = new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        GameSession gs = new GameSession();
                        gs.Initialize();
                        gs = dataSnapshot.getValue(GameSession.class);
                        gs.Initialize();
                        chosenSession = gs;
                        prepareValueLists();
                        updateValueLists();
                        SessionFieldsFragment.notifyDataSets();
                        setOverviewList(chosenSession);
                        OverviewFragment.overviewRecyclesAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                };
                drs.addValueEventListener(vel);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void addToOverviewList(String playerName, String valueType, String valueName, String value)
    {
        if(playerName == null)
            playerName = "";
        if(valueType == null)
            valueType = "";
        if(valueName == null)
            valueName = "";
        if(value == null)
            value = "";
        overviewList.add(playerName);
        overviewList.add(valueType);
        overviewList.add(valueName);
        overviewList.add(value);
    }

    public void hideKeyboard(){
        InputMethodManager inputManager = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);

        inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);
    }

    private void setOverviewList(final GameSession gs)
    {
        final String tf = "Text fields";
        final String nf = "Number fields";
        final String bf = "True/false fields";
        if(fd == null)
            return;

        if(overviewList != null)
            overviewList.clear();
        addToOverviewList("Shared stats", "", "", "");
        if(gs.sharedStats.valuesText != null) {
            addToOverviewList("", tf, "", "");
            for (String key : gs.sharedStats.valuesText.keySet()) {
                addToOverviewList("", "", key, gs.sharedStats.valuesText.get(key));
            }
        }
        if(gs.sharedStats.valuesNumber != null) {
            addToOverviewList("", nf, "", "");
            for(String key : gs.sharedStats.valuesNumber.keySet()) {
                String number = Integer.toString(gs.sharedStats.valuesNumber.get(key));
                addToOverviewList("", "", key, number);
            }
        }
        if(gs.sharedStats.valuesBool != null) {
            addToOverviewList("", bf, "", "");
            for(String key : gs.sharedStats.valuesBool.keySet()) {
                String bool = Boolean.toString(gs.sharedStats.valuesBool.get(key));
                addToOverviewList("", "", key, bool);
            }
        }

        if(gs.playerStats == null)
            return;
        for(final String plKey : gs.playerStats.keySet())
        {
            DatabaseReference ref = fd.getReference(playerStr).child(plKey).child("name");
            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    String usernmae = dataSnapshot.getValue(String.class);
                    addToOverviewList(usernmae,"", "", "");
                    if(gs.playerStats.get(plKey).valuesText != null){
                        addToOverviewList("",tf,"","");
                        for(String key : gs.playerStats.get(plKey).valuesText.keySet())
                        {
                            String value = gs.playerStats.get(plKey).valuesText.get(key);
                            addToOverviewList("", "", key, value);
                        }
                    }
                    if(gs.playerStats.get(plKey).valuesNumber != null){
                        addToOverviewList("",nf,"","");
                        for(String key : gs.playerStats.get(plKey).valuesNumber.keySet())
                        {
                            String value = Integer.toString(gs.playerStats.get(plKey).valuesNumber.get(key));
                            addToOverviewList("", "", key, value);
                        }
                    }
                    if(gs.playerStats.get(plKey).valuesBool != null){
                        addToOverviewList("",bf,"","");
                        for(String key : gs.playerStats.get(plKey).valuesBool.keySet())
                        {
                            String value = Boolean.toString(gs.playerStats.get(plKey).valuesBool.get(key));
                            addToOverviewList("", "", key, value);
                        }
                    }
                    OverviewFragment.overviewRecyclesAdapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_session);

        prepareValueLists();

        Intent intentBundle = getIntent();
        Bundle bundle = intentBundle.getExtras();
        if(bundle.isEmpty())
            finish();
        if(!bundle.containsKey("sessionKey"))
            finish();
        chosenSessionKey = bundle.getString("sessionKey");
        if(!bundle.containsKey("templateKey"))
            finish();
        chosenTemplateKey = bundle.getString("templateKey");

        fd = FirebaseDatabase.getInstance();

        overviewList = new ArrayList<>();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        AppBarLayout.LayoutParams params = (AppBarLayout.LayoutParams) toolbar.getLayoutParams();
        params.setScrollFlags(0);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));

        mViewPager.setOffscreenPageLimit(2);
        getUserID();
        setChosenSessionAndTemplate(chosenTemplateKey,chosenSessionKey);

    }

    @Override
    protected void onStop(){
        super.onStop();
        drs.removeEventListener(vel);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_single_session, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this,DiceRollActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public static class OverviewFragment extends Fragment {

        private static final String ARG_SECTION_NUMBER = "section_number";
        private static OverviewRecyclesAdapter overviewRecyclesAdapter;

        public OverviewFragment(){}

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
        }

        private void setupRecyclerView(@NonNull RecyclerView recyclerView, List<String> list)
        {
            overviewRecyclesAdapter = new OverviewRecyclesAdapter(this,list);
            recyclerView.setAdapter(overviewRecyclesAdapter);
        }

        public static OverviewFragment newInstance(int sectionNumber) {
            OverviewFragment fragment = new OverviewFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_overview, container, false);
            View listView = rootView.findViewById(R.id.over_list);
            assert listView != null;
            setupRecyclerView((RecyclerView) listView, overviewList);
            return rootView;
        }
    }

    public static class SessionFieldsFragment extends Fragment {

        private static final String ARG_SECTION_NUMBER = "section_number";
        private boolean playerValues;

        private static SessionTextRecyclesViewAdapter sharedTextViewAdapter;
        private static SessionTextRecyclesViewAdapter playerTextViewAdapter;
        private static SessionNumberRecyclesViewAdapter sharedNumberViewAdapter;
        private static SessionNumberRecyclesViewAdapter playerNumberViewAdapter;
        private static SessionBooleanRecyclesViewAdapter sharedBooleanViewAdapter;
        private static SessionBooleanRecyclesViewAdapter playerBooleanViewAdapter;

        public SessionFieldsFragment() {
        }

        private static void notifyDataSets()
        {
            sharedBooleanViewAdapter.notifyDataSetChanged();
            sharedNumberViewAdapter.notifyDataSetChanged();
            sharedTextViewAdapter.notifyDataSetChanged();
            playerBooleanViewAdapter.notifyDataSetChanged();
            playerNumberViewAdapter.notifyDataSetChanged();
            playerTextViewAdapter.notifyDataSetChanged();
        }

        private void setupStringRecyclerView(@NonNull RecyclerView recyclerView, List<String> keys, List<String> values)
        {
            if(playerValues) {
                playerTextViewAdapter = new SessionTextRecyclesViewAdapter(this, keys, values, new ClickListener() {
                    @Override
                    public void onPositionClicked(int position) {

                    }
                });
                recyclerView.setAdapter(playerTextViewAdapter);
            }
            else {
                sharedTextViewAdapter = new SessionTextRecyclesViewAdapter(this, keys, values, new ClickListener() {
                    @Override
                    public void onPositionClicked(int position) {

                    }
                });
                recyclerView.setAdapter(sharedTextViewAdapter);
            }
        }

        private void setupIntegerRecyclerView(@NonNull RecyclerView recyclerView, List<String> keys, List<Integer> values)
        {
            if(playerValues) {
                playerNumberViewAdapter = new SessionNumberRecyclesViewAdapter(this, keys, values, new ClickListener() {
                    @Override
                    public void onPositionClicked(int position) {

                    }
                });
                recyclerView.setAdapter(playerNumberViewAdapter);
            }
            else {
                sharedNumberViewAdapter = new SessionNumberRecyclesViewAdapter(this, keys, values, new ClickListener() {
                    @Override
                    public void onPositionClicked(int position) {

                    }
                });
                recyclerView.setAdapter(sharedNumberViewAdapter);
            }
        }

        private void setupBooleanRecyclerView(@NonNull RecyclerView recyclerView, List<String> keys, List<Boolean> values)
        {
            if(playerValues) {
                playerBooleanViewAdapter = new SessionBooleanRecyclesViewAdapter(this, keys, values, new ClickListener() {
                    @Override
                    public void onPositionClicked(int position) {

                    }
                });
                recyclerView.setAdapter(playerBooleanViewAdapter);
            }
            else {
                sharedBooleanViewAdapter = new SessionBooleanRecyclesViewAdapter(this, keys, values, new ClickListener() {
                    @Override
                    public void onPositionClicked(int position) {

                    }
                });
                recyclerView.setAdapter(sharedBooleanViewAdapter);
            }
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            if (getArguments() != null) {
                if(getArguments().getInt(ARG_SECTION_NUMBER) == 1)
                    playerValues = true;
                else
                    playerValues = false;
            }
        }

        public static SessionFieldsFragment newInstance(int sectionNumber) {
            SessionFieldsFragment fragment = new SessionFieldsFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_session_fields, container, false);

            View stringListView = rootView.findViewById(R.id.string_sess_field_list);
            View intListView = rootView.findViewById(R.id.int_sess_field_list);
            View boolListView = rootView.findViewById(R.id.bool_sess_field_list);
            assert stringListView != null;
            assert intListView != null;
            assert boolListView != null;
            if(playerValues) {
                setupStringRecyclerView((RecyclerView) stringListView, stringListKeysP, stringListValuesP);
                setupIntegerRecyclerView((RecyclerView) intListView, integerListKeysP, integerListValuesP);
                setupBooleanRecyclerView((RecyclerView) boolListView, booleanListKeysP, booleanListValuesP);
            } else {
                setupStringRecyclerView((RecyclerView) stringListView, stringListKeysS, stringListValuesS);
                setupIntegerRecyclerView((RecyclerView) intListView, integerListKeysS, integerListValuesS);
                setupBooleanRecyclerView((RecyclerView) boolListView, booleanListKeysS, booleanListValuesS);
            }
            return rootView;
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
            if(position == 1)
                title = "Player fields";
            else if(position == 2)
                title = "Shared fields";
            else {
                title = "Overview";
            }
            getSupportActionBar().setTitle(title);
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a SessionFieldsFragment (defined as a static inner class below).
            if(position == 0)
                return OverviewFragment.newInstance(position);
            else
                return SessionFieldsFragment.newInstance(position);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }
    }

    public static class SessionTextRecyclesViewAdapter extends RecyclerView.Adapter<SessionTextRecyclesViewAdapter.TextViewHolder>
    {
        private final SessionFieldsFragment parent;
        private final List<String> textFieldsKeys;
        private final List<String> textFieldsValues;
        private final ClickListener listener;

        SessionTextRecyclesViewAdapter(SessionFieldsFragment parent, List<String> textFieldsKeys, List<String> textFieldsValues, ClickListener listener)
        {
            this.parent = parent;
            this.textFieldsKeys = textFieldsKeys;
            this.textFieldsValues = textFieldsValues;
            this.listener = listener;
        }

        @Override
        public void onBindViewHolder(final TextViewHolder holder, final int position) {

            holder.sessTextNameView.setText(textFieldsKeys.get(position));
            holder.sessTextValue.setText(textFieldsValues.get(position));
            holder.itemView.setTag(position);
        }

        @Override
        public TextViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.session_text_content, parent, false);
            return new TextViewHolder(view, listener);
        }

        @Override
        public int getItemCount() {return textFieldsKeys.size();}

        class TextViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            final TextView sessTextNameView;
            final EditText sessTextValue;
            final Button sessTextButton;
            final LinearLayout mLayout;
            private WeakReference<ClickListener> listenerRef;

            TextViewHolder(View view, ClickListener listener) {
                super(view);
                sessTextNameView = view.findViewById(R.id.textViewSessionTextContent);
                sessTextValue = view.findViewById(R.id.editTextSessionTextContent);
                sessTextButton = view.findViewById(R.id.button_session_text_edit);
                mLayout = view.findViewById(R.id.sess_item_content_layout);
                listenerRef = new WeakReference<ClickListener>(listener);
                sessTextButton.setOnClickListener(this);
            }

            @Override
            public void onClick(View v) {
//                Toast.makeText(v.getContext(), "ROW PRESSED = " + String.valueOf(getAdapterPosition()) + sessTextValue.getText().toString(), Toast.LENGTH_SHORT).show();
                SessionFieldsFragment fragment = (SessionFieldsFragment) mSectionsPagerAdapter.getCurrentFragment();
                DatabaseOperations.updateSessionValueText(fd,chosenTemplateKey,chosenSessionKey,sessTextNameView.getText().toString(),sessTextValue.getText().toString(), fragment.playerValues, userID);
                listenerRef.get().onPositionClicked(getAdapterPosition());
            }
        }
    }

    public static class SessionNumberRecyclesViewAdapter extends RecyclerView.Adapter<SessionNumberRecyclesViewAdapter.NumberViewHolder>
    {
        private final SessionFieldsFragment parent;
        private final List<String> NumberFieldsKeys;
        private final List<Integer> NumberFieldsValues;
        private final ClickListener listener;

        SessionNumberRecyclesViewAdapter(SessionFieldsFragment parent, List<String> numberFieldsKeys, List<Integer> numberFieldsValues, ClickListener listener)
        {
            this.parent = parent;
            this.NumberFieldsKeys = numberFieldsKeys;
            this.NumberFieldsValues = numberFieldsValues;
            this.listener = listener;
        }

        @Override
        public void onBindViewHolder(final NumberViewHolder holder, final int position) {

            holder.sessNumberNameView.setText(NumberFieldsKeys.get(position));
            holder.sessNumberValue.setText(NumberFieldsValues.get(position).toString());
            holder.itemView.setTag(position);
        }

        @Override
        public NumberViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.session_number_content, parent, false);
            return new NumberViewHolder(view, listener);
        }

        @Override
        public int getItemCount() {return NumberFieldsKeys.size();}

        class NumberViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            final TextView sessNumberNameView;
            final EditText sessNumberValue;
            final Button sessNumberButton;
            final LinearLayout mLayout;
            private WeakReference<ClickListener> listenerRef;

            NumberViewHolder(View view, ClickListener listener) {
                super(view);
                sessNumberNameView = view.findViewById(R.id.textViewSessionNumberContent);
                sessNumberValue = view.findViewById(R.id.editTextSessionNumberContent);
                sessNumberButton = view.findViewById(R.id.button_session_number_content);
                mLayout = view.findViewById(R.id.sess_item_content_layout);
                listenerRef = new WeakReference<ClickListener>(listener);
                sessNumberButton.setOnClickListener(this);
            }

            @Override
            public void onClick(View v) {
                SessionFieldsFragment fragment = (SessionFieldsFragment) mSectionsPagerAdapter.getCurrentFragment();
                Integer value = Integer.parseInt(sessNumberValue.getText().toString());
                DatabaseOperations.updateSessionValueNumber(fd,chosenTemplateKey,chosenSessionKey,sessNumberNameView.getText().toString(), value, fragment.playerValues, userID);
                listenerRef.get().onPositionClicked(getAdapterPosition());
            }
        }
    }

    public static class SessionBooleanRecyclesViewAdapter extends RecyclerView.Adapter<SessionBooleanRecyclesViewAdapter.BooleanViewHolder>
    {
        private final SessionFieldsFragment parent;
        private final List<String> BooleanFieldsKeys;
        private final List<Boolean> BooleanFieldsValues;
        private final ClickListener listener;

        SessionBooleanRecyclesViewAdapter(SessionFieldsFragment parent, List<String> booleanFieldsKeys, List<Boolean> booleanFieldsValues, ClickListener listener)
        {
            this.parent = parent;
            this.BooleanFieldsKeys = booleanFieldsKeys;
            this.BooleanFieldsValues = booleanFieldsValues;
            this.listener = listener;
        }

        @Override
        public void onBindViewHolder(final BooleanViewHolder holder, final int position) {

            holder.sessBooleanNameView.setText(BooleanFieldsKeys.get(position));
            holder.sessBooleanCheckbox.setChecked(BooleanFieldsValues.get(position));
            holder.itemView.setTag(position);
        }

        @Override
        public BooleanViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.session_boolean_content, parent, false);
            return new BooleanViewHolder(view, listener);
        }

        @Override
        public int getItemCount() {return BooleanFieldsKeys.size();}

        class BooleanViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            final TextView sessBooleanNameView;
            final CheckBox sessBooleanCheckbox;
            final LinearLayout mLayout;
            private WeakReference<ClickListener> listenerRef;

            BooleanViewHolder(View view, ClickListener listener) {
                super(view);
                sessBooleanNameView = view.findViewById(R.id.textViewSessionBooleanContent);
                sessBooleanCheckbox = view.findViewById(R.id.checkBoxSession);
                mLayout = view.findViewById(R.id.sess_item_content_layout);
                listenerRef = new WeakReference<ClickListener>(listener);
                sessBooleanCheckbox.setOnClickListener(this);
            }

            @Override
            public void onClick(View v) {
                SessionFieldsFragment fragment = (SessionFieldsFragment) mSectionsPagerAdapter.getCurrentFragment();
                Boolean value = sessBooleanCheckbox.isChecked();
                DatabaseOperations.updateSessionValueBool(fd,chosenTemplateKey,chosenSessionKey,sessBooleanNameView.getText().toString(), value, fragment.playerValues, userID);
                listenerRef.get().onPositionClicked(getAdapterPosition());
            }
        }
    }

    public static class OverviewRecyclesAdapter extends RecyclerView.Adapter<OverviewRecyclesAdapter.OverViewHolder>
    {
        private final OverviewFragment parent;
        private final List<String> list;

        OverviewRecyclesAdapter(OverviewFragment parent, List<String> list)
        {
            this.parent = parent;
            this.list = list;
        }

        @Override
        public void onBindViewHolder(final OverViewHolder holder, final int position) {

            holder.playerName.setText(list.get(4*position));
            holder.fieldType.setText(list.get(4*position + 1));
            holder.fieldName.setText(list.get(4*position + 2));
            holder.fieldValue.setText(list.get(4*position + 3));
            holder.itemView.setTag(position);
        }

        @Override
        public OverViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.overview_item_content, parent, false);
            return new OverViewHolder(view);
        }

        @Override
        public int getItemCount() {return list.size() / 4;}

        class OverViewHolder extends RecyclerView.ViewHolder{
            final TextView playerName;
            final TextView fieldType;
            final TextView fieldName;
            final TextView fieldValue;

            OverViewHolder(View view){
                super(view);
                playerName = view.findViewById(R.id.textViewPlayerName);
                fieldType = view.findViewById(R.id.textViewFieldType);
                fieldName = view.findViewById(R.id.textViewFieldName);
                fieldValue = view.findViewById(R.id.textViewFieldValue);
            }
        }
    }

    public interface ClickListener {

        void onPositionClicked(int position);
    }
}
