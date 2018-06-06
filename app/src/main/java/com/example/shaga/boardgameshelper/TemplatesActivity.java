package com.example.shaga.boardgameshelper;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.restfb.FacebookClient;

import java.util.ArrayList;
import java.util.List;

public class TemplatesActivity extends AppCompatActivity {

    private static Toolbar toolbar;
    private static String playerStr = "players";
    private static String gamesStr = "games";
    private static String tempStr = "templates";
    private static List<String> tLKeys;
    private static List<GameTemplate> tL;
    private static String chosenTemplateId = null;
    private static GameTemplate chosenTemplate = null;
    private static int clickedTemplatePos = -1;
    private FacebookClient facebookClient;
    private static AccessToken accessToken;
    private static String userID = null;
    private static FirebaseDatabase fd;
    private static boolean playerFields;

    private static List<String> chosenTemplateStringListP;
    private static List<String> chosenTemplateIntListP;
    private static List<String> chosenTemplateBoolListP;
    private static List<String> chosenTemplateStringListS;
    private static List<String> chosenTemplateIntListS;
    private static List<String> chosenTemplateBoolListS;

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private static TempFragPagerAdapter mPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private static CustomViewPager mViewPager;
    private static ItemRecyclesViewAdapter mViewAdapter;

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

    private static void updateChosenTemplate(GameTemplate newTemplate, String newTemplateID)
    {
        chosenTemplate = newTemplate;
        chosenTemplateId = newTemplateID;
        chosenTemplate.Initialize();

        if(chosenTemplateStringListP == null)
            chosenTemplateStringListP = new ArrayList<String>();
        if(chosenTemplateIntListP == null)
            chosenTemplateIntListP = new ArrayList<String>();
        if(chosenTemplateBoolListP == null)
            chosenTemplateBoolListP = new ArrayList<String>();
        if(chosenTemplateStringListS == null)
            chosenTemplateStringListS = new ArrayList<String>();
        if(chosenTemplateIntListS == null)
            chosenTemplateIntListS = new ArrayList<String>();
        if(chosenTemplateBoolListS == null)
            chosenTemplateBoolListS = new ArrayList<String>();

        chosenTemplateStringListP.clear();
        for (String i : chosenTemplate.playerValueNamesText)
            chosenTemplateStringListP.add(i);
        chosenTemplateIntListP.clear();
        for (String i : chosenTemplate.playerValueNamesNumber)
            chosenTemplateIntListP.add(i);
        chosenTemplateBoolListP.clear();
        for (String i : chosenTemplate.playerValueNamesBool)
            chosenTemplateBoolListP.add(i);
        chosenTemplateStringListS.clear();
        for (String i : chosenTemplate.sharedValueNamesText)
            chosenTemplateStringListS.add(i);
        chosenTemplateIntListS.clear();
        for (String i : chosenTemplate.sharedValueNamesNumber)
            chosenTemplateIntListS.add(i);
        chosenTemplateBoolListS.clear();
        for (String i : chosenTemplate.sharedValueNamesBool)
            chosenTemplateBoolListS.add(i);
    }

    private boolean isPlayerFields()
    {
        int a = mViewPager.getCurrentItem();
        String curPage = Integer.toString(a);
        if(curPage.equals("1")) {
            return true;
        } else {
            return false;
        }
    }

    public void hideKeyboard(){
        InputMethodManager inputManager = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);

        inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);
    }

    public void textFieldPlus(View view)
    {
        FragmentSingleTemplate fragment = (FragmentSingleTemplate) mPagerAdapter.getCurrentFragment();
        EditText textField = (EditText) fragment.getView().findViewById(R.id.editTextTextField);
        if(textField == null)
            return;
        String name = textField.getText().toString();
        if(name == null || name.equals(""))
            return;
        if(isPlayerFields())
        {
            chosenTemplateStringListP.add(name);
            FragmentSingleTemplate.mStringFieldsRecyclesViewAdapter.notifyDataSetChanged();

        }
        else
        {
            chosenTemplateStringListS.add(name);
            FragmentSingleTemplate.mStringFieldsRecyclesViewAdapterShared.notifyDataSetChanged();
        }
        textField.setText("");
        textField.clearFocus();
        hideKeyboard();
    }

    public void numberFieldPlus(View view)
    {
        FragmentSingleTemplate fragment = (FragmentSingleTemplate) mPagerAdapter.getCurrentFragment();
        EditText textField = (EditText) fragment.getView().findViewById(R.id.editTextNumberField);
        if(textField == null)
            return;
        String name = textField.getText().toString();
        if(name == null || name.equals(""))
            return;
        if(isPlayerFields())
        {
            chosenTemplateIntListP.add(name);
            FragmentSingleTemplate.mIntFieldsRecyclesViewAdapter.notifyDataSetChanged();
        }
        else
        {
            chosenTemplateIntListS.add(name);
            FragmentSingleTemplate.mIntFieldsRecyclesViewAdapterShared.notifyDataSetChanged();
        }
        textField.setText("");
        textField.clearFocus();
        hideKeyboard();
    }

    public void boolenFieldPlus(View view)
    {
        FragmentSingleTemplate fragment = (FragmentSingleTemplate) mPagerAdapter.getCurrentFragment();
        EditText textField = (EditText) fragment.getView().findViewById(R.id.editTextBoolField);
        if(textField == null)
            return;
        String name = textField.getText().toString();
        if(name == null || name.equals(""))
            return;
        if(isPlayerFields())
        {
            chosenTemplateBoolListP.add(name);
            FragmentSingleTemplate.mBoolFieldsRecyclesViewAdapter.notifyDataSetChanged();
        }
        else
        {
            chosenTemplateBoolListS.add(name);
            FragmentSingleTemplate.mBoolFieldsRecyclesViewAdapterShared.notifyDataSetChanged();
        }
        textField.setText("");
        textField.clearFocus();
        hideKeyboard();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_templates);

        fd = FirebaseDatabase.getInstance();
        tL = new ArrayList<GameTemplate>();
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        AppBarLayout.LayoutParams params = (AppBarLayout.LayoutParams) toolbar.getLayoutParams();
        params.setScrollFlags(0);

        // Create the adapter that will return a fragment for each of the two
        // primary sections of the activity.
        mPagerAdapter = new TempFragPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (CustomViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mPagerAdapter);

        if(chosenTemplateId == null)
            mViewPager.disableScroll(true);

        getUserID();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                int a = mViewPager.getCurrentItem();
                String curPage = Integer.toString(a);
                boolean creatingTemplate;
                if(curPage.equals("0")) {
                    creatingTemplate = true;
                } else {
                    creatingTemplate = false;
                }
                clickPlusButton(creatingTemplate);
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                chosenTemplateId = null;
                chosenTemplate = null;
                clickedTemplatePos = -1;
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void clickPlusButton(boolean createTemplate)
    {
        if(createTemplate)
        {
            assert fd != null;
            assert userID != null;
            EditText editName = (EditText) findViewById(R.id.editTextNewTemplate);
            EditText editMinP = (EditText) findViewById(R.id.editTextMinPlayers);
            EditText editMaxP = (EditText) findViewById(R.id.editTextMaxPlayers);

            if(editName == null || editMinP == null || editMaxP == null)
                return;

            String gtName = editName.getText().toString();
            String minPS = editMinP.getText().toString();
            String maxPS = editMaxP.getText().toString();
            Integer minP;
            Integer maxP;
            if((minPS != null && !minPS.isEmpty()) && (maxPS != null && !maxPS.isEmpty())) {
                minP = Integer.parseInt(minPS);
                maxP = Integer.parseInt(maxPS);
            } else {
                Toast.makeText(this, "Wrong amount of players.", Toast.LENGTH_LONG).show();
                return;
            }
            if(gtName == null || gtName.equals("") || minP == null || maxP == null) {
                Toast.makeText(this, "Empty name.", Toast.LENGTH_LONG).show();
                return;
            }

            if(minP < 1 || maxP < 1 || minP > maxP) {
                Toast.makeText(this, "Wrong amount of players.", Toast.LENGTH_LONG).show();
                return;
            }

            GameTemplate newTemp = new GameTemplate();
            newTemp.name = gtName;
            newTemp.minPlayers = minP;
            newTemp.maxPlayers = maxP;
            newTemp.Initialize();
            DatabaseOperations.createTemplate(fd,userID,newTemp);
            editName.setText("");
            editName.clearFocus();
            editMaxP.setText("");
            editMaxP.clearFocus();
            editMinP.setText("");
            editMinP.clearFocus();
            InputMethodManager inputManager = (InputMethodManager)
                    getSystemService(Context.INPUT_METHOD_SERVICE);

            inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
        }
        else
        {
            if(chosenTemplateId == null || chosenTemplate == null)
                return;
            chosenTemplate.playerValueNamesText = chosenTemplateStringListP;
            chosenTemplate.playerValueNamesNumber = chosenTemplateIntListP;
            chosenTemplate.playerValueNamesBool = chosenTemplateBoolListP;
            chosenTemplate.sharedValueNamesText = chosenTemplateStringListS;
            chosenTemplate.sharedValueNamesNumber = chosenTemplateIntListS;
            chosenTemplate.sharedValueNamesBool = chosenTemplateBoolListS;
            chosenTemplate.Initialize();
            DatabaseOperations.updateTemplate(fd,chosenTemplateId,chosenTemplate);
        }
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    public void onNewClick(View v) {
        clickPlusButton(true);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class TemplatesListFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */

//        private static String playerStr = "players";
//        private static String gamesStr = "games";
//        private static String tempStr = "templates";
//        private static List<GameTemplate> tL;
        private static final String ARG_SECTION_NUMBER = "section_number";
        private static ItemRecyclesViewAdapter mItemsRecyclesViewAdapter;
        private static int nr = 0;

        public TemplatesListFragment() {
        }

        private void setupRecyclerView(@NonNull RecyclerView recyclerView, List<GameTemplate> list) {
            mItemsRecyclesViewAdapter = new ItemRecyclesViewAdapter(this, list);
            recyclerView.setAdapter(mItemsRecyclesViewAdapter);
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static TemplatesListFragment newInstance(int sectionNumber) {
            TemplatesListFragment fragment = new TemplatesListFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_templates, container, false);

            View tempListView = rootView.findViewById(R.id.temp_list);
            assert tempListView != null;

            if(tL == null)
                tL = new ArrayList<GameTemplate>();

            setupRecyclerView((RecyclerView) tempListView, tL);

            attachUserTemplatesListeners(TemplatesActivity.fd, TemplatesActivity.userID);
//            textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
            return rootView;
        }

        private void attachUserTemplatesListeners(final FirebaseDatabase fd, String userID)
        {
            if(fd == null || userID == null)
            {
                Log.d("templates", "Non-initialized database or id.");
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
                                mItemsRecyclesViewAdapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Log.d("templates", "Error.");
                            }
                        });
                        ++tS;
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.d("templates", "Error.");
                }
            });
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class TempFragPagerAdapter extends FragmentPagerAdapter {

        private Fragment mCurrentFragment;
        private SparseArray<Fragment> registeredFragments = new SparseArray<>();

        public TempFragPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        public Fragment getCurrentFragment()
        {
            return mCurrentFragment;
        }

        public Fragment getRegisteredFragment(int position) {
            return registeredFragments.get(position);
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
                title = "Templates";
                if(chosenTemplateId == null)
                    mViewPager.disableScroll(true);
            }
            getSupportActionBar().setTitle(title);
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a TemplatesListFragment (defined as a static inner class below).
            Fragment ret = null;
            if(position == 0) {
                return TemplatesListFragment.newInstance(position);
            }
            else if(position == 1) {
                playerFields = true;
                return FragmentSingleTemplate.newInstance(position);
            }
            else if(position == 2) {
                playerFields = false;
                return FragmentSingleTemplate.newInstance(position);
            }
            return ret;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            Fragment fragment = (Fragment) super.instantiateItem(container, position);
            registeredFragments.put(position, fragment);
            return fragment;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            registeredFragments.remove(position);
            super.destroyItem(container, position, object);
        }

        @Override
        public int getCount() {
            return 3;
        }
    }

    public static class FragmentSingleTemplate extends Fragment {

        private static final String ARG_SECTION_NUMBER = "section_number";

        private static FieldRecyclesViewAdapter mStringFieldsRecyclesViewAdapter;
        private static FieldRecyclesViewAdapter mStringFieldsRecyclesViewAdapterShared;
        private static FieldRecyclesViewAdapter mIntFieldsRecyclesViewAdapter;
        private static FieldRecyclesViewAdapter mIntFieldsRecyclesViewAdapterShared;
        private static FieldRecyclesViewAdapter mBoolFieldsRecyclesViewAdapter;
        private static FieldRecyclesViewAdapter mBoolFieldsRecyclesViewAdapterShared;

        private FragmentSingleTemplate.OnFragmentInteractionListener mListener;

        private boolean playerValues;

        public FragmentSingleTemplate() {
            // Required empty public constructor
        }

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
//         * @param param1 Parameter 1.
//         * @param param2 Parameter 2.
//         * @return A new instance of fragment FragmentSingleTemplate.
         */
//         TODO: Rename and change types and number of parameters

//        public static FragmentSingleTemplate newInstance(String param1, String param2) {
//            FragmentSingleTemplate fragment = new FragmentSingleTemplate();
//            Bundle args = new Bundle();
//            args.putString(ARG_PARAM1, param1);
//            args.putString(ARG_PARAM2, param2);
//            fragment.setArguments(args);
//            return fragment;
//        }

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

        public static FragmentSingleTemplate newInstance(int sectionNumber) {
            FragmentSingleTemplate fragment = new FragmentSingleTemplate();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        private static void notifyDataSets()
        {
            mStringFieldsRecyclesViewAdapter.notifyDataSetChanged();
            mIntFieldsRecyclesViewAdapter.notifyDataSetChanged();
            mBoolFieldsRecyclesViewAdapter.notifyDataSetChanged();
            if(mStringFieldsRecyclesViewAdapterShared != null)
                mStringFieldsRecyclesViewAdapterShared.notifyDataSetChanged();
            if(mIntFieldsRecyclesViewAdapterShared != null)
                mIntFieldsRecyclesViewAdapterShared.notifyDataSetChanged();
            if(mBoolFieldsRecyclesViewAdapterShared != null)
                mBoolFieldsRecyclesViewAdapterShared.notifyDataSetChanged();
        }

        private void setupStringRecyclerView(@NonNull RecyclerView recyclerView, List<String> list) {
            if(playerValues) {
                mStringFieldsRecyclesViewAdapter = new FieldRecyclesViewAdapter(this, list);
                recyclerView.setAdapter(mStringFieldsRecyclesViewAdapter);
            }
            else {
                mStringFieldsRecyclesViewAdapterShared = new FieldRecyclesViewAdapter(this, list);
                recyclerView.setAdapter(mStringFieldsRecyclesViewAdapterShared);
            }
        }

        private void setupIntRecyclerView(@NonNull RecyclerView recyclerView, List<String> list) {
            if(playerValues) {
                mIntFieldsRecyclesViewAdapter = new FieldRecyclesViewAdapter(this, list);
                recyclerView.setAdapter(mIntFieldsRecyclesViewAdapter);
            }
            else {
                mIntFieldsRecyclesViewAdapterShared = new FieldRecyclesViewAdapter(this, list);
                recyclerView.setAdapter(mIntFieldsRecyclesViewAdapterShared);
            }
        }

        private void setupBoolRecyclerView(@NonNull RecyclerView recyclerView, List<String> list) {
            if(playerValues) {
                mBoolFieldsRecyclesViewAdapter = new FieldRecyclesViewAdapter(this, list);
                recyclerView.setAdapter(mBoolFieldsRecyclesViewAdapter);
            }
            else {
                mBoolFieldsRecyclesViewAdapterShared = new FieldRecyclesViewAdapter(this, list);
                recyclerView.setAdapter(mBoolFieldsRecyclesViewAdapterShared);
            }
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_single_template, container, false);

            if(chosenTemplate == null)
            {
                chosenTemplate = new GameTemplate();
            }
            if(chosenTemplateId == null)
            {
                chosenTemplateId = new String();
            }
            updateChosenTemplate(chosenTemplate, chosenTemplateId);

            View stringListView = rootView.findViewById(R.id.string_temp_field_list);
            View intListView = rootView.findViewById(R.id.int_temp_field_list);
            View boolListView = rootView.findViewById(R.id.bool_temp_field_list);
            assert stringListView != null;
            assert intListView != null;
            assert boolListView != null;
            if(playerValues) {
                setupStringRecyclerView((RecyclerView) stringListView, chosenTemplateStringListP);
                setupIntRecyclerView((RecyclerView) intListView, chosenTemplateIntListP);
                setupBoolRecyclerView((RecyclerView) boolListView, chosenTemplateBoolListP);
            } else {
                setupStringRecyclerView((RecyclerView) stringListView, chosenTemplateStringListS);
                setupIntRecyclerView((RecyclerView) intListView, chosenTemplateIntListS);
                setupBoolRecyclerView((RecyclerView) boolListView, chosenTemplateBoolListS);
            }
            return rootView;
        }

        // TODO: Rename method, update argument and hook method into UI event
        public void onButtonPressed(Uri uri) {
            if (mListener != null) {
                mListener.onFragmentInteraction(uri);
            }
        }

        @Override
        public void onAttach(Context context) {
            super.onAttach(context);
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
        }

//        @Override
//        public void onResume(){
//            super.onResume();
//            String title;
//            if(playerValues)
//                title = "Player Values";
//            else
//                title = "Shared Values";
//            getActivity().getActionBar().setDisplayShowTitleEnabled(true);
//
//            getActivity().getActionBar().setTitle(title);
//        }

        @Override
        public void onDetach() {
            super.onDetach();
            mListener = null;
        }

        /**
         * This interface must be implemented by activities that contain this
         * fragment to allow an interaction in this fragment to be communicated
         * to the activity and potentially other fragments contained in that
         * activity.
         * <p>
         * See the Android Training lesson <a href=
         * "http://developer.android.com/training/basics/fragments/communicating.html"
         * >Communicating with Other Fragments</a> for more information.
         */
        public interface OnFragmentInteractionListener {
            // TODO: Update argument type and name
            void onFragmentInteraction(Uri uri);
        }
    }

    public static class ItemRecyclesViewAdapter extends RecyclerView.Adapter<ItemRecyclesViewAdapter.TemplateViewHolder>
    {

        private final TemplatesListFragment parent;
        private final List<GameTemplate> templates;
//        private int row_index = -1;
//        private final List<DummyContent.DummyItem> mValues;
        private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                GameTemplate item = (GameTemplate) view.getTag();
//                int position = (int) view.getTag();
//                chosenTemplateId = position;
//                chosenTemplate = templates.get(position);
//                FragmentSingleTemplate.notifyDataSets();
//                Context context = view.getContext();
//                chosenTemplate = item;
//                Intent intent = new Intent(context, EventDetailActivity.class);
//                intent.putExtra(EventDetailFragment.ARG_ITEM_ID, item.id);

//                context.startActivity(intent);

            }
        };

        ItemRecyclesViewAdapter(TemplatesListFragment parent, List<GameTemplate> templates)
        {
            this.parent = parent;
//            this.mValues = templates;
            this.templates = templates;
        }

        @Override
        public TemplateViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.template_item_content, parent, false);
            return new TemplateViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final TemplateViewHolder holder,final int position) {
            holder.templateNameView.setText(templates.get(position).name);
            if(templates.get(position).sessions != null)
                holder.sessionsInfoView.setText("Sessions: " + templates.get(position).sessions.size());

//            holder.itemView.setTag(templates.get(position));
            holder.itemView.setTag(position);
//            holder.templateNameView.setText(mValues.get(position).id);
//            holder.sessionsInfoView.setText(mValues.get(position).content);
//
//            holder.itemView.setTag(mValues.get(position));

//            holder.itemView.setOnClickListener(mOnClickListener);
            holder.mLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = (int) view.getTag();
                    chosenTemplateId = tLKeys.get(position);
                    chosenTemplate = templates.get(position);
                    updateChosenTemplate(chosenTemplate,chosenTemplateId);
                    FragmentSingleTemplate.notifyDataSets();
                    clickedTemplatePos = position;
                    mViewPager.disableScroll(false);
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

    public static class FieldRecyclesViewAdapter extends RecyclerView.Adapter<FieldRecyclesViewAdapter.FieldViewHolder>
    {
        private final FragmentSingleTemplate parent;
        private final List<String> fields;

        FieldRecyclesViewAdapter(FragmentSingleTemplate parent, List<String> fields)
        {
            this.parent = parent;
            this.fields = fields;
        }

        @Override
        public FieldViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
        {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.template_single_field, parent, false);
            return new FieldViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final FieldViewHolder holder, final int position)
        {
            String str = fields.get(position);
            holder.mFieldTextView.setText(str);
            holder.itemView.setTag(str);
        }

        @Override
        public int getItemCount() {return fields.size();}

        class FieldViewHolder extends RecyclerView.ViewHolder
        {
            final TextView mFieldTextView;

            FieldViewHolder(View view)
            {
                super(view);
                mFieldTextView = (TextView) view.findViewById(R.id.template_field);
            }
        }
    }

}
