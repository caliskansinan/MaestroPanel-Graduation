package net.sinancaliskan.maestropanel.ui;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import net.sinancaliskan.maestropanel.R;
import net.sinancaliskan.maestropanel.adapters.ResellerIPAdapter;
import net.sinancaliskan.maestropanel.models.pojo.ResellerObjects.Reseller;

public class ResellerIPManagement extends AppCompatActivity {
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private Toolbar toolbar;

    private ResellerIPAdapter adapter;
    private Reseller model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reseller_ip_management);
        Bundle extras=getIntent().getExtras();                                                      //get previous activity transfered objects
        this.model= (Reseller) extras.get("model");                                                        //cast object to domain model

        toolbar= (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle(getString(R.string.ip_management) +" "+ model.username);

        swipeRefreshLayout= (SwipeRefreshLayout) findViewById(R.id.swipe);                     //set swipeRefreshLayout component
        recyclerView= (RecyclerView) findViewById(R.id.recycler);                              //set recyclerView component
        adapter=new ResellerIPAdapter(ResellerIPManagement.this,swipeRefreshLayout,recyclerView,model);                    //set adapter with parameters

    }
}
