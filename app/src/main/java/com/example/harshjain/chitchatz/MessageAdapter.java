package com.example.harshjain.chitchatz;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
/**
 * Created by Harsh Jain on 23-01-2019.
 */
public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder>{
private FirebaseAuth mAuth;
private List<Messages> mMessageList;
public MessageAdapter(List<Messages> mMessageList) {
        this.mMessageList = mMessageList;
}
@Override
public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
        .inflate(R.layout.message_single_layout ,parent, false);
        return new MessageViewHolder(v);
        }
public class MessageViewHolder extends RecyclerView.ViewHolder {
    public TextView messageText,righttext;
    public LinearLayout left,right;
    public ImageView messageImage;
    public MessageViewHolder(View view) {
        super(view);
        messageText = (TextView) view.findViewById(R.id.message_text_layout);
        righttext = (TextView)view.findViewById(R.id.message_right_layout);
        messageImage = (ImageView) view.findViewById(R.id.message_image_layout);
        left = (LinearLayout)view.findViewById(R.id.left_layout);
        right = (LinearLayout)view.findViewById(R.id.right_layout);

    }
}
    @Override
    public void onBindViewHolder(final MessageViewHolder viewHolder, int i) {
        mAuth = FirebaseAuth.getInstance();
        String current_user_id = mAuth.getCurrentUser().getUid().toString();
        Messages c = mMessageList.get(i);
        String from_user = c.getFrom();
        String message_type = c.getType();
        if(from_user.equals(current_user_id))
        {
            if(message_type.equals("text"))
            {
                viewHolder.right.setVisibility(View.VISIBLE);
                viewHolder.righttext.setText(c.getMessage());
                viewHolder.left.setVisibility(LinearLayout.GONE);
                viewHolder.righttext.setBackgroundColor(Color.WHITE);
                viewHolder.righttext.setTextColor(Color.BLACK);
                viewHolder.messageImage.setVisibility(View.GONE);
            }
            else
            {
                viewHolder.messageText.setVisibility(View.GONE);
                viewHolder.righttext.setVisibility(View.GONE);
                viewHolder.messageImage.setVisibility(View.VISIBLE);
                Picasso.get().load(c.getMessage())
                        .placeholder(R.mipmap.default_avatar).into(viewHolder.messageImage);
            }
        }
        else
        {
            if(message_type.equals("text"))
            {
                viewHolder.left.setVisibility(View.VISIBLE);
                viewHolder.messageText.setText(c.getMessage());
                viewHolder.right.setVisibility(LinearLayout.GONE);
                viewHolder.messageText.setBackgroundColor(R.drawable.message_text_background);
                viewHolder.messageText.setTextColor(Color.WHITE);
                viewHolder.messageImage.setVisibility(View.GONE);
            }
            else
            {
                viewHolder.messageText.setVisibility(View.GONE);
                viewHolder.righttext.setVisibility(View.GONE);
                viewHolder.messageImage.setVisibility(View.VISIBLE);
                Picasso.get().load(c.getMessage())
                        .placeholder(R.mipmap.default_avatar).into(viewHolder.messageImage);
            }
        }
    }
    @Override
    public int getItemCount() {
        return mMessageList.size();
    }
}