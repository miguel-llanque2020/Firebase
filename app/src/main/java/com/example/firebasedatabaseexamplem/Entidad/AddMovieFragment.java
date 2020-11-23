package com.example.firebasedatabaseexamplem.Entidad;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.firebasedatabaseexamplem.R;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.UUID;

public class AddMovieFragment extends Fragment {

    private DatabaseReference mDatabaseReference;
    private TextInputEditText movieName;
   // private TextInputEditText movieLogo;
    private RatingBar mRatingBar;
    private Button bSubmit,bfoto;
    private ImageView imgportada;
    private Spinner spinner;
    private final int MIS_PERMISOS = 100;
    private ProgressDialog progressDialog;
    private static final int COD_SELECCIONA = 10;
    Uri uri;
    private StorageReference mStorageRef;
    View v;
    ArrayList<String> listaCargos;
    ArrayAdapter<Categoria> adapterCargos;
    private DatabaseReference refereccate,reference;
    android.app.AlertDialog.Builder builder1;
    AlertDialog alert;
    ArrayList<Categoria> birdList=new ArrayList<>();
    Button nuevacate;
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.movie_fragmen,container,false);
        movieName = (TextInputEditText) v.findViewById(R.id.tiet_movie_name);
       // movieLogo = (TextInputEditText) v.findViewById(R.id.tiet_movie_logo);
        bSubmit = (Button) v.findViewById(R.id.b_submit);
        mRatingBar = (RatingBar) v.findViewById(R.id.rating_bar);
        imgportada=(ImageView)v.findViewById(R.id.idimgportada);
        bfoto=(Button)v.findViewById(R.id.b_foto);
        nuevacate=(Button)v.findViewById(R.id.btnnuecacategoria);
        spinner=(Spinner)v.findViewById(R.id.spinercategoria);
        nuevacate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CAtegoria();


            }
        });
        birdList = new ArrayList<>();
        mStorageRef = FirebaseStorage.getInstance().getReference();
        bfoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/");
                startActivityForResult(intent.createChooser(intent,"Seleccione"),COD_SELECCIONA);// 10
            }
        });

        //initializing database reference
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        bSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isEmpty(movieName) && !isEmpty(movieName)  &&  uri!=null ){
                    myNewMovie("53", movieName.getText().toString().trim(),mRatingBar.getRating());
                }else{
                    if(isEmpty(movieName)){
                        Toast.makeText(getContext(), "Please enter a movie name!", Toast.LENGTH_SHORT).show();
                    }
                    if (uri==null){
                        Toast.makeText(getContext(), "Elija Foto !", Toast.LENGTH_SHORT).show();
                    }
                }
                //to remove current fragment
                getActivity().onBackPressed();
            }
        });
        return v;

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
                adapterCargos= new ArrayAdapter<Categoria>(getContext(),android.R.layout.simple_spinner_item,birdList);
                spinner.setAdapter(adapterCargos);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void myNewMovie(String userId, String movieName, float rating) {
        //Creating a movie object with user defined variables
        try {

            progressDialog = new ProgressDialog(getContext());
            progressDialog.setTitle("Subiendo..");
            progressDialog.setCancelable(false);
            progressDialog.show();
            final StorageReference mountainsRef=mStorageRef.child("fotos").child(uri.getLastPathSegment());
            imgportada.setDrawingCacheEnabled(true);
            imgportada.buildDrawingCache();
            Bitmap bitmap = ((BitmapDrawable) imgportada.getDrawable()).getBitmap();
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
                               // referenceUsuarios.child("image_usuario").setValue(downloadUri.toString());
                                Movie movie = new Movie(UUID.randomUUID().toString(),movieName,downloadUri.toString(),rating,spinner.getSelectedItem().toString());
                                //referring to movies node and setting the values from movie object to that location
                                mDatabaseReference.child("users").child(userId).child("movies").child(movie.getId()).setValue(movie);
                                 }
                            else {
                                Toast.makeText(getContext(), "Error al subir", Toast.LENGTH_SHORT).show();
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            //TODO : ESTO SELECCIONA DE LA GALERIA
            case COD_SELECCIONA:

                if (data==null){
                    Toast.makeText(getContext(), "No selecciono una imagen", Toast.LENGTH_SHORT).show();
                    return;
                }
                uri=data.getData();
                imgportada.setImageURI(uri);
                break;
            default:
                uri=null;
                Toast.makeText(getContext(), "No existe la foto", Toast.LENGTH_SHORT).show();
                break;
        }

    }

    private void CAtegoria(){

        builder1 = new AlertDialog.Builder(getContext());
        Button btcerrrar;
        EditText nombre;
        View v = LayoutInflater.from(getActivity()).inflate(R.layout.dialogo_categoria, null);
        refereccate= FirebaseDatabase.getInstance().getReference("Categoria");
        builder1.setView(v);
        btcerrrar=(Button)v.findViewById(R.id.register);
        nombre=(EditText)v.findViewById(R.id.cate);
        btcerrrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            String ca=nombre.getText().toString();

                String key = refereccate.push().getKey();
                Categoria o =new Categoria(key,ca);
                refereccate.child(key).setValue(o).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(getContext(), "Agregado", Toast.LENGTH_SHORT).show();
                          //  progressDialog.dismiss();
                            alert.dismiss();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), "Error :" +e.getMessage(), Toast.LENGTH_SHORT).show();
                        alert.dismiss();
                      //  progressDialog.dismiss();
                    }
                });

            }
        });
        alert  = builder1.create();


        alert.show();
    }
    //check if edittext is empty
    private boolean isEmpty(TextInputEditText textInputEditText) {
        if (textInputEditText.getText().toString().trim().length() > 0)
            return false;
        return true;
    }

}
