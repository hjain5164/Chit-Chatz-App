package com.example.harshjain.chitchatz;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;
public class UsersActivity extends AppCompatActivity {
    private DatabaseReference mUserDatabase, mUserRef;
    private Toolbar mToolbar;
    private FirebaseAuth mAuth;
    private RecyclerView mUsersList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);
        mToolbar = (Toolbar)findViewById(R.id.users_appBar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("All Users");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mAuth = FirebaseAuth.getInstance();
        mUserRef = FirebaseDatabase.getInstance().getReference().child("Users")
                .child(mAuth.getCurrentUser().getUid());
        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        mUsersList = (RecyclerView)findViewById(R.id.users_list);
        mUsersList.setHasFixedSize(true);
        mUsersList.setLayoutManager(new LinearLayoutManager(this));
    }
    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser == null)
        {
            sendToStart();
        }
        else
        {
            mUserRef.child("online").setValue("true");
        }
        FirebaseRecyclerAdapter<Users,UsersViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Users, UsersViewHolder>(Users.class,
                R.layout.users_single_layout,UsersViewHolder.class,mUserDatabase) {
            @Override
            protected void populateViewHolder(UsersViewHolder viewHolder, Users model, int position) {
                viewHolder.setName(model.getName());
                viewHolder.setStatus(model.getStatus());
                viewHolder.setImage(model.getThumb_image(),getApplicationContext());

                final String uid = getRef(position).getKey();
                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Intent i = new Intent(UsersActivity.this,ProfileActivity.class);
                        i.putExtra("uid",uid);
                        startActivity(i);

                    }
                });
            }
        };
    mUsersList.setAdapter(firebaseRecyclerAdapter);
    }
    // ----------- ONLINE STATUS ---------------

    private void sendToStart() {
        Intent startIntent = new Intent(UsersActivity.this,StartActivity.class);
        startActivity(startIntent);
        finish();

    }
    @Override
    protected void onPause() {
        super.onPause();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser == null)
        {
            sendToStart();
        }
        else
        {
            mUserRef.child("online").setValue("true");
        }
    }
    @Override
    protected void onRestart() {
        super.onRestart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser == null)
        {
            sendToStart();
        }
        else
        {
            mUserRef.child("online").setValue("true");
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser == null)
        {
            sendToStart();
        }
        else
        {
            mUserRef.child("online").setValue("true");
        }
    }
    public static class UsersViewHolder extends RecyclerView.ViewHolder{
        View mView;
        public UsersViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }
        public void setName(String name){
            TextView mUserName = (TextView) mView.findViewById(R.id.user_single_name);
            mUserName.setText(name);
        }
        public void setStatus(String status){
            TextView mUserStatus = (TextView)mView.findViewById(R.id.user_single_status);
            mUserStatus.setText(status);
        }
        public void setImage(String thumb_image, Context context)
        {
            CircleImageView muserImage = (CircleImageView)mView.findViewById(R.id.user_single_image);
            Picasso.get().load(thumb_image).placeholder(R.mipmap.default_avatar).into(muserImage);
        }
    }
}
