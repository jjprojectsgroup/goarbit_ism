package com.example.goarbit_ism.ui.profile;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.goarbit_ism.MainActivity;
import com.example.goarbit_ism.R;
import com.example.goarbit_ism.databinding.FragmentProfileBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;

    private EditText inputName = null;
    private EditText inputLastName = null;
    private EditText inputNickname = null;
    private EditText inputEmail = null;
    private EditText inputPassword1 = null;
    private EditText inputPassword2 = null;
    private DatabaseReference mDatabase;
    AlertDialog dialog = null;
    private ProgressDialog progress ;
    private FirebaseAuth mAuth;
    boolean result = false;
    private Button updateButton = null;
    FirebaseUser user = null;
    private String name = null;
    private String lastName = null;
    private String email = null;
    public static String TAG = "ProfileFragment";
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ProfileViewModel profileViewModel =
                new ViewModelProvider(this).get(ProfileViewModel.class);

        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        user = FirebaseAuth.getInstance().getCurrentUser();
        inputName = binding.name;
        inputLastName = binding.lastName;
        inputEmail = binding.email;
        inputPassword1 = binding.password1;
        inputPassword2 = binding.password2;
        progress = new ProgressDialog(getContext());

        updateButton = binding.updateButton;
        mDatabase = FirebaseDatabase.getInstance().getReference().child("users");



        updateButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (MainActivity.validarConexion(getContext())) {

                    progress.setMessage(getString(R.string.message_updating_data));
                    progress.setCanceledOnTouchOutside(false);
                    progress.show();
                    String password1 = inputPassword1.getText().toString();
                    String password2 = inputPassword2.getText().toString();

                    if (password1.length() == 0 || password2.length() == 0) {
                        updateProfile(false);
                        return;
                    }
                    if (password1.length() < 8 || password2.length() < 8) {
                        progress.dismiss();
                        showMessage(getString(R.string.failed_process), getString(R.string.password_must_be_8_characters_or_more), 0, getContext());
                        return;
                    }
                    if (password1.equals(password2)) {
                        validatePassword();
                    } else {
                        progress.dismiss();
                        showMessage(getString(R.string.failed_process), getString(R.string.message_passwords_not_match), 0, getContext());
                    }
                    // updateProfile(false);
                }else{
                    showMessage(getString(R.string.failed_process),getString(R.string.message_connection),1, getContext());
                }
            }

        });


        getUserProfile();
        return root;
    }

    public void showMessage(String title, String message, int tipo, Context context){
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        //alertDialog.setCancelable(false);
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setButton(context.getString(R.string.confirm), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                if (tipo == 1) {
                    Intent intent = new Intent(context, MainActivity.class);
                    startActivity(intent);
                }
            }
        });
        alertDialog.show();
    }

    private void updateProfile(boolean password) {
        if (user != null && inputName.getText().length() > 0 && inputLastName.getText().length() > 0) {

            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                    .setDisplayName(inputName.getText().toString()+" "+inputLastName.getText().toString())
                    .build();

            user.updateProfile(profileUpdates)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Map<String, Object> result = new HashMap<>();
                                result.put("name", inputName.getText().toString());
                                result.put("lastName", inputLastName.getText().toString());
                                mDatabase.child(user.getUid()).updateChildren(result);

                                if(password){
                                    String newPassword = inputPassword2.getText().toString();
                                    user.updatePassword(newPassword)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        Log.d(TAG, "User password updated.");
                                                    }
                                                }
                                            });
                                }
                                getUserProfile();
                                progress.dismiss();

                                showMessage(getString(R.string.successful_process),getString(R.string.user_data_updated_successfully),1, getContext());
                            }
                        }
                    });
        }
    }

    public void getUserProfile() {
        // [START get_user_profile]

        if (user != null) {

            boolean emailVerified = user.isEmailVerified();

            String uid = user.getUid();

            mDatabase.child(uid).get().addOnCompleteListener(task -> {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                }
                else {
                    Log.d("firebase", String.valueOf(task.getResult().getValue()));

                    name = String.valueOf(task.getResult().child("name").getValue());
                    email = String.valueOf(task.getResult().child("email").getValue());
                    lastName = String.valueOf(task.getResult().child("lastName").getValue());

                    inputName.setText(name);
                    inputLastName.setText(lastName);
                    inputEmail.setText(email);
                }
            });
        }
        // [END get_user_profile]
    }

    public void updatePassword(){
        String newPassword = inputPassword2.getText().toString();
        if(user == null){
            return;
        }
        user.updatePassword(newPassword)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "User password updated.");
                            showMessage(getString(R.string.failed_process),getString(R.string.password_updated),1, getContext());
                        }
                    }
                });
    }
    public void validatePassword() {
            //boolean result = false;

            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            View mView = getLayoutInflater().inflate(R.layout.layout_verify, null);
            final EditText txtPassword = (EditText) mView.findViewById(R.id.txtPassword);
            Button btnValidar = (Button) mView.findViewById(R.id.btnValidar);
            ImageButton btnSalir = (ImageButton) mView.findViewById(R.id.btnSalir);

            btnSalir.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });

            btnValidar.setOnClickListener(view -> {
                String password = txtPassword.getText().toString().trim();
                if(!password.isEmpty() && password.length() >= 8) {
                    progress.setMessage("Espere un Momento: ");
                    progress.setCanceledOnTouchOutside(false);
                    progress.show();
                    verifyPassword(password);
                }else {
                    progress.dismiss();
                    Toast.makeText(getContext(), getString(R.string.message_valid_password),
                            Toast.LENGTH_SHORT).show();
                }
            });

            builder.setView(mView);
            dialog = builder.create();
            dialog.show();

      //  return result;
    }

    private void verifyPassword(String password) {

        mAuth = FirebaseAuth.getInstance();
        mAuth.signInWithEmailAndPassword(Objects.requireNonNull(user.getEmail()), password)
                .addOnCompleteListener((Activity) requireContext(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signIn:success");
                            progress.dismiss();
                            updateProfile(true);
                            result(true);

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signIn:failure", task.getException());
                            progress.dismiss();
                            Toast.makeText(getContext(), getString(R.string.message_password_incorrect),
                                    Toast.LENGTH_SHORT).show();

                            result(false);
                        }
                    }
                });
        //return result;
    }


    private void result(boolean b) {
        result = b;

    }


    @Override
    public void onPause() {
        super.onPause();
        binding = null;
    }
}