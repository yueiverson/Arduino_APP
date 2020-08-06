package com.dfrobot.angelo.blunobasicdemo;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class MainActivity extends BlunoLibrary {

	private Button buttonScan;
	private Button buttonLog;
	private Button buttonRe;
	private TextView text_p1;
	private TextView text_p2;
	private TextView time;
	public static double p1_value;
	public static String p2_value,p3_value;
	public static SQLiteOpenHelper DatabaseHelper = null;
	public static SQLiteDatabase db = null;
	public static ContentValues values=null;
	public static SimpleDateFormat sdFormat = null;
	public static Date date = null;
	public static String strDate = null,second,minute;
	public static long startTime,endTime,completeTime;



	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
        onCreateProcess();
		setupUI();

		DatabaseHelper = new DatabaseHelper(this);
		db = DatabaseHelper.getWritableDatabase();
		values=new ContentValues();
		sdFormat = new SimpleDateFormat("MM/dd hh:mm:ss");
	}

	//1. 初始化UI介面===============================================================================
	public void setupUI(){
		serialBegin(115200);										//包率
		buttonLog = (Button) findViewById(R.id.buttonLog);
		buttonLog.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				buttonLog();
			}
		});
		buttonScan = (Button) findViewById(R.id.buttonScan);
		buttonScan.setOnClickListener(new OnClickListener() {
			//Alert Dialog for selecting the BLE device
			public void onClick(View v) {
				buttonScanOnClickProcess();
			}
		});
		buttonRe = (Button) findViewById(R.id.buttonRe);
		text_p1 = (TextView)findViewById(R.id.text_view_p1_value);
		text_p2 = (TextView)findViewById(R.id.text_view_p2_value);
		time=(TextView)findViewById(R.id.date);
	}
	//顯示時間
	public void timeshow(){
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd\t\t\tHH:mm");
		Date date = new Date(System.currentTimeMillis());
		time.setText(simpleDateFormat.format(date));

	}
	//連線狀態
	public void onConectionStateChange(connectionStateEnum theConnectionState) {
		switch (theConnectionState) {
		case isConnected:
			startTime = System.currentTimeMillis();
			buttonScan.setText("中 斷 連 線");
			break;
		case isConnecting:
			buttonScan.setText("配 對 中");
			break;
		case isToScan:
			buttonScan.setText("連 接 裝 置");
			break;
		case isScanning:
			buttonScan.setText("掃 瞄 中");
			break;
		case isDisconnecting:
			buttonScan.setText("離 線");
			break;
		default:
			break;
		}

	}


	//過濾封包內容
	public void onSerialReceived(String theString) {
		ArrayList<String> list=new ArrayList ();
		list.add(theString);
		for (String token:list){
			text_p1.setText(token);
			p1_value = Double.parseDouble(token);
			endTime = System.currentTimeMillis();
			completeTime = endTime - startTime;
			second = Long.toString(((completeTime)/1000)%60);
			minute =Long.toString((((completeTime)/1000)/60)%60);;
			text_p2.setText(minute+"分"+second + "秒");
			p2_value = minute;
			p3_value = second;
		}
		try{
			date = new Date();
			strDate = sdFormat.format(date);
		    timeshow();
		    values.put("P0", strDate);
			values.put("P1", p1_value);
            values.put("P2", p2_value);
			values.put("P3", p3_value);
			db.insert("table_collision",null,values);
		}
		catch (SQLiteException e){
			Toast toast = Toast.makeText(this, "Database unavailable", Toast.LENGTH_SHORT);
			toast.show();
		}
		buttonRe.setOnClickListener(new View.OnClickListener() {
										@Override
										public void onClick(View v) {
											endTime = System.currentTimeMillis();
											completeTime = endTime - startTime;
											second = Long.toString(((completeTime)/1000)%60);
											minute =Long.toString((((completeTime)/1000)/60)%60);;
											text_p2.setText(minute+"分"+second + "秒");
											p2_value = minute;
											p3_value = second;
											date = new Date();
											strDate = sdFormat.format(date);
											timeshow();
											values.put("P0", strDate);
											values.put("P1", p1_value);
											values.put("P2", minute);
											values.put("P3", second);
											db.insert("table_collision",null,values);
										}
									}

		);
	}

	public void buttonLog(){
		Intent intent = new Intent(this, LogActivity.class);
		startActivity(intent);
	}

	protected void onResume(){
		super.onResume();
		System.out.println("BlUNOActivity onResume");
		onResumeProcess();														//onResume Process by BlunoLibrary
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		onActivityResultProcess(requestCode, resultCode, data);					//onActivityResult Process by BlunoLibrary
		super.onActivityResult(requestCode, resultCode, data);
	}
	@Override
	protected void onPause() {
		super.onPause();
		onPauseProcess();														//onPause Process by BlunoLibrary
	}
	protected void onStop() {
		super.onStop();
		onStopProcess();														//onStop Process by BlunoLibrary
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
		onDestroyProcess();														//onDestroy Process by BlunoLibrary
	}

}
