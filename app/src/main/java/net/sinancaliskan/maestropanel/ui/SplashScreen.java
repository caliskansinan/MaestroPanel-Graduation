package net.sinancaliskan.maestropanel.ui;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import net.sinancaliskan.maestropanel.API;
import net.sinancaliskan.maestropanel.R;
import net.sinancaliskan.maestropanel.interfaces.IUser;
import net.sinancaliskan.maestropanel.models.Reply;
import net.sinancaliskan.maestropanel.models.pojo.UserObjects.User;
import net.sinancaliskan.maestropanel.utils.SharedInformation;


import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static java.lang.Thread.sleep;
                        //inherited AppCompatActivity
public class SplashScreen extends AppCompatActivity {
    private IUser service=null;                                                                   //define retrofit service for user
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(android.R.style.Theme_Holo_NoActionBar);                                         //set activity theme
        setContentView(R.layout.activity_splash_screen);                                          //set layout file
        String language=SharedInformation.getData("language");                                    //read sharedpreference user language settings
        switch (language){                                                                        //default language is english
            case "Türkçe":
                SharedInformation.changeLanguage("tr");                                           //change locale language
                break;
        }
        Thread thread=new Thread(){
            @Override
            public void run() {                                                                  //create thread for check operations
                boolean autoLogin=SharedInformation.getBoolean("auto");                           //read autologin settings
                if (autoLogin)
                {
                    String key=SharedInformation.getData("serverKey");                            //read api key
                    String ip=SharedInformation.getData("serverIP");                              //read server address
                    service= API.getClient(ip).create(IUser.class);                               //create retrofit component

                    Call<Reply> call=service.Whoami(key);                                         //calling whoami method it returns user information
                    call.enqueue(new Callback<Reply>() {                                          //adding queue to callback
                        @Override
                        public void onResponse(Call<Reply> call, Response<Reply> response) {      //when callback return response
                            ObjectMapper mapper=new ObjectMapper();                               //creating objectmapper for convert json to class
                            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);//set doesnt fail when doesnt parse to class
                            User user=mapper.convertValue(response.body().details,User.class);    //set object to model
                            if (user!=null)
                                startMainActivity(user);                                          //call startMainActivity with user information
                            else
                                startBarcodeActivity();                                           //call startBarcodeActivity
                        }
                        @Override
                        public void onFailure(Call<Reply> call, Throwable throwable) {
                            call.cancel();                                                        //cancel callback method
                            startBarcodeActivity();                                               //call startBarcodeActivity
                        }
                    });
                }
                else
                    startBarcodeActivity();                                                       //call startBarcodeActivity
            }
        };
        thread.start();
    }
    void startBarcodeActivity(){
        try {
            sleep(2000);                                                                          //sleep thread for showing picture
            startActivity(new Intent(getApplicationContext(),BarcodeActivity.class));              //creating new intent typeof BarcodeActivity and start
            finish();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    void startMainActivity(User user){
        try {
            sleep(200);                                                                           //sleep thread for showing picture
            Intent intent=new Intent(getApplicationContext(),MainActivity.class);                 //creating new intent typeof MainActivity
            intent.putExtra("model",user);                                                        //adding model for MainActivity for reuse user object
            startActivity(intent);                                                                //starting DomainActivity
            finish();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}