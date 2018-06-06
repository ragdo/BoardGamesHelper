package com.example.shaga.boardgameshelper;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import android.support.v7.app.ActionBar;
import android.view.MenuItem;

import com.example.shaga.boardgameshelper.dummy.DummyContent;

import java.util.Arrays;
import java.util.List;

import static android.support.v4.app.NavUtils.navigateUpFromSameTask;

import com.facebook.AccessToken;
import com.facebook.AccessTokenSource;
import com.restfb.Connection;
import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.Parameter;
import com.restfb.types.Event;
import com.restfb.types.Page;
import com.restfb.Version;
import com.restfb.exception.FacebookNetworkException;

import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import org.json.JSONObject;

/**
 * An activity representing a list of Events. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link EventDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class EventListActivity extends AppCompatActivity {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;
    private FacebookClient facebookClient;
    private AccessToken accessToken;
    private String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_list);

        accessToken = AccessToken.getCurrentAccessToken();
        if(accessToken != null) {
            if(!accessToken.isExpired()) {
                userID = accessToken.getUserId();
            }
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        if (findViewById(R.id.event_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }

        View recyclerView = findViewById(R.id.event_list);
        assert recyclerView != null;
        setupRecyclerView((RecyclerView) recyclerView);

        fetchData();
    }

    private void fetchData()
    {
//        if(facebookClient != null)
//        {
//            try {
//                facebookClient.fetchObject("cocacola", Page.class, Parameter.with("fields", "fan_count"));
//            }
//            catch(FacebookNetworkException ex){
//
//            }
//        }
//        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("email", "user_friends"));

        GraphRequest request1 = GraphRequest.newMeRequest(
                accessToken,
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(
                            JSONObject object,
                            GraphResponse response) {
                        Log.d("graphU", object.toString());
                    }
                });
        Bundle parameters1 = new Bundle();
        parameters1.putString("fields", "id,name,link,email");
        request1.setParameters(parameters1);
        request1.executeAsync();

        GraphRequest request = GraphRequest.newGraphPathRequest(
                accessToken,
                "/search",
                new GraphRequest.Callback() {
                    @Override
                    public void onCompleted(GraphResponse response) {
                       JSONObject object = response.getJSONObject();
                       Log.d("graphS", response.toString());
                    }
                });

        Bundle parameters = new Bundle();
        parameters.putString("q", "cafe");
        parameters.putString("type", "place");
        request.setParameters(parameters);
        request.executeAsync();

//        GraphRequest request2 = GraphRequest.newGraphPathRequest(
//                accessToken,
//                "/104006329635804",
//                new GraphRequest.Callback() {
//                    @Override
//                    public void onCompleted(GraphResponse response) {
//                        JSONObject object = response.getJSONObject();
//                        Log.d("graph", response.toString());
//
//                    }
//                });
//
//        request2.executeAsync();

        GraphRequest request3 = GraphRequest.newMeRequest(
                accessToken,
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        Log.d("graphF", object.toString());
                    }
                });

        Bundle parameters3 = new Bundle();
        parameters3.putString("fields", "id,name,friends{id}");
        request3.setParameters(parameters3);
        request3.executeAsync();

//        String PageAccessToken = "EAAcSEZAmcy5sBAHhr2ZBjB8rvUZCzstZBY0KZBKPuPuLPapJUuaX7wy0AhvKZAJiTxHhafgK8jeZAIKCsEIz0ZAlvQQ65QDYU6KLe6R7RoJBhbSDT4Jf0l5H4vhfl2ADmSO2mWEX8sHPh5k04VK1GZAJlC3HiLL34x5IZD";
//        AccessToken customToken = new AccessToken(PageAccessToken, AccessToken.getCurrentAccessToken().getApplicationId(), AccessToken.getCurrentAccessToken().getUserId(),
//                AccessToken.getCurrentAccessToken().getPermissions(),
//                null, AccessTokenSource.FACEBOOK_APPLICATION_NATIVE, AccessToken.getCurrentAccessToken().getExpires(), null);
//
//        GraphRequest request4 = GraphRequest.newGraphPathRequest(
//                customToken,
//                "/search",
//                new GraphRequest.Callback() {
//                    @Override
//                    public void onCompleted(GraphResponse response) {
//                        JSONObject object = response.getJSONObject();
//                        Log.d("graph", response.toString());
//                    }
//                });
//
//        Bundle parameters2 = new Bundle();
//        parameters2.putString("q", "cafe");
//        parameters2.putString("type", "place");
//        request4.setParameters(parameters2);
//        request4.executeAsync();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button. In the case of this
            // activity, the Up button is shown. Use NavUtils to allow users
            // to navigate up one level in the application structure. For
            // more details, see the Navigation pattern on Android Design:
            //
            // http://developer.android.com/design/patterns/navigation.html#up-vs-back
            //
            navigateUpFromSameTask(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(this, DummyContent.ITEMS, mTwoPane));
    }

    public static class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        private final EventListActivity mParentActivity;
        private final List<DummyContent.DummyItem> mValues;
        private final boolean mTwoPane;
        private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DummyContent.DummyItem item = (DummyContent.DummyItem) view.getTag();
                if (mTwoPane) {
                    Bundle arguments = new Bundle();
                    arguments.putString(EventDetailFragment.ARG_ITEM_ID, item.id);
                    EventDetailFragment fragment = new EventDetailFragment();
                    fragment.setArguments(arguments);
                    mParentActivity.getSupportFragmentManager().beginTransaction()
                            .replace(R.id.event_detail_container, fragment)
                            .commit();
                } else {
                    Context context = view.getContext();
                    Intent intent = new Intent(context, EventDetailActivity.class);
                    intent.putExtra(EventDetailFragment.ARG_ITEM_ID, item.id);

                    context.startActivity(intent);
                }
            }
        };

        SimpleItemRecyclerViewAdapter(EventListActivity parent,
                                      List<DummyContent.DummyItem> items,
                                      boolean twoPane) {
            mValues = items;
            mParentActivity = parent;
            mTwoPane = twoPane;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.event_list_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            holder.mIdView.setText(mValues.get(position).id);
            holder.mContentView.setText(mValues.get(position).content);

            holder.itemView.setTag(mValues.get(position));
            holder.itemView.setOnClickListener(mOnClickListener);
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            final TextView mIdView;
            final TextView mContentView;

            ViewHolder(View view) {
                super(view);
                mIdView = (TextView) view.findViewById(R.id.id_text);
                mContentView = (TextView) view.findViewById(R.id.content);
            }
        }
    }
}
