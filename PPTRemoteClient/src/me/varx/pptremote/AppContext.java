package me.varx.pptremote;


import me.varx.pptremote.network.Controller;
import me.varx.pptremote.network.TcpConnect;
import me.varx.pptremote.network.ThumbDownload;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class AppContext {
	/**
	 * callBack message define
	 */
	//network
	public static final byte NETWORK_OK=0x10;
	public static final byte NETWORK_ERROR=0x11;
	//thumb
	public static final byte GOT_THUMB=0x12;
	public static final byte GOT_THUMB_FAIL=0x13;
	//object created
	public static final byte THUMB_DOWNLOAD_CREATED=0x14;
	public static final byte CONTROLLER_CREATED=0x15;
	//remote message
	public static final byte JUMP_TO=0x16;
	public static final byte START=0x17;
	public static final byte END=0x18;
	public static final byte NEW_FILE=0x19;
	public static final byte CLOSE_FILE=0x1A;
	//the logcat tag
	private static String TAG="AppContext";
	//
	private static AppContext app;
	private Handler handler;
	
	public Controller controller;
	private TcpConnect tcpConnect;
	private ThumbDownload thumbDownload;
	//for remember the request thumb id
	private int thumbId;
	private boolean isReadingThumb=false;
	
	private AppContext(){
	}
	
	
	public static AppContext getApp(){
		if(app==null)
			app=new AppContext();
		return app;
	}
	
	public void sethandler(Handler handler){
		this.handler=handler;
	}
	
	public void getThumb(int id){
		if(isReadingThumb){
			Log.d(TAG, "reading,cancel getThumb request");
			return;
		}
		isReadingThumb=true;
		thumbId=id;
		thumbDownload.getThumb(id);
	}
	
	private void sendMessage(int what){
		handler.sendEmptyMessage(what);
	}
	
	private void sendMessage(Message msg){
		handler.sendMessage(msg);
	}
	
	//for the callback with object
	public void callBack(byte type,Object obj){
		switch (type) {
		case THUMB_DOWNLOAD_CREATED:
			thumbDownload=(ThumbDownload) obj;
			break;
		case CONTROLLER_CREATED:
			controller=(Controller) obj;
			break;
		}
	}
	//for the callback with int value
	public void callBack(byte type,int val){
		switch (type) {
		case JUMP_TO:
			if(val<1||val>Presentation.totlePages)
				break;
			Message msg=new Message();
			msg.what=MSGVAL.JUMP;
			msg.arg1=val;
			sendMessage(msg);
			break;
		default:
			break;
		}
	}
	
	public void callBack(byte type){
		switch (type) {
		case GOT_THUMB:
			isReadingThumb=false;
			Message msg=new Message();
			msg.what=MSGVAL.GOT_THUMB;
			msg.arg2=thumbId;
			sendMessage(msg);
			break;
		case NETWORK_OK:
			sendMessage(MSGVAL.CONNECT_OK);
			break;
		case NETWORK_ERROR:
			sendMessage(MSGVAL.ERROR_NET_IO);
			break;
		case START:
			sendMessage(MSGVAL.START);
			break;
		case END:
			sendMessage(MSGVAL.STOP);
			break;
		case NEW_FILE:
			sendMessage(MSGVAL.NEWFILE);
			break;
		case CLOSE_FILE:
			sendMessage(MSGVAL.CLOSEFILE);
		default:
			break;
		}
	}
	
	public void connect(){
		tcpConnect=new TcpConnect(this,"192.168.137.2",12306);
		tcpConnect.connect();
	}
	
	public void disConnect(){
		tcpConnect.disconnect();
	}
}
