package me.varx.pptremote.network;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import me.varx.pptremote.AppContext;
import android.util.Log;

public class TcpConnect{
	
	
	private AppContext app;
	
	private String ipAddress;
	private int port;
	
	private static String LOGTAG="TcpConnect";
	
	
	public TcpConnect(AppContext app,String ip,int port) {
		this.app=app;
		this.ipAddress=ip;
		this.port=port;
	}

	
	public void connect(){
		new Thread(new Runnable() {
			@Override
			public void run() {
				Socket socket;
				
				socket=getNewSocket();
				if(socket==null){
					report(AppContext.NETWORK_ERROR);
					return;
				}
				Controller controller=new Controller(socket,app);
				Log.d(LOGTAG, "new controller created");
				app.callBack(AppContext.CONTROLLER_CREATED, controller);
				
				socket=getNewSocket();
				if(socket==null){
					report(AppContext.NETWORK_ERROR);
					return;
				}
				ThumbDownload download=new ThumbDownload(socket,app);
				Log.d(LOGTAG, "new ThumbDownload created");
				app.callBack(AppContext.THUMB_DOWNLOAD_CREATED, download);
				
				report(AppContext.NETWORK_OK);
			}
		}).start();
	}
	
//	private void getThumbDownload(){
//		new Thread(new Runnable() {
//			public void run() {
//				ThumbDownload download=new ThumbDownload(getNewSocket(),app);
//				Log.d(LOGTAG, "new ThumbDownload created");
//				app.callBack(AppContext.THUMB_DOWNLOAD_CREATED, download);
//			}
//		}).start();
//	}
	
	private Socket getNewSocket(){
		Socket newSocket = null;
		try {
			newSocket=new Socket(ipAddress, port);
			Log.d(LOGTAG, "new Socket Connected");
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return newSocket;
	}
	
	
	public void report(byte msg){
		app.callBack(msg);
	}
	
	public void disconnect(){
		
	}
}
