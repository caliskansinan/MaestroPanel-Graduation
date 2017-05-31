package net.sinancaliskan.maestropanel.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import net.sinancaliskan.maestropanel.API;
import net.sinancaliskan.maestropanel.R;
import net.sinancaliskan.maestropanel.dialog.ResellerPasswordChange;
import net.sinancaliskan.maestropanel.interfaces.IDomain;
import net.sinancaliskan.maestropanel.interfaces.IReseller;
import net.sinancaliskan.maestropanel.models.Reply;
import net.sinancaliskan.maestropanel.models.pojo.DomainObjects.Domain;
import net.sinancaliskan.maestropanel.models.pojo.ResellerObjects.Reseller;
import net.sinancaliskan.maestropanel.utils.Notification;
import net.sinancaliskan.maestropanel.utils.SharedInformation;

import java.text.SimpleDateFormat;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ResellerActivity extends AppCompatActivity {
    final String key= SharedInformation.getData("serverKey");                                       //get user api key
    private IReseller service =null;                                                                //define reseller service

    private ProgressDialog progressDialog;                                                          //define progressDialog
    private Reseller model;                                                                         //define domain model
    private Intent intent=null;                                                                     //define Intent
    private Toolbar toolbar;
    private Button start,stop,changePassword,domain,ip;                                             //define buttons
    private TextView owner,username,expirationDate,email,organization;                              //define textviews

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reseller);
        Bundle extras=getIntent().getExtras();                                                      //get previous activity transfered objects
        model= (Reseller) extras.get("model");                                                      //cast object to reseller model

        toolbar= (Toolbar) findViewById(R.id.toolbar);                                              //set toolbar component
        setSupportActionBar(toolbar);                                                               //set setSupportActionBar with toolbar
        setTitle(model.firstName + " " + model.lastName);                                           //set title with reseller name

        progressDialog=new ProgressDialog(ResellerActivity.this);                                   //creating new progressDialog for showing loading actions
        progressDialog.setMessage(getString(R.string.reseller)+getString(R.string.isLoading));        //setting message on resource file
        progressDialog.setCancelable(false);                                                        //assign progressDialog is not cancelable
        progressDialog.show();

        if (model!=null){
            owner= (TextView) findViewById(R.id.txtOwner);
            owner.setText(model.firstName +" " +model.lastName);
            username= (TextView) findViewById(R.id.txtUsername);
            username.setText(model.username);
            expirationDate= (TextView) findViewById(R.id.txtExpirationDate);
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
            String date=model.expirationDate.replace("/Date(","").replace(")/","");
            expirationDate.setText(dateFormat.format(Long.valueOf(date)));
            email= (TextView) findViewById(R.id.txtEmail);
            email.setText(model.email.toString());
            organization= (TextView) findViewById(R.id.txtOrganization);
            //organization.setText(model.organization);

            start= (Button) findViewById(R.id.start);
            start.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    start.setEnabled(false);                                                            //disable buttons
                    stop.setEnabled(false);
                    Call<Reply> call=null;                                                              //define call
                    service = API.getClient().create(IReseller.class);                                    //crete service for domain
                    call=service.Start(key,model.username);                                                 //create service call method with parameters
                    call.enqueue(new Callback<Reply>() {                                                //adding queue to callback
                        @Override
                        public void onResponse(Call<Reply> call, Response<Reply> response) {            //when callback return response
                            try {                                                                       //show notification user action is completed
                                Notification.Show(ResellerActivity.this,R.drawable.ic_supervisor_account_white_24dp,R.string.reseller_started,response.body().message);
                            }
                            catch (Exception e){                                                        //show notification user action is fail
                                Notification.Show(ResellerActivity.this,R.drawable.ic_supervisor_account_white_24dp,R.string.reseller_started,e.getMessage());
                            }
                            start.setEnabled(true);                                                             //enable buttons
                            stop.setEnabled(true);
                        }
                        @Override
                        public void onFailure(Call<Reply> call, Throwable t) {
                            call.cancel();
                            Toast.makeText(ResellerActivity.this,getString(R.string.somethingIsWrong), Toast.LENGTH_SHORT).show();
                            start.setEnabled(true);                                                             //enable buttons
                            stop.setEnabled(true);
                        }
                    });
                }
            });
            stop= (Button) findViewById(R.id.stop);
            stop.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    start.setEnabled(false);                                                            //disable buttons
                    stop.setEnabled(false);
                    Call<Reply> call=null;                                                              //define call
                    service = API.getClient().create(IReseller.class);                                    //crete service for domain
                    call=service.Stop(key,model.username);                                                 //create service call method with parameters
                    call.enqueue(new Callback<Reply>() {                                                //adding queue to callback
                        @Override
                        public void onResponse(Call<Reply> call, Response<Reply> response) {            //when callback return response
                            try {                                                                       //show notification user action is completed
                                Notification.Show(ResellerActivity.this,R.drawable.ic_supervisor_account_white_24dp,R.string.reseller_stoped,response.body().message);
                            }
                            catch (Exception e){                                                        //show notification user action is fail
                                Notification.Show(ResellerActivity.this,R.drawable.ic_supervisor_account_white_24dp,R.string.reseller_stoped,e.getMessage());
                            }
                            start.setEnabled(true);                                                             //enable buttons
                            stop.setEnabled(true);
                        }
                        @Override
                        public void onFailure(Call<Reply> call, Throwable t) {
                            call.cancel();
                            Toast.makeText(ResellerActivity.this,getString(R.string.somethingIsWrong), Toast.LENGTH_SHORT).show();
                            start.setEnabled(true);                                                             //enable buttons
                            stop.setEnabled(true);
                        }
                    });
                }
            });
            changePassword= (Button) findViewById(R.id.changePassword);                                 //set button
            changePassword.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {                                                  //create click listener
                    new ResellerPasswordChange(ResellerActivity.this,model);                                //starting ResellerPasswordChange dialog
                }
            });

            domain= (Button) findViewById(R.id.managementDomain);
            domain.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    intent=new Intent(ResellerActivity.this,DomainsActivity.class);
                    intent.putExtra("model",model);
                    startActivity(intent);
                }
            });
            ip= (Button) findViewById(R.id.managementIP);
            ip.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    intent=new Intent(ResellerActivity.this,ResellerIPManagement.class);
                    intent.putExtra("model",model);
                    startActivity(intent);
                }
            });
        }
        if (progressDialog!=null)
            progressDialog.dismiss();                                                               //progressDialog close
    }

}
