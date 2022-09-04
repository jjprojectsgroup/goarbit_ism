package com.example.goarbit_ism.ui.register;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.goarbit_ism.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.example.goarbit_ism.databinding.ActivityRegisterBinding;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;



public class RegisterActivity extends AppCompatActivity {

    private ActivityRegisterBinding binding;

    private FirebaseAuth mAuth;
    FirebaseDatabase databaseRef = FirebaseDatabase.getInstance();
    private DatabaseReference db = databaseRef.getReference();

    private static final String TAG = "EmailPassword";


    // final ProgressBar loadingProgressBar = binding.loading;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        final EditText nameEditText = binding.name;
        final EditText lastNameEditText = binding.lastName;
        final EditText userNameEditText = binding.userName;
        final EditText emailEditText = binding.email;
        final EditText password1EditText = binding.password1;
        final EditText password2EditText = binding.password2;
        final Button singUpButton = binding.singUpButton;

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        singUpButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                //loadingProgressBar.setVisibility(View.VISIBLE);
                // Toast.makeText(LoginActivity.this,"Versión 1.0.", Toast.LENGTH_SHORT).show();



               // databaseRef.child("users").child("miles").setValue(user);
               if(!nameEditText.getText().toString().isEmpty() && !lastNameEditText.getText().toString().isEmpty()
                && !userNameEditText.getText().toString().isEmpty() && !emailEditText.getText().toString().isEmpty()
                && !password1EditText.getText().toString().isEmpty() && !password2EditText.getText().toString().isEmpty() && password1EditText.getText().toString().equals(password2EditText.getText().toString())){
                   String[] data = new String[]{
                           "name :" + nameEditText.getText().toString(),
                           "lastName: " + lastNameEditText.getText().toString(),
                           "userName: " + userNameEditText.getText().toString(),
                           "email:" + emailEditText.getText().toString(),
                           "password:" + password1EditText.getText().toString(),
                           "terminos:" + "1",
                   };
                    createAccount(emailEditText.getText().toString(), password1EditText.getText().toString(), data);
                }
            }
        });

    }

    private void createAccount(String username, String password, String[] data) {
        // [START create_user_with_email]
        mAuth.createUserWithEmailAndPassword(username, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "createUserWithEmail:success");
                    Toast.makeText(RegisterActivity.this, "Registro Exitoso.",
                            Toast.LENGTH_SHORT).show();
                    FirebaseUser user = mAuth.getCurrentUser();
                    updateUI(user, data);
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "createUserWithEmail:failure", task.getException());
                    Toast.makeText(RegisterActivity.this, "Registro Fallido.",
                            Toast.LENGTH_SHORT).show();
                    updateUI(null, null);
                }
            }
        });
        // [END create_user_with_email]
    }
    private void updateUI(FirebaseUser user, String[] data) {

        if (user != null && data != null) {
            // Name, email address, and profile photo Url
           // String name = user.getDisplayName();
           // String email = user.getEmail();
            //Uri photoUrl = user.getPhotoUrl();

            // Check if user's email is verified
           // boolean emailVerified = user.isEmailVerified();
            Toast.makeText(RegisterActivity.this, "entro en registrar datos.",
                    Toast.LENGTH_SHORT).show();
            // The user's ID, unique to the Firebase project. Do NOT use this value to
            // authenticate with your backend server, if you have one. Use
            // FirebaseUser.getIdToken() instead.
            String uid = user.getUid();


          //  JSONObject jsonObject = new JsonParser().parse(data).getAsJsonObject();

           // System.out.println(jsonObject.get("name").getAsString()); //John
            //System.out.println(jsonObject.get("age").getAsInt()); //21
            db.child("users").child(uid).setValue(data);
        }

    }



}