package com.example.harshjain.chitchatz;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {

    private ImageView mImageView;
    private TextView mProfileName , mProfileStatus ;
    private Button mProfileSendReqBtn , mProfileDeclineReqBtn;
    private DatabaseReference mDatabseRef;
    private String current_state;
    private FirebaseUser mCurrentUser;
    private DatabaseReference mNotificationDatabase;
    private DatabaseReference mFriendDatabase;
    private DatabaseReference mRootRef,mUserRef;
    private FirebaseAuth mAuth;
    private DatabaseReference mFriendRequestDatabase;
    private ProgressDialog mProgressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        final String uid = getIntent().getStringExtra("uid");
        String main = getIntent().getStringExtra("main");
        if(main== null)
            main = "0";
//        Toast.makeText(this, main, Toast.LENGTH_SHORT).show();
        mRootRef = FirebaseDatabase.getInstance().getReference();
        mDatabseRef = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);
        mFriendRequestDatabase = FirebaseDatabase.getInstance().getReference().child("Friend_req");
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        mFriendDatabase = FirebaseDatabase.getInstance().getReference().child("Friends");
        current_state = "not_friends";
        mAuth = FirebaseAuth.getInstance();
        mUserRef = FirebaseDatabase.getInstance().getReference().child("Users")
                .child(mAuth.getCurrentUser().getUid());
        mNotificationDatabase = FirebaseDatabase.getInstance().getReference().child("notifications");
//        mProfileFriendsCount = (TextView)findViewById(R.id.display_profile_totalfriends);
        mImageView = (ImageView)findViewById(R.id.display_profile_imae);
        mProfileSendReqBtn = (Button)findViewById(R.id.profile_send_req_btn);
        mProfileName = (TextView)findViewById(R.id.display_profile_name);
        mProfileStatus = (TextView)findViewById(R.id.display_profile_status);
        mProfileDeclineReqBtn = (Button)findViewById(R.id.profile_decline_req_btn);
        mProgressDialog = new ProgressDialog(ProfileActivity.this);
        mProgressDialog.setTitle("Loading Profile");
        mProgressDialog.setMessage("Please wait while we load the requested profile!!");
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.show();
        mProfileDeclineReqBtn.setVisibility(View.INVISIBLE);
        mProfileDeclineReqBtn.setEnabled(false);
        mDatabseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String name = dataSnapshot.child("name").getValue().toString();
                String status = dataSnapshot.child("status").getValue().toString();
                String image = dataSnapshot.child("image").getValue().toString();

                mProfileName.setText(name);
                mProfileStatus.setText(status);
                if(uid.equals(mCurrentUser.getUid().toString()))
                {
                    mProfileSendReqBtn.setVisibility(View.INVISIBLE);
                    mProfileSendReqBtn.setEnabled(false);
                }
                else {
                    //--------------------------------Friends List / Requests---------
                    mFriendRequestDatabase.child(mCurrentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            if (dataSnapshot.hasChild(uid)) {
                                String type = dataSnapshot.child(uid).child("request_type").getValue().toString();
                                if (type.equals("received")) {
                                    current_state = "req_received";
                                    mProfileDeclineReqBtn.setVisibility(View.VISIBLE);
                                    mProfileDeclineReqBtn.setEnabled(true);
                                    mProfileSendReqBtn.setText("Accept Friend Request");
                                } else if (type.equals("sent")) {
                                    mProfileDeclineReqBtn.setVisibility(View.INVISIBLE);
                                    mProfileDeclineReqBtn.setEnabled(false);
                                    current_state = "req_sent";
                                    mProfileSendReqBtn.setText("Cancel Friend Request");
                                }
                            } else {
                                mFriendDatabase.child(mCurrentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.hasChild(uid)) {
                                            current_state = "friends";
                                            mProfileSendReqBtn.setText("Unfriend");

                                            mProfileDeclineReqBtn.setVisibility(View.INVISIBLE);
                                            mProfileDeclineReqBtn.setEnabled(false);

                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                            }
//                        mProgressDialog.dismiss();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                    Picasso.get().load(image).placeholder(R.mipmap.default_avatar).into(mImageView);
                }
                mProgressDialog.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        // ---------- NOT FRIENDS STATE -----------------
        final String finalMain = main;
        mProfileSendReqBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mProfileSendReqBtn.setEnabled(false);

                // First State
                if(current_state.equals("not_friends")){

                    DatabaseReference newNotification = mRootRef.child("notifications").child(uid).push();
                    String newNotificationid = newNotification.getKey();
                    HashMap<String , String> notificationData = new HashMap<>();
                    notificationData.put("from",mCurrentUser.getUid());
                    notificationData.put("type" , "request");

                    Map requestMap = new HashMap();
                    requestMap.put("Friend_req/" +mCurrentUser.getUid()+"/" + uid + "/request_type","sent");
                    requestMap.put("Friend_req/" + uid + "/" + mCurrentUser.getUid()+ "/request_type","received");
                    requestMap.put("notifications/" + uid+ "/" + newNotificationid, notificationData );
                    mRootRef.updateChildren(requestMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            if(databaseError != null)
                            {
                                Toast.makeText(ProfileActivity.this, "There was some error!!!", Toast.LENGTH_SHORT).show();
                            }
                            mProfileSendReqBtn.setEnabled(true);

                            current_state = "req_sent";
                            Toast.makeText(ProfileActivity.this, "Request Sent Successfully!!", Toast.LENGTH_SHORT).show();
                            mProfileSendReqBtn.setText("Cancel Friend Request");


                        }
                    });
                }
                if(current_state.equals("req_sent"))
                {
//                    Toast.makeText(ProfileActivity.this, current_state, Toast.LENGTH_SHORT).show();

                    mFriendRequestDatabase.child(mCurrentUser.getUid()).child(uid).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            mFriendRequestDatabase.child(uid).child(mCurrentUser.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                    mProfileSendReqBtn.setEnabled(true);
                                    current_state = "not_friends";
                                    Toast.makeText(ProfileActivity.this, "Cancelled the Friend Request Successfully!!", Toast.LENGTH_SHORT).show();
                                    mProfileSendReqBtn.setText("Send Friend Request");
                                    mProfileDeclineReqBtn.setVisibility(View.INVISIBLE);
                                    mProfileDeclineReqBtn.setEnabled(false);
                                }
                            });
                        }
                    });
                }

                //------------- REQ RECEIVED STATE -----------

                if(current_state.equals("req_received"))
                {
                    final String currentDate = DateFormat.getDateTimeInstance().format(new Date());

                    Map friendsMap = new HashMap();
                friendsMap.put("Friends/" + mCurrentUser.getUid() + "/" + uid + "/date", currentDate);
                friendsMap.put("Friends/" + uid+ "/"  + mCurrentUser.getUid() + "/date", currentDate);


                friendsMap.put("Friend_req/" + mCurrentUser.getUid() + "/" + uid, null);
                friendsMap.put("Friend_req/" + uid + "/" + mCurrentUser.getUid(), null);


                mRootRef.updateChildren(friendsMap, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {


                        if(databaseError == null){

                            mProfileSendReqBtn.setEnabled(true);
                            current_state = "friends";
                            Toast.makeText(ProfileActivity.this, "Friend Request Accepted", Toast.LENGTH_SHORT).show();
                            mProfileSendReqBtn.setText("Unfriend this Person");

                            mProfileDeclineReqBtn.setVisibility(View.INVISIBLE);
                            mProfileDeclineReqBtn.setEnabled(false);

                        } else {

                            String error = databaseError.getMessage();

                            Toast.makeText(ProfileActivity.this, error, Toast.LENGTH_SHORT).show();


                        }

                    }
                });

            }


            // ---------UNFRIEND  ---------------
                if(current_state.equals("friends"))
                {
                    Map unfriendMap = new HashMap();
                    unfriendMap.put("Friends/" + mCurrentUser.getUid()+"/"+uid, null);
                    unfriendMap.put("Friends/"+uid+"/"+mCurrentUser.getUid(),null);
                    mRootRef.updateChildren(unfriendMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                            if(databaseError == null){
                                current_state = "not_friends";
                                Toast.makeText(ProfileActivity.this, "Unfriend Successfully!!", Toast.LENGTH_SHORT).show();
                                mProfileSendReqBtn.setText("Send Friend Request");
                                mProfileDeclineReqBtn.setVisibility(View.INVISIBLE);
                                mProfileDeclineReqBtn.setEnabled(true);
                            } else{
                                String error = databaseError.getMessage();
                                Toast.makeText(ProfileActivity.this, error, Toast.LENGTH_SHORT).show();
                            }
                            mProfileSendReqBtn.setEnabled(true);
                        }
                    });
                }
