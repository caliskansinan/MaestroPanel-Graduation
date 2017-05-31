package net.sinancaliskan.maestropanel.ui;

import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import net.sinancaliskan.maestropanel.R;
import net.sinancaliskan.maestropanel.adapters.SubdomainAdapter;
import net.sinancaliskan.maestropanel.dialog.SubdomainRegister;
import net.sinancaliskan.maestropanel.models.pojo.DomainObjects.Domain;

public class SubdomainManagement extends AppCompatActivity {
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private Toolbar toolbar;
    private FloatingActionButton fabAdd;

    private SubdomainAdapter adapter;
    private Domain model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subdomain_management);
        Bundle extras=getIntent().getExtras();
        model= (Domain) extras.get("model");

        toolbar= (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle(getString(R.string.subdomain_management) +" "+ model.name);

        swipeRefreshLayout= (SwipeRefreshLayout) findViewById(R.id.swipe);
        recyclerView= (RecyclerView) findViewById(R.id.recycler);
        adapter=new SubdomainAdapter(SubdomainManagement.this,swipeRefreshLayout,recyclerView,model);
        fabAdd= (FloatingActionButton) findViewById(R.id.fabAdd);
        //region domain create
        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SubdomainRegister(SubdomainManagement.this,adapter);
            }
        });
        //endregion
    }
}
