package com.github.tvbox.osc.ui.dialog;

import android.content.Context;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.cast.dlna.dmc.DLNACastManager;
import com.blankj.utilcode.util.GsonUtils;
import com.blankj.utilcode.util.LogUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.github.tvbox.osc.R;
import com.github.tvbox.osc.base.App;
import com.github.tvbox.osc.bean.CastVideo;
import com.github.tvbox.osc.callback.DLNACallback;
import com.github.tvbox.osc.ui.adapter.CastDevicesAdapter;
import com.github.tvbox.osc.util.LocalIPAddress;
import com.lxj.xpopup.core.BottomPopupView;
import com.lxj.xpopup.core.CenterPopupView;

import org.fourthline.cling.model.meta.Device;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class CastListDialog extends CenterPopupView {

    private static final String URL_LOCAL = "127.0.0.1";
    private final CastVideo castVideo;
    private CastDevicesAdapter adapter;
    private DLNACallback dlnaCallback;

    public CastListDialog(@NonNull @NotNull Context context, CastVideo castVideo) {
        super(context);
        this.castVideo = castVideo;
        checkUrl(context);
    }

    public CastListDialog(@NonNull @NotNull Context context, CastVideo castVideo, DLNACallback dlnaCallback) {
        super(context);
        this.dlnaCallback = dlnaCallback;
        this.castVideo = castVideo;
        checkUrl(context);
    }

    private void checkUrl(Context context) {
        if (castVideo != null) {
            String url = castVideo.getUri();
            Log.d("hoheiya", ">>cast url:" + url);
            if (url.contains(URL_LOCAL)) {
                String string = LocalIPAddress.getIP(context);
                url = url.replace(URL_LOCAL, string);
                Log.d("hoheiya", ">>cast new url:" + url);
                castVideo.setUrl(url);
            }
        }
    }

    @Override
    protected int getImplLayoutId() {
        return R.layout.dialog_cast;
    }

    @Override
    protected void onCreate() {
        super.onCreate();
        DLNACastManager.getInstance().bindCastService(App.getInstance());
        findViewById(R.id.btn_cancel).setOnClickListener(view -> {
            dismiss();
            if (dlnaCallback != null) {
                dlnaCallback.onClose();
            }
        });
        findViewById(R.id.btn_confirm).setOnClickListener(view -> {
            adapter.setNewData(new ArrayList<>());
            DLNACastManager.getInstance().search(null, 1);
        });
        RecyclerView rv = findViewById(R.id.rv);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new CastDevicesAdapter();
        rv.setAdapter(adapter);
        DLNACastManager.getInstance().registerDeviceListener(adapter);

        adapter.setOnItemClickListener((adapter, view, position) -> {
            Device item = (Device) adapter.getItem(position);
            if (item != null) {
                DLNACastManager.getInstance().cast(item, castVideo);
                dismiss();
                if (dlnaCallback != null) {
                    dlnaCallback.onDLNA();
                }
            }
        });
    }

    @Override
    protected void onDismiss() {
        super.onDismiss();
        DLNACastManager.getInstance().unregisterListener(adapter);
        DLNACastManager.getInstance().unbindCastService(App.getInstance());
    }
}