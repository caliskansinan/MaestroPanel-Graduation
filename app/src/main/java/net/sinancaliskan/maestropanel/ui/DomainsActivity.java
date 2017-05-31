package net.sinancaliskan.maestropanel.ui;

import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import net.sinancaliskan.maestropanel.R;
import net.sinancaliskan.maestropanel.adapters.DomainAdapter;
import net.sinancaliskan.maestropanel.adapters.ResellerDomainAdapter;
import net.sinancaliskan.maestropanel.dialog.DomainRegister;
import net.sinancaliskan.maestropanel.dialog.ResellerDomainRegister;
import net.sinancaliskan.maestropanel.models.pojo.ResellerObjects.Reseller;

public class DomainsActivity extends AppCompatActivity {
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private Toolbar toolbar;
    private FloatingActionButton fabAdd;

    private ResellerDomainAdapter adapter;
    private Reseller model;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_domains);
        Bundle extras=getIntent().getExtras();                                                      //get previous activity transfered objects
        this.model= (Reseller) extras.get("model");                                                        //cast object to domain model

        toolbar= (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle(getString(R.string.domain_management) +" "+ model.username);

        swipeRefreshLayout= (SwipeRefreshLayout) findViewById(R.id.swipe);                     //set swipeRefreshLayout component
        recyclerView= (RecyclerView) findViewById(R.id.recycler);                              //set recyclerView component
        adapter=new ResellerDomainAdapter(DomainsActivity.this,swipeRefreshLayout,recyclerView,model.username);                    //set adapter with parameters
        fabAdd= (FloatingActionButton) findViewById(R.id.fabAdd);                              //set FloatingActionButton component
        //region domain create
        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {                                                          //create fabAdd on click listener
                new ResellerDomainRegister(DomainsActivity.this,adapter,model.username);                                //create DomainRegister dialog with parameters
            }
        });
        //endregion
    }
}
