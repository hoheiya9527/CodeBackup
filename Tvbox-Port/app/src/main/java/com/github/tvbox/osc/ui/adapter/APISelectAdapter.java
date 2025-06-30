package com.github.tvbox.osc.ui.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.github.tvbox.osc.R;
import com.github.tvbox.osc.bean.API;
import com.github.tvbox.osc.util.HawkConfig;
import com.orhanobut.hawk.Hawk;

import java.util.ArrayList;

public class APISelectAdapter extends BaseQuickAdapter<API, BaseViewHolder> {
    public APISelectAdapter() {
        super(R.layout.item_api_select, new ArrayList<>());
    }

    @Override
    protected void convert(BaseViewHolder helper, API item) {
        helper.setText(R.id.tvName, item.getLabel());
        API api = API.get(Hawk.get(HawkConfig.API_TYPE, 0));
        helper.setText(R.id.tvSelect, api.getType() == item.getType() ? "√" : "");
        helper.addOnClickListener(R.id.tvName, R.id.tvSelect);
    }
}