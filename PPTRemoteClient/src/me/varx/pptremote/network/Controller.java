package me.varx.pptremote.network;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import me.varx.pptremote.AppContext;
import me.varx.pptremote.MSGVAL;
import android.os.Handler;


public class Controller {
	
	Thread workThread;
	ListenWork clientWork;
	Handler handler;
	Socket socket;
	AppContext appContext;
	OutputStream outputStream;
	InputStream inputStream;
	
	public Controller(Socket socket,AppContext appContext){
		this.socket=socket;
		this.appContext=appContext;
		try {
			outputStream=socket.getOutputStream();
			inputStream=socket.getInputStream();
			outputStream.write(MSGVAL.SOCKET_IDCMD);
		} catch (IOException e) {
			onNetworkDown();
			e.printStackTrace();
		}
		startListenThread();
	}
	
	public void startPlay(){
		try {
			sendCommand(MSGVAL.START);
		} catch (IOException e) {
			onNetworkDown();
			e.printStackTrace();
		}
	}
	
	public void nextPage(){
		try {
			sendCommand(MSGVAL.NEXT);
		} catch (IOException e) {
			onNetworkDown();
			e.printStackTrace();
		}
	}

	public void jumpTo(int index){
		try {
			sendCommand(MSGVAL.JUMP);
			sendShort(index);
		} catch (IOException e) {
			onNetworkDown();
			e.printStackTrace();
		}
	}
	public void prePage(){
		try {
			sendCommand(MSGVAL.PRE);
		} catch (IOException e) {
			onNetworkDown();
			e.printStackTrace();
		}
	}
	public void endPlay(){
		try {
			sendCommand(MSGVAL.STOP);
		} catch (IOException e) {
			onNetworkDown();
			e.printStackTrace();
		}
	}
	private void startListenThread(){
		clientWork=new ListenWork(inputStream,appContext);
		workThread=new Thread(clientWork);
		workThread.start();
	}
	
	private void sendCommand(byte msg) throws IOException{
		byte[] data=new byte[1];
		data[0]=msg;
		outputStream.write(data, 0, 1);
	}

	private  void onNetworkDown(){
		appContext.callBack(AppContext.NETWORK_ERROR);
		System.out.println("服务器断开连接");
	}

	private void sendShort(int val) throws IOException{
		if(val>65535||val<0)
			return;
		byte[] data=new byte[2];
		data[0]=(byte)val;
		data[1]=(byte)(val>>8);
		outputStream.write(data, 0, 2);
	}

	public void blackScreen() {
		try {
			sendCommand(MSGVAL.BLACK_SCREEN);
		} catch (IOException e) {
			onNetworkDown();
			e.printStackTrace();
		}
		
	}

	public void whiteScreen() {
		try {
			sendCommand(MSGVAL.WHITE_SCREEN);
		} catch (IOException e) {
			onNetworkDown();
			e.printStackTrace();
		}
	}
}
