package com.example.firebasedatabaseexamplem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.firebasedatabaseexamplem.Entidad.AddMovieFragment;
import com.example.firebasedatabaseexamplem.Entidad.Movie;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity {


    private FloatingActionButton fab;
    ScaleAnimation shrinkAnim;
    private RecyclerView mRecyclerView;
    private StaggeredGridLayoutManager mLayoutManager;
    private FirebaseRecyclerAdapter<Movie,MovieViewHolder> adapter;
    private TextView tvNoMovies;

    //Getting reference to Firebase Database

    private DatabaseReference MoviesRef;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    private static final String userId = "53";
    public static  String idmovies= "idmovies";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        tvNoMovies = (TextView) findViewById(R.id.tv_no_movies);
        //scale animation to shrink floating actionbar
        shrinkAnim = new ScaleAnimation(1.15f, 0f, 1.15f, 0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        if (mRecyclerView != null) {
            //to enable optimization of recyclerview
            mRecyclerView.setHasFixedSize(true);
        }
        mLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(mLayoutManager);
        MoviesRef = FirebaseDatabase.getInstance().getReference().child("users").child(userId).child("movies");
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();

        FirebaseRecyclerOptions<Movie> options = new FirebaseRecyclerOptions.Builder<Movie>().setQuery(MoviesRef, Movie.class).build();
        FirebaseRecyclerAdapter<Movie,MovieViewHolder> adapter =
                new FirebaseRecyclerAdapter<Movie, MovieViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull MovieViewHolder holder, int position, @NonNull final Movie model) {
                        if(tvNoMovies.getVisibility()== View.VISIBLE){
                            tvNoMovies.setVisibility(View.GONE);
                        }
                        holder.tvMovieName.setText(model.getMovieName());
                        holder.ratingBar.setRating(model.getMovieRating());
                        Picasso.with(MainActivity.this).load(model.getMoviePoster()).resize(300, 300)
                                .centerCrop().into(holder.ivMoviePoster);
                        Log.e("url", model.getMoviePoster());

                        holder.Eliminar.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                AlertDialog.Builder confirmacion= new AlertDialog.Builder(MainActivity.this);
                                confirmacion.setMessage("¿Seguro que quiere eliminar esta categoría?").setCancelable(false).setPositiveButton("Si", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        databaseReference.child("users").child(userId).child("movies").child(model.getId()).removeValue();
                                        Toast toast1 = Toast.makeText(getApplicationContext(), "Categoría Borrada", Toast.LENGTH_SHORT);
                                        toast1.show();

                                    }
                                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                    }
                                });
                                AlertDialog alert =confirmacion.create();
                                alert.setTitle("Eliminar Categoría");
                                alert.show();

                            }
                        });



                        holder.Modificar.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                idmovies =   model.getId();
                                Intent intent = new Intent(MainActivity.this, EditMovie.class);
                                intent.putExtra(EditMovie.idmovies,idmovies);
                                startActivity(intent);

                            }
                        });

                    }



                    @NonNull
                    @Override
                    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                        View  view =  LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.movie_board_item,viewGroup,false);
                        MovieViewHolder viewHolder = new MovieViewHolder(view);
                        return viewHolder;
                    }
                };

        mRecyclerView.setAdapter(adapter);
        adapter.startListening();

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.frame_container, new AddMovieFragment())
                        .addToBackStack(null)
                        .commit();
                //animation being used to make floating actionbar disappear
                shrinkAnim.setDuration(400);
                fab.setAnimation(shrinkAnim);
                shrinkAnim.start();
                shrinkAnim.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                    }
                    @SuppressLint("RestrictedApi")
                    @Override
                    public void onAnimationEnd(Animation animation) {
                        //changing floating actionbar visibility to gone on animation end
                        fab.setVisibility(View.GONE);
                    }
                    @Override
                    public void onAnimationRepeat(Animation animation) {
                    }
                });
            }
        });


    }


    @SuppressLint("RestrictedApi")
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (fab.getVisibility() == View.GONE)
            fab.setVisibility(View.VISIBLE);
    }

    public static class MovieViewHolder extends RecyclerView.ViewHolder{
        TextView tvMovieName;
        RatingBar ratingBar;
        ImageView ivMoviePoster;
        ImageButton Eliminar;
        ImageButton Modificar;
        public MovieViewHolder(View v) {
            super(v);
            tvMovieName = (TextView) v.findViewById(R.id.tv_name);
            ratingBar = (RatingBar) v.findViewById(R.id.rating_bar);
            ivMoviePoster = (ImageView) v.findViewById(R.id.iv_movie_poster);
             Modificar = (ImageButton) v.findViewById(R.id.modificar);
            Eliminar = (ImageButton) v.findViewById(R.id.eliminar);
        }
    }
}


