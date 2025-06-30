package com.github.tvbox.osc.ui.dialog;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.github.tvbox.osc.R;
import com.github.tvbox.osc.bean.API;
import com.github.tvbox.osc.databinding.DialogTitleListBinding;
import com.github.tvbox.osc.ui.adapter.APISelectAdapter;
import com.github.tvbox.osc.ui.widget.LinearSpacingItemDecoration;
import com.lxj.xpopup.core.CenterPopupView;

import java.util.Arrays;
import java.util.List;

public class ApiSelectDialog extends CenterPopupView {
    private final OnInputConfirmListener mOnInputConfirmListener;

    public interface OnInputConfirmListener {
        void onConfirm(API api);
    }

    private List<API> list = null;

    public ApiSelectDialog(@NonNull Context context, OnInputConfirmListener onInputConfirmListener) {
        super(context);
        mOnInputConfirmListener = onInputConfirmListener;
    }

    @Override
    protected int getImplLayoutId() {
        return R.layout.dialog_title_list;
    }

    @Override
    protected void onCreate() {
        super.onCreate();
        DialogTitleListBinding binding = DialogTitleListBinding.bind(getPopupImplView());
        binding.title.setText("请选择配置源");
        binding.ivUseTip.setVisibility(View.GONE);
        binding.rv.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rv.addItemDecoration(new LinearSpacingItemDecoration(20, true));
        APISelectAdapter adapter = new APISelectAdapter();
        binding.rv.setAdapter(adapter);
        list = Arrays.asList(API.values());
        adapter.setNewData(list);
        adapter.setOnItemChildClickListener((adapter1, view, position) -> {
            mOnInputConfirmListener.onConfirm(list.get(position));
            dismiss();
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}