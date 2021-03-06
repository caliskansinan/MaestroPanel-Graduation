package net.sinancaliskan.maestropanel.ui;

import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import net.sinancaliskan.maestropanel.R;
import net.sinancaliskan.maestropanel.adapters.AliasAdapter;
import net.sinancaliskan.maestropanel.dialog.AliasRegister;
import net.sinancaliskan.maestropanel.models.pojo.DomainObjects.Domain;
                            //inherited AppCompatActivity
public class AliasManagement extends AppCompatActivity {
    private SwipeRefreshLayout swipeRefreshLayout;                                                  //define SwipeRefreshLayout
    private RecyclerView recyclerView;                                                              //define RecyclerView
    private FloatingActionButton fabAdd;                                                            //define FloatingActionButton
    private Toolbar toolbar;                                                                        //define toolbar

    private AliasAdapter adapter;                                                                   //define AliasAdapter
    private Domain model;                                                                           //define domain for reuse

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alias_management);                                         //set layout file
        Bundle extras=getIntent().getExtras();                                                      //get previous activity transferred objects
        model= (Domain) extras.get("model");                                                        //cast object to domain model

        toolbar= (Toolbar) findViewById(R.id.toolbar);                                              //set toolbar component
        setSupportActionBar(toolbar);                                                               //set setSupportActionBar with toolbar
        setTitle(getString(R.string.alias_management) +" "+ model.name);                                              //set toolbar title

        swipeRefreshLayout= (SwipeRefreshLayout) findViewById(R.id.swipe);                          //set swipeRefreshLayout component
        recyclerView= (RecyclerView) findViewById(R.id.recycler);                                   //set recyclerView component
        adapter=new AliasAdapter(AliasManagement.this,swipeRefreshLayout,recyclerView,model);       //set adapter with parameters
        fabAdd= (FloatingActionButton) findViewById(R.id.fabAdd);                                   //set FloatingActionButton component
        //region domain create
        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {                                                          //create fabAdd on click listener
                new AliasRegister(AliasManagement.this,adapter);                                    //create AliasRegister dialog with parameters
            }
        });
        //endregion
    }
}