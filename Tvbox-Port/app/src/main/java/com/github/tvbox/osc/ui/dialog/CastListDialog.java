package com.github.tvbox.osc.ui.dialog;

import android.content.Context;
import android.service.controls.DeviceTypes;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.cast.dlna.dmc.DLNACastManager;
import com.android.cast.dlna.dmc.control.DeviceControl;
import com.android.cast.dlna.dmc.control.OnDeviceControlListener;
import com.android.cast.dlna.dmc.control.ServiceActionCallback;
import com.blankj.utilcode.util.ToastUtils;
import com.github.tvbox.osc.R;
import com.github.tvbox.osc.base.App;
import com.github.tvbox.osc.bean.CastVideo;
import com.github.tvbox.osc.callback.DLNACallback;
import com.github.tvbox.osc.ui.adapter.CastDevicesAdapter;
import com.github.tvbox.osc.util.LocalIPAddress;
import com.lxj.xpopup.core.CenterPopupView;

import org.fourthline.cling.model.meta.Device;
import org.fourthline.cling.support.lastchange.EventedValue;
import org.fourthline.cling.support.model.TransportState;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import kotlin.Unit;

public class CastListDialog extends CenterPopupView {

    private static final String URL_LOCAL = "127.0.0.1";
    private final CastVideo castVideo;
    private CastDevicesAdapter adapter;
    private DLNACallback dlnaCallback;
    private DeviceControl deviceControl;

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
        Log.d("hoheiya", "CastListDialog onCreate()");

        DLNACastManager.INSTANCE.bindCastService(App.getInstance());
        findViewById(R.id.btn_cancel).setOnClickListener(view -> {
            dismiss();
            if (dlnaCallback != null) {
                dlnaCallback.onClose();
            }
        });
        findViewById(R.id.btn_confirm).setOnClickListener(view -> {
            adapter.setNewData(new ArrayList<>());
            DLNACastManager.INSTANCE.search(null);
        });
        RecyclerView rv = findViewById(R.id.rv);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new CastDevicesAdapter();
        rv.setAdapter(adapter);
        DLNACastManager.INSTANCE.registerDeviceListener(adapter);

        adapter.setOnItemClickListener((adapter, view, position) -> {
            Device item = (Device) adapter.getItem(position);
            if (item != null) {
                cast(item, castVideo);
//                DLNACastManager.INSTANCE.cast(item, castVideo);
//                dismiss();
//                if (dlnaCallback != null) {
//                    dlnaCallback.onDLNA();
//                }
            }
        });
    }

    private void cast(Device item, CastVideo castVideo) {
        Log.d("hoheiya", "connectDevice " + item.getDetails().getFriendlyName());
        deviceControl = DLNACastManager.INSTANCE.connectDevice(item,
                new OnDeviceControlListener() {
                    @Override
                    public void onConnected(@NonNull Device<?, ?, ?> device) {
                        Log.d("hoheiya", "设备[" + item.getDetails().getFriendlyName() + "] 连接成功");
                        deviceControl.setAVTransportURI(castVideo.getUri(), castVideo.getName(),
                                new ServiceActionCallback<Unit>() {
                                    @Override
                                    public void onSuccess(Unit unit) {
                                        DLNACastManager.INSTANCE.disconnectDevice(device);
                                        if (dlnaCallback != null) {
                                            dlnaCallback.onDLNA();
                                        }
                                        dismiss();
                                    }

                                    @Override
                                    public void onFailure(@NonNull String s) {
                                        DLNACastManager.INSTANCE.disconnectDevice(device);
                                        ToastUtils.showShort("投屏失败，" + s);
                                        if (dlnaCallback != null) {
                                            dlnaCallback.onClose();
                                        }
                                        dismiss();
                                    }
                                });
                    }

                    @Override
                    public void onDisconnected(@NonNull Device<?, ?, ?> device) {
                        Log.d("hoheiya", "设备[" + item.getDetails().getFriendlyName() + "] 连接已断开");
                    }

                    @Override
                    public void onEventChanged(@NonNull EventedValue<?> eventedValue) {
                        Log.d("hoheiya", "onEventChanged " + eventedValue);

                    }

                    @Override
                    public void onAvTransportStateChanged(@NonNull TransportState transportState) {
                        Log.d("hoheiya", "onAvTransportStateChanged " + transportState);
                    }

                    @Override
                    public void onRendererVolumeChanged(int i) {
                        Log.d("hoheiya", "onRendererVolumeChanged " + i);
                    }

                    @Override
                    public void onRendererVolumeMuteChanged(boolean b) {
                        Log.d("hoheiya", "onRendererVolumeMuteChanged " + b);
                    }
                });

    }

    @Override
    protected void onDismiss() {
        super.onDismiss();
        DLNACastManager.INSTANCE.unregisterListener(adapter);
        DLNACastManager.INSTANCE.unbindCastService(App.getInstance());
    }
}