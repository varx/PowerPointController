package me.varx.pptremote.network;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import me.varx.pptremote.AppContext;
import me.varx.pptremote.MSGVAL;
import me.varx.pptremote.Presentation;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

public class ThumbDownload {
	private InputStream inputStream;
	private OutputStream outputStream;
	private Socket socket;
	private int SlideId;
	
	public Bitmap bitmap;
	
	private static String LOGTAG="ThumbDownload";
	private AppContext app;
	
	
	public ThumbDownload(Socket socket,AppContext app) {
		this.socket=socket;
		this.app=app;
		initConnect();
	}
	
	private void callBack(byte msg){
		app.callBack(msg);
	}
	
	
	public void getThumb(int id){
		Log.d(LOGTAG,"getThumb Id : "+id);
		this.SlideId=id;
		new Thread(downloadRunnable).start();
	}
	
	private void initConnect(){
		try {
			inputStream=socket.getInputStream();
			outputStream=socket.getOutputStream();
			outputStream.write(MSGVAL.SOCKET_IDIMG);
		} catch (IOException e) {
			callBack(AppContext.NETWORK_ERROR);
			Log.d(LOGTAG, "get Stream Error");
			e.printStackTrace();
		}
	}
	
	
	Runnable downloadRunnable=new Runnable() {
		@Override
		public void run() {
			Log.d(LOGTAG, "request the thumb,id is "+SlideId+1);
			sendUshort(SlideId+1);
			readImg();
		}
	};
	
	private void sendUshort(int val){
		if(val>65535||val<0)
			return;
		byte[] data=new byte[2];
		data[0]=(byte)val;
		data[1]=(byte)(val>>8);
		try {
			outputStream.write(data, 0, 2);
		} catch (IOException e) {
			Log.d(LOGTAG, "send Stream Error");
		}
	}
	
    private void readImg(){
    	byte[] dataImg;
    	try {
    		int index=0;
			int length=readInt();
			Log.d(LOGTAG, "IMG length "+length+" bytes");
			dataImg=new byte[length];
			int bufferSize=socket.getReceiveBufferSize();
			int readSize=0;
			while(index<length){
				if(bufferSize<(length-index)){
					readSize=inputStream.read(dataImg, index,bufferSize);
				}else{
					readSize=inputStream.read(dataImg, index, length-index);
				}
				if(readSize==-1){
					throw new IOException();
				}else{
					index+=readSize;
				}
			}
			bitmap=BitmapFactory.decodeByteArray(dataImg, 0, dataImg.length);
			Presentation.setThumb(SlideId, bitmap);
			callBack(AppContext.GOT_THUMB);
		} catch (IOException e) {
			callBack(AppContext.NETWORK_ERROR);
			Log.d(LOGTAG, "read image error");
			e.printStackTrace();
		}
    }
    
    private int readInt() throws IOException{
    	DataInputStream dataInputStream=new DataInputStream(inputStream);
    	int data=dataInputStream.readInt();
    	return data;
    }

}
