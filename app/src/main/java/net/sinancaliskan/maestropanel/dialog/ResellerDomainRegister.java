package net.sinancaliskan.maestropanel.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import net.sinancaliskan.maestropanel.API;
import net.sinancaliskan.maestropanel.R;
import net.sinancaliskan.maestropanel.adapters.ResellerDomainAdapter;
import net.sinancaliskan.maestropanel.interfaces.IReseller;
import net.sinancaliskan.maestropanel.models.Reply;
import net.sinancaliskan.maestropanel.utils.Notification;
import net.sinancaliskan.maestropanel.utils.SharedInformation;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by SinanCaliskan on 20.05.2017.
 */

public class ResellerDomainRegister {
    final String key= SharedInformation.getData("serverKey");                                       //get user api key
    private IReseller service =null;                                                                  //define domain service

    private AlertDialog.Builder builder=null;                                                       //define AlertDialog.Builder
    private LayoutInflater layoutInflater=null;                                                     //define LayoutInflater
    public ResellerDomainRegister(final Context context, final ResellerDomainAdapter adapter, final String username) {
        builder=new AlertDialog.Builder(context);                                                   //create AlertDialog.Builder with context
        layoutInflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);  //set layoutInflater with LAYOUT_INFLATER_SERVICE

        final View view=layoutInflater.inflate(R.layout.dialog_domain_create,null);                 //create view with dialog_domain_create layout
        builder.setCancelable(false);                                                               //set dialog is not cancelable
        builder.setView(view);                                                                      //AlertDialog.Builder attached view component
        Toolbar toolbar= (Toolbar) view.findViewById(R.id.dialog_toolbar);                          //define and set toolbar
        toolbar.setTitle(context.getString(R.string.domain_create));                                                          //set toolbar title

        final TextView txtDomainName= (TextView) view.findViewById(R.id.txtDomainName);             //define and set textview
        final TextView txtPlanAlias= (TextView) view.findViewById(R.id.txtPlanAlias);                         //define and set spinner
        final TextView txtUsername= (TextView) view.findViewById(R.id.txtUsername);                 //define and set textview
        final TextView txtPassword= (TextView) view.findViewById(R.id.txtPassword);                 //define and set textview
        final CheckBox chkActiveDomainUser= (CheckBox) view.findViewById(R.id.chkActiveDomainUser); //define and set checkbox
        final TextView txtFirstName= (TextView) view.findViewById(R.id.txtFirstName);               //define and set textview
        final TextView txtLastName= (TextView) view.findViewById(R.id.txtLastName);                 //define and set textview
        final TextView txtEmail= (TextView) view.findViewById(R.id.txtEmail);                       //define and set textview
        final TextView txtExpirationDate= (TextView) view.findViewById(R.id.txtExpirationDate);     //define and set textview
        final TextView txtIP= (TextView) view.findViewById(R.id.txtIP);                             //define and set textview

        chkActiveDomainUser.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {                 //create checkbox change listener
                LinearLayout isActive= (LinearLayout) view.findViewById(R.id.isActive);             //get linearlayout view
                if (isChecked)
                    isActive.setVisibility(View.VISIBLE);                                           //set visible linearlayout view
                else
                    isActive.setVisibility(View.GONE);                                              //set gone linearlayout view
            }
        });
        builder.setPositiveButton(context.getString(R.string.register), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {                                  //set dialog positivebutton with name and click method
                Call<Reply> call=null;                                                              //define call
                String domainName=txtDomainName.getText().toString(),                               //set string for domain create
                        planAlias=txtPlanAlias.getText().toString(),
                        domainUsername=txtUsername.getText().toString(),
                        password=txtPassword.getText().toString(),
                        firstName=txtFirstName.getText().toString(),
                        lastName=txtLastName.getText().toString(),
                        email=txtEmail.getText().toString(),
                        expirationDate=txtExpirationDate.getText().toString(),
                        ip=txtIP.getText().toString();
                service = API.getClient().create(IReseller.class);                                    //crete service for domain
                if (!chkActiveDomainUser.isChecked())
                    call=service.AddDomain(key,username,domainName,planAlias,domainUsername,password,"false");        //create service call method with parameters
                else{
                    if (firstName.length()==0)firstName=null;
                    if (lastName.length()==0)lastName=null;
                    if (email.length()==0)email=null;
                    if (expirationDate.length()==0)expirationDate=null;
                    if (ip.length()==0)ip=null;                                                     //create service call method with parameters
                    call=service.AddDomain(key,username,domainName,planAlias,domainUsername,password,"true",firstName,lastName,email,expirationDate,ip);
                }                                                                                   //show message for user information
                Toast.makeText(context,context.getString(R.string.domain)+context.getString(R.string.create_operation), Toast.LENGTH_SHORT).show();
                call.enqueue(new Callback<Reply>() {                                                //adding queue to callback
                    @Override
                    public void onResponse(Call<Reply> call, Response<Reply> response) {            //when callback return response
                        //show notification action is completed
                        Notification.Show(context,R.drawable.domains,R.string.create_completed,response.body().message);
                        adapter.refresh();                                                          //adapter refresh method is called for refresh
                    }
                    @Override
                    public void onFailure(Call<Reply> call, Throwable t) {
                        call.cancel();
                        Toast.makeText(context,context.getString(R.string.somethingIsWrong), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        builder.setNegativeButton(context.getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {                                    //set dialog negativebutton with name and click method
                dialog.dismiss();                                                                   //dismiss alertdialog
            }
        });
        AlertDialog alert=builder.create();                                                         //create alertdialog with alertdialog.builder
        alert.show();
    }
}
