package net.sinancaliskan.maestropanel.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import net.sinancaliskan.maestropanel.API;
import net.sinancaliskan.maestropanel.R;
import net.sinancaliskan.maestropanel.adapters.AliasAdapter;
import net.sinancaliskan.maestropanel.interfaces.IDomain;
import net.sinancaliskan.maestropanel.models.Reply;
import net.sinancaliskan.maestropanel.models.pojo.DomainObjects.Domain;
import net.sinancaliskan.maestropanel.utils.Notification;
import net.sinancaliskan.maestropanel.utils.SharedInformation;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by SinanCaliskan on 16.04.2017.
 */

public class AliasRegister {
    final String key= SharedInformation.getData("serverKey");                                       //get user api key
    private IDomain service =null;                                                                  //define domain service

    private AlertDialog.Builder builder=null;                                                       //define AlertDialog.Builder
    private LayoutInflater layoutInflater=null;                                                     //define LayoutInflater

    public AliasRegister(final Context context,final AliasAdapter adapter) {
        builder=new AlertDialog.Builder(context);                                                   //create AlertDialog.Builder with context
        layoutInflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);  //set layoutInflater with LAYOUT_INFLATER_SERVICE

        final View view=layoutInflater.inflate(R.layout.dialog_alias_create,null);                  //create view with dialog_alias_create layout
        builder.setCancelable(false);                                                               //set dialog is not cancelable
        builder.setView(view);                                                                      //AlertDialog.Builder attached view component
        Toolbar toolbar= (Toolbar) view.findViewById(R.id.dialog_toolbar);                          //define and set toolbar
        toolbar.setTitle(context.getString(R.string.alias_create));                                                           //set toolbar title

        final TextView txtAlias= (TextView) view.findViewById(R.id.txtAlias);                       //define and set textview

        builder.setPositiveButton(context.getString(R.string.register), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {                                  //set dialog positivebutton with name and click method
                Call<Reply> call=null;                                                              //define call
                String domainName=adapter.domain.name,                                              //set string for alias create
                        alias=txtAlias.getText().toString();
                service = API.getClient().create(IDomain.class);                                    //crete service for domain
                call=service.AddDomainAlias(key,domainName,alias);                                  //create service call method with parameters
                                                                                                    //show message for user information
                Toast.makeText(context,context.getString(R.string.alias)+context.getString(R.string.create_operation), Toast.LENGTH_SHORT).show();
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
        alert.show();                                                                               //show on screen
    }
}
