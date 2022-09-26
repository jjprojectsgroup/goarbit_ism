package com.example.goarbit_ism;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
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
import com.example.goarbit_ism.ui.util.Constantes;
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

    UploadTask uploadTask;
    private static final String TAG = "EmailPassword";
    private static final int CODIGO_PERMISOS_STORAGE = 1;
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
                if (validarConexion(MainActivity.this)){
                    Uri url_W = Uri.parse(Constantes.url_Whatsapp);
                Intent link_W = new Intent(Intent.ACTION_VIEW, url_W);
                startActivity(link_W);
                }else{
                    Toast.makeText(MainActivity.this, getString(R.string.message_connection),
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                Bundle extras = new Bundle();
        if(validarConexion(MainActivity.this)){


        }else{
            Toast.makeText(MainActivity.this, getString(R.string.message_connection),
                    Toast.LENGTH_SHORT).show();

            return true;
        }
                int id=menuItem.getItemId();
                //it's possible to do more actions on several items, if there is a large amount of items I prefer switch(){case} instead of if()
                if (id==R.id.nav_home){
                    extras=null;
                }
                if (id==R.id.nav_profile){
                    extras=null;
                }
                if (id==R.id.nav_goarbit){
                    extras=null;
                }
                if (id==R.id.nav_news){
                    extras=null;
                }
                if (id==R.id.nav_calculator){
                    extras=null;
                }
                if (id==R.id.nav_goarbit_inf){
                    extras.putString("URL", Constantes.url_GoArbit_inf);
                }
                if (id==R.id.nav_gotv){
                    extras.putString("URL", Constantes.url_GoTv);
                }
                if (id==R.id.nav_token){
                    extras.putString("URL", Constantes.url_Token);
                }
                if (id==R.id.nav_iamgo_pro){
                    extras.putString("URL", Constantes.url_Iamgo_Pro);
                }
                if (id==R.id.nav_dig_crypto){
                    extras.putString("URL", Constantes.url_Dig_Crypto);
                }
                if (id==R.id.nav_card){
                    extras.putString("URL", Constantes.url_Card);
                }
                if (id==R.id.nav_cashiers){
                    extras.putString("URL", Constantes.url_Cashiers);
                }
                if (id==R.id.nav_Stake){
                    extras.putString("URL", Constantes.url_Stake);
                }
                if(extras != null){
                    System.out.println("----------------*************************Extras: " + extras);
                    Intent intent = new Intent(MainActivity.this, ExternalActivity.class);
                    intent.putExtras(extras);
                    startActivity(intent);
                }{
                    //This is for maintaining the behavior of the Navigation view
                    NavigationUI.onNavDestinationSelected(menuItem,navController);
                    //This is for closing the drawer after acting on it
                    drawer.closeDrawer(GravityCompat.START);
                    return true;
                }


            }
        });


    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_about:
                Intent intent = new Intent( MainActivity.this, AboutActivity.class );
                startActivity(intent);
                return true;

            case R.id.action_logout:
                logout();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void logout() {

        System.out.println("Sesion Cerrada.");
        validacion();
    }

    public static boolean validarConexion(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        return isConnected;

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
                        finish();
                        //android.os.Process.killProcess(android.os.Process.myPid()); //Su funcion es algo similar a lo que se llama cuando se presiona el botón "Forzar Detención" o "Administrar aplicaciones", lo cuál mata la aplicación
                        //finish(); Si solo quiere mandar la aplicación a segundo plano
                    }
                }).show();
    }


    public void cargarImagen(){
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)) {
            //Mostramos un AlertDialog al usuario explicándole la necesidad del permiso
            AlertDialog AD;
            AlertDialog.Builder ADBuilder = new AlertDialog.Builder(MainActivity.this);
            ADBuilder.setMessage(getString(R.string.storage_access));
            ADBuilder.setPositiveButton("Continuar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    /*Cuando el usuario pulse sobre el botón del AlertDialog se procede a solicitar
                     el permiso con el siguiente código:*/

                    ActivityCompat.requestPermissions(
                            MainActivity.this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            CODIGO_PERMISOS_STORAGE);
                }
            });
            //Mostramos el AlertDialog
            AD = ADBuilder.create();
            AD.show();
        } else {
            /*Si no hay necesidad de una explicación racional, pasamos a solicitar el
            permiso directamente*/
            ActivityCompat.requestPermissions(
                    MainActivity.this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    CODIGO_PERMISOS_STORAGE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CODIGO_PERMISOS_STORAGE) {
            /* Resultado de la solicitud para permiso de cámara
             Si la solicitud es cancelada por el usuario, el método .lenght sobre el array
             'grantResults' devolverá null.*/

            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permiso concedido, podemos iniciar camara
                permisoDeAlmacenamientoConcedido();
                // IniciarCamara();
            } else {
                permisoDeAlmacenamientoDenegado();
                /* Permiso no concedido
                 Aquí habría que explicar al usuario el por qué de este permiso
                 y volver a solicitarlo .*/
//
            }
        }
    }

    private void permisoDeAlmacenamientoConcedido() {
        Toast.makeText(MainActivity.this, "El permiso para el almacenamiento está concedido", Toast.LENGTH_SHORT).show();
        Intent galeria = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galeria.setType("image/");
        startActivityForResult(getIntent().createChooser(galeria, "Selecione la Imagen"), 10);
    }

    private void permisoDeAlmacenamientoDenegado() {
        Toast.makeText(MainActivity.this, "El permiso para el almacenamiento está denegado", Toast.LENGTH_SHORT).show();
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
   /* @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
           // super.finish();
            Toast.makeText(MainActivity.this, "cerrar app",
                    Toast.LENGTH_SHORT).show();
        }
        return super.onKeyDown(keyCode, event);
    }*/

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
                if (validarConexion(MainActivity.this)) {
                    cargarImagen();
                }else {
                    Toast.makeText(MainActivity.this, getString(R.string.message_connection),
                            Toast.LENGTH_SHORT).show();
                }
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
// Se controla la pulsacion del boton atras
@Override
public void onBackPressed() {
    AlertDialog.Builder builder = new AlertDialog.Builder(this);
    builder.setMessage(getString(R.string.exit_app))
            .setPositiveButton(getString(R.string.message_yes), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(Intent.ACTION_MAIN);
                    intent.addCategory(Intent.CATEGORY_HOME);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                }
            })
            .setNegativeButton(getString(R.string.message_cancel), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
    builder.show();
}
public void errorConecction(){
    AlertDialog.Builder builder = new AlertDialog.Builder(this);
    builder.setMessage(getString(R.string.message_connection))
            .setNegativeButton(("Ok"), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    return;
                }
            });
}

}
