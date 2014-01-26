package com.trolldad.dashclock.redditheadlines.fragment;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.widget.Toast;

import com.trolldad.dashclock.redditheadlines.R;
import com.trolldad.dashclock.redditheadlines.RedditHeadlinesApplication;
import com.trolldad.dashclock.redditheadlines.RedditHeadlinesExtension;
import com.trolldad.dashclock.redditheadlines.imgur.ImgurAlbumResponse;
import com.trolldad.dashclock.redditheadlines.imgur.ImgurClient;
import com.trolldad.dashclock.redditheadlines.imgur.ImgurImage;
import com.viewpagerindicator.LinePageIndicator;
import com.viewpagerindicator.PageIndicator;
import com.viewpagerindicator.UnderlinePageIndicator;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

/**
 * Created by jacob-tabak on 1/19/14.
 */
@EFragment(R.layout.fragment_album_viewpager)
public class ImgurAlbumFragment extends Fragment {
    @FragmentArg
    String mAlbumId;

    @Bean
    ImgurClient mImgurClient;

    @ViewById(R.id.album_viewpager)
    ViewPager mPager;

    @ViewById(R.id.album_line_page_indicator)
    UnderlinePageIndicator mIndicator;

    private AlbumAdapter mAdapter;

    @AfterViews
    void init() {
        loadAlbumInfo();
        mAdapter = new AlbumAdapter(getChildFragmentManager());
        mPager.setAdapter(mAdapter);
        mIndicator.setViewPager(mPager);
        mIndicator.setFades(false);
    }

    @Background
    void loadAlbumInfo() {
        try {
            ImgurAlbumResponse response = mImgurClient.getService().albumInfo(mAlbumId);
            updateImages(response.getAlbum().images);
        } catch (Exception e) {
            Log.e(RedditHeadlinesExtension.TAG, Log.getStackTraceString(e));
            RedditHeadlinesApplication.toast("Unable to load album");
        }
    }

    @UiThread
    void updateImages(ImgurImage[] images) {
        mAdapter.setImages(images);
    }

    static class AlbumAdapter extends FragmentStatePagerAdapter {
        private ImgurImage[] mImages = new ImgurImage[0];

        public AlbumAdapter(FragmentManager fm) {
            super(fm);
        }

        public void setImages(ImgurImage[] images) {
            mImages = images;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return mImages.length;
        }

        @Override
        public Fragment getItem(int i) {
            return ImgurImageFragment_.builder().mImage(mImages[i]).build();
        }
    }
}