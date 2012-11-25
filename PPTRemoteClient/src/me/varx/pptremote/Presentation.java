package me.varx.pptremote;
import java.util.ArrayList;

import android.graphics.Bitmap;
import android.util.Log;
import android.widget.ImageView;



public class Presentation {
	public static boolean isOpen=false;
	
	public static String fileName;
	public static int totlePages=1;
	public static int currentPage=1;

	public static String[] titles;
	public static String[] notes;
	public static ArrayList<Bitmap> thumbs=new ArrayList<Bitmap>();
	public static ArrayList<ImageView> slides=new ArrayList<ImageView>();
	
	public static void setThumb(int index,Bitmap thumb){
		Log.d("setThumb", "index="+index);
		Log.d("setThumb", "size="+thumbs.size());
		while(index>=thumbs.size()){
			Log.d("setThumb", "add:"+thumbs.size());
			thumbs.add(null);
		}
		thumbs.set(index, thumb);
		//slides.get(index).setImageBitmap(thumb);
	}
	
	public static boolean isSetThumb(int index){
		if(index>=thumbs.size())
			return false;
		if(thumbs.get(index)==null)
			return false;
		return true;
	}
	
	private static int titleIndex=0;
	public static void addTitle(String title){
		if(titles==null){
			titles=new String[totlePages];
			titleIndex=0;
		}
		titles[titleIndex]=title;
		titleIndex++;
	}
	public static void addNote(int index,String note){
		if(notes==null){
			notes=new String[totlePages];
		}
		notes[index]=note;
	}
	
	public static void init(){
		titles=new String[totlePages];
		notes=new String[totlePages];
		titleIndex=0;
	}
	
	public static void reset(){
		titles=null;
		notes=null;
//		slides=new ArrayList<ImageView>();
//		thumbs=new ArrayList<Bitmap>();
		totlePages=0;
		currentPage=1;
	}
}