//                if(finalMain.equals("1")){
//                    Toast.makeText(ProfileActivity.this, "Request", Toast.LENGTH_SHORT).show();
//                    startActivity(new Intent(ProfileActivity.this,MainActivity.class));
//                }
            }
        });
        final String finalMain1 = main;
        mProfileDeclineReqBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Map declineReqMap = new HashMap();
                declineReqMap.put("Friend_req/" + mCurrentUser.getUid()+"/"+uid, null);
                declineReqMap.put("Friend_req/"+uid+"/"+mCurrentUser.getUid(),null);
                mRootRef.updateChildren(declineReqMap, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                        if(databaseError == null){
                            current_state = "not_friends";
                            Toast.makeText(ProfileActivity.this, "Friend Request Declined Successfully", Toast.LENGTH_SHORT).show();
                            mProfileSendReqBtn.setText("Send Friend Request");
                            mProfileDeclineReqBtn.setVisibility(View.INVISIBLE);
                            mProfileDeclineReqBtn.setEnabled(true);
                        } else{
                            String error = databaseError.getMessage();
                            Toast.makeText(ProfileActivity.this, error, Toast.LENGTH_SHORT).show();
                        }
                        mProfileSendReqBtn.setEnabled(true);
                    }
                });
//                if(finalMain1.equals("1")){
//                    Toast.makeText(ProfileActivity.this, "Request", Toast.LENGTH_SHORT).show();
//                    startActivity(new Intent(ProfileActivity.this,MainActivity.class));
//                }
            }
        });

    }

    // ----------- ONLINE STATUS ---------------
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
//            Toast.makeText(this, currentUser.getUid().toString(), Toast.LENGTH_SHORT).show();
            mUserRef.child("online").setValue("true");
        }
    }
    private void sendToStart() {
        Intent startIntent = new Intent(ProfileActivity.this,StartActivity.class);
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
}
