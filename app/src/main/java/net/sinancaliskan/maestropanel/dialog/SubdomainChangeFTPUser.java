package net.sinancaliskan.maestropanel.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import net.sinancaliskan.maestropanel.API;
import net.sinancaliskan.maestropanel.R;
import net.sinancaliskan.maestropanel.adapters.SubdomainAdapter;
import net.sinancaliskan.maestropanel.interfaces.IDomain;
import net.sinancaliskan.maestropanel.models.Reply;
import net.sinancaliskan.maestropanel.models.pojo.DomainObjects.Domain;
import net.sinancaliskan.maestropanel.models.pojo.DomainObjects.SubDomain;
import net.sinancaliskan.maestropanel.utils.Notification;
import net.sinancaliskan.maestropanel.utils.SharedInformation;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by SinanCaliskan on 16.04.2017.
 */

public class SubdomainChangeFTPUser {
    final String key = SharedInformation.getData("serverKey");
    private IDomain service = null;

    private Context context;
    private AlertDialog.Builder builder = null;
    private LayoutInflater layoutInflater = null;

    private Domain model;
    private SubDomain subdomain;
    private List<String> ftpUsers=new ArrayList<>();
    public SubdomainChangeFTPUser(final Context context,final SubdomainAdapter adapter,final SubDomain subdomain){
        this.context=context;

        this.model=adapter.domain;
        this.subdomain=subdomain;
        this.ftpUsers=adapter.ftpUsers;

        builder=new AlertDialog.Builder(context);
        layoutInflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        final View view=layoutInflater.inflate(R.layout.dialog_subdomain_change,null);
        builder.setView(view);
        Toolbar toolbar= (Toolbar) view.findViewById(R.id.dialog_toolbar);
        toolbar.setTitle(context.getString(R.string.subdomain_change));

        final Spinner spinner=(Spinner) view.findViewById(R.id.ftpUser);
        final ArrayAdapter spinnerAdapter=new ArrayAdapter(view.getContext(),
                R.layout.spinner_item,ftpUsers);
        spinnerAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);
        spinner.setSelection(ftpUsers.indexOf(subdomain.ftpUser));
        builder.setPositiveButton(context.getString(R.string.change), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Call<Reply> call=null;
                final String domainName=model.name,
                        ftpUser=spinner.getSelectedItem().toString();

                service = API.getClient().create(IDomain.class);

                call=service.SetSubDomainftpAccount(key,domainName,subdomain.name,ftpUser);

                Toast.makeText(context,context.getString(R. string.subdomain)+context.getString(R.string.change_operation), Toast.LENGTH_SHORT).show();
                call.enqueue(new Callback<Reply>() {
                    @Override
                    public void onResponse(Call<Reply> call, Response<Reply> response) {
                        Notification.Show(context,R.drawable.domains,R.string.change_complated,response.body().message);
                        adapter.updateItem(subdomain,ftpUser);
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
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alert=builder.create();
        alert.show();
    }
}
