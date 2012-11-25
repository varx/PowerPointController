package me.varx.pptremote.adapter;

import java.util.ArrayList;

import me.varx.pptremote.AppContext;
import me.varx.pptremote.Presentation;
import me.varx.pptremote.R;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class SlideAdapter extends PagerAdapter {

	ArrayList<ImageView> slides=Presentation.slides;
	Context context;
	AppContext app;
	ImageView blankImage;
	static String LOGTAG="ADAPTER";

	public SlideAdapter(Context context,AppContext app) {
		this.context=context;
		this.app=app;
		for (int i = 0; i < Presentation.totlePages; i++) {
			blankImage=new ImageView(context);
			blankImage.setImageResource(R.drawable.blank);
			slides.add(blankImage);
		}
	}
	
	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		Log.d(LOGTAG, "instantiateItem :"+position);
		ImageView IV=(ImageView) slides.get(position);
		if(!Presentation.isSetThumb(position)){
			Log.d(LOGTAG, "postion is set "+position+" : false");
			app.getThumb(position);
		}
		container.addView(IV);
		return IV;
	}
	
	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		Log.d(LOGTAG, "destroyItem");
		container.removeView(slides.get(position));
	}
	
	@Override
	public int getCount() {
		return Presentation.totlePages;
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return arg0==arg1;
	}

}
