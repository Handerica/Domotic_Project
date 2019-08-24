package com.example.domotic_project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Locale;

public class Sala extends AppCompatActivity {
    TextView txtLuces;
    Switch swtLuces;
    TextView txtVentilador;
    Switch swtVentilador;
    private DatabaseReference housetic;
    TTSManager ttsManager=null;
    Cambio_base fire = new Cambio_base();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sala);
        ttsManager=new TTSManager();
        ttsManager.init(this);
        txtLuces = (TextView) findViewById(R.id.textView_Luces1);
        txtVentilador = (TextView) findViewById(R.id.textView_Ventilador1);
        swtVentilador= (Switch) findViewById(R.id.switch_Ventilador1);
        swtLuces = (Switch) findViewById(R.id.switch_Luces1);


        housetic = FirebaseDatabase.getInstance().getReference();
        housetic.child("sala").addValueEventListener(new ValueEventListener() {
            @Override

            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    String resultadoConsultaLuces= dataSnapshot.child("luces").getValue().toString();
                    if (resultadoConsultaLuces.equalsIgnoreCase("OFF")) {
                        swtLuces.setChecked(false);
                        txtLuces.setText("Desactivado");
                        Toast.makeText(Sala.this,"Conexión Establecida", Toast.LENGTH_LONG).show();
                    } else {
                        swtLuces.setChecked(true);
                        txtLuces.setText("Activado");
                        Toast.makeText(Sala.this,"Conexión Establecida", Toast.LENGTH_LONG).show();
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
        housetic.child("sala").addValueEventListener(new ValueEventListener() {
            @Override

            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    String resultadoConsultaVentilador= dataSnapshot.child("ventilador").getValue().toString();
                    if (resultadoConsultaVentilador.equalsIgnoreCase("OFF")) {
                        swtVentilador.setChecked(false);
                        txtVentilador.setText("Desactivado");
                        Toast.makeText(Sala.this,"Conexión Establecida", Toast.LENGTH_LONG).show();
                    } else {
                        swtVentilador.setChecked(true);
                        txtVentilador.setText("Activado");
                        Toast.makeText(Sala.this,"Conexión Establecida", Toast.LENGTH_LONG).show();
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

    }

    public void estaciones(View v) {
        Intent i = new Intent(this, Estaciones.class);
        startActivity(i);

    }


    public void onclicVentilador(View view){

        if (view.getId() == R.id.switch_Ventilador1){
            if (swtVentilador.isChecked()){
                txtVentilador.setText("Activado");
                fire.FireCambioBase("sala","ventilador","ON");
                ttsManager.initQueue("Ventilador activado");
                Toast.makeText(Sala.this,"Conexión Establecida", Toast.LENGTH_LONG).show();
            }else{
                txtVentilador.setText("Desactivado");
                fire.FireCambioBase("sala","ventilador","OFF");
                ttsManager.initQueue("Ventilador desactivado");
                Toast.makeText(Sala.this,"Conexión Establecida", Toast.LENGTH_LONG).show();
            }
        }
    }

    public void onclicLuces(View view){

        if (view.getId() == R.id.switch_Luces1){
            if (swtLuces.isChecked()){
                txtLuces.setText("Activado");
                fire.FireCambioBase("sala","luces","ON");
                ttsManager.initQueue("Luces activadas");
                Toast.makeText(Sala.this,"Conexión Establecida", Toast.LENGTH_LONG).show();
            }else{
                txtLuces.setText("Desactivado");
                fire.FireCambioBase("sala","luces","OFF");
                ttsManager.initQueue("Luces desactivadas");
                Toast.makeText(Sala.this,"Conexión Establecida", Toast.LENGTH_LONG).show();
            }
        }
    }


    public void getInputSpeech(View view){
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        if(intent.resolveActivity(getPackageManager())!= null){
            startActivityForResult(intent, 10);
        } else {
            mensaje("Tu dispositivo no reconoce entrada de voz");
        }
    }

    public void mensaje(String text){
        Toast.makeText(Sala.this,text, Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        switch (requestCode){
            case 10:
                if(resultCode == RESULT_OK && data != null){
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    String comando = result.get(0);
                    if(comando.equalsIgnoreCase("Encender luces")){
                        fire.FireCambioBase("sala","luces","ON");
                        ttsManager.initQueue("Luces encendidas");
                        mensaje("Luces encendidas");
                    } else if( comando.equalsIgnoreCase("Apagar luces")){
                        fire.FireCambioBase("sala","luces","OFF");
                        ttsManager.initQueue("Luces apagadas");
                        mensaje("Luces apagadas");
                    }else if(comando.equalsIgnoreCase("Encender ventilador")){
                        fire.FireCambioBase("sala","ventilador","ON");
                        ttsManager.initQueue("Ventilador encendido");
                        mensaje("Ventilador encendido");
                    } else if( comando.equalsIgnoreCase("Apagar ventilador")){
                        fire.FireCambioBase("sala","ventilador","OFF");
                        ttsManager.initQueue("Ventilador apagado");
                        mensaje("Ventilador apagado");
                    }
                }
                break;
        }
    }
}
