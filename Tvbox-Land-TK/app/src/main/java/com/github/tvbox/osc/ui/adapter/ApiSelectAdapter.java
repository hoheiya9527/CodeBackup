package com.github.tvbox.osc.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.github.tvbox.osc.R;
import com.github.tvbox.osc.bean.API;
import com.github.tvbox.osc.util.HawkConfig;
import com.orhanobut.hawk.Hawk;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ApiSelectAdapter extends ListAdapter<API, ApiSelectAdapter.SelectViewHolder> {


    class SelectViewHolder extends RecyclerView.ViewHolder {

        public SelectViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
        }
    }

    public interface SelectDialogInterface {
        void click(API value);
    }


    private ArrayList<API> data = new ArrayList<>();

    private SelectDialogInterface dialogInterface = null;

    public ApiSelectAdapter(SelectDialogInterface dialogInterface) {
        super(new DiffUtil.ItemCallback<API>() {

            @Override
            public boolean areItemsTheSame(@NonNull API oldItem, @NonNull API newItem) {
                return oldItem.equals(newItem);
            }

            @Override
            public boolean areContentsTheSame(@NonNull API oldItem, @NonNull API newItem) {
                return oldItem.equals(newItem);
            }
        });
        this.dialogInterface = dialogInterface;
    }

    public void setData(List<API> newData) {
        data.clear();
        data.addAll(newData);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return data.size();
    }


    @Override
    public ApiSelectAdapter.SelectViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        return new ApiSelectAdapter.SelectViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_dialog_api_select, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ApiSelectAdapter.SelectViewHolder holder, int position) {
        API value = data.get(position);
        API api = API.get(Hawk.get(HawkConfig.API_TYPE, 0));
        ((TextView) holder.itemView.findViewById(R.id.tv_select)).setText(api.getType() == value.getType() ? "√" : " ");
        ((TextView) holder.itemView.findViewById(R.id.tvName)).setText(value.getLabel());
        holder.itemView.findViewById(R.id.tvName).setOnClickListener(v -> {
            dialogInterface.click(value);
        });
    }
}
