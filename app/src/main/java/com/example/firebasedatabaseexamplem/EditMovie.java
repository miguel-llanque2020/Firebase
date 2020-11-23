package com.example.firebasedatabaseexamplem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.firebasedatabaseexamplem.Entidad.Categoria;
import com.example.firebasedatabaseexamplem.Entidad.Movie;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.UUID;

import javax.microedition.khronos.egl.EGLDisplay;

public class EditMovie extends AppCompatActivity {
    public static final String idmovies = "idmovies";;
    private TextInputEditText movieName;
  //  private TextInputEditText movieLogo;
    private RatingBar mRatingBar;
    private Button update;
    private TextView txtidmovies;
    private static final String userId = "53";
    ArrayList<Categoria> birdList=new ArrayList<>();
    DatabaseReference MoviesRef;
    FirebaseDatabase firebaseDatabase;
    private DatabaseReference mDatabaseReference;
    ImageView imgfoto;
    private Spinner spinner;
    ArrayAdapter<Categoria> adapterCargos;
    private DatabaseReference refereccate,reference;
    Button galeria;
    private final int MIS_PERMISOS = 100;
    private ProgressDialog progressDialog;
    private static final int COD_SELECCIONA = 10;
    Uri uri;
    private StorageReference mStorageRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_movie);

        movieName = (TextInputEditText) findViewById(R.id.tiet_movie_nameU);
       // movieLogo = (TextInputEditText) findViewById(R.id.tiet_movie_logoU);
        update = (Button) findViewById(R.id.update);
        mRatingBar = (RatingBar) findViewById(R.id.rating_barU);
        txtidmovies =(TextView)findViewById(R.id.idMovie);
        imgfoto=(ImageView)findViewById(R.id.idimgportada1);
        String idmovies = getIntent().getStringExtra("idmovies");
        txtidmovies.setText(idmovies);
        txtidmovies.setVisibility(View.INVISIBLE);
        firebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = firebaseDatabase.getReference();
        spinner=(Spinner)findViewById(R.id.spinercategoria1);
        galeria=(Button)findViewById(R.id.b_foto1);
        galeria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/");
                startActivityForResult(intent.createChooser(intent,"Seleccione"),COD_SELECCIONA);// 10
            }
        });
        birdList = new ArrayList<>();
        MoviesRef = FirebaseDatabase.getInstance().getReference().child("users").child(userId).child("movies");

        MoviesRef.child(idmovies).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    movieName.setText(dataSnapshot.child("movieName").getValue().toString());
                  //  movieLogo.setText(dataSnapshot.child("moviePoster").getValue().toString());
                    Picasso.with(EditMovie.this).load(dataSnapshot.child("moviePoster").getValue().toString()).resize(300, 300)
                            .centerCrop().into(imgfoto);
                    mRatingBar.setRating(Float.parseFloat(dataSnapshot.child("movieRating").getValue().toString()));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isEmpty(movieName) && !isEmpty(movieName) ){
                    myNewMovie(txtidmovies.getText().toString(), movieName.getText().toString().trim(),"",mRatingBar.getRating(),spinner.getSelectedItem().toString());
                }else{
                    if(isEmpty(movieName)){
                        Toast.makeText(EditMovie.this, "Please enter a movie name!", Toast.LENGTH_SHORT).show();
                    }
                }
                Intent intent = new Intent(EditMovie.this, MainActivity.class);
                startActivity(intent);

            }
        });

    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            //TODO : ESTO SELECCIONA DE LA GALERIA
            case COD_SELECCIONA:
                if (data==null){
                    Toast.makeText(EditMovie.this, "No selecciono una imagen", Toast.LENGTH_SHORT).show();
                    return;
                }
                uri=data.getData();
                imgfoto.setImageURI(uri);
                break;
            default:
                Toast.makeText(EditMovie.this, "No existe la foto", Toast.LENGTH_SHORT).show();
                break;
        }

    }
    private void myNewMovie(String idmovies, String movieName, String moviePoster, float rating,String categoria) {
        //Creating a movie object with user defined variables

        try {

            progressDialog = new ProgressDialog(EditMovie.this);
            progressDialog.setTitle("Subiendo..");
            progressDialog.setCancelable(false);
            progressDialog.show();
            final StorageReference mountainsRef=mStorageRef.child("fotos").child(uri.getLastPathSegment());
            imgfoto.setDrawingCacheEnabled(true);
            imgfoto.buildDrawingCache();
            Bitmap bitmap = ((BitmapDrawable) imgfoto.getDrawable()).getBitmap();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos);
            byte[] path = baos.toByteArray();
            final UploadTask uploadTask = mountainsRef.putBytes(path);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {

                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if (!task.isSuccessful()) {
                                throw task.getException();
                            }
                            return mountainsRef.getDownloadUrl();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {
                                Uri downloadUri = task.getResult();
                                progressDialog.dismiss();
                                Movie movie = new Movie(idmovies,movieName,downloadUri.toString(),rating,categoria);
                                mDatabaseReference.child("users").child(userId).child("movies").child(movie.getId()).setValue(movie);

                            }
                            else {
                                Toast.makeText(EditMovie.this, "Error al subir", Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                            }
                        }
                    });
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    //check if edittext is empty
    private boolean isEmpty(TextInputEditText textInputEditText) {
        if (textInputEditText.getText().toString().trim().length() > 0)
            return false;
        return true;
    }
    @Override
    public void onStart() {
        super.onStart();
        reference=FirebaseDatabase.getInstance().getReference("Categoria");
        Query q=reference;
        q.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                birdList.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Categoria artist = postSnapshot.getValue(Categoria.class);
                    birdList.add(artist);
                }
                adapterCargos= new ArrayAdapter<Categoria>(EditMovie.this,android.R.layout.simple_spinner_item,birdList);
                spinner.setAdapter(adapterCargos);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
}