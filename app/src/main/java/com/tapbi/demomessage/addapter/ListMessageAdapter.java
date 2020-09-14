package com.tapbi.demomessage.addapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tapbi.demomessage.R;
import com.tapbi.demomessage.dto.ItemMessage;

import java.util.List;

public class ListMessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int OTHERS = 0;
    private static final int ME = 1 ;
    private List<ItemMessage> messageList;

    public ListMessageAdapter(List<ItemMessage> messageList) {
        this.messageList = messageList;
    }

    public void setData(List<ItemMessage> data){
        if (data.size() != 0 ){
            messageList.addAll(data);
            notifyDataSetChanged();
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (messageList.get(position).getFolder().equals("inbox")) {
            return OTHERS;
        } else {
            return ME;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
            switch (viewType) {
                case OTHERS:
                    View itemView1 = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_message_1, parent, false);
                    viewHolder = new ViewholderMessage1(itemView1);
                    break;
                case ME:
                    View itemView2 = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_message_2, parent, false);
                    viewHolder = new ViewholderMessage2(itemView2);
                    break;
            }

        return  viewHolder;
    }



    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()){
            case OTHERS:
                ViewholderMessage1 viewholderMessage1 = (ViewholderMessage1) holder;
                ItemMessage itemMessage1 = messageList.get(position);
                viewholderMessage1.tv_message_list_1.setText(itemMessage1.getContent());
                viewholderMessage1.tv_date.setText(itemMessage1.getDate());
                break;
            case ME:
                ViewholderMessage2 viewholderMessage2 = (ViewholderMessage2) holder;
                ItemMessage itemMessage2 = messageList.get(position);
                viewholderMessage2.tv_message_list_2.setText(itemMessage2.getContent());
                viewholderMessage2.tv_date.setText(itemMessage2.getDate());
                break;
        }
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    
    public class ViewholderMessage1 extends RecyclerView.ViewHolder{
        TextView tv_message_list_1, tv_date;

        public ViewholderMessage1(@NonNull View itemView) {
            super(itemView);
            tv_message_list_1 = itemView.findViewById(R.id.tv_list_message_1);
            tv_date = itemView.findViewById(R.id.tv_date);
        }
    }

    public class ViewholderMessage2 extends RecyclerView.ViewHolder{
        TextView tv_message_list_2, tv_date;

        public ViewholderMessage2(@NonNull View itemView) {
            super(itemView);
            tv_message_list_2 = itemView.findViewById(R.id.tv_list_message_2);
            tv_date = itemView.findViewById(R.id.tv_date);
        }
    }

}
