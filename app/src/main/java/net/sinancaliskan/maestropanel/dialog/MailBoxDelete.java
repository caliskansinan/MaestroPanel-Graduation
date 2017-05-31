package net.sinancaliskan.maestropanel.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import net.sinancaliskan.maestropanel.API;
import net.sinancaliskan.maestropanel.R;
import net.sinancaliskan.maestropanel.adapters.MailAdapter;
import net.sinancaliskan.maestropanel.interfaces.IDomain;
import net.sinancaliskan.maestropanel.models.Reply;
import net.sinancaliskan.maestropanel.utils.Notification;
import net.sinancaliskan.maestropanel.utils.SharedInformation;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by SinanCaliskan on 17.04.2017.
 */

public class MailBoxDelete {
    final String key= SharedInformation.getData("serverKey");                                       //get user api key
    private IDomain service =null;                                                                  //define domain service

    private AlertDialog.Builder builder=null;                                                       //define AlertDialog.Builder
    private LayoutInflater layoutInflater=null;                                                     //define LayoutInflater
    public MailBoxDelete(final Context context, final MailAdapter adapter, final String account) {
        builder=new AlertDialog.Builder(context);                                                   //create AlertDialog.Builder with context
        layoutInflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);  //set layoutInflater with LAYOUT_INFLATER_SERVICE

        final View view=layoutInflater.inflate(R.layout.dialog_delete,null);                        //create view with dialog_delete layout
        builder.setCancelable(false);                                                               //set dialog is not cancelable
        builder.setView(view);                                                                      //AlertDialog.Builder attached view component
        Toolbar toolbar= (Toolbar) view.findViewById(R.id.dialog_toolbar);                          //define and set toolbar
        toolbar.setTitle(context.getString(R.string.are_you_sure_to_delete));                                                //set toolbar title

        final TextView txtDelete= (TextView) view.findViewById(R.id.txtDelete);                     //define and set textview
        txtDelete.setText(account);
        builder.setPositiveButton(context.getString(R.string.delete), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {                                    //set dialog positivebutton with name and click method
                Call<Reply> call=null;                                                              //define call
                service = API.getClient().create(IDomain.class);                                    //crete service for domain
                String name=account.replace("@"+adapter.domain.name,"");
                call=service.DeleteMailBox(key,adapter.domain.name,name);                           //create service call method with parameters
                                                                                                    //show message for user information
                Toast.makeText(context,context.getString(R.string.mailbox)+context.getString(R.string.delete_operation), Toast.LENGTH_SHORT).show();
                call.enqueue(new Callback<Reply>() {                                                //adding queue to callback
                    @Override
                    public void onResponse(Call<Reply> call, Response<Reply> response) {            //when callback return response
                                                                                                    //show notification user action is completed
                        Notification.Show(context,R.drawable.domains,R.string.delete_completed,response.body().message);
                        adapter.removeItem(account);                                                //adapter remove method with account
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