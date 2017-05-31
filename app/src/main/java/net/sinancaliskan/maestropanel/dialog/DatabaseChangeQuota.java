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
import net.sinancaliskan.maestropanel.adapters.DatabaseAdapter;
import net.sinancaliskan.maestropanel.interfaces.IDomain;
import net.sinancaliskan.maestropanel.models.Reply;
import net.sinancaliskan.maestropanel.models.pojo.DomainObjects.Database;
import net.sinancaliskan.maestropanel.models.pojo.DomainObjects.Domain;
import net.sinancaliskan.maestropanel.utils.Notification;
import net.sinancaliskan.maestropanel.utils.SharedInformation;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by SinanCaliskan on 19.04.2017.
 */

public class DatabaseChangeQuota {
    final String key= SharedInformation.getData("serverKey");
    private IDomain service =null;

    private Context context;
    private AlertDialog.Builder builder=null;
    private LayoutInflater layoutInflater=null;

    private Domain model;
    public DatabaseChangeQuota(final Context context, final DatabaseAdapter adapter, final Database db) {
        this.context=context;

        this.model=adapter.domain;

        builder=new AlertDialog.Builder(context);
        layoutInflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        final View view=layoutInflater.inflate(R.layout.dialog_database_change_quota,null);
        builder.setView(view);
        Toolbar toolbar= (Toolbar) view.findViewById(R.id.dialog_toolbar);
        toolbar.setTitle(context.getString(R.string.database_change_quota));

        final TextView txtQuota= (TextView) view.findViewById(R.id.txtQuota);
        txtQuota.setText(db.diskQuota.toString());
        builder.setPositiveButton(context.getString(R.string.change), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Call<Reply> call=null;
                final String domainName=model.name,
                        database=db.name,
                        dbType=db.dbType,
                        quota=txtQuota.getText().toString();

                service = API.getClient().create(IDomain.class);

                call=service.SetDatabaseQuota(key,domainName,dbType,database,quota);

                Toast.makeText(context,context.getString(R. string.database)+context.getString(R.string.change_operation), Toast.LENGTH_SHORT).show();
                call.enqueue(new Callback<Reply>() {
                    @Override
                    public void onResponse(Call<Reply> call, Response<Reply> response) {
                        Notification.Show(context,R.drawable.domains,R.string.change_complated,response.body().message);
                        adapter.updateItem(db,Integer.parseInt(quota));
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
