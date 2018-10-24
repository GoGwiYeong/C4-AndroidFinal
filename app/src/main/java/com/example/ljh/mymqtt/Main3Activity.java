package com.example.ljh.mymqtt;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;

public class Main3Activity extends AppCompatActivity {
    Switch auto,light1,light2,fire,window;
    Button control1;
    String light1topic = "c4/light";
    String autotopic = "c4/auto";
    String windowtopic = "c4/window";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);
        auto = (Switch) findViewById(R.id.auto);
        light1 = (Switch) findViewById(R.id.light1);
        // light2 = (Switch) findViewById(R.id.light2);
       // fire = (Switch) findViewById(R.id.fire);
        window = (Switch) findViewById(R.id.window);
        control1 = (Button)findViewById(R.id.control1);
        Intent intent2 = getIntent();
        final String getwifi = intent2.getStringExtra("wifi");

        String clientId = MqttClient.generateClientId();
//"tcp://192.168.0.11:1883"
        //"tcp://" + getwifi "tcp://" + getwifi +":1883"
        final MqttAndroidClient client = new MqttAndroidClient(this.getApplicationContext(), "tcp://"+getwifi+":1883", clientId);

        //client.setCallback(new MqttCallbackHandler(client));//This is here for when a message is received

        MqttConnectOptions options = new MqttConnectOptions();

        //푸시 알림을 보내기위해 시스템에 권한을 요청하여 생성

        final NotificationManager notificationManager =
                (NotificationManager)Main3Activity.this.getSystemService
                        (Main3Activity.this.NOTIFICATION_SERVICE);
        final Intent intent = new Intent(Main3Activity.this.getApplicationContext(),Main3Activity.class);
        //Notification 객체 생성
        final Notification.Builder builder = new Notification.Builder(getApplicationContext());
//푸시 알림을 터치하여 실행할 작업에 대한 Flag 설정 (현재 액티비티를 최상단으로 올린다 | 최상단 액티비티를 제외하고 모든 액티비티를 제거한다)
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);

        try {
            options.setUserName("pi");
            options.setPassword("raspberry".toCharArray());
            IMqttToken token = client.connect(options);

            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    // We are connected
                    Log.d("mqtt", "onSuccess");

                    try {
                        client.subscribe(light1topic, 2, new IMqttMessageListener() {
                            @Override
                            public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {

                                final String lightState = new String(mqttMessage.getPayload());
                                runOnUiThread(new Runnable()
                                {
                                    public void run()
                                    {
                                        if (lightState.equals("1"))
                                            light1.setChecked(true);
                                        else
                                            light1.setChecked(false);
                                    }
                                });
                            }
                        });

                        client.subscribe(windowtopic, 2, new IMqttMessageListener() {
                            @Override
                            public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {

                                final String windowState = new String(mqttMessage.getPayload());
                                runOnUiThread(new Runnable()
                                {
                                    public void run()
                                    {
                                        if (windowState.equals("1"))
                                            window.setChecked(true);
                                        else
                                            window.setChecked(false);
                                    }
                                });
                            }
                        });
                    } catch (MqttException e) {
                        e.printStackTrace();
                    }
                    //-----------------------------------------------------------------------------------------------
                    //PUBLISH THE MESSAGE

                    light1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            if(isChecked){
                                // do something when check is selected
                                MqttMessage message = new MqttMessage("1".getBytes());
                                message.setQos(2);
                                message.setRetained(false);
                                try{
                                    client.publish(light1topic, message);
                                } catch (MqttPersistenceException e) {
                                    e.printStackTrace();
                                } catch (MqttException e) {
                                    e.printStackTrace();
                                }

                            }else {
                                // do somting when unchecked
                                MqttMessage message = new MqttMessage("0".getBytes());
                                message.setQos(2);
                                message.setRetained(false);

                                try{
                                    client.publish(light1topic, message);
                                } catch (MqttPersistenceException e) {
                                    e.printStackTrace();
                                } catch (MqttException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    });
                    auto.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            if(isChecked){
                                // do something when check is selected
                                MqttMessage message = new MqttMessage("1".getBytes());
                                message.setQos(2);
                                message.setRetained(false);
                                try{
                                    client.publish(autotopic, message);
                                } catch (MqttPersistenceException e) {
                                    e.printStackTrace();
                                } catch (MqttException e) {
                                    e.printStackTrace();
                                }

                            }else {
                                // do somting when unchecked
                                MqttMessage message = new MqttMessage("0".getBytes());
                                message.setQos(2);
                                message.setRetained(false);

                                try{
                                    client.publish(autotopic, message);
                                } catch (MqttPersistenceException e) {
                                    e.printStackTrace();
                                } catch (MqttException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    });

//-----------------------------------------------------------------------------------------------

                    window.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            if(isChecked){
                                // do something when check is selected
                                MqttMessage message = new MqttMessage("1".getBytes());
                                message.setQos(2);
                                message.setRetained(false);
                                try{
                                    client.publish(windowtopic, message);
                                } catch (MqttPersistenceException e) {
                                    e.printStackTrace();
                                } catch (MqttException e) {
                                    e.printStackTrace();
                                }

                            }else {
                                // do somting when unchecked
                                MqttMessage message = new MqttMessage("0".getBytes());
                                message.setQos(2);
                                message.setRetained(false);

                                try{
                                    client.publish(windowtopic, message);
                                } catch (MqttPersistenceException e) {
                                    e.printStackTrace();
                                } catch (MqttException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    });
//-----------------------------------------------------------------------------------------------
                    String subtopic  = "tester";
                    int qos = 1;
                    try {
                        IMqttToken subToken = client.subscribe(subtopic, qos);
                        subToken.setActionCallback(new IMqttActionListener() {
                            @Override
                            public void onSuccess(IMqttToken asyncActionToken) {
                                // The message was published
                                Log.i("mqtt", "subscription success");
                            }

                            @Override
                            public void onFailure(IMqttToken asyncActionToken,
                                                  Throwable exception) {
                                // The subscription could not be performed, maybe the user was not
                                // authorized to subscribe on the specified topic e.g. using wildcards
                                Log.i("mqtt", "subscription failed");

                            }
                        });



                    } catch (MqttException e) {
                        e.printStackTrace();
                    }

//---------------------------------------------------------------------------

                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    // Something went wrong e.g. connection timeout or firewall problems
                    Log.d("mqtt", "onFailure");

                }

            });


        } catch (MqttException e) {
            e.printStackTrace();
        }
        control1.setOnClickListener(new Button.OnClickListener(){

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Main3Activity.this,Main2Activity.class);
                intent.putExtra("wifi",getwifi.toString());
                startActivity(intent);
                finish();
            }
        });
    }

}