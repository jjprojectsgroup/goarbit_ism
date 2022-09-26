package com.example.goarbit_ism.ui.register;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.goarbit_ism.MainActivity;
import com.example.goarbit_ism.R;
import com.example.goarbit_ism.databinding.ActivityRegisterBinding;
import com.example.goarbit_ism.ui.condicion.CondicionActivity;
import com.example.goarbit_ism.ui.login.LoginActivity;
import com.example.goarbit_ism.ui.util.Constantes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
public class RegisterActivity extends AppCompatActivity {



    private ActivityRegisterBinding binding;

    private FirebaseAuth mAuth;
    FirebaseDatabase databaseRef = FirebaseDatabase.getInstance();
    private DatabaseReference db = databaseRef.getReference();

    private static final String TAG = "RegisterActivity";
    private ProgressDialog progress ;

    // final ProgressBar loadingProgressBar = binding.loading;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        progress = new ProgressDialog(this);

        final EditText nameEditText = binding.name;
        final EditText lastNameEditText = binding.lastName;
        final EditText emailEditText = binding.email;
        final EditText password1EditText = binding.password1;
        final EditText password2EditText = binding.password2;
        final Button singUpButton = binding.singUpButton;
        final CheckBox termCond = binding.checkBox;

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        singUpButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                if (MainActivity.validarConexion(RegisterActivity.this)) {

                    if (!nameEditText.getText().toString().isEmpty() && !lastNameEditText.getText().toString().isEmpty()
                           && !emailEditText.getText().toString().isEmpty()
                            && !password1EditText.getText().toString().isEmpty() && !password2EditText.getText().toString().isEmpty())  {
                        if (password1EditText.length() < 8 || password2EditText.length() < 8) {
                            message(getString(R.string.password_must_be_8_characters_or_more));
                            return;
                        }
                        if (!password1EditText.getText().toString().equals(password2EditText.getText().toString())) {
                            message(getString(R.string.message_passwords_not_match));
                            return;
                        }

                        if (!termCond.isChecked()) {
                        message(getString(R.string.message_accept_terms));
                        return;
                        }
                        progress.setMessage(getString(R.string.message_creating_account));
                        progress.setCanceledOnTouchOutside(false);
                        progress.show();
                        String[] usuario = new String[10];
                        usuario[0] = nameEditText.getText().toString();
                        usuario[1] = lastNameEditText.getText().toString();
                        usuario[2] = emailEditText.getText().toString();
                        createAccount(emailEditText.getText().toString(), password1EditText.getText().toString(), usuario);

                    }else{
                        message(getString(R.string.message_empty_field));
                    }
                }else{
                    message(getString(R.string.message_connection));
                }

            }
        });

        TextView mTextViewCondicion;
        mTextViewCondicion=findViewById(R.id.termino_condicion);

        mTextViewCondicion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(RegisterActivity.this, CondicionActivity.class);
                startActivity(intent);
            }
        });
    }

    private void createAccount(String username, String password, String[] usuario) {
        // [START create_user_with_email]
        mAuth.createUserWithEmailAndPassword(username, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "createUserWithEmail:success");
                    progress.setMessage(getString(R.string.message_successful_reg));
                    progress.dismiss();
                    FirebaseUser user = mAuth.getCurrentUser();
                    updateUI(user, usuario);
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "createUserWithEmail:failure", task.getException());
                    progress.dismiss();
                    message(getString(R.string.message_verify_valid));
                   // updateUI(null, null);

                }
            }
        });
        // [END create_user_with_email]
    }
    private void updateUI(FirebaseUser user, String[] usuario) {

        if (user != null && usuario != null) {

            String uid = user.getUid();

            String fecha = new SimpleDateFormat("yyyy/MM/dd").format(Calendar.getInstance().getTime());

            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                    .setDisplayName(usuario[0]+" "+usuario[1])
                    .setPhotoUri(Uri.parse(Constantes.photoUrl_New_User))
                    .build();

            user.updateProfile(profileUpdates)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                db.child("users").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                                        if (!task.isSuccessful()) {
                                            Log.e("firebase", "Error getting data", task.getException());
                                        }
                                        else {
                                            Log.d("firebase", String.valueOf(task.getResult().getValue()));
                                            long contador = task.getResult().getChildrenCount();
                                            Map<String, Object> result = new HashMap<>();
                                            result.put("name", usuario[0]);
                                            result.put("lastName", usuario[1]);
                                            result.put("email", usuario[2]);
                                            result.put("TyC", "1");
                                            result.put("fecha", fecha);
                                            result.put("photoUrl", Constantes.photoUrl_New_User);
                                            result.put("id", contador+1);
                                            result.put("active", "0");
                                            result.put("isPlaying", "0");
                                            db.child("users").child(user.getUid()).updateChildren(result);
                                            Log.d(TAG, "User profile updated.");
                                            //db.child("users").child(uid).setValue(new User(usuario[0], usuario[1],usuario[2], usuario[3], "1", fecha, constantes.photoUrl_New_User));
                                            Intent intent = new Intent( RegisterActivity.this, LoginActivity.class );
                                            startActivity(intent);
                                        }
                                    }
                                });

                            }
                        }
                    });
           // db.child("users").child(uid).setValue(new User(usuario[0], usuario[1],usuario[2], usuario[3], "1", fecha, constantes.photoUrl_New_User));
        }

    }

    public void message( String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message)
                .setNegativeButton(("Ok"), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        builder.show();
    }

}