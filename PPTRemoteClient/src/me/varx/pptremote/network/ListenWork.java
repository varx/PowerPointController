package me.varx.pptremote.network;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

import me.varx.pptremote.AppContext;
import me.varx.pptremote.MSGVAL;
import me.varx.pptremote.Presentation;
import android.util.Log;

public class ListenWork implements Runnable {

	InputStream inputStream;
	DataInputStream dataInputStream;
	String dataString;
	AppContext app;
	private static String LOGTAG="ListenWork";

	int titleCount = 0;

	public ListenWork(InputStream IStream, AppContext appContext) {
		this.inputStream = IStream;
		app = appContext;
	}

	@Override
	public void run() {
		byte cmd;
		while (true) {
			try {
				cmd = readByte();
				parseCommand(cmd);
			} catch (IOException e) {
				Log.d(LOGTAG, "network error");
				e.printStackTrace();
				onNetworkDown();
				break;
			}
		}
	}
	

	private void parseCommand(byte cmd) {
		switch (cmd) {
		case MSGVAL.FILENAME:
			getFileInfo();
			break;
		case MSGVAL.CLOSE:
			closeFile();
			break;
		case MSGVAL.START:
			startPlay();
			break;
		case MSGVAL.JUMP:
			jumpTo();
			break;
		case MSGVAL.STOP:
			stopPlay();
			break;
		case MSGVAL.TITLE:
			getTitles();
			break;
		case MSGVAL.NOTE:
			getNote();
			break;
		case MSGVAL.TOTLE:
			getTotlePages();
			break;
		case MSGVAL.BYE:
			break;
		default:
			
			break;
		}
	}

	private void getNote() {
		String note;
		try {
			int length = readShort();
			Log.d(LOGTAG, "slide note length : "+length);
			note = readString(length);
			Presentation.addNote(titleCount, note);
		} catch (IOException e) {
			e.printStackTrace();
			onNetworkDown();
		}
	}

	private void sendMessage(byte cmd) {
		app.callBack(cmd);
	}

	private void sendMessage(byte cmd, int val) {
		app.callBack(cmd, val);
	}

	private void closeFile() {
		sendMessage(AppContext.CLOSE_FILE);
		Presentation.reset();
	}

	private void stopPlay() {
		sendMessage(AppContext.END);
	}

	private void jumpTo() {
		try {
			int pageNum = readShort();
			sendMessage(AppContext.JUMP_TO, pageNum);
		} catch (IOException e) {
			onNetworkDown();
		}
	}

	private void getTotlePages() {
		try {
			Presentation.totlePages = readShort();
			Log.d(LOGTAG, "总页数" + Presentation.totlePages);
		} catch (IOException e) {
			onNetworkDown();
		}
	}

	private void startPlay() {
		sendMessage(AppContext.START);
	}

	private void getFileInfo() {
		Presentation.reset();
		Presentation.isOpen = true;
		titleCount = 0;
		String fileName;
		try {
			int length = readShort();
			Log.d(LOGTAG, "fileName length : "+length);
			fileName = readString(length);
			Log.d(LOGTAG, "文件名:" + fileName);
			Presentation.fileName = fileName;
		} catch (IOException e) {
			e.printStackTrace();
			onNetworkDown();
		}
	}

	private void getTitles() {
		String title;
		try {
			int length = readShort();
			Log.d(LOGTAG, "title name length : "+length);
			title = readString(length);
			titleCount++;
			Log.d(LOGTAG, "title NO." + titleCount + ": "+title);
			Presentation.addTitle(title);
			if (titleCount == Presentation.totlePages) {
				Log.d(LOGTAG, "got all the titles,report the new file");
				Presentation.isOpen=true;
				sendMessage(AppContext.NEW_FILE);
			}
		} catch (IOException e) {
			e.printStackTrace();
			onNetworkDown();
		}
	}

	private void onNetworkDown() {
		Log.d(LOGTAG, "onNetworkDown");
		try {
			inputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		sendMessage(AppContext.NETWORK_ERROR);
	}

	private String readString(int length) throws IOException {
		byte[] buffer = new byte[length];
		Log.d(LOGTAG, "reading string");
		if (inputStream.read(buffer, 0, length)==-1){
			throw new IOException();
		}
		return new String(buffer, Charset.forName("UTF-8"));
	}

	private byte readByte() throws IOException {
		byte[] buffer = new byte[1];
		if(inputStream.read(buffer, 0, 1)==-1){
			throw new IOException();
		}
		return buffer[0];
	}

	// private int readInt() throws IOException{
	// int data=dataInputStream.readInt();
	// return data;
	// }

	private int readShort() throws IOException {
		int result = -1;
		byte[] data=new byte[2];
		if(inputStream.read(data, 0, 2)==-1){
			throw new IOException();
		}
		result=(0x00FF&data[1])|(0xFF00&(data[0]<<8));
		//result = dataInputStream.readUnsignedShort();
		Log.d(LOGTAG, "read short val "+result);
		return result;
	}

}
