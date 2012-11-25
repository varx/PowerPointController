package me.varx.pptremote.ui;

import me.varx.pptremote.AppContext;
import me.varx.pptremote.MSGVAL;
import me.varx.pptremote.Presentation;
import me.varx.pptremote.R;
import me.varx.pptremote.adapter.SlideAdapter;
import me.varx.pptremote.network.Controller;
import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

public class ControllerActivity extends Activity implements android.content.DialogInterface.OnClickListener, OnPageChangeListener {

	
	private Switch switchPlay;
	
	private TextView textViewFile;
	private TextView textViewTitle;
	private TextView textViewTotle;
	private TextView textViewCurrent;
	private TextView textViewComment;
	
	private ViewPager viewPager;
	
	private ProgressBar progressBar;
	
	private Button buttonWhite;
	private Button buttonBlack;
	private Button buttonJump;
	
	private AppContext appContext;
	private Controller controller;
	
	private String LOGTAG="ControllerActivity";
	
	private Dialog dialog;
	
	private boolean isPlaying=false;
	private boolean setViewPagerManual= false;
	
	private Handler handler=new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case MSGVAL.NEWFILE:
				setFileInfo();
				break;
			case MSGVAL.GOT_THUMB:
				int slideId=msg.arg2;
				setThumb(slideId);
				break;
			case MSGVAL.ERROR_NET_IO:
				networkError();
				break;
			case MSGVAL.CLOSEFILE:
				closeFile();
				break;
			case MSGVAL.JUMP:
				int page=msg.arg1;
				Presentation.currentPage=page;
				jumpTo();
				break;
			case MSGVAL.STOP:
				endPlay();
			default:
				break;
			}
		}


	};
	private void endPlay() {
		if(isPlaying){
			isPlaying=false;
			switchPlay.setChecked(false);
		}
	}
	private void setThumb(int slideId) {
		Presentation.slides.get(slideId).setImageBitmap(Presentation.thumbs.get(slideId));
	}
	
	private OnClickListener	buttonClickListener= new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.buttonJump:
				showPageMenu();
				break;
			case R.id.buttonBlackScreen:
				controller.blackScreen();
				break;
			case R.id.buttonWhiteScreen:
				controller.whiteScreen();
				break;
			default:
				break;
			}
		}
	};
	private OnCheckedChangeListener switchChangeListener=new OnCheckedChangeListener() {
		
		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			if(isChecked&&(!isPlaying)){
				Log.d(LOGTAG, "start paly manual");
				jumpTo();
				isPlaying=true;
				controller.jumpTo(Presentation.currentPage);
			}
			if((!isChecked)&&(isPlaying)){
				Log.d(LOGTAG, "end paly manual");
				isPlaying=false;
				controller.endPlay();
			}
		}
	};
	
	private void networkError(){
		closeFile();
		showToast("连接中断");
		Intent intent=new Intent(this,MainActivity.class);
		startActivity(intent);
		this.finish();
	}
	
	private void showToast(String msg){
		Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
	}
	
	
	private void setFileInfo(){
		textViewFile.setText(Presentation.fileName);
		textViewTotle.setText("/"+Presentation.totlePages);
		textViewCurrent.setText("1");
		textViewTitle.setText(Presentation.titles[0]);
		dialog=null;
		progressBar.setMax(Presentation.totlePages);
	}
	
	private void closeFile(){
		textViewFile.setText("请在powerPoint中打开文档");
		textViewTotle.setText("/0");
		textViewCurrent.setText("0");
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_controller);
		
		findView();
		
		appContext=AppContext.getApp();
		controller=appContext.controller;
		appContext.sethandler(handler);
		
		buttonBlack.setOnClickListener(buttonClickListener);
		buttonWhite.setOnClickListener(buttonClickListener);
		buttonJump.setOnClickListener(buttonClickListener);
		
		switchPlay.setOnCheckedChangeListener(switchChangeListener);
		
		SlideAdapter slideAdapter=new SlideAdapter(this, appContext);
		viewPager.setAdapter(slideAdapter);
		viewPager.setOnPageChangeListener(this);
		
		if(Presentation.isOpen){
			setFileInfo();
		}
	}
	
	
	private void findView(){
		textViewFile=(TextView) findViewById(R.id.textViewFile);
		textViewTitle=(TextView) findViewById(R.id.textViewTitle);
		textViewTotle=(TextView) findViewById(R.id.textViewTotleId);
		textViewCurrent=(TextView) findViewById(R.id.textViewCurrentId);
		
		switchPlay =(Switch) findViewById(R.id.switchPlay);
		textViewComment=(TextView) findViewById(R.id.textViewComment);
		
		buttonBlack=(Button) findViewById(R.id.buttonBlackScreen);
		buttonWhite=(Button) findViewById(R.id.buttonWhiteScreen);
		buttonJump=(Button) findViewById(R.id.buttonJump);
		
		viewPager=(ViewPager) findViewById(R.id.viewPager);
		progressBar=(ProgressBar) findViewById(R.id.progressBar);
	}

	private void showPageMenu() {
		if(dialog==null){
			Builder builder=new Builder(this);
			builder.setItems(Presentation.titles, this);
			dialog=builder.create();
		}
		dialog.show();
	}
	
	private void jumpTo() {
		Log.d(LOGTAG, "PC:jump to "+Presentation.currentPage);
		if(!isPlaying){
			Log.d(LOGTAG, "not playing,set status to playing");
			isPlaying=true;
			switchPlay.setChecked(true);
		}
		Log.d(LOGTAG, " current Page ="+Presentation.currentPage);
		if(Presentation.currentPage>Presentation.totlePages)
			return;
		setDisplaySlide();
	}
	
	private void setDisplaySlide(){
		int id=Presentation.currentPage;
		if(id<=Presentation.titles.length){
			progressBar.setProgress(id);
			textViewCurrent.setText(id+" ");
			textViewTitle.setText(Presentation.titles[id-1]);
			setViewPagerManual=true;
			viewPager.setCurrentItem(id-1, true);
			setViewPagerManual=false;
			
			if(Presentation.notes[id-1]!=null){
				textViewComment.setText(Presentation.notes[id-1]);
			}
		}
	}
	
	@Override
	public void onClick(DialogInterface arg0, int arg1) {
		Presentation.currentPage=arg1+1;
		setDisplaySlide();
		if(isPlaying)
			controller.jumpTo(arg1+1);
	}
	
	@Override
	public void onPageScrollStateChanged(int arg0) {
	}
	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
	}
	@Override
	public void onPageSelected(int arg0) {
		if(setViewPagerManual)
			return;
		Presentation.currentPage=arg0+1;
		setDisplaySlide();
		if(!isPlaying)
			return;
		jumpTo();
		controller.jumpTo(arg0+1);
	}
	
}
