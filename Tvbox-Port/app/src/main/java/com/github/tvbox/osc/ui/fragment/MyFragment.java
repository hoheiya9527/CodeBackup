package com.github.tvbox.osc.ui.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.widget.Toast;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.ClipboardUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.github.tvbox.osc.R;
import com.github.tvbox.osc.base.BaseActivity;
import com.github.tvbox.osc.base.BaseVbFragment;
import com.github.tvbox.osc.bean.API;
import com.github.tvbox.osc.databinding.FragmentMyBinding;
import com.github.tvbox.osc.ui.activity.CollectActivity;
import com.github.tvbox.osc.ui.activity.DetailActivity;
import com.github.tvbox.osc.ui.activity.HistoryActivity;
import com.github.tvbox.osc.ui.activity.LiveActivity;
import com.github.tvbox.osc.ui.activity.MainActivity;
import com.github.tvbox.osc.ui.activity.MovieFoldersActivity;
import com.github.tvbox.osc.ui.activity.SettingActivity;
import com.github.tvbox.osc.ui.activity.SubscriptionActivity;
import com.github.tvbox.osc.ui.dialog.AboutDialog;
import com.github.tvbox.osc.ui.dialog.ApiHistoryDialog;
import com.github.tvbox.osc.ui.dialog.ApiSelectDialog;
import com.github.tvbox.osc.util.DocumentUtil;
import com.github.tvbox.osc.util.HawkConfig;
import com.github.tvbox.osc.util.Utils;
import com.hjq.permissions.OnPermissionCallback;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;
import com.lxj.xpopup.XPopup;
import com.orhanobut.hawk.Hawk;

import java.util.Arrays;
import java.util.List;

/**
 * @author pj567
 * @date :2021/3/9
 * @description:
 */
public class MyFragment extends BaseVbFragment<FragmentMyBinding> {


    @Override
    protected void init() {
        mBinding.tvVersion.setText("v" + AppUtils.getAppVersionName());

        mBinding.addrPlay.setOnClickListener(v -> {
            new XPopup.Builder(getContext()).asInputConfirm("播放", "", isPush(ClipboardUtils.getText().toString()) ? ClipboardUtils.getText() : "", "地址", text -> {
                if (!TextUtils.isEmpty(text)) {
                    Intent newIntent = new Intent(mContext, DetailActivity.class);
                    newIntent.putExtra("id", text);
                    newIntent.putExtra("sourceKey", "push_agent");
                    newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(newIntent);
                }
            }, null, R.layout.dialog_input).show();
        });
        //mBinding.tvLive.setOnClickListener(v -> jumpActivity(LivePlayActivity.class));
        mBinding.tvLive.setOnClickListener(v -> jumpActivity(LiveActivity.class));
        mBinding.llUrlSwitch.setOnClickListener(view -> {
//            int type = Hawk.get(HawkConfig.API_TYPE, 0) + 1;
//            API api = API.get(type);
//            Hawk.put(HawkConfig.API_TYPE, api.getType());
//            mBinding.tvUrlSwitch.setText(api.getLabel());
//            updateUrl()
            new XPopup.Builder(getContext())
                    .asCustom(new ApiSelectDialog(getContext(), api -> {
//                        Toast.makeText(getContext(), "选择 " + api.getLabel(), Toast.LENGTH_SHORT).show();
                        Hawk.put(HawkConfig.API_TYPE, api.getType());
                        mBinding.tvUrlSwitch.setText(api.getLabel());
                        updateUrl();
                    }))
                    .show();
            ;
        });
        mBinding.tvUrlSwitch.setText(API.get(Hawk.get(HawkConfig.API_TYPE, 0)).getLabel());
        mBinding.tvSetting.setOnClickListener(v -> jumpActivity(SettingActivity.class));

        mBinding.tvHistory.setOnClickListener(v -> jumpActivity(HistoryActivity.class));

        mBinding.tvFavorite.setOnClickListener(v -> jumpActivity(CollectActivity.class));

        mBinding.tvLocal.setOnClickListener(v -> {
            if (!XXPermissions.isGranted(mContext, Permission.MANAGE_EXTERNAL_STORAGE)) {
                showPermissionTipPopup();
            } else {
                jumpActivity(MovieFoldersActivity.class);
            }
        });

        mBinding.llSubscription.setOnClickListener(v -> jumpActivity(SubscriptionActivity.class));

        mBinding.llAbout.setOnClickListener(v -> {
            new XPopup.Builder(mActivity).asCustom(new AboutDialog(mActivity)).show();
        });
    }

    private void updateUrl() {
        if (getActivity() == null || getActivity().isDestroyed()) {
            return;
        }
        BaseActivity activity = (BaseActivity) getActivity();
        activity.showLoadingDialog();
        DocumentUtil.getTvboxUrl(result -> {
            if (!TextUtils.isEmpty(result)) {
                Hawk.put(HawkConfig.API_URL, result);
                new Handler(Looper.getMainLooper()).post(() -> {
                    activity.dismissLoadingDialog();
                    Toast.makeText(getActivity(), "配置已成功更新", Toast.LENGTH_LONG).show();
                    ActivityUtils.finishAllActivities(true);
                    startActivity(new Intent(getActivity(), MainActivity.class));
                });
            } else {
                new Handler(Looper.getMainLooper()).post(() -> {
                    activity.dismissLoadingDialog();
                    Toast.makeText(getActivity(), "获取" + API.get(Hawk.get(HawkConfig.API_TYPE, 0)).getLabel() + "失败", Toast.LENGTH_LONG).show();
                });
            }
        });
    }


    private void showPermissionTipPopup() {
        new XPopup.Builder(mActivity).isDarkTheme(Utils.isDarkTheme()).asConfirm("提示", "为了播放视频、音频等,我们需要访问您设备文件的读写权限", () -> {
            getPermission();
        }).show();
    }

    private void getPermission() {
        XXPermissions.with(this).permission(Permission.MANAGE_EXTERNAL_STORAGE).request(new OnPermissionCallback() {
            @Override
            public void onGranted(List<String> permissions, boolean all) {
                if (all) {
                    jumpActivity(MovieFoldersActivity.class);
                } else {
                    ToastUtils.showLong("部分权限未正常授予,请授权");
                }
            }

            @Override
            public void onDenied(List<String> permissions, boolean never) {
                if (never) {
                    ToastUtils.showLong("读写文件权限被永久拒绝，请手动授权");
                    // 如果是被永久拒绝就跳转到应用权限系统设置页面
                    XXPermissions.startPermissionActivity(mActivity, permissions);
                } else {
                    ToastUtils.showShort("获取权限失败");
                    showPermissionTipPopup();
                }
            }
        });
    }

    private boolean isPush(String text) {
        return !TextUtils.isEmpty(text) && Arrays.asList("smb", "http", "https", "thunder", "magnet", "ed2k", "mitv", "jianpian").contains(Uri.parse(text).getScheme());
    }

}