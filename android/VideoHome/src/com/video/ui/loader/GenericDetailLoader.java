package com.video.ui.loader;

import android.content.Context;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.VolleyHelper;
import com.google.gson.reflect.TypeToken;
import com.tv.ui.metro.model.DisplayItem;
import com.tv.ui.metro.model.GenericItemList;
import com.tv.ui.metro.model.VideoItem;

/**
 * Created by liuhuadong on 9/10/14.
 */
public abstract class GenericDetailLoader<T> extends BaseGsonLoader<GenericItemList<T>> {
    public static int GAME_LOADER_ID  = 0x701;
    public static int VIDEO_LOADER_ID = 0x702;

    public GenericDetailLoader(Context con, DisplayItem item){
        super(con, item);
    }

    public static GenericDetailLoader<VideoItem> generateVideotLoader(Context con, DisplayItem item){
        GenericDetailLoader<VideoItem> loader = new GenericDetailLoader<VideoItem>(con, item){
            @Override
            public void setCacheFileName() {
                cacheFileName = "video_item_";
            }

            @Override
            protected void loadDataByGson() {
                RequestQueue requestQueue = VolleyHelper.getInstance(getContext().getApplicationContext()).getAPIRequestQueue();
                GsonRequest<GenericItemList<VideoItem>> gsonRequest = new GsonRequest<GenericItemList<VideoItem>>(calledURL, new TypeToken<GenericItemList<VideoItem>>(){}.getType(), null, listener, errorListener);
                gsonRequest.setCacheNeed(getContext().getCacheDir() + "/" + cacheFileName + mItem.id + ".cache");
                requestQueue.add(gsonRequest);
            }
        };
        return  loader;
    }
    
    @Override
    public void setLoaderURL(DisplayItem _item) {
        String url = CommonUrl.BaseURL + _item.ns + "/" + _item.type + "?id=" + _item.id;
        calledURL = new CommonUrl(getContext()).addCommonParams(url);
    }

    public GenericDetailLoader(Context context) {
        super(context);
    }
}
