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
import net.sinancaliskan.maestropanel.dialog.DomainPasswordChange;
import net.sinancaliskan.maestropanel.interfaces.IDomain;
import net.sinancaliskan.maestropanel.models.Reply;
import net.sinancaliskan.maestropanel.models.pojo.DomainObjects.Domain;
import net.sinancaliskan.maestropanel.utils.Notification;
import net.sinancaliskan.maestropanel.utils.SharedInformation;

import java.text.SimpleDateFormat;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DomainActivity extends AppCompatActivity {
    final String key= SharedInformation.getData("serverKey");                                       //get user api key
    private IDomain service =null;                                                                  //define domain service

    private ProgressDialog progressDialog;                                                          //define progressDialog
    private Domain model;                                                                           //define domain model
    private Intent intent=null;                                                                     //define Intent
    private Toolbar toolbar;
    private Button start,stop,changePassword,subDomain,alias,mailbox,database,ftp,dns;              //define buttons
    private TextView domain,expirationDate,owner,ip,diskUsage,email;                                //define textviews

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_domain);                                                   //set layout file
        Bundle extras=getIntent().getExtras();                                                      //get previous activity transfered objects
        model= (Domain) extras.get("model");                                                        //cast object to domain model

        toolbar= (Toolbar) findViewById(R.id.toolbar);                                              //set toolbar component
        setSupportActionBar(toolbar);                                                               //set setSupportActionBar with toolbar
        setTitle(model.name);                                                                       //set title with domain name

        progressDialog=new ProgressDialog(DomainActivity.this);                                     //creating new progressDialog for showing loading actions
        progressDialog.setMessage(getString(R.string.domain)+getString(R.string.isLoading));        //setting message on resource file
        progressDialog.setCancelable(false);                                                        //assign progressDialog is not cancelable
        progressDialog.show();                                                                      //showing progressDialog

        start= (Button) findViewById(R.id.start);                                                   //set start button
        //region start
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {                                                           //create click listener
                start.setEnabled(false);                                                            //disable buttons
                stop.setEnabled(false);
                Call<Reply> call=null;                                                              //define call
                service = API.getClient().create(IDomain.class);                                    //crete service for domain
                call=service.Start(key,model.name);                                                 //create service call method with parameters
                call.enqueue(new Callback<Reply>() {                                                //adding queue to callback
                    @Override
                    public void onResponse(Call<Reply> call, Response<Reply> response) {            //when callback return response
                        try {                                                                       //show notification user action is completed
                            Notification.Show(DomainActivity.this,R.drawable.domains,R.string.started,response.body().message);
                        }
                        catch (Exception e){                                                        //show notification user action is fail
                            Notification.Show(DomainActivity.this,R.drawable.domains,R.string.started,e.getMessage());
                        }
                        start.setEnabled(true);                                                             //enable buttons
                        stop.setEnabled(true);
                    }
                    @Override
                    public void onFailure(Call<Reply> call, Throwable t) {
                        call.cancel();
                        Toast.makeText(DomainActivity.this,getString(R.string.somethingIsWrong), Toast.LENGTH_SHORT).show();
                        start.setEnabled(true);                                                             //enable buttons
                        stop.setEnabled(true);
                    }
                });

            }
        });
        //endregion
        stop= (Button) findViewById(R.id.stop);
        //region stop
        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                start.setEnabled(false);                                                            //disable buttons
                stop.setEnabled(false);
                Call<Reply> call=null;                                                              //define call
                service = API.getClient().create(IDomain.class);                                    //crete service for domain
                call=service.Stop(key,model.name);                                                  //create service call method with parameters
                call.enqueue(new Callback<Reply>() {                                                //adding queue to callback
                    @Override
                    public void onResponse(Call<Reply> call, Response<Reply> response) {            //when callback return response
                        try {                                                                       //show notification user action is completed
                            Notification.Show(DomainActivity.this,R.drawable.domains, R.string.stoped,response.body().message);
                        }
                        catch (Exception e){                                                        //show notification user action is fail
                            Notification.Show(DomainActivity.this,R.drawable.domains,R.string.stoped,e.getMessage());
                        }
                        start.setEnabled(true);                                                             //enable buttons
                        stop.setEnabled(true);
                    }
                    @Override
                    public void onFailure(Call<Reply> call, Throwable t) {
                        call.cancel();
                        Toast.makeText(DomainActivity.this,getString(R.string.somethingIsWrong), Toast.LENGTH_SHORT).show();
                        start.setEnabled(true);                                                             //enable buttons
                        stop.setEnabled(true);
                    }
                });
            }
        });
        //endregion
        changePassword= (Button) findViewById(R.id.changePassword);                                 //set button
        //region change password
        changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {                                                  //create click listener
                new DomainPasswordChange(DomainActivity.this,model);                                //starting DomainPasswordChange dialog
            }
        });
        //endregion
        subDomain= (Button) findViewById(R.id.managementSubdomain);                                 //set button
        //subdomain management
        subDomain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {                                                       //create click listener
                intent=new Intent(getApplicationContext(), SubdomainManagement.class);              //creating new intent typeof SubdomainManagement
                intent.putExtra("model",model);                                                     //adding model for SubdomainManagement for reuse user object
                startActivity(intent);                                                              //starting Activity
            }
        });
        alias= (Button) findViewById(R.id.managementAlias);                                         //set button
        //alias management
        alias.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {                                                           //create click listener
                intent=new Intent(getApplicationContext(), AliasManagement.class);                  //creating new intent typeof AliasManagement
                intent.putExtra("model",model);                                                     //adding model for AliasManagement for reuse user object
                startActivity(intent);                                                              //starting Activity
            }
        });
        mailbox= (Button) findViewById(R.id.managementMailbox);                                     //set button
        //mailbox management
        mailbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {                                                         //create click listener
                intent=new Intent(getApplicationContext(), MailboxManagement.class);                //creating new intent typeof MailboxManagement
                intent.putExtra("model",model);                                                     //adding model for MailboxManagement for reuse user object
                startActivity(intent);                                                              //starting Activity
            }
        });
        database= (Button) findViewById(R.id.managementDatabase);                                   //set button
        //database management
        database.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {                                                        //create click listener
                intent=new Intent(getApplicationContext(), DatabaseManagement.class);               //creating new intent typeof DatabaseManagement
                intent.putExtra("model",model);                                                     //adding model for DatabaseManagement for reuse user object
                startActivity(intent);                                                              //starting -Activity
            }
        });
        ftp= (Button) findViewById(R.id.managementFTP);                                             //set button
        //ftp management
        ftp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {                                                             //create click listener
                intent=new Intent(getApplicationContext(), FTPManagement.class);                    //creating new intent typeof FTPManagement
                intent.putExtra("model",model);                                                     //adding model for FTPManagement for reuse user object
                startActivity(intent);                                                              //starting Activity
            }
        });
        dns= (Button) findViewById(R.id.managementDNS);                                             //set button
        //dns management
        dns.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {                                                             //create click listener
                intent=new Intent(getApplicationContext(), DNSManagement.class);                    //creating new intent typeof DNSManagement
                intent.putExtra("model",model);                                                     //adding model for DNSManagement for reuse user object
                startActivity(intent);                                                              //starting Activity
            }
        });
        get(model.name);                                                                            //call get method with parameters

    }
    private void get(String name){
        service = API.getClient().create(IDomain.class);                                            //creating new Domain service api
        Call<Reply> call=service.GetListItem(key,name);                                             //creating GetList method with api key
        call.enqueue(new Callback<Reply>() {                                                        //adding queue to callback
            @Override
            public void onResponse(Call<Reply> call, Response<Reply> response) {                    //when callback return response
                ObjectMapper mapper=new ObjectMapper();                                             //creating objectmapper for convert json to class
                mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);         //set doesnt fail when doesnt parse to class
                Domain model=mapper.convertValue(response.body().details,Domain.class);             //set object to model

                domain= (TextView) findViewById(R.id.txtDomain);                                    //define and set views with domain values
                domain.setText(model.name);
                expirationDate= (TextView) findViewById(R.id.txtExpirationDate);
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
                String date=model.expirationDate.replace("/Date(","").replace(")/","");
                expirationDate.setText(dateFormat.format(Long.valueOf(date)));
                owner= (TextView) findViewById(R.id.txtOwner);
                owner.setText(model.ownerName);
                ip= (TextView) findViewById(R.id.txtIP);
                ip.setText((String)model.ipAddr);
                diskUsage= (TextView) findViewById(R.id.txtDiskUsage);
                diskUsage.setText(model.disk.toString() + "%");
                email= (TextView) findViewById(R.id.txtEmail);
                email.setText(model.email.toString() +getString(R.string.registered));

                if (progressDialog!=null)
                    progressDialog.dismiss();                                                       //progressDialog close
            }
            @Override
            public void onFailure(Call<Reply> call, Throwable throwable) {                          //when callback return failure
                call.cancel();
                Toast.makeText(null, getResources().getString(R.string.somethingIsWrong), Toast.LENGTH_SHORT).show();//showing error message
                if (progressDialog!=null)
                    progressDialog.dismiss();                                                       //progressDialog close
            }
        });
    }
}
