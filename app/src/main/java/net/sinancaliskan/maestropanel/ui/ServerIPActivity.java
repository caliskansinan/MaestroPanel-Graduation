package net.sinancaliskan.maestropanel.ui;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import net.sinancaliskan.maestropanel.R;
import net.sinancaliskan.maestropanel.adapters.ServerIPAdapter;
import net.sinancaliskan.maestropanel.models.pojo.ServerObjects.Server;

public class ServerIPActivity extends AppCompatActivity {
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private Toolbar toolbar;

    private ServerIPAdapter adapter;
    private Server model;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server_ip);
        Bundle extras=getIntent().getExtras();
        model= (Server) extras.get("model");

        toolbar= (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle(getString(R.string.server_ip_address) +" "+ model.name);

        swipeRefreshLayout= (SwipeRefreshLayout) findViewById(R.id.swipe);
        recyclerView= (RecyclerView) findViewById(R.id.recycler);
        adapter=new ServerIPAdapter(ServerIPActivity.this,swipeRefreshLayout,recyclerView,model);
    }
}
