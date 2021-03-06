package com.gala.video.app.epg.ui.setting.ui;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import com.gala.tvapi.tv2.TVApi;
import com.gala.tvapi.tv2.model.DeviceCheck;
import com.gala.tvapi.tv2.result.ApiResultDeviceCheck;
import com.gala.video.api.ApiException;
import com.gala.video.api.IApiCallback;
import com.gala.video.app.epg.R;
import com.gala.video.app.epg.apkupgrade.UpdateManager;
import com.gala.video.lib.framework.core.proguard.Keep;
import com.gala.video.lib.framework.core.utils.LogUtils;
import com.gala.video.lib.share.common.widget.QToast;
import com.gala.video.lib.share.ifmanager.bussnessIF.epg.upgrade.AppVersion;
import com.gala.video.lib.share.ifmanager.bussnessIF.epg.upgrade.IUpdateManager.UpdateOperation;
import com.gala.video.lib.share.network.NetworkStatePresenter;

@Keep
public class UpdateCheckApk {
    private static final int DELAY_THREE_SECOND = 3000;
    private static final String TAG = "UpdateCheckApk";
    private Activity mActivity;
    private Handler mHandler = new Handler(Looper.getMainLooper());

    public UpdateCheckApk(Activity activity) {
        this.mActivity = activity;
    }

    public void checkApk() {
        TVApi.deviceCheck.call(new IApiCallback<ApiResultDeviceCheck>() {
            public void onSuccess(ApiResultDeviceCheck apiResult) {
                AppVersion ver = new AppVersion();
                final DeviceCheck deviceCheck = apiResult.data;
                if (deviceCheck != null) {
                    ver.setVersion(deviceCheck.version);
                    ver.setTip(deviceCheck.tip);
                    ver.setUrl(deviceCheck.url);
                    ver.setUpgradeType(deviceCheck.upgradeType);
                }
                UpdateManager.getInstance().setAppVersion(ver);
                UpdateCheckApk.this.mHandler.post(new Runnable() {
                    public void run() {
                        if (deviceCheck.hasUpdateVersion()) {
                            UpdateCheckApk.this.showUpdateDialog(false);
                        } else {
                            UpdateCheckApk.this.showNoUpdateToast();
                        }
                    }
                });
            }

            public void onException(ApiException e) {
                if (LogUtils.mIsDebug) {
                    LogUtils.e(UpdateCheckApk.TAG, "checkUpdate() -> ApiException() -> e:", e);
                }
                UpdateCheckApk.this.mHandler.post(new Runnable() {
                    public void run() {
                        if (NetworkStatePresenter.getInstance().handleNetWork()) {
                            UpdateCheckApk.this.showNoUpdateToast();
                        }
                    }
                });
            }
        }, new String[0]);
    }

    private void showNoUpdateToast() {
        QToast.makeTextAndShow(this.mActivity, this.mActivity.getString(R.string.not_need_update), 3000);
    }

    public void showUpdateDialog(boolean isFetchData) {
        LogUtils.d(TAG, "showUpdateDialog isFetchData=" + isFetchData);
        UpdateManager.getInstance().showDialogAndStartDownload(this.mActivity, true, new UpdateOperation() {
            public void exitApp() {
            }

            public void cancelUpdate() {
            }
        });
    }
}
