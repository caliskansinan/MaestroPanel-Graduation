package net.sinancaliskan.maestropanel.ui;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fasterxml.jackson.databind.ObjectMapper;

import net.sinancaliskan.maestropanel.API;
import net.sinancaliskan.maestropanel.R;
import net.sinancaliskan.maestropanel.interfaces.IServer;
import net.sinancaliskan.maestropanel.models.Reply;
import net.sinancaliskan.maestropanel.models.pojo.ServerObjects.Server;
import net.sinancaliskan.maestropanel.models.pojo.ServerObjects.ServerResource;
import net.sinancaliskan.maestropanel.utils.SharedInformation;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ServerActivity extends AppCompatActivity {
    private ProgressDialog progressDialog;

    private IServer service;
    private Server model;
    private Toolbar toolbar;

    private List<ServerResource> models=new ArrayList<>();
    private TextView version,host,serverName,processor,operatingSystem;
    private LinearLayout linearLayout=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server);
        Bundle extras=getIntent().getExtras();
        model= (Server) extras.get("model");

        toolbar= (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle(model.name);

        version= (TextView) findViewById(R.id.txtVersion);
        version.setText(model.version);
        host= (TextView) findViewById(R.id.txtHostName);
        host.setText(model.host);
        serverName= (TextView) findViewById(R.id.txtServerName);
        serverName.setText(model.computerName);
        processor= (TextView) findViewById(R.id.txtProcessor);
        processor.setText(model.cpu);
        operatingSystem= (TextView) findViewById(R.id.txtOperatingSystem);
        operatingSystem.setText(model.operatingSystem);

        progressDialog=new ProgressDialog(ServerActivity.this);
        progressDialog.setMessage(getString(R.string.server)+ getString(R.string.isLoading));
        progressDialog.setCancelable(false);
        progressDialog.show();

        linearLayout= (LinearLayout) findViewById(R.id.resources);

        get(model.name);
    }
    private void get(String name){
        service = API.getClient().create(IServer.class);
        String key= SharedInformation.getData("serverKey");
        Call<Reply> call=service.GetResources(key,name);
        call.enqueue(new Callback<Reply>() {
            @Override
            public void onResponse(Call<Reply> call, Response<Reply> response) {
                ArrayList<ServerResource> objects= ((ArrayList<ServerResource>) response.body().details);
                ObjectMapper mapper = new ObjectMapper();
                LinearLayout.LayoutParams paramLabel = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                paramLabel.setMargins(15,15,15,15);
                LinearLayout.LayoutParams paramValue = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                paramValue.setMargins(100,15,15,15);
                //labels
                for (int i=0;i<objects.size();i++){
                    ServerResource model=mapper.convertValue(objects.get(i),ServerResource.class);
                    TextView label,value;
                    LinearLayout row=new LinearLayout(ServerActivity.this);
                    row.setOrientation(LinearLayout.HORIZONTAL);
                    //labels
                    LinearLayout labels=new LinearLayout(ServerActivity.this);
                    labels.setOrientation(LinearLayout.VERTICAL);
                    labels.setLayoutParams(paramLabel);

                    label=new TextView(ServerActivity.this);
                    label.setText(getString(R.string.resource));
                    label.setTextSize(20);
                    label.setTextColor(getResources().getColor(R.color.accent_blue));
                    labels.addView(label);

                    label=new TextView(ServerActivity.this);
                    label.setText(getString(R.string.total));
                    label.setTextSize(20);
                    label.setTextColor(getResources().getColor(R.color.accent_blue));
                    labels.addView(label);

                    label=new TextView(ServerActivity.this);
                    label.setText(getString(R.string.total));
                    label.setTextSize(20);
                    label.setTextColor(getResources().getColor(R.color.accent_blue));
                    labels.addView(label);

                    //values
                    LinearLayout values=new LinearLayout(ServerActivity.this);
                    values.setOrientation(LinearLayout.VERTICAL);
                    values.setLayoutParams(paramLabel);

                    value=new TextView(ServerActivity.this);
                    value.setText(model.resourceName);
                    value.setTextSize(20);
                    values.addView(value);

                    value=new TextView(ServerActivity.this);
                    value.setText(model.total);
                    value.setTextSize(20);
                    values.addView(value);

                    value=new TextView(ServerActivity.this);
                    value.setText(model.used);
                    value.setTextSize(20);
                    values.addView(value);

                    row.addView(labels,paramLabel);
                    row.addView(values,paramValue);

                    linearLayout.addView(row,paramValue);
                }

                if (progressDialog!=null)
                    progressDialog.dismiss();
            }
            @Override
            public void onFailure(Call<Reply> call, Throwable throwable) {
                call.cancel();
                Toast.makeText(null, getString(R.string.somethingIsWrong), Toast.LENGTH_SHORT).show();
                if (progressDialog!=null)
                    progressDialog.dismiss();
            }
        });
    }
}
