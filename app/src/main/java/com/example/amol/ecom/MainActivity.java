package com.example.amol.ecom;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.example.amol.ecom.login.Login;
import com.example.amol.ecom.login.SessionManager;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    DrawerLayout drawer;
    FrameLayout frameLayout;
    NavigationView navigationView;
    ActionBarDrawerToggle toogle;
    Fragment fragment;
    NetworkImageView networkImageView;
    public TextView nametextview, emailtextview;
    private TextView TextViewPrices;
    private Toolbar toolbaritems;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        frameLayout = (FrameLayout) findViewById(R.id.frame_container);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        toogle = new ActionBarDrawerToggle(MainActivity.this, drawer, R.string.open_Drawer, R.string.close_Drawer);
        drawer.setDrawerListener(toogle);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setItemIconTintList(null);
        View headerLayout = navigationView.inflateHeaderView(R.layout.header_layout);


        ImageLoader imageLoader = AppController.getInstance().getImageLoader();
        networkImageView = (NetworkImageView) headerLayout.findViewById(R.id.circleImageView);
        networkImageView.setImageUrl(SessionManager.personPicture, imageLoader);
        nametextview = (TextView) headerLayout.findViewById(R.id.name_textview);
        nametextview.setText(SessionManager.personName);
        emailtextview = (TextView) headerLayout.findViewById(R.id.email_textview);
        emailtextview.setText(SessionManager.personEmail);
        //networkImageView.setImageURI(SessionManager.personPhoto);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        emailtextview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.frame_container, new Profile()).commit();
                getSupportActionBar().setTitle("Profile");
                drawer.closeDrawers();
            }
        });

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.frame_container, new HomeFragment()).commit();
    }

    @Override
    public void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        toogle.syncState();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }
        if (id == android.R.id.home) {
            if (drawer.isDrawerOpen(navigationView)) {
                drawer.closeDrawer(navigationView);
            } else drawer.openDrawer(navigationView);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        item.setCheckable(true);

        if (id == R.id.home) {
            fragment = new HomeFragment();
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.frame_container, fragment);
            getSupportActionBar().setTitle("Home");
            ft.commit();
        }
        if (id == R.id.profile) {
            fragment = new Profile();
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.frame_container, fragment);
            getSupportActionBar().setTitle("Profile");
            ft.commit();
        }
        if (id == R.id.notify) {
            fragment = new Notification();
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.frame_container, fragment);
            getSupportActionBar().setTitle("Notification");
            ft.commit();
        }
        if (id == R.id.feedback) {
            Intent i = new Intent(Intent.ACTION_SEND);
            i.setData(Uri.parse("mailto:"));
            String[] to = {"wangate.amol@gmail.com", "wangate.amol@yahoo.com"};
            i.putExtra(Intent.EXTRA_EMAIL, to);
            i.putExtra(Intent.EXTRA_TEXT, "");
            i.putExtra(Intent.EXTRA_SUBJECT, "");
            i.setType("message/rfc822");
            startActivity(i);

        }
        if (id == R.id.share) {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.putExtra(Intent.EXTRA_TEXT, "http://");
            intent.setType("text/plain");
            Intent chooser = Intent.createChooser(intent, "Share Using");
            startActivity(chooser);
        }
        if (id == R.id.rateus) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("market://"));
            Intent chooser = Intent.createChooser(intent, "Complete Action Using");
            startActivity(chooser);

        }
        if (id == R.id.aboutus) {
            // Intent intent=new Intent(MainActivity.this,About.class);
            // startActivity(intent);

        }

        if (id == R.id.logout) {
            logout();
        }
        drawer.closeDrawers();
        return true;
    }


    private void logout() {

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setMessage("Are You Sure You Want to Logout ?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                SharedPreferences sharedPreferences = MainActivity.this.getSharedPreferences(SessionManager.SHARED_PREF_NAME, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean(SessionManager.LOGGEDIN_SHARED_PREF, false);
                editor.putString(SessionManager.EMAIL_SHARED_PREF, "");
                editor.apply();
                Intent intent = new Intent(MainActivity.this, Login.class);
                startActivity(intent);
            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.setTitle("Logout");
        alertDialog.show();
    }
}
