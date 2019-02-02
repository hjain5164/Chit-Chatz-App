package com.example.harshjain.chitchatz;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class RequestFragment extends Fragment {
    private RecyclerView mReqList;

    private DatabaseReference mConvDatabase;
//    private DatabaseReference mMessageDatabase;
    private DatabaseReference mUsersDatabase;
//    private RelativeLayout relativeLayout;
    private FirebaseAuth mAuth;
    private int i =0;
    private String mCurrent_user_id;
    private String state;
    private List<Object> values;
    private View mMainView;
    private TextView name;
    private CircleImageView image;

    public RequestFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mMainView = inflater.inflate(R.layout.fragment_request, container, false);
        mReqList = (RecyclerView) mMainView.findViewById(R.id.request_list);
        mAuth = FirebaseAuth.getInstance();
        mCurrent_user_id = mAuth.getCurrentUser().getUid();
        mConvDatabase = FirebaseDatabase.getInstance().getReference().child("Friend_req").child(mCurrent_user_id);
        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
//         relativeLayout = (RelativeLayout)mMainView.findViewById(R.id.req_relative_layout);

        mReqList.setHasFixedSize(true);
        mReqList.setLayoutManager(linearLayoutManager);

        return mMainView;
    }

    @Override
    public void onStart() {
        super.onStart();
//        Query conversationQuery = mConvDatabase.orderByChild("timestamp")
        FirebaseRecyclerAdapter<Request, RequestsViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Request, RequestFragment.RequestsViewHolder>
                (Request.class, R.layout.request_layout, RequestFragment.RequestsViewHolder.class, mConvDatabase) {
            @Override
            protected void populateViewHolder(final RequestsViewHolder viewHolder, final Request model, int position) {

                final String list_user_id = getRef(position).getKey();
//                final String state;
                mConvDatabase.child(list_user_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.hasChild("request_type"))
                        state = dataSnapshot.child("request_type").getValue().toString();
//                        Toast.makeText(getContext(), state, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                mUsersDatabase.child(list_user_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        final String userName = dataSnapshot.child("name").getValue().toString();
                        String userThumb = dataSnapshot.child("thumb_image").getValue().toString();
//                        String state = dataSnapshot.child()
//                        Toast.makeText(getContext(), state, Toast.LENGTH_SHORT).show();
                        viewHolder.setName(userName);
                        viewHolder.setUserImage(userThumb, getContext());
                        if(!state.equals("sent")) {
                            viewHolder.setVisibility("received");
                        }
                        else {
                            viewHolder.setVisibility("sent");
                        }
                        mConvDatabase.keepSynced(true);
                        mUsersDatabase.keepSynced(true);
                        viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent i = new Intent(getContext(), ProfileActivity.class);
                                i.putExtra("uid", list_user_id);
                                i.putExtra("main","1");
                                startActivity(i);
                            }

                        });
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        };
        mReqList.setAdapter(firebaseRecyclerAdapter);
    }


    public static class RequestsViewHolder extends RecyclerView.ViewHolder{
        View mView;
        public RequestsViewHolder(View itemView){
            super(itemView);
            mView = itemView;
        }
        public void setName(String name){

            TextView userNameView = (TextView) mView.findViewById(R.id.req_name);
            userNameView.setText(name);

        }
        public  void setVisibility(String state){

            TextView sent = (TextView)mView.findViewById(R.id.sent);
            TextView receive = (TextView)mView.findViewById(R.id.receive);
            if(state.equals("sent")){
                sent.setVisibility(View.VISIBLE);
                receive.setVisibility(View.INVISIBLE);
            }
            else
            {
                sent.setVisibility(View.INVISIBLE);
                receive.setVisibility(View.VISIBLE);
            }

        }

        public void setUserImage(String thumb_image, Context ctx){

            CircleImageView userImageView = (CircleImageView) mView.findViewById(R.id.req_image);
            if(!thumb_image.equals("no"))
            Picasso.get().load(thumb_image).placeholder(R.mipmap.default_avatar).into(userImageView);
            else
                userImageView.setVisibility(View.INVISIBLE);

        }
    }
}
