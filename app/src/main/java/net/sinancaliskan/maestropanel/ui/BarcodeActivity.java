package net.sinancaliskan.maestropanel.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import net.sinancaliskan.maestropanel.API;
import net.sinancaliskan.maestropanel.R;
import net.sinancaliskan.maestropanel.interfaces.IUser;
import net.sinancaliskan.maestropanel.models.Reply;
import net.sinancaliskan.maestropanel.models.pojo.UserObjects.User;
import net.sinancaliskan.maestropanel.utils.SharedInformation;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BarcodeActivity extends AppCompatActivity {
    private IUser service =null;                                                                  //define user service
    private IntentIntegrator qrScan;                                                              //define barcode reader
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barcode);                                                //set layout file
        qrScan = new IntentIntegrator(this);                                                      //create new barcode reader with application context

        final TextView txtQR= (TextView) findViewById(R.id.txtQR);                                //define and set txtQr textview component
        final TextView txtServer= (TextView) findViewById(R.id.txtServer);                        //define and set txtServer textview component
        final ToggleButton tbAuto= (ToggleButton) findViewById(R.id.tbAuto);                      //define and set tbAuto togglebutton component
        Button btnQR= (Button) findViewById(R.id.btnReadQR);                                      //define and set btnQR button component
        Button btnLogin= (Button) findViewById(R.id.btnLogin);                                    //define and set btnLogin button component

        txtQR.setText(SharedInformation.getData("serverKey"));                                    //read serverKey in preference and set value
        txtServer.setText(SharedInformation.getData("serverIP"));                                 //read serverIP in preference and set value
        tbAuto.setChecked(SharedInformation.getBoolean("auto"));                                  //read autoLogin in preference and set value
                                                                                                  //create btnQr click listener and when click run qrScan.initiateScan method
        btnQR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                qrScan.initiateScan();
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {                                  //create btnLogin click listener and when click check api key with server ip
            @Override
            public void onClick(View v) {
                service = API.getClient(txtServer.getText().toString()).create(IUser.class);      //create retrofit component with server ip

                Call<Reply> call= service.Whoami(txtQR.getText().toString());                     //calling whoami method with api key it returns user information
                call.enqueue(new Callback<Reply>() {
                    @Override
                    public void onResponse(Call<Reply> call, Response<Reply> response) {
                        ObjectMapper mapper=new ObjectMapper();                                     //creating objectmapper for convert json to class
                        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false); //set doesnt fail when doesnt parse to class
                        User user=mapper.convertValue(response.body().details,User.class);          //set object to model
                        if (user!=null){
                            SharedInformation.saveData("serverKey",txtQR.getText().toString());   //save api key in user sharedpreferences
                            SharedInformation.saveData("serverIP",txtServer.getText().toString());//save server ip in user sharedpreferences
                           if (tbAuto.isChecked())
                               SharedInformation.saveData("auto",true);                           //save autologin in user sharedpreferences
                            String language=SharedInformation.getData("language");                                    //read sharedpreference user language settings
                            switch (language){                                                                        //default language is english
                                case "Türkçe":
                                    SharedInformation.changeLanguage("tr");                                           //change locale language
                                    break;
                            }
                            Intent intent=new Intent(getApplicationContext(),MainActivity.class); //creating new intent typeof MainActivity
                            intent.putExtra("model",user);                                         //adding model for MainActivity for reuse user object
                            startActivity(intent);                                                //starting DomainActivity
                            finish();
                        }
                        else
                            Toast.makeText(BarcodeActivity.this, getString(R.string.somethingIsWrong), Toast.LENGTH_SHORT).show();//show message for when occur error
                    }
                    @Override
                    public void onFailure(Call<Reply> call, Throwable throwable) {
                        call.cancel();
                        Toast.makeText(BarcodeActivity.this, getString(R.string.somethingIsWrong), Toast.LENGTH_SHORT).show();    //show message for call is failure error
                    }
                });
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);  //get barcode screen result
        if (result != null) {
            if (result.getContents() == null) {                                                     //check result has a contents it contains server key
                Toast.makeText(this, getString(R.string.not_found_result), Toast.LENGTH_LONG).show();
            } else {
                try {
                    Toast.makeText(this, result.getContents(), Toast.LENGTH_SHORT).show();
                    TextView txt= (TextView) findViewById(R.id.txtQR);                              //define and set txtQr textview
                    txt.setText(result.getContents());                                              //set returned api key
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(this, result.getContents(), Toast.LENGTH_LONG).show();
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);                                  //call IntentIntegrator.onActivityResult method
        }
    }
    @Override
    public void onBackPressed(){return;}                                                          //cancel back button action
}