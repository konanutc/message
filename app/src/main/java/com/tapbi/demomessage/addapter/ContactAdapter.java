package com.tapbi.demomessage.addapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tapbi.demomessage.R;
import com.tapbi.demomessage.dto.ItemContact;

import java.util.ArrayList;
import java.util.List;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ViewHolderContact> {
    private List<ItemContact> contactList;
    private OnCallBack mListener;

    public ContactAdapter(List<ItemContact> contactList, OnCallBack listener) {
        this.contactList = contactList;
        this.mListener = listener;
    }

    public void filterList(ArrayList<ItemContact> filterdNames) {
         this.contactList = filterdNames;
         notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolderContact onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_contact, parent, false);
        return new ViewHolderContact(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderContact holder, final int position) {
        ItemContact itemContact = contactList.get(position);
        holder.tv_name_contact.setText(itemContact.getName());
        holder.tv_number.setText(itemContact.getNumber());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onItemClicked(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return contactList.size();
    }

    public class ViewHolderContact extends RecyclerView.ViewHolder {
        TextView tv_name_contact, tv_number;
        ImageView iv_contact;

        public ViewHolderContact(@NonNull View itemView) {
            super(itemView);
            tv_name_contact = itemView.findViewById(R.id.tv_name_contact);
            tv_number = itemView.findViewById(R.id.tv_number);
            iv_contact = itemView.findViewById(R.id.iv_contact);

        }
    }


    public interface OnCallBack {
        void onItemClicked(int position);
    }

}
