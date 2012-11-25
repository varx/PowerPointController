package me.varx.pptremote.ui;

import me.varx.pptremote.AppContext;
import me.varx.pptremote.MSGVAL;
import me.varx.pptremote.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity implements OnClickListener {
	private Button btnConnect;

	private TextView textViewTitle;

	private boolean isConnected=false;
	AppContext appContext;

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case MSGVAL.CONNECTF_FAIL:
				connectFail();
				break;
			case MSGVAL.CONNECT_OK:
				Log.d("ppt", "handle���ӳɹ�");
				connectSuccess();
				break;
			default:
				break;
			}
		};
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		btnConnect = (Button) findViewById(R.id.btnConnect);

		textViewTitle = (TextView) findViewById(R.id.TextViewMsg);

		btnConnect.setOnClickListener(this);
		appContext=AppContext.getApp();
	}
	@Override
	protected void onResume() {
		appContext.sethandler(handler);
		super.onResume();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}
	
	private void connecting(){
		if(isConnected){
			diconnect();
		}else{
			appContext.connect();
			btnConnect.setEnabled(false);
			textViewTitle.setText("��������");
		}
	}
	
	private void diconnect() {
		
		isConnected=false;
		textViewTitle.setText("�������");
		btnConnect.setText("����");
	}

	private void connectFail(){
		btnConnect.setEnabled(true);
		textViewTitle.setText("����ʧ��");
	}
	
	private void connectSuccess(){
		textViewTitle.setText("������");
		btnConnect.setEnabled(true);
		btnConnect.setText("�Ͽ�");
		isConnected=true;
		Intent intent=new Intent(this,ControllerActivity.class);
		startActivity(intent);
		this.finish();
	}

	public void onClick(View v) {
		connecting();
	}
}
