package com.example.goarbit_ism.ui.login;

import static java.lang.Thread.sleep;

import android.app.Activity;

import androidx.annotation.NonNull;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.goarbit_ism.ui.register.RegisterActivity;
import com.example.goarbit_ism.MainActivity;
import com.example.goarbit_ism.R;
import com.example.goarbit_ism.databinding.ActivityLoginBinding;
import com.example.goarbit_ism.ui.user.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.skydoves.elasticviews.ElasticCheckButton;

public class LoginActivity extends AppCompatActivity {

    private LoginViewModel loginViewModel;
    private ActivityLoginBinding binding;


    public ElasticCheckButton recuperar;
    public String correo;
    FirebaseAuth auth;


    private ProgressDialog progress ;
    private FirebaseAuth mAuth;
    private static final String TAG = "EmailPassword";
AlertDialog dialog = null;


    String url_Facebook = "https://www.facebook.com/Joan.invertir";
    String url_Web ="https://invertirsinmiedo.com/";
            String url_Instagram ="https://www.instagram.com/invertir_sinmiedo/";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        progress = new ProgressDialog(this);
        auth = FirebaseAuth.getInstance();

        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            Intent intent = new Intent( LoginActivity.this, MainActivity.class );
            startActivity(intent);
            System.out.println("sesion ya iniciada por el usuario "+user.getDisplayName());
        }

        loginViewModel = new ViewModelProvider(this, new LoginViewModelFactory())
                .get(LoginViewModel.class);

        final EditText usernameEditText = binding.email;
        final EditText passwordEditText = binding.password;
        final Button loginButton = binding.loginButton;
        final TextView createAccountButton = binding.createAccountButton;
        final TextView forgotPassword = binding.forgotPassword;

        final ImageButton btn_Facebook= binding.buttonFacebook;
        final ImageButton btn_Web= binding.buttonWeb;
        final ImageButton btn_Instagram= binding.buttonInstagram;

        loginViewModel.getLoginFormState().observe(this, new Observer<LoginFormState>() {
            @Override
            public void onChanged(@Nullable LoginFormState loginFormState) {
                if (loginFormState == null) {
                    return;
                }
                loginButton.setEnabled(loginFormState.isDataValid());
                if (loginFormState.getUsernameError() != null) {
                    usernameEditText.setError(getString(loginFormState.getUsernameError()));
                }
                if (loginFormState.getPasswordError() != null) {
                    passwordEditText.setError(getString(loginFormState.getPasswordError()));
                }
            }
        });

        loginViewModel.getLoginResult().observe(this, new Observer<LoginResult>() {
            @Override
            public void onChanged(@Nullable LoginResult loginResult) {
                if (loginResult == null) {
                    return;
                }

                if (loginResult.getError() != null) {
                    showLoginFailed(loginResult.getError());
                }
                if (loginResult.getSuccess() != null) {
                    loginButton.performClick();
                   // updateUiWithUser(loginResult.getSuccess());
                }
                setResult(Activity.RESULT_OK);

                //Complete and destroy login activity once successful
             //   finish();
            }
        });

        TextWatcher afterTextChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // ignore
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // ignore
            }

            @Override
            public void afterTextChanged(Editable s) {
                loginViewModel.loginDataChanged(usernameEditText.getText().toString(),
                        passwordEditText.getText().toString());
            }
        };
        usernameEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    loginViewModel.login(usernameEditText.getText().toString(),
                            passwordEditText.getText().toString());
                }
                return false;
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                login(usernameEditText.getText().toString(),
                        passwordEditText.getText().toString());


            }
        });

        createAccountButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        forgotPassword.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                View mView = getLayoutInflater().inflate(R.layout.layout_recuperar, null);
              final EditText txtMail = (EditText) mView.findViewById(R.id.txtMail);
              Button btnRecuperar = (Button) mView.findViewById(R.id.btnRecuperar);
                ImageButton btnSalir = (ImageButton) mView.findViewById(R.id.btnSalir);

                btnSalir.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        dialog.dismiss();
                    }
                });

   btnRecuperar.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        String email = txtMail.getText().toString().trim();
        if(!email.isEmpty()) {

            progress.setMessage("Espere un Momento: ");
            progress.setCanceledOnTouchOutside(false);
            progress.show();
            enviarCorreo(email);
        }else {
            Toast.makeText(LoginActivity.this, "Es necesario ingresar un correo electronico",
                    Toast.LENGTH_SHORT).show();
        }
    }
});

                builder.setView(mView);
                 dialog = builder.create();
                dialog.show();

            }
        });


       btn_Facebook.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                Uri url_F = Uri.parse(url_Facebook);
                Intent link_F = new Intent(Intent.ACTION_VIEW,url_F);
                startActivity(link_F);
            }
        });

        btn_Web.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                Uri url_W = Uri.parse(url_Web);
                Intent link_W = new Intent(Intent.ACTION_VIEW,url_W);
                startActivity(link_W);
            }
        });
        btn_Instagram.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                Uri url_I = Uri.parse(url_Instagram);
                Intent link_I = new Intent(Intent.ACTION_VIEW,url_I);
                startActivity(link_I);
            }
        });

    }

    private void enviarCorreo(String email) {
        auth.setLanguageCode("es");
        auth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(LoginActivity.this, "Por favor revise su correo para restaurar contraseña",
                            Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    progress.dismiss();
                }else{
                    Toast.makeText(LoginActivity.this, "El correo no se pudo enviar",
                            Toast.LENGTH_SHORT).show();
                             progress.dismiss();
                }
            }
        });

    }

    private void updateUiWithUser(LoggedInUserView model) {
        String welcome = getString(R.string.welcome) + model.getDisplayName();
        // TODO : initiate successful logged in experience
        Toast.makeText(getApplicationContext(), welcome, Toast.LENGTH_LONG).show();
    }

    private void showLoginFailed(@StringRes Integer errorString) {
        Toast.makeText(getApplicationContext(), errorString, Toast.LENGTH_SHORT).show();
    }

    // [START on_start_check_user]
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            reload();
        }
    }

    private void reload() { }
    // [END on_start_check_user]

    private void createAccount(String username, String password) {
        // [START create_user_with_email]
        mAuth.createUserWithEmailAndPassword(username, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "createUserWithEmail:success");
                    Toast.makeText(LoginActivity.this, "Registro Exitoso.",
                            Toast.LENGTH_SHORT).show();
                    FirebaseUser user = mAuth.getCurrentUser();
                    updateUI(user);
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "createUserWithEmail:failure", task.getException());
                    Toast.makeText(LoginActivity.this, "Registro Fallido.",
                            Toast.LENGTH_SHORT).show();
                    updateUI(null);
                }
            }
        });
        // [END create_user_with_email]
    }
    private void updateUI(FirebaseUser user) {
       // new User(user.get)
    }

    private void login(String email, String password) {
        // [START sign_in_with_email]
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            Toast.makeText(LoginActivity.this, "Ingreso Exitoso.",
                                    Toast.LENGTH_SHORT).show();
                            FirebaseUser user = mAuth.getCurrentUser();
                          //  updateUI(user);
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Ingreso Fallido.",
                                    Toast.LENGTH_SHORT).show();
                            //updateUI(null);
                        }
                    }

                });

        // [END sign_in_with_email]
    }

    public void signOut() {
        // [START auth_sign_out]
        FirebaseAuth.getInstance().signOut();
        // [END auth_sign_out]
    }

    public void getUserProfile() {
        // [START get_user_profile]
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // Name, email address, and profile photo Url
            String name = user.getDisplayName();
            String email = user.getEmail();
            Uri photoUrl = user.getPhotoUrl();

            // Check if user's email is verified
            boolean emailVerified = user.isEmailVerified();

            // The user's ID, unique to the Firebase project. Do NOT use this value to
            // authenticate with your backend server, if you have one. Use
            // FirebaseUser.getIdToken() instead.
            String uid = user.getUid();
        }
        // [END get_user_profile]
    }

    private void sendEmailVerification() {
        // Send verification email
        // [START send_email_verification]
        final FirebaseUser user = mAuth.getCurrentUser();
        user.sendEmailVerification()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // Email sent
                    }
                });
        // [END send_email_verification]
    }


}
