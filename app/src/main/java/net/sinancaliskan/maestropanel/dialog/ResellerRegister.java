package net.sinancaliskan.maestropanel.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

import net.sinancaliskan.maestropanel.API;
import net.sinancaliskan.maestropanel.R;
import net.sinancaliskan.maestropanel.adapters.ResellerAdapter;
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
 * Created by SinanCaliskan on 26.05.2017.
 */

public class ResellerRegister{
    final String key= SharedInformation.getData("serverKey");                                       //get user api key
    private IReseller service =null;                                                                  //define domain service

    private AlertDialog.Builder builder=null;                                                       //define AlertDialog.Builder
    private LayoutInflater layoutInflater=null;                                                     //define LayoutInflater
    public ResellerRegister(final Context context, final ResellerAdapter adapter) {
        builder=new AlertDialog.Builder(context);                                                   //create AlertDialog.Builder with context
        layoutInflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);  //set layoutInflater with LAYOUT_INFLATER_SERVICE

        final View view=layoutInflater.inflate(R.layout.dialog_reseller_create,null);                 //create view with dialog_domain_create layout
        builder.setCancelable(false);                                                               //set dialog is not cancelable
        builder.setView(view);                                                                      //AlertDialog.Builder attached view component
        Toolbar toolbar= (Toolbar) view.findViewById(R.id.dialog_toolbar);                          //define and set toolbar
        toolbar.setTitle(context.getString(R.string.reseller_create));                                                          //set toolbar title

        final TextView txtUsername= (TextView) view.findViewById(R.id.txtUsername);                 //define and set textview
        final TextView txtPassword= (TextView) view.findViewById(R.id.txtPassword);                 //define and set textview
        final TextView txtPlanAlias= (TextView) view.findViewById(R.id.txtPlanAlias);               //define and set spinner
        final TextView txtFirstName= (TextView) view.findViewById(R.id.txtFirstName);               //define and set textview
        final TextView txtLastName= (TextView) view.findViewById(R.id.txtLastName);                 //define and set textview
        final TextView txtEmail= (TextView) view.findViewById(R.id.txtEmail);                       //define and set textview
        final TextView txtCountry= (TextView) view.findViewById(R.id.txtCountry);                   //define and set textview
        final TextView txtOrganization= (TextView) view.findViewById(R.id.txtOrganization);         //define and set textview
        final TextView txtAddress1= (TextView) view.findViewById(R.id.txtAddress1);                 //define and set textview
        final TextView txtAddress2= (TextView) view.findViewById(R.id.txtAddress2);                 //define and set textview
        final TextView txtCity= (TextView) view.findViewById(R.id.txtCity);                         //define and set textview
        final TextView txtProvince= (TextView) view.findViewById(R.id.txtProvince);                 //define and set textview
        final TextView txtPostalCode= (TextView) view.findViewById(R.id.txtPostalCode);             //define and set textview
        final TextView txtPhone= (TextView) view.findViewById(R.id.txtPhone);                       //define and set textview
        final TextView txtFax= (TextView) view.findViewById(R.id.txtFax);                           //define and set textview

        builder.setPositiveButton(context.getString(R.string.register), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {                                  //set dialog positivebutton with name and click method
                Call<Reply> call=null;                                                              //define call
                String  username=txtUsername.getText().toString(),
                        password=txtPassword.getText().toString(),
                        planAlias=txtPlanAlias.getText().toString(),
                        firstName=txtFirstName.getText().toString(),
                        lastName=txtLastName.getText().toString(),
                        email=txtEmail.getText().toString(),
                        country=txtCountry.getText().toString(),
                        organization=txtOrganization.getText().toString(),
                        address1=txtAddress1.getText().toString(),
                        address2=txtAddress2.getText().toString(),
                        city=txtCity.getText().toString(),
                        province=txtProvince.getText().toString(),
                        postalCode=txtPostalCode.getText().toString(),
                        phone=txtPhone.getText().toString(),
                        fax=txtFax.getText().toString();
                service = API.getClient().create(IReseller.class);                                    //crete service for domain
                call=service.Create(key,username,password,planAlias,firstName,lastName,email,country,organization,address1,address2,city,province,postalCode,phone,fax);
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
