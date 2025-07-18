package com.github.tvbox.osc.ui.dialog;

import android.content.Context;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.github.tvbox.osc.R;
import com.github.tvbox.osc.bean.API;
import com.github.tvbox.osc.ui.adapter.ApiSelectAdapter;
import com.owen.tvrecyclerview.widget.TvRecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ApiSelectDialog extends BaseDialog {
    public ApiSelectDialog(@NonNull @NotNull Context context) {
        super(context, R.style.CustomDialogStyleDim);
        setContentView(R.layout.dialog_api_select);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void setTip(String tip) {
        ((TextView) findViewById(R.id.title)).setText(tip);
    }

    public void setAdapter(ApiSelectAdapter.SelectDialogInterface sourceBeanSelectDialogInterface,
                           List<API> data) {
        ApiSelectAdapter adapter = new ApiSelectAdapter(sourceBeanSelectDialogInterface);
        adapter.setData(data);
        TvRecyclerView tvRecyclerView = ((TvRecyclerView) findViewById(R.id.list));
        tvRecyclerView.setAdapter(adapter);
    }
}
