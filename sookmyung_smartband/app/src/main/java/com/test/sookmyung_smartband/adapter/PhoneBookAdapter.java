package com.test.sookmyung_smartband.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.bumptech.glide.Glide;
import com.test.sookmyung_smartband.R;
import com.test.sookmyung_smartband.beans.PhoneBook;
import com.test.sookmyung_smartband.ui.OnInnerViewClickListener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class PhoneBookAdapter extends RecyclerView.Adapter {

    private Context mContext;
    private List<PhoneBook> mDataList;
    private OnInnerViewClickListener onBtnPhoneClickListener;
    private OnInnerViewClickListener onBtnDeleteClickListener;

    public PhoneBookAdapter(Context context) {
        mContext = context;
        mDataList = new ArrayList<>();
    }

    public class PhoneBookViewHolder extends RecyclerView.ViewHolder {

        public TextView nameView;
        public ImageView picView;
        public TextView phoneView;
        public TextView homePhoneView;
        public TextView addressView;

        public BootstrapButton btnPhone;

        public ImageButton btnDelete;

        public PhoneBookViewHolder(View itemView) {
            super(itemView);

            nameView = (TextView)itemView.findViewById(R.id.name);
            picView = (ImageView)itemView.findViewById(R.id.ivUserPic);
            phoneView = (TextView)itemView.findViewById(R.id.phone);
            homePhoneView = (TextView)itemView.findViewById(R.id.homephone);
            addressView = (TextView)itemView.findViewById(R.id.address);

            btnPhone = (BootstrapButton)itemView.findViewById(R.id.btnPhone);

            btnDelete = (ImageButton)itemView.findViewById(R.id.btnDelete);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        PhoneBook phoneBook = mDataList.get(position);

        PhoneBookViewHolder h = (PhoneBookViewHolder)holder;
        h.nameView.setText(phoneBook.getName() + " (" + phoneBook.getEmail() + ")");
        Glide.with(mContext)
                .load(new File(phoneBook.getUserPic()))
                .centerCrop()
                .thumbnail(0.1f)
                .into(h.picView);
        h.phoneView.setText("핸드폰 번호 : " + phoneBook.getPhoneNumber());
        h.homePhoneView.setText("생년월일 : " + phoneBook.getHomeNumber());
        h.addressView.setText("주소 : " + phoneBook.getAddress());

        h.btnPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(onBtnPhoneClickListener != null) {
                    onBtnPhoneClickListener.onItemClick(mDataList.get(position).getPhoneNumber());
                }
            }
        });

        h.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(onBtnDeleteClickListener != null) {
                    onBtnDeleteClickListener.onItemClick(mDataList.get(position).getCode());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemLayoutView = null;
        itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_phonebook, parent, false);
        return new PhoneBookViewHolder(itemLayoutView);
    }

    public void add(PhoneBook phoneBook) {
        mDataList.add(phoneBook);
    }

    public String getHome(int position) {
        return mDataList.get(position).getAddress();
    }

    public String getPhone(int position) {
        return mDataList.get(position).getPhoneNumber();
    }

    public void remove(int position) {
        mDataList.remove(position);
    }

    public void removeAll() {
        mDataList.clear();
    }

    public void setOnBtnPhoneClickListener(OnInnerViewClickListener onBtnPhoneClickListener) {
        this.onBtnPhoneClickListener = onBtnPhoneClickListener;
    }

    public void setOnBtnDeleteClickListener(OnInnerViewClickListener onBtnDeleteClickListener) {
        this.onBtnDeleteClickListener = onBtnDeleteClickListener;
    }
}
