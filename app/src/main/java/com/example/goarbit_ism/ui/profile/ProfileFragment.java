package com.example.goarbit_ism.ui.profile;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.goarbit_ism.MainActivity;
import com.example.goarbit_ism.R;
import com.example.goarbit_ism.databinding.FragmentProfileBinding;
import com.example.goarbit_ism.ui.login.LoginActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;

    private EditText inputName = null;
    private EditText inputLastName = null;
    private EditText inputNickname = null;
    private EditText inputEmail = null;
    private EditText inputPassword1 = null;
    private EditText inputPassword2 = null;
    private DatabaseReference mDatabase;

    private Button updateButton = null;

    private String name = null;
    private String lastName = null;
    private String email = null;
    private String userName = null;
    public static String TAG = "PurchaseConfirmationDialog";
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ProfileViewModel profileViewModel =
                new ViewModelProvider(this).get(ProfileViewModel.class);

        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        inputName = binding.name;
        inputLastName = binding.lastName;
        inputNickname = binding.userName;
        inputEmail = binding.email;
        inputPassword1 = binding.password1;
        inputPassword2 = binding.password2;

        updateButton = binding.updateButton;
        mDatabase = FirebaseDatabase.getInstance().getReference().child("users");

        updateButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                updateProfile();
            }

        });

        if(inputPassword1!=null && inputPassword2!=null ){
            if(inputPassword1.toString().equals(inputPassword2.toString()) ) {
                updatePassword();
            }else{
               // onCreateDialog();
            }
        }
        getUserProfile();
        return root;
    }

    public class PurchaseConfirmationDialogFragment extends DialogFragment {
        @NonNull
        @Override
        public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
            return new AlertDialog.Builder(requireContext())
                    .setMessage("Las contraseñas ingresadas no coinciden")
                    .setPositiveButton(getString(android.R.string.ok), (dialog, which) -> {} )
                    .create();
        }


    }

    private void updateProfile() {
        FirebaseUser data = FirebaseAuth.getInstance().getCurrentUser();
        if (data != null && inputName.getText().length() > 0 && inputLastName.getText().length() > 0 && inputNickname.getText().length() > 0) {
         // mDatabase = FirebaseDatabase.getInstance().getReference();

         //  UpdateUser user = new UpdateUser(inputName.getText().toString(),inputLastName.getText().toString(),inputNickname.getText().toString());
            Map<String, Object> result = new HashMap<>();
            result.put("name", inputName.getText().toString());
            result.put("lastName", inputLastName.getText().toString());
            result.put("userName", inputNickname.getText().toString());
         //    Map<String, Object> postValues = user.toMap();

         //   Map<String, Object> childUpdates = new HashMap<>();
            mDatabase.child(data.getUid()).updateChildren(result);
        }

    }

    public void getUserProfile() {
        // [START get_user_profile]
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {

            boolean emailVerified = user.isEmailVerified();

            String uid = user.getUid();

            mDatabase.child(uid).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    if (!task.isSuccessful()) {
                        Log.e("firebase", "Error getting data", task.getException());
                    }
                    else {
                        Log.d("firebase", String.valueOf(task.getResult().getValue()));

                        name = String.valueOf(task.getResult().child("name").getValue());
                        email = String.valueOf(task.getResult().child("email").getValue());
                        lastName = String.valueOf(task.getResult().child("lastName").getValue());
                        userName = String.valueOf(task.getResult().child("userName").getValue());

                        inputName.setText(name);
                        inputLastName.setText(lastName);
                        inputNickname.setText(userName);
                        inputEmail.setText(email);
                    }
                }
            });
        }
        // [END get_user_profile]
    }

    public void updatePassword(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String newPassword = "SOME-SECURE-PASSWORD";

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



    @Override
    public void onPause() {
        super.onPause();
        binding = null;
    }
}