package net.sinancaliskan.maestropanel.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import net.sinancaliskan.maestropanel.API;
import net.sinancaliskan.maestropanel.R;
import net.sinancaliskan.maestropanel.adapters.MailAdapter;
import net.sinancaliskan.maestropanel.interfaces.IDomain;
import net.sinancaliskan.maestropanel.models.Reply;
import net.sinancaliskan.maestropanel.models.pojo.DomainObjects.Domain;
import net.sinancaliskan.maestropanel.utils.Notification;
import net.sinancaliskan.maestropanel.utils.SharedInformation;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by SinanCaliskan on 17.04.2017.
 */

public class MailBoxRegister {
    final String key= SharedInformation.getData("serverKey");                                       //get user api key
    private IDomain service =null;                                                                  //define domain service

    private AlertDialog.Builder builder=null;                                                       //define AlertDialog.Builder
    private LayoutInflater layoutInflater=null;                                                     //define LayoutInflater
    public MailBoxRegister(final Context context,final MailAdapter adapter) {
        builder=new AlertDialog.Builder(context);                                                   //create AlertDialog.Builder with context
        layoutInflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);  //set layoutInflater with LAYOUT_INFLATER_SERVICE

        final View view=layoutInflater.inflate(R.layout.dialog_mailbox_create,null);                //create view with dialog_mailbox_create layout
        builder.setCancelable(false);                                                               //set dialog is not cancelable
        builder.setView(view);                                                                      //AlertDialog.Builder attached view component
        Toolbar toolbar= (Toolbar) view.findViewById(R.id.dialog_toolbar);                          //define and set toolbar
        toolbar.setTitle(context.getString(R.string.mailbox_create));                                                         //set toolbar title

        final TextView txtAccount= (TextView) view.findViewById(R.id.txtAccount);                   //define and set textviews
        final TextView txtPassword= (TextView) view.findViewById(R.id.txtPassword);
        final TextView txtQuota= (TextView) view.findViewById(R.id.txtQuota);
        final CheckBox chkRedirect=(CheckBox) view.findViewById(R.id.chkRedirect);
        final TextView txtReMail= (TextView) view.findViewById(R.id.txtReMail);
        final TextView txtDisplayName= (TextView) view.findViewById(R.id.txtDisplayName);

        chkRedirect.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {                         //create checkbox change listener
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
                String domainName=adapter.domain.name,                                              //set string for mail create
                        account=txtAccount.getText().toString(),
                        password=txtPassword.getText().toString(),
                        quota=txtQuota.getText().toString(),
                        reMail=txtReMail.getText().toString(),
                        displayName=txtDisplayName.getText().toString();
                service = API.getClient().create(IDomain.class);                                    //crete service for domain
                if(quota.length()==0) quota="-1";
                if (!chkRedirect.isChecked()) {
                    if (reMail.length()==0)reMail=null;
                    if (displayName.length()==0)displayName=null;                                   //create service call method with parameters
                    call = service.AddMailBox(key, domainName, account, password, quota, "false", reMail, displayName);
                }
                else{
                    if (reMail.length()==0)reMail=null;                                             //create service call method with parameters
                    call = service.AddMailBox(key, domainName, account, password, quota, "true", reMail, displayName);
                }                                                                                   //show message for user information
                Toast.makeText(context,context.getString(R.string.mailbox)+context.getString(R.string.create_operation), Toast.LENGTH_SHORT).show();
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
