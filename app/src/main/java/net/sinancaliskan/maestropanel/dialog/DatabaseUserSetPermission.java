package net.sinancaliskan.maestropanel.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
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

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by sinancaliskan on 28/04/2017.
 */

public class DatabaseUserSetPermission {
    final String key = SharedInformation.getData("serverKey");
    private IDomain service = null;

    private Context context;
    private AlertDialog.Builder builder = null;
    private LayoutInflater layoutInflater = null;

    private Domain model;

    public DatabaseUserSetPermission(final Context context, final DatabaseUserAdapter adapter, final Database db, final DatabaseUser account) {
        this.context=context;

        this.model=adapter.domain;

        builder=new AlertDialog.Builder(context);
        layoutInflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        final View view=layoutInflater.inflate(R.layout.dialog_database_user_set_permission,null);
        builder.setView(view);
        Toolbar toolbar= (Toolbar) view.findViewById(R.id.dialog_toolbar);
        toolbar.setTitle(context.getString(R.string.database_user_set_permission));

        LinearLayout mysql= (LinearLayout) view.findViewById(R.id.mysql);
        LinearLayout mssql= (LinearLayout) view.findViewById(R.id.mssql);
        //mssql
        final CheckBox mssqlRead=(CheckBox) view.findViewById(R.id.mssqlRead);
        final CheckBox mssqlUpdate=(CheckBox) view.findViewById(R.id.mssqlUpdate);
        final CheckBox mssqlDelete=(CheckBox) view.findViewById(R.id.mssqlDelete);
        final CheckBox mssqlWrite=(CheckBox) view.findViewById(R.id.mssqlWrite);
        //mysql
        final CheckBox mysqlSelect=(CheckBox) view.findViewById(R.id.mysqlSelect);
        final CheckBox mysqlInsert=(CheckBox) view.findViewById(R.id.mysqlInsert);
        final CheckBox mysqlUpdate=(CheckBox) view.findViewById(R.id.mysqlUpdate);
        final CheckBox mysqlDelete=(CheckBox) view.findViewById(R.id.mysqlDelete);
        final CheckBox mysqlDrop=(CheckBox) view.findViewById(R.id.mysqlDrop);
        final CheckBox mysqlCreate=(CheckBox) view.findViewById(R.id.mysqlCreate);
        final CheckBox mysqlAlter=(CheckBox) view.findViewById(R.id.mysqlAlter);
        final CheckBox mysqlShowView=(CheckBox) view.findViewById(R.id.mysqlShowView);
        final CheckBox mysqlAlterRoutine=(CheckBox) view.findViewById(R.id.mysqlAlterRoutine);
        final CheckBox mysqlEvent=(CheckBox) view.findViewById(R.id.mysqlEvent);
        final CheckBox mysqlIndex=(CheckBox) view.findViewById(R.id.mysqlIndex);
        final CheckBox mysqlReferences=(CheckBox) view.findViewById(R.id.mysqlReferences);
        final CheckBox mysqlTrigger=(CheckBox) view.findViewById(R.id.mysqlTrigger);
        final CheckBox mysqlLockTables=(CheckBox) view.findViewById(R.id.mysqlLockTables);
        final CheckBox mysqlCreateRoutine=(CheckBox) view.findViewById(R.id.mysqlCreateRoutine);
        final CheckBox mysqlCreateTemporaryTables=(CheckBox) view.findViewById(R.id.mysqlCreateTemporaryTables);
        final CheckBox mysqlCreateView=(CheckBox) view.findViewById(R.id.mysqlCreateView);

        if (db.dbType.equals("mssql")){
            mssql.setVisibility(View.VISIBLE);
            mysql.setVisibility(View.GONE);
            if (account.rights!=null){
                String [] permission=account.rights.split(";");//READ;WRITE;UPDATE;DELETE
                ArrayList<String> permissions=new ArrayList<>();
                for (int i = 0; i < permission.length; i++) {
                    permissions.add(permission[i].toString());
                }
                mssqlRead.setChecked(permissions.contains("READ"));
                mssqlUpdate.setChecked(permissions.contains("UPDATE"));
                mssqlDelete.setChecked(permissions.contains("DELETE"));
                mssqlWrite.setChecked(permissions.contains("WRITE"));
            }
        }
        else if(db.dbType.equals("mysql")){
            mysql.setVisibility(View.VISIBLE);
            mssql.setVisibility(View.GONE);
            if (account.rights!=null){
                String [] permission=account.rights.split(";");//SELECT;INSERT;UPDATE;DELETE;DROP;CREATE;ALTER;SHOW VIEW;ALTER ROUTINE;EVENT;INDEX;REFERENCES;TRIGGER;LOCK TABLES;CREATE ROUTINE;CREATE TEMPORARY TABLES;CREATE VIEW
                ArrayList<String> permissions=new ArrayList<>();
                for (int i = 0; i < permission.length; i++) {
                    permissions.add(permission[i].toString());
                }
                mysqlSelect.setChecked(permissions.contains("SELECT"));
                mysqlInsert.setChecked(permissions.contains("INSERT"));
                mysqlUpdate.setChecked(permissions.contains("UPDATE"));
                mysqlDelete.setChecked(permissions.contains("DELETE"));
                mysqlDrop.setChecked(permissions.contains("DROP"));
                mysqlCreate.setChecked(permissions.contains("CREATE"));
                mysqlAlter.setChecked(permissions.contains("ALTER"));
                mysqlShowView.setChecked(permissions.contains("SHOW VIEW"));
                mysqlAlterRoutine.setChecked(permissions.contains("ALTER ROUTINE"));
                mysqlEvent.setChecked(permissions.contains("EVENT"));
                mysqlIndex.setChecked(permissions.contains("INDEX"));
                mysqlReferences.setChecked(permissions.contains("REFERENCES"));
                mysqlTrigger.setChecked(permissions.contains("TRIGGER"));
                mysqlLockTables.setChecked(permissions.contains("LOCK TABLES"));
                mysqlCreateRoutine.setChecked(permissions.contains("CREATE ROUTINE"));
                mysqlCreateTemporaryTables.setChecked(permissions.contains("CREATE TEMPORARY TABLES"));
                mysqlCreateView.setChecked(permissions.contains("CREATE VIEW"));
            }
        }

        builder.setPositiveButton(context.getString(R.string.change), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Call<Reply> call=null;
                final String domainName=model.name,
                        dbType=db.dbType,
                        database=db.name,
                        username=account.username;
                String permission="";
                //region permission
                if (db.dbType.equals("mssql")){
                     if(mssqlRead.isChecked())
                         permission+="READ;";
                    if(mssqlUpdate.isChecked())
                        permission+="UPDATE;";
                    if(mssqlDelete.isChecked())
                        permission+="DELETE;";
                    if(mssqlWrite.isChecked())
                        permission+="WRITE;";
                }
                else if (db.dbType.equals("mysql")){
                    if (mysqlSelect.isChecked())
                        permission+="SELECT;";
                    if (mysqlInsert.isChecked())
                        permission+="INSERT;";
                    if (mysqlUpdate.isChecked())
                        permission+="UPDATE;";
                    if (mysqlDelete.isChecked())
                        permission+="DELETE;";
                    if (mysqlDrop.isChecked())
                        permission+="DROP;";
                    if (mysqlCreate.isChecked())
                        permission+="CREATE;";
                    if (mysqlAlter.isChecked())
                        permission+="ALTER;";
                    if (mysqlShowView.isChecked())
                        permission+="SHOW VIEW;";
                    if (mysqlAlterRoutine.isChecked())
                        permission+="ALTER ROUTINE;";
                    if (mysqlEvent.isChecked())
                        permission+="EVENT;";
                    if (mysqlIndex.isChecked())
                        permission+="INDEX;";
                    if (mysqlReferences.isChecked())
                        permission+="REFERENCES;";
                    if (mysqlTrigger.isChecked())
                        permission+="TRIGGER;";
                    if (mysqlLockTables.isChecked())
                        permission+="LOCK TABLES;";
                    if (mysqlCreateRoutine.isChecked())
                        permission+="CREATE ROUTINE;";
                    if (mysqlCreateTemporaryTables.isChecked())
                        permission+="CREATE TEMPORARY TABLES;";
                    if (mysqlCreateView.isChecked())
                        permission+="CREATE VIEW;";
                }
                if (permission.length()>0)
                    permission=permission.substring(0,permission.length()-1);
                //endregion
                service = API.getClient().create(IDomain.class);
                call=service.SetDatabaseUserPermissions(key,domainName,dbType,database,username,permission);

                Toast.makeText(context,context.getString(R. string.database_user_set_permission)+context.getString(R.string.change_operation), Toast.LENGTH_SHORT).show();
                call.enqueue(new Callback<Reply>() {
                    @Override
                    public void onResponse(Call<Reply> call, Response<Reply> response) {
                        Notification.Show(context,R.drawable.domains,R.string.change_complated,response.body().message);
                        adapter.refresh();
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
