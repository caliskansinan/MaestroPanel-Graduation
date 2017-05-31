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
import net.sinancaliskan.maestropanel.adapters.DatabaseUserAdapter;
import net.sinancaliskan.maestropanel.interfaces.IDomain;
import net.sinancaliskan.maestropanel.models.Reply;
import net.sinancaliskan.maestropanel.models.pojo.DomainObjects.Database;
import net.sinancaliskan.maestropanel.models.pojo.DomainObjects.DatabaseUser;
import net.sinancaliskan.maestropanel.models.pojo.DomainObjects.Domain;
import net.sinancaliskan.maestropanel.utils.Notification;
import net.sinancaliskan.maestropanel.utils.SharedInformation;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by sinancaliskan on 20/04/2017.
 */

public class DatabaseUserDelete {
    final String key= SharedInformation.getData("serverKey");
    private IDomain service =null;

    private Context context;
    private AlertDialog.Builder builder=null;
    private LayoutInflater layoutInflater=null;

    private Domain model;
    public DatabaseUserDelete(final Context context, final DatabaseUserAdapter adapter, final Database database , final DatabaseUser databaseUser) {
        this.context=context;

        this.model=adapter.domain;

        builder=new AlertDialog.Builder(context);
        layoutInflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        final View view=layoutInflater.inflate(R.layout.dialog_delete,null);
        builder.setCancelable(false);
        builder.setView(view);
        Toolbar toolbar= (Toolbar) view.findViewById(R.id.dialog_toolbar);
        toolbar.setTitle(context.getString(R.string.are_you_sure_to_delete));

        final TextView txtDelete= (TextView) view.findViewById(R.id.txtDelete);
        txtDelete.setText(databaseUser.username);
        builder.setPositiveButton(context.getString(R.string.delete), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Call<Reply> call=null;
                service = API.getClient().create(IDomain.class);
                call=service.DeleteDatabaseUser(key,model.name,database.dbType,database.name,databaseUser.username);
                Toast.makeText(context,context.getString(R.string.database_user)+context.getString(R.string.delete_operation), Toast.LENGTH_SHORT).show();
                call.enqueue(new Callback<Reply>() {
                    @Override
                    public void onResponse(Call<Reply> call, Response<Reply> response) {
                        Notification.Show(context,R.drawable.domains,R.string.delete_completed,response.body().message);
                        adapter.removeItem(databaseUser.username);
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
