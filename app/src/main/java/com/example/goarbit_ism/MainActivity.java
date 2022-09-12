package com.example.goarbit_ism;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.goarbit_ism.databinding.ActivityMainBinding;
import com.example.goarbit_ism.ui.login.LoginActivity;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

public class MainActivity extends AppCompatActivity {
    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;
    ActionBar actionBar;
    private CircleImageView imageUser = null;
    private ImageView imageUserProfile = null;

    private TextView textName = null;
    private TextView textEmail = null;
    boolean datosPerfil = false;
    String viewUpdateName = null;

    UploadTask uploadTask;
    private static final String TAG = "EmailPassword";

    FirebaseStorage storageRef = FirebaseStorage.getInstance();
    private StorageReference dbstorage = storageRef.getReference();

    FirebaseDatabase databaseRef = FirebaseDatabase.getInstance();
    private DatabaseReference db = databaseRef.getReference();
//////
    ProgressDialog cargando;
    Bitmap thumb_bitmap = null;
    private DatabaseReference imgReference;
    private StorageReference storageReference;
/////

    String url_Whatsapp = "https://wa.me/573004761225";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarMain.toolbar);
        binding.appBarMain.btnwhatsapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            System.out.println("sesion ya iniciada por el usuario "+user.getDisplayName());

        } else {
            Intent intent = new Intent( MainActivity.this, LoginActivity.class );
            startActivity(intent);
            System.out.println("no hay sesion activa ");

        }
        actionBar = getSupportActionBar();
        ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#2A2971"));
        actionBar.setBackgroundDrawable(colorDrawable);

        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_goarbit, R.id.nav_calculator, R.id.nav_news, R.id.nav_profile)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);


        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        final ImageButton btnwhatsapp= binding.appBarMain.btnwhatsapp;
        btnwhatsapp.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                Uri url_W = Uri.parse(url_Whatsapp);
                Intent link_W = new Intent(Intent.ACTION_VIEW,url_W);
                startActivity(link_W);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_about:
                about();
                return true;
            case R.id.action_logout:
                logout();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void logout() {
        Toast.makeText(MainActivity.this, "entro en logout.",
                Toast.LENGTH_SHORT).show();
        System.out.println("Sesion Cerrada.");
     //   FirebaseAuth.getInstance().signOut();
        validacion();
     /*   Intent intent = new Intent( MainActivity.this, LoginActivity.class );
        startActivity(intent);*/
    }

    private void validacion() {
        new AlertDialog.Builder(this)
              //  .setIcon(R.drawable.alacran)
                .setTitle("¿Realmente desea cerrar sesion?")
                .setCancelable(false)
                .setNegativeButton(android.R.string.cancel, null)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {// un listener que al pulsar, cierre la aplicacion
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FirebaseAuth.getInstance().signOut();
                        Intent intent = new Intent( MainActivity.this, LoginActivity.class );
                        startActivity(intent);
                        //android.os.Process.killProcess(android.os.Process.myPid()); //Su funcion es algo similar a lo que se llama cuando se presiona el botón "Forzar Detención" o "Administrar aplicaciones", lo cuál mata la aplicación
                        //finish(); Si solo quiere mandar la aplicación a segundo plano
                    }
                }).show();
    }

    private void about() {
        Toast.makeText(MainActivity.this, "entro en about.",
                Toast.LENGTH_SHORT).show();
        System.out.println("entro en about.");

    }

    public void cargarImagen(){
        Intent galeria = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galeria.setType("image/");
        startActivityForResult(getIntent().createChooser(galeria, "Selecione la Imagen"),10);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            Uri imgUri =  data.getData();
           String ruta = getRealPathFromURI(imgUri);

            // final File imageurl = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/myCompressor");
            final File url = new File(ruta);
            //comprimiendo imagen
            try {
                thumb_bitmap = new Compressor(MainActivity.this)
                        .setMaxWidth(640)
                        .setMaxHeight(480)
                        .setQuality(90)
                        .compressToBitmap(url);
            }catch (IOException e) {
                e.printStackTrace();
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            thumb_bitmap.compress(Bitmap.CompressFormat.JPEG,90,out);
            final byte[] thumb_byte = out.toByteArray();
            ///fin del compresor
            cargando.setTitle("Subiendo foto de perfil..");
            cargando.setMessage("Espere por favor...");
            cargando.show();

            imageUser = (CircleImageView) findViewById(R.id.imageUser);
         //   Uri ruta = data.getData();
            imageUser.setImageURI(imgUri);
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

            updateUI(user,imgUri,thumb_byte);
        }
    }

    public String getRealPathFromURI(Uri contentUri){
        String[] proj = { MediaStore.Audio.Media.DATA };
        Cursor cursor = managedQuery(contentUri, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
        cursor.moveToFirst();

        return cursor.getString(column_index);
    }

    private void updateUI(FirebaseUser user, Uri ruta, byte[] thumb_byte) {

        if (user != null && ruta != null) {
            Toast.makeText(MainActivity.this, "entro en registrar datos.",
                    Toast.LENGTH_SHORT).show();

            String uid = user.getUid();
            final StorageReference ref = dbstorage.child("users/" + ruta.getLastPathSegment());
            uploadTask = ref.putBytes(thumb_byte);
            Task<Uri> uriTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw Objects.requireNonNull(task.getException());
                    }
                    return ref.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        Toast.makeText(MainActivity.this, "archivo cargado exitosamente.",
                                Toast.LENGTH_SHORT).show();
                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                .setPhotoUri(downloadUri)
                                .build();

                        user.updateProfile(profileUpdates)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Log.d(TAG, "User profile updated.");
                                        }
                                    }
                                });
                        db.child("users").child(uid).child("photoUrl").setValue(downloadUri.toString());
                        cargando.dismiss();
                    } else {
                        cargando.dismiss();
                        Toast.makeText(MainActivity.this, "fallo la subida del archivo",
                                Toast.LENGTH_SHORT).show();
                        // Handle failures
                        // ...
                    }
                    // imgReference.push().child()
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        imgReference = databaseRef.getReference().child("Fotos_subidas");
        storageReference = storageRef.getReference().child("img_comprimidas");
        cargando = new ProgressDialog(MainActivity.this);
        imageUser = (CircleImageView) findViewById(R.id.imageUser);


        imageUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cargarImagen();
            }
        });



        if(!datosPerfil) {
            getUserProfile();
            //   photo();
        }
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    public void getUserProfile() {
        // [START get_user_profile]
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            datosPerfil=true;
            // Name, email address, and profile photo Url
            String name = user.getDisplayName();
            String email = user.getEmail();
            Uri photoUrl = user.getPhotoUrl();
            System.out.println("Photo URL2: " + photoUrl);
            Glide.with(this).load(photoUrl)
                    .apply(new RequestOptions()
                            .placeholder(R.mipmap.ic_launcher)
                            .override(640, 480)
                            .centerCrop()
                            .dontAnimate()
                            .dontTransform().diskCacheStrategy(DiskCacheStrategy.RESOURCE))
                    .thumbnail(.5f)
                    .into(imageUser);
            // Check if user's email is verified
            boolean emailVerified = user.isEmailVerified();

            String uid = user.getUid();
            textName = (TextView) findViewById(R.id.textName);
            textEmail = (TextView) findViewById(R.id.textEmail);
            textName.setText(name);
            textEmail.setText(email);

        }
        // [END get_user_profile]
    }

}
