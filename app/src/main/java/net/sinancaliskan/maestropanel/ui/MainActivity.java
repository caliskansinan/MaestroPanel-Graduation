package net.sinancaliskan.maestropanel.ui;

import android.content.Intent;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import net.sinancaliskan.maestropanel.R;
import net.sinancaliskan.maestropanel.fragments.Domains.DomainListFragment;
import net.sinancaliskan.maestropanel.fragments.Resellers.ResellerListFragment;
import net.sinancaliskan.maestropanel.fragments.Servers.ServerListFragment;
import net.sinancaliskan.maestropanel.models.pojo.UserObjects.User;
import net.sinancaliskan.maestropanel.utils.SharedInformation;

public class MainActivity extends AppCompatActivity {
    User model=null;                                                                                //define user model
    private Toolbar toolbar;                                                                        //define toolbar
    private BottomNavigationView bottomNavigationView;                                              //define bottomNavigationView
    private Fragment fragment;                                                                      //define fragment
    private int previousFragmentID=-1;                                                              //define for detect previous fragment
    private FragmentManager fragmentManager;                                                        //define fragmentManager
    private long lastBackPressed;                                                                   //define for lastBackPress time

    private DomainListFragment domains=new DomainListFragment();                                    //define and create DomainListFragment
    private ResellerListFragment resellers=new ResellerListFragment();                              //define and create ResellerListFragment
    private ServerListFragment servers=new ServerListFragment();                                    //define and create ServerListFragment
    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        if (lastBackPressed + 2000 > System.currentTimeMillis()) {
            super.onBackPressed();
        } else {
            Toast.makeText(getBaseContext(), getString(R.string.exit_message), Toast.LENGTH_SHORT).show();
        }
        lastBackPressed = System.currentTimeMillis();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {                                                 //create option menus on top
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override                                                                                       //set option menus
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id=item.getItemId();
        switch (id)
        {
            case R.id.action_settings:
                startActivity(new Intent(getApplicationContext(),SettingsActivity.class));          //show SettingsActivity
                return  true;
            case R.id.action_logout:
                startActivity(new Intent(getApplicationContext(),BarcodeActivity.class));           //show BarcodeActivity
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);                                                     //set layout file

        Bundle extras=getIntent().getExtras();                                                      //get previous activity transfered objects
        model= (User) extras.get("model");                                                          //cast object to user model

        toolbar= (Toolbar) findViewById(R.id.toolbar);                                              //set toolbar component
        setSupportActionBar(toolbar);                                                               //set setSupportActionBar with toolbar
        toolbar.setTitle(getString(R.string.app_name));                                             //set toolbar title
        bottomNavigationView= (BottomNavigationView) findViewById(R.id.navigation);                 //set bottomNavigationView component
        fragmentManager=getSupportFragmentManager();                                                //get getSupportFragmentManager and set fragmentManager
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {                        //create item select listener
                int id=item.getItemId();                                                            //get fragment dynamic id
                if (previousFragmentID!=id){
                    changeFragment(id);                                                             //call change fragment with fragment id
                }
                return true;
            }
        });
        changeFragment(-1);                                                                         //default fragment change

        Toast.makeText(getApplicationContext(), getString(R.string.welcome_message) + model.firstName + " " + model.lastName, Toast.LENGTH_SHORT).show();//show welcome message
    }
    private void changeFragment(int position){
        switch (position) {
            case R.id.domains:
                fragment=domains;
                break;
            case R.id.resellers:
                fragment=resellers;
                break;
            case R.id.servers:
                fragment=servers;
                break;
            default:
                fragment=domains;
                break;
        }
        if (fragment!=null){
            final FragmentTransaction transaction=fragmentManager.beginTransaction();               //start fragment transaction
            transaction.replace(R.id.content,fragment).commit();                                    //replace with fragment
            previousFragmentID=position;                                                            //set previousFragmentID change current fragment id
        }
    }
}