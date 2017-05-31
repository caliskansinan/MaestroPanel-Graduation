package net.sinancaliskan.maestropanel.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import net.sinancaliskan.maestropanel.API;
import net.sinancaliskan.maestropanel.R;
import net.sinancaliskan.maestropanel.adapters.DNSAdapter;
import net.sinancaliskan.maestropanel.interfaces.IDomain;
import net.sinancaliskan.maestropanel.models.Reply;
import net.sinancaliskan.maestropanel.models.pojo.DomainObjects.Domain;
import net.sinancaliskan.maestropanel.utils.Notification;
import net.sinancaliskan.maestropanel.utils.SharedInformation;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by SinanCaliskan on 19.04.2017.
 */

public class DnsRecordRegister {
    final String key = SharedInformation.getData("serverKey");
    private IDomain service = null;

    private Context context;
    private AlertDialog.Builder builder = null;
    private LayoutInflater layoutInflater = null;

    private Domain model;

    public DnsRecordRegister(final Context context, final DNSAdapter adapter) {
        this.context = context;
        this.model = adapter.domain;

        builder = new AlertDialog.Builder(context);
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        final View view = layoutInflater.inflate(R.layout.dialog_dns_record_create, null);
        builder.setView(view);
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.dialog_toolbar);
        toolbar.setTitle(context.getString(R.string.dns_create));

        final Spinner recordType=(Spinner) view.findViewById(R.id.recordType);
        final ArrayList<String> recType=new ArrayList<>();
        //AFSDB, ATMA, A, HINFO, AAAA, CNAME, TXT,PTR, MX, NS, MG, MB, MINFO, MR, RP, RT, SRV, SIG, WKS,X25,KEY
        recType.add("AFSDB");recType.add("ATMA");recType.add("A");recType.add("HINFO");recType.add("AAAA");recType.add("CNAME");
        recType.add("TXT");recType.add("PT");recType.add("MX");recType.add("NS");recType.add("MG");recType.add("MR");recType.add("RP");
        recType.add("SRV");recType.add("SIG");recType.add("WKS");recType.add("X25");recType.add("KEY");
        final ArrayAdapter recordAdapter=new ArrayAdapter(view.getContext(),
                R.layout.spinner_item,recType);//hata var
        recordAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        recordType.setAdapter(recordAdapter);
        final LinearLayout isActive= (LinearLayout) view.findViewById(R.id.isActive);
        recordType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selected=recordType.getItemAtPosition(position).toString();
                if (selected.equals("MX")){
                    isActive.setVisibility(View.VISIBLE);
                }
                else{
                    isActive.setVisibility(View.GONE);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        final TextView txtRecordName = (TextView) view.findViewById(R.id.txtRecordName);
        final TextView txtRecordValue = (TextView) view.findViewById(R.id.txtRecordValue);
        final TextView txtPriority = (TextView) view.findViewById(R.id.txtPriority);

        builder.setPositiveButton(context.getString(R.string.register), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                Call<Reply> call = null;
                String domainName = model.name,
                        rec_type = recordType.getSelectedItem().toString(),
                        rec_name=txtRecordName.getText().toString(),
                        rec_value=txtRecordValue.getText().toString(),
                        priority=txtPriority.getText().toString();
                if(!(rec_name.length()>0))
                    rec_name="@";
                if (!(priority.length()>0))
                    priority="0";
                service = API.getClient().create(IDomain.class);
                call = service.AddDnsRecord(key, domainName,rec_type,rec_name,rec_value,priority);

                Toast.makeText(context, context.getString(R.string.dns) + context.getString(R.string.create_operation), Toast.LENGTH_SHORT).show();
                call.enqueue(new Callback<Reply>() {
                    @Override
                    public void onResponse(Call<Reply> call, Response<Reply> response) {
                        Notification.Show(context, R.drawable.domains, R.string.create_completed, response.body().message);
                        adapter.refresh();
                    }

                    @Override
                    public void onFailure(Call<Reply> call, Throwable t) {
                        call.cancel();
                        Toast.makeText(context, context.getString(R.string.somethingIsWrong), Toast.LENGTH_SHORT).show();
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
        AlertDialog alert = builder.create();
        alert.show();
    }
}