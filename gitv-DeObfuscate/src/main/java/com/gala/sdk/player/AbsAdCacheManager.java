package com.gala.sdk.player;

import android.util.Log;
import com.gala.sdk.player.AdCacheManager.AdCacheItem;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public abstract class AbsAdCacheManager implements AdCacheManager {
    public final String TAG = ("AbsAdCacheManager@" + Integer.toHexString(super.hashCode()));
    private int f631a = 0;
    private final Object f632a = new Object();
    private List<AdCacheItem> f633a = new ArrayList();

    public abstract String getRootPath(int i);

    public boolean isCached(AdCacheItem item) {
        return m417a(m416a(item));
    }

    public String getCacheFilePath(AdCacheItem item) {
        String a = m416a(item);
        if (m417a(a)) {
            return a;
        }
        return "";
    }

    public void addTask(AdCacheItem item) {
        Log.d(this.TAG, "addCacheTask: item=" + item);
        synchronized (this.f632a) {
            this.f633a.add(item);
        }
    }

    public void setCurrentRunningState(int level) {
        Log.d(this.TAG, "setCurrentBusyLevel:" + level);
        this.f631a = level;
    }

    protected void flushAllCachedInvokes() {
        AdCacheItem[] adCacheItemArr;
        int length;
        int i = 0;
        Log.d(this.TAG, "flushAllCachedInvokes()");
        setCurrentRunningState(this.f631a);
        synchronized (this.f632a) {
            int size = this.f633a.size();
            if (size > 0) {
                adCacheItemArr = new AdCacheItem[size];
                for (int i2 = 0; i2 < size; i2++) {
                    adCacheItemArr[i2] = (AdCacheItem) this.f633a.get(i2);
                }
                this.f633a.clear();
            } else {
                adCacheItemArr = null;
            }
        }
        String str = this.TAG;
        StringBuilder stringBuilder = new StringBuilder("flushCachedTask: size=");
        if (adCacheItemArr != null) {
            length = adCacheItemArr.length;
        } else {
            length = 0;
        }
        Log.d(str, stringBuilder.append(length).toString());
        if (adCacheItemArr != null && adCacheItemArr.length > 0) {
            length = adCacheItemArr.length;
            while (i < length) {
                addTask(adCacheItemArr[i]);
                i++;
            }
        }
    }

    private static boolean m418a(String... strArr) {
        for (int i = 0; i <= 0; i++) {
            String str = strArr[0];
            if (str != null && !str.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    private boolean m417a(String str) {
        boolean exists = new File(str).exists();
        Log.d(this.TAG, "checkExist: return " + exists + ", fileName=" + str);
        return exists;
    }

    private String m416a(AdCacheItem adCacheItem) {
        if (adCacheItem == null) {
            Log.d(this.TAG, "generateCacheFilePath: item is null");
            return "";
        }
        if (m418a(getRootPath(adCacheItem.getAdCacheType()))) {
            Log.d(this.TAG, "generateCacheFilePath: ad cache root path is empty");
            return "";
        }
        String url = adCacheItem.getUrl();
        if (!m418a(url)) {
            int lastIndexOf = url.lastIndexOf("/");
            int lastIndexOf2 = url.lastIndexOf("?");
            if (lastIndexOf2 > lastIndexOf && lastIndexOf >= 0 && lastIndexOf2 >= 0) {
                url = url.substring(lastIndexOf + 1, lastIndexOf2);
                if (m418a(url)) {
                    url = r1 + "/qcache/data/ad_cache/" + url;
                    Log.d(this.TAG, "generateCacheFilePath: return " + url);
                    return url;
                }
                Log.d(this.TAG, "generateCacheFilePath: cacheFileName is empty");
                return "";
            }
        }
        url = "";
        if (m418a(url)) {
            url = r1 + "/qcache/data/ad_cache/" + url;
            Log.d(this.TAG, "generateCacheFilePath: return " + url);
            return url;
        }
        Log.d(this.TAG, "generateCacheFilePath: cacheFileName is empty");
        return "";
    }
}
