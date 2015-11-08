package com.hsdemo.auction;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.InputType;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AnticipateOvershootInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.NetworkImageView;
import com.hsdemo.auction.api.BiddingClient;
import com.hsdemo.auction.api.DataManager;
import com.hsdemo.auction.events.BidsRefreshedEvent;
import com.hsdemo.auction.models.AuctionItem;
import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.greenrobot.event.EventBus;

public class EditNoteActivity extends ActionBarActivity {

    @InjectView(R.id.itemslist)
    ListView itemsList;

    List<AuctionItem> allItems = new ArrayList<AuctionItem>();
    BaseAdapter adapter;

    boolean gotFirstBids;

    @InjectView(R.id.toolbar)
    Toolbar toolbar;

    @InjectView(R.id.base_tint_darken)
    View tint;

    @InjectView(R.id.mainprogress)
    ProgressBar progress;

    Handler handler = new Handler();

    DataManager data = DataManager.getInstance();
    boolean isInitializing = true;
    boolean bidding = false;
    boolean cardsUntouched = true;

    @InjectView(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;
    ActionBarDrawerToggle mDrawerToggle;

    @InjectView(R.id.left_drawer)
    View drawer;

    @InjectView(R.id.menu_all)
    View all;

    @InjectView(R.id.menu_nobids)
    View noBids;
    //////

    @InjectView(R.id.menu_myitems)
    View myItems;

    // @InjectView(R.id.add_item)
    // View myItems;

    @InjectView(R.id.menu_logout)
    View logout;

    @InjectView(R.id.menu_email)
    TextView userEmail;

    String listQuery = DataManager.QUERY_ALL;

    /** Called when the activity is first created. */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DisplayUtils.init(this);

        setContentView(R.layout.main);
        ButterKnife.inject(this);

        progress.setVisibility(View.VISIBLE);

        setSupportActionBar(toolbar);
        toolbar.setTitleTextAppearance(this, R.style.basefont_light);
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        tint.setVisibility(View.VISIBLE);

        getSupportActionBar().setTitle("All Items");

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            if (extras.containsKey("title"))
                getSupportActionBar().setTitle(extras.getString("title"));

            if (extras.containsKey("query"))
                listQuery = extras.getString("query");
        }

       setupDrawer();
        //commenting setup menu breaks drawer list
       setupMenu();
    }


/*
  ParseQuery<mrtest> query = ParseQuery.getQuery("mrtest");
  query.whereEqualTo("playerName", "Dan Stemkoski");
  query.findInBackground(new FindCallback<ParseObject>() {
    public void done(List<ParseObject> scoreList, ParseException e) {
      if (e == null) {
        Log.d("score", "Retrieved " + scoreList.size() + " scores");
      } else {
        Log.d("score", "Error: " + e.getMessage());
      }
    }
  });
  */


    //commenting on event causes system to crash
    public void onEvent(BidsRefreshedEvent event) {
        Log.i("TEST", "Received refresh event");
        if (isInitializing) {
            Log.i("TEST", "Init...");
            isInitializing = false;
           // setup();
        }

        if (!bidding) {
            allItems = data.getItemsMatchingQuery(listQuery, this);
            adapter.notifyDataSetChanged();
        }
    }

//commenting setup does not crash system but stops data from loading

    public void setupDrawer() {
        // ActionBarDrawerToggle is responsible for the menu indicator next to the icon, as well as making
        // the logo area open the drawer
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, (Toolbar) findViewById(R.id.toolbar), R.string.app_name, R.string.app_name) {
            @Override
            public void onDrawerClosed(View view) {}

            @Override
            public void onDrawerOpened(View drawerView) {}

            @Override
            public void onDrawerSlide(View v, float amt) {
                super.onDrawerSlide(v, amt);
            }
        };

        mDrawerLayout.setDrawerListener(mDrawerToggle);}

    public void setupMenu() {
        userEmail.setText(IdentityManager.getEmail(this));

        all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent listActivity = new Intent(EditNoteActivity.this, ItemListActivity.class);
                listActivity.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                listActivity.putExtra("title", "All Items");
                listActivity.putExtra("query", DataManager.QUERY_ALL);
                startActivity(listActivity);
            }
        });

        myItems.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(EditNoteActivity.this,
                        "this is the edit note activity", Toast.LENGTH_LONG).show();
        /*
        Intent listActivity = new Intent(ItemListActivity.this, ItemListActivity.class);
        listActivity.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        listActivity.putExtra("title", "My Items");
        listActivity.putExtra("query", DataManager.QUERY_MINE);
        startActivity(listActivity);
        */
            }
        });

        noBids.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Toast.makeText(ItemListActivity.this,
                //         "Your Message", Toast.LENGTH_LONG).show();

                Intent intent = new Intent(EditNoteActivity.this, EditNoteActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);

                //step 1 disable no bids
                //step 2 examine code code in crud example by runnning it noticeing new note
                //then noticing he used intent
                //integrate intents.



                Intent listActivity = new Intent(EditNoteActivity.this, EditNoteActivity.class);
                //  listActivity.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                // listActivity.putExtra("title", "Items with No Bids");
                // listActivity.putExtra("query", DataManager.QUERY_NOBIDS);
                startActivity(listActivity);



        /*
        Intent listActivity = new Intent(ItemListActivity.this, ItemListActivity.class);
        listActivity.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        listActivity.putExtra("title", "Items with No Bids");
        listActivity.putExtra("query", DataManager.QUERY_NOBIDS);
        startActivity(listActivity);
        */
                //step 1 disable bid option
                //step2 make input screen appear when when no bid clicked on
                //when inputting a new object in crud what class and xml is activated? investigate.

            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog dialog;
                AlertDialog.Builder alert = new AlertDialog.Builder(EditNoteActivity.this);

                alert.setTitle("Log Out");
                alert.setMessage("You sure?");

                alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        IdentityManager.setEmail("", EditNoteActivity.this);
                        IdentityManager.setName("", EditNoteActivity.this);
                        finish();
                    }
                });

                alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                    }
                });

                dialog = alert.show();
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.mainmenu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        data.beginBidCoverage(this);
        PushReceiver.clearAll();

        if (IdentityManager.getEmail(this).length() < 5) {
            startActivity(new Intent(this, LoginActivity.class));
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        data.endBidCoverage();
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }
}
