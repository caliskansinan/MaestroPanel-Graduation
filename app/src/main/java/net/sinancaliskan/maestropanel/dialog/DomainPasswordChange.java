package net.sinancaliskan.maestropanel.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import net.sinancaliskan.maestropanel.API;
import net.sinancaliskan.maestropanel.R;
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

public class DomainPasswordChange {
    final String key= SharedInformation.getData("serverKey");                                       //get user api key
    private IDomain service =null;                                                                  //define domain service

    private AlertDialog.Builder builder=null;                                                       //define AlertDialog.Builder
    private LayoutInflater layoutInflater=null;                                                     //define LayoutInflater
    public DomainPasswordChange(final Context context, final Domain model) {
        builder=new AlertDialog.Builder(context);                                                   //create AlertDialog.Builder with context
        layoutInflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);  //set layoutInflater with LAYOUT_INFLATER_SERVICE

        final View view=layoutInflater.inflate(R.layout.dialog_domain_change_password,null);        //create view with dialog_domain_change_password layout
        builder.setCancelable(false);                                                               //set dialog is not cancelable
        builder.setView(view);                                                                      //AlertDialog.Builder attached view component
        final Toolbar toolbar= (Toolbar) view.findViewById(R.id.dialog_toolbar);                    //define and set toolbar
        toolbar.setTitle(context.getString(R.string.change_password));                                                        //set toolbar title
        final TextView txtNewPassword= (TextView) view.findViewById(R.id.txtNewPassword);           //define and set textview
        builder.setPositiveButton(R.string.change_password, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {                            //set dialog positivebutton with name and click method
                Call<Reply> call=null;                                                              //define call
                String domainName=model.name,newPassword=txtNewPassword.getText().toString();       //set string for domain change password
                service = API.getClient().create(IDomain.class);                                    //crete service for domain
                call=service.ChangePassword(key,domainName,newPassword);                            //create service call method with parameters
                                                                                                    //show message for action information
                Toast.makeText(context,context.getString(R.string.domain)+context.getString(R.string.change_operation), Toast.LENGTH_SHORT).show();
                call.enqueue(new Callback<Reply>() {                                                //adding queue to callback
                    @Override
                    public void onResponse(Call<Reply> call, Response<Reply> response) {            //when callback return response
                        try {                                                                       //show notification action is completed
                            Notification.Show(context,R.drawable.domains,R.string.change_complated,response.body().message);
                        }
                        catch (Exception e){                                                        //show notification action isn't completed
                            Notification.Show(context,R.drawable.domains,R.string.change_operation,e.getMessage());
                        }
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
            public void onClick(DialogInterface dialog, int which) {          //set dialog negativebutton with name and click method
                dialog.dismiss();                                                                   //dismiss alertdialog
            }
        });
        AlertDialog alert=builder.create();                                                         //create alertdialog with alertdialog.builder
        alert.show();
    }
}
