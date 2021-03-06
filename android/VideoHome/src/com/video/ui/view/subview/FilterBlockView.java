package com.video.ui.view.subview;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.tv.ui.metro.model.Block;
import com.tv.ui.metro.model.DisplayItem;
import com.tv.ui.metro.model.VideoItem;
import com.video.ui.R;
import com.video.ui.view.LayoutConstant;
import com.video.ui.view.MetroLayout;

import java.util.ArrayList;

/**
 * Created by liuhuadong on 12/1/14.
 */
public class FilterBlockView  extends BaseCardView implements DimensHelper {
    public FilterBlockView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public FilterBlockView(Context context){
        super(context, null , 0);
    }


    private MetroLayout ml;
    private ArrayList<DisplayItem.FilterItem>filtes;
    public static final int Filter_Type  = LayoutConstant.linearlayout_filter;
    public static final int Episode_Type = LayoutConstant.linearlayout_episode;
    public static final int Episode_list_Type  = LayoutConstant.linearlayout_episode_list;
    public static final int Filter_select_Type = LayoutConstant.linearlayout_filter_select;

    private OnClickListener mItemClick;

    private Drawable mNewBackground;
    public void setOnClickListener(OnClickListener itemClick, Drawable newBackground){
        mItemClick     = itemClick;
        mNewBackground = newBackground;
    }

    public OnClickListener getItemClick(){
        return  mItemClick;
    }

    int mUIType = -1;
    public FilterBlockView(final Context context, ArrayList<DisplayItem.FilterItem> filtes, final int uiType) {
        super(context, null, 0);
        this.filtes = filtes;
        int selectIndex = 2;
        mUIType = uiType;

        RelativeLayout Root = (RelativeLayout) View.inflate(context, R.layout.relative_layout_container, this);

        if(uiType == Filter_Type) {
            Root.setBackgroundResource(R.drawable.com_block_n);
        }

        ml = new MetroLayout(context);
        int step      = 0;
        int row_count = 4;


        int padding = (getDimens().width - row_count*getResources().getDimensionPixelSize(R.dimen.filter_button_width))/(row_count+1);
        if(uiType == LayoutConstant.linearlayout_episode) {
            padding = (getDimens().width - row_count*getResources().getDimensionPixelSize(R.dimen.detail_ep_multy_btn_width))/(row_count+1);
        }
        int itemHeight = 0;
        for(final DisplayItem.FilterItem item: filtes) {
            View convertView = null;
            if(uiType == LayoutConstant.linearlayout_filter) {
                convertView = View.inflate(getContext(), R.layout.filter_item_layout, null);
                convertView.setBackgroundResource(R.drawable.editable_title_com_btn_bg);
            }else if(uiType == LayoutConstant.linearlayout_episode) {
                convertView = View.inflate(getContext(), R.layout.episode_item_layout, null);
                convertView.setBackgroundResource(R.drawable.com_btn_bg);
            }

            if(uiType == LayoutConstant.linearlayout_filter || uiType == LayoutConstant.linearlayout_episode){
                TextView mFiter = (TextView) convertView.findViewById(R.id.channel_filter_btn);
                if(uiType == LayoutConstant.linearlayout_episode) {
                    if (selectIndex == step)
                        mFiter.setTextColor(getResources().getColor(R.color.orange));
                    else
                        mFiter.setTextColor(getResources().getColor(R.color.p_80_black));
                }

                if (step == filtes.size()-1 && uiType == Filter_Type) {
                    Drawable filDrawble = getResources().getDrawable(R.drawable.detail_screening_icon);
                    filDrawble.setBounds(0+dpToPx(10), 0, filDrawble.getMinimumWidth()+dpToPx(10), filDrawble.getMinimumHeight());
                    mFiter.setCompoundDrawables(filDrawble, null, null, null);
                }

                mFiter.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(mItemClick != null){
                            mItemClick.onClick(v);
                        }else {
                            if(uiType == LayoutConstant.linearlayout_filter){
                                if("-1".equals(item.fid)) {
                                    Block<DisplayItem> block = new Block<DisplayItem>();
                                    block.title = item.name;
                                    Intent intent = new Intent(Intent.ACTION_VIEW);
                                    intent.setData(Uri.parse("mvschema://video/filter?rid=" + block.id));
                                    intent.putExtra("item", block);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    getContext().startActivity(intent);
                                }else {
                                    DisplayItem launchItem = new DisplayItem();
                                    launchItem.title = item.name;
                                    launchItem.type = "channelfilter";
                                    launchItem.id = "channel_movie_usa";
                                    launchItem.ns = "video";
                                    launcherAction(context, launchItem);
                                }
                            }
                        }
                    }
                });

