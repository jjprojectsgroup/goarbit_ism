package com.example.goarbit_ism.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.goarbit_ism.R;
import com.example.goarbit_ism.ui.login.LoginActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.skydoves.elasticviews.ElasticCheckButton;

public class    RecuperarActivity extends AppCompatActivity {

    private TextInputEditText gmail;
    private ElasticCheckButton recuperar;
    private ProgressDialog progress;
    private String correo;
    FirebaseAuth auth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recuperar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        gmail = findViewById(R.id.gmail);
        recuperar = findViewById(R.id.btnRecuperar);

        auth = FirebaseAuth.getInstance();

        progress = new ProgressDialog(this);

        recuperar();
    }

    private void recuperar() {
        System.out.println("entroo ///////////////////////////////////////////////////");
        recuperar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                correo = gmail.getText().toString().trim();
                if(!correo.isEmpty()) {
                    progress.setMessage("Espere un Momento: ");
                    progress.setCanceledOnTouchOutside(false);
                    progress.show();
                    enviarCorreo();
                }else {
                    Toast.makeText(RecuperarActivity.this, "Es necesario ingresar un correo electronico",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void enviarCorreo() {
        auth.setLanguageCode("es");
        auth.sendPasswordResetEmail(correo).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
           if(task.isSuccessful()){
               Toast.makeText(RecuperarActivity.this, "Por favor revise su correo para restaurar contraseña",
                       Toast.LENGTH_SHORT).show();
               Intent i = new Intent(RecuperarActivity.this, LoginActivity.class);
               startActivity(i);
               finish();
           }else{
               Toast.makeText(RecuperarActivity.this, "El correo no se pudo enviar",
                       Toast.LENGTH_SHORT).show();
               progress.dismiss();
           }
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}