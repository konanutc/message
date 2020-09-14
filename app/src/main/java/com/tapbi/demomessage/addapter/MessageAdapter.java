package com.tapbi.demomessage.addapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tapbi.demomessage.R;
import com.tapbi.demomessage.dto.ItemMessage;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolderMessage> {
    private List<ItemMessage> messageList;
    private OnCallBack mListener;


    public MessageAdapter(OnCallBack mListener, List<ItemMessage> messageList) {
        this.mListener = mListener;
        this.messageList = messageList;
    }

    @NonNull
    @Override
    public ViewHolderMessage onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message, parent, false);
            return new ViewHolderMessage(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderMessage holder, int position) {
        ItemMessage itemMessage = messageList.get(position);
        holder.tv_name_contact.setText(itemMessage.getName());
        holder.tv_content.setText(itemMessage.getContent());
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    public class ViewHolderMessage extends RecyclerView.ViewHolder{
        TextView tv_name_contact, tv_content;
        RelativeLayout rl_item_message;

        public ViewHolderMessage(@NonNull View itemView) {
            super(itemView);
            tv_name_contact = itemView.findViewById(R.id.tv_name_contact);
            tv_content = itemView.findViewById(R.id.tv_content);
            rl_item_message = itemView.findViewById(R.id.rl_item_message);
            rl_item_message.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                     mListener.onItemClicked(getPosition());
                }
            });
        }
    }
    public interface OnCallBack{
        void onItemClicked(int position);
    }
}