                mFiter.setText(item.name);
                ml.addItemViewPort(convertView, uiType == LayoutConstant.linearlayout_filter?LayoutConstant.linearlayout_filter_item:LayoutConstant.linearlayout_episode_item,step % row_count, step / row_count, padding);
                step++;

            }else if(uiType == LayoutConstant.linearlayout_episode_list){

            }
        }

        if(itemHeight == 0){
            if(uiType == LayoutConstant.linearlayout_filter) {
                itemHeight = getResources().getDimensionPixelSize(R.dimen.size_74);
            }else if(uiType == LayoutConstant.linearlayout_episode) {
                itemHeight = getResources().getDimensionPixelSize(R.dimen.detail_ep_multy_btn_height);
            }
        }

        getDimens().height += (itemHeight)* ((step+row_count-1)/row_count) + padding*((step+row_count-1)/row_count + 1);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, getDimens().height);
        lp.addRule(CENTER_HORIZONTAL);
        Root.addView(ml, lp);
    }

    public static FilterBlockView createEpisodeListBlockView(final Context context, ArrayList<VideoItem.Video> episodes, int selectIndex, int maxVisible) {
        final FilterBlockView filterView = new FilterBlockView(context);
        RelativeLayout Root = (RelativeLayout) View.inflate(context, R.layout.relative_layout_container, filterView);
        MetroLayout ml = new MetroLayout(context);

        int row_count  = 1;
        int padding    = 0;
        int itemHeight = 0;
        int size       = episodes.size();
        if(size > maxVisible)
            size = maxVisible;

        for(int i=0;i<size;i++){
            VideoItem.Video item = episodes.get(i);
            VarietyEpisode view = new VarietyEpisode(context);

            if(size == 1) {
                view.setBackgroundResource(R.drawable.com_item_bg_full);
            } else {
                if(i == 0) {
                    view.setBackgroundResource(R.drawable.com_item_bg_up);
                }else if(i == size -1 && episodes.size() <= maxVisible){
                    view.setBackgroundResource(R.drawable.com_item_bg_down);
                    view.findViewById(R.id.line).setVisibility(GONE);
                }
                else {
                    view.setBackgroundResource(R.drawable.com_item_bg_mid);
                }
            }

            view.setData(item, i == selectIndex);
            ml.addItemViewPort(view, LayoutConstant.linearlayout_episode_list_item,0, i , padding);

            view.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(filterView.getItemClick() != null){
                        filterView.getItemClick().onClick(v);
                    }
                }
            });

        }

        if(itemHeight == 0){
            itemHeight = context.getResources().getDimensionPixelSize(R.dimen.detail_variety_item_height);
        }

        filterView.getDimens().height += (itemHeight)* size ;

        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, filterView.getDimens().height);
        lp.addRule(CENTER_HORIZONTAL);
        Root.addView(ml, lp);

        return filterView;
    }

    public static FilterBlockView createFilterSelectBlockView(final Context context, ArrayList<DisplayItem.FilterItem> filters, int maxVisible) {
        final FilterBlockView filterView = new FilterBlockView(context);
        RelativeLayout view = (RelativeLayout) View.inflate(context, R.layout.relative_layout_container, filterView);

        MetroLayout ml = new MetroLayout(context);
        int step      = 0;
        int row_count = 4;
        int padding = (filterView.getDimens().width - row_count*context.getResources().getDimensionPixelSize(R.dimen.filter_button_width))/(row_count+1);

        int itemHeight = 0;
        for(DisplayItem.FilterItem item: filters) {
            View convertView = View.inflate(context, R.layout.filter_check_item_layout, null);
            convertView.setBackgroundResource(R.drawable.com_btn_bg);

            final TextView mFiter = (TextView) convertView.findViewById(R.id.channel_filter_btn);
            mFiter.setText(item.name);
            ml.addItemViewPort(convertView, LayoutConstant.linearlayout_episode_item,step % row_count, step / row_count, padding);

            final ImageView imageView = (ImageView) convertView.findViewById(R.id.channel_filter_selected);
            convertView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (filterView.getItemClick() != null) {
                        filterView.getItemClick().onClick(v);
                    }
                    if (imageView.getVisibility() == VISIBLE) {
                        imageView.setVisibility(GONE);
                        mFiter.setTextColor(context.getResources().getColor(R.color.text_color_deep_dark));
                        mFiter.setBackgroundResource(0);
                    } else {
                        imageView.setVisibility(VISIBLE);
                        mFiter.setTextColor(context.getResources().getColor(R.color.orange));
                        mFiter.setBackgroundResource(R.drawable.editable_title_com_btn_bg_s);
                    }
                }
            });
            step++;
        }

        if(itemHeight == 0){
            itemHeight = context.getResources().getDimensionPixelSize(R.dimen.detail_ep_multy_btn_height);
        }

        filterView.getDimens().height += (itemHeight)* ((step+row_count-1)/row_count) + padding*((step+row_count-1)/row_count);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, filterView.getDimens().height);
        lp.addRule(CENTER_HORIZONTAL);
        filterView.addView(ml, lp);

        return filterView;
    }

    public static FilterBlockView createSearchBlockView(Context context,ArrayList<DisplayItem>items, int maxVisible) {
        FilterBlockView filterView = new FilterBlockView(context);
        RelativeLayout view = (RelativeLayout) View.inflate(context, R.layout.relative_layout_container, filterView);

        MetroLayout ml = new MetroLayout(context);
        int step      = 0;
        int row_count = 4;


        int padding = (filterView.getDimens().width - row_count*context.getResources().getDimensionPixelSize(R.dimen.filter_button_width))/(row_count+1);

        int itemHeight = 0;
        for(DisplayItem item: items) {
            View convertView = View.inflate(context, R.layout.episode_item_layout, null);
            convertView.setBackgroundResource(R.drawable.com_btn_bg);

            final TextView mFiter = (TextView) convertView.findViewById(R.id.channel_filter_btn);
            mFiter.setText(item.title);
            ml.addItemViewPort(convertView, LayoutConstant.linearlayout_episode_item,step % row_count, step / row_count, padding);

            mFiter.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                }
            });
            step++;
        }

        if(itemHeight == 0){
            itemHeight = context.getResources().getDimensionPixelSize(R.dimen.detail_ep_multy_btn_height);
        }

        filterView.getDimens().height += (itemHeight)* ((step+row_count-1)/row_count) + padding*((step+row_count-1)/row_count);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, filterView.getDimens().height);
        lp.addRule(CENTER_HORIZONTAL);
        filterView.addView(ml, lp);

        return filterView;
    }


    private DimensHelper.Dimens mDimens;
    @Override
    public DimensHelper.Dimens getDimens() {
        if(mDimens == null){
            mDimens = new DimensHelper.Dimens();
            mDimens.width  = getResources().getDimensionPixelSize(R.dimen.media_banner_width);
            mDimens.height = 0;
        }
        return mDimens;
    }

    @Override
    public void invalidateUI() {
    }

    @Override
    public void unbindDrawables(View view) {

    }

    public static class VarietyEpisode extends RelativeLayout {
        //UI
        private View mPoster;
        private TextView mData;
        private TextView mName;

        private int mColorNormal;
        private int mColorSelected;

        //data
        private VideoItem.Video mItem;

        public VarietyEpisode(Context context, AttributeSet attrs) {
            this(context, attrs, 0);
        }
        public VarietyEpisode(Context context) {
            this(context, null, 0);
        }

        public VarietyEpisode(Context context, AttributeSet attrs, int uiStyle) {
            super(context, attrs, uiStyle);
            init();
        }


        public VideoItem.Video getData() {
            return mItem;
        }

        public void setData(VideoItem.Video item, boolean selected) {
            this.mItem = item;
            refresh(selected);
        }

        //init
        private void init() {
            View.inflate(getContext(), R.layout.detail_ep_item_variety, this);
            mColorNormal   = getResources().getColor(R.color.p_80_black);
            mColorSelected = getResources().getColor(R.color.orange);
            mPoster = findViewById(R.id.detail_variety_item_poster);
            mData = (TextView) findViewById(R.id.detail_variety_item_data);
            mName = (TextView) findViewById(R.id.detail_variety_item_name);
        }

        //packaged method
        private void refresh(boolean selected) {
            if(mItem == null) {
                return;
            }
            mData.setText(mItem.name);
            mName.setText(mItem.desc);

            mPoster.setSelected(selected);
            if(selected) {
                mName.setTextColor(mColorSelected);
                mData.setTextColor(mColorSelected);
            } else {
                mName.setTextColor(mColorNormal);
                mData.setTextColor(mColorNormal);
            }
        }
    }
}
