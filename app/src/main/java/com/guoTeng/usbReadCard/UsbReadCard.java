package com.guoTeng.usbReadCard;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.invs.UsbBase;
import com.invs.UsbSam;
import com.invs.invsIdCard;
import com.invs.invsUtil;
import com.invs.invswlt;
import com.tony.ReadCardDemo.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.usb.UsbAccessory;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.view.View.OnClickListener;

//回复 Android_Tutor：SkyGray说的对，进程并没有真正退出。
//可以在onCreate判断savedInstanceState是否等于NULL就可以知道是不是re-initialized了，
//或者在onBackPressed调用System.exit(0)真正退出进程。一点拙见，请轻砸。
public class UsbReadCard extends Activity implements OnClickListener{
	/**
	 * Called when the activity is first created.
	 */
	public boolean g_bFinger = false;
	public int g_iStatus = 0;//0，空闲 1、单次读卡 2、循环读卡
	public int g_iOkCardCount = 0;
	public int g_iErrorCardCount = 0;
	ReadCardThread mReadCardThread = null;
	public boolean g_bTerminate = false;

	public int g_iCardType = 0;//0,身份证，1，身份证id，2，a卡id

	public String mPhone = "18628199200";
	public invsIdCard mCard;
	private Bitmap bm;//图片资源Bitmap

	byte[] cardId = new byte[10];

	String g_szDevId = "";

	static int id[] = {R.id.button1, R.id.button2, R.id.button3, R.id.button4};
	private void switchBtn(int btn[], boolean open){
		if (open){
			for (int i=0; i< id.length; i++){
				Button b = (Button) findViewById(id[i]);
				b.setEnabled(false)	;
			}
			for (int i=0; i< btn.length; i++){
				Button b = (Button) findViewById(btn[i]);
				b.setEnabled(true)	;
			}
		}else{
			for (int i=0; i< id.length; i++){
				Button b = (Button) findViewById(id[i]);
				b.setEnabled(true)	;
			}
			for (int i=0; i< btn.length; i++){
				Button b = (Button) findViewById(btn[i]);
				b.setEnabled(false)	;
			}
		}
	}
	void set()
	{
		LayoutInflater inflater = getLayoutInflater();
		View view = inflater.inflate(R.layout.dialog, null);
		SharedPreferences sp = getSharedPreferences("set", Activity.MODE_PRIVATE);
		final SharedPreferences.Editor edit = sp.edit();

		int iCardType = sp.getInt("CardType", 0);
		final RadioButton mRadio1 = (RadioButton) view.findViewById(R.id.CheckButton1);
		mRadio1.setChecked(sp.getBoolean("AssignPath", false));

		final RadioButton mRadio2 = (RadioButton) view.findViewById(R.id.CheckButton2);
		mRadio2.setChecked(sp.getBoolean("CardId", false));

		final RadioButton mRadio3 = (RadioButton) view.findViewById(R.id.CheckButton3);
		mRadio3.setChecked(sp.getBoolean("ACardId", false));

		switch(iCardType){
			case 0:
				mRadio1.setChecked(true);
				break;
			case 1:
				mRadio2.setChecked(true);
				break;
			case 2:
				mRadio3.setChecked(true);
				break;
			default:
				mRadio1.setChecked(true);
				break;
		}
		final AlertDialog.Builder ad =new AlertDialog.Builder(this);
		ad.setView(view);

		ad.setTitle("读卡设置");
		ad.setNegativeButton("返回", null);
		ad.setPositiveButton("设置", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (mRadio1.isChecked()){
					g_iCardType = 0;
				}else if (mRadio2.isChecked()){
					g_iCardType = 1;
				}else if (mRadio3.isChecked()){
					g_iCardType = 2;
				}
				edit.putInt("CardType", g_iCardType);

				edit.commit();
				dialog.dismiss();
			}
		});
		ad.show();
	}

	public void onClick(View v) {
		int iRet =0;
		g_iErrorCardCount = 0;
		g_iOkCardCount = 0;
		if(R.id.button1 == v.getId()){//单次读卡
			iRet = UsbBase.CheckDev(this);
			if (iRet == -1){
				InitView();
				TextView v1 = (TextView)findViewById(R.id.textView9);
				v1.setText("读卡失败：打开设备失败");
				return ;
			}else if (iRet == 0){
				g_iStatus1 = 1;
				return;
			}

			switchBtn(new int[] {R.id.button3}, true);
			g_iStatus = 1;
		}else if(R.id.button2 == v.getId()){//循环读卡
			Button b = (Button) findViewById(R.id.button2);
			if (b.getText().toString() == "停止读卡"){
				switchBtn(new int[] {}, false);
				b.setText("循环读卡");
				g_iStatus = 0;
			}else{
				iRet = UsbBase.CheckDev(this);
				if (iRet == -1){
					InitView();
					TextView v1 = (TextView)findViewById(R.id.textView9);
					v1.setText("读卡失败：打开设备失败");
					return ;
				}else if (iRet == 0){
					g_iStatus1 = 2;
					return;
				}

				switchBtn(new int[] {R.id.button2, R.id.button3}, true);
				b.setText("停止读卡");
				g_iStatus = 2;
			}
		}else if(R.id.button3 == v.getId()){//退出
			g_iStatus = 0;
			g_bTerminate = true;
			finish();
		}else if(R.id.button4 == v.getId()){
			set();
		}
	}
	public void InitView()
	{
		TextView v = (TextView)findViewById(R.id.textView1);
		//setTitle("国腾二代证蓝牙读卡程序");
		//setTitle("易铖二代证读卡程序");
		v.setText("姓名  ");
		v = (TextView)findViewById(R.id.textView2);
		v.setText("性别  ");
		v = (TextView)findViewById(R.id.textView3);
		v.setText("名族  ");
		v = (TextView)findViewById(R.id.textView4);
		v.setText("出生  ");
		v = (TextView)findViewById(R.id.textView5);
		v.setText("住址  ");
		v = (TextView)findViewById(R.id.textView6);
		v.setText("公民身份证号  ");
		v = (TextView)findViewById(R.id.textView7);
		v.setText("签发机关  ");
		v = (TextView)findViewById(R.id.textView8);
		v.setText("有效期限  ");
		v = (TextView)findViewById(R.id.textView9);
		v.setText(new String(" "));
		v = (TextView)findViewById(R.id.textView10);
		v.setText(new String(" "));
		v = (TextView)findViewById(R.id.textView11);
		v.setText(new String(" "));

		v = (TextView)findViewById(R.id.textView12);
		v.setText(new String(" "));

		ImageView mImageView = (ImageView)findViewById(R.id.imageView1);
		mImageView.setImageDrawable(getResources().getDrawable(R.drawable.tmp));
	}
	public void InitThread()
	{
		try {
			mReadCardThread = new ReadCardThread(this, mHandler);
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		mReadCardThread.start();
	}


	long GetTickCount()
	{
		return SystemClock.uptimeMillis();
	}

	public int g_iStatus1 = 0;//0，空闲 1、单次读卡 2、循环读卡

	private PendingIntent mPermissionIntent;
	private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver()
	{
		@Override
		public void onReceive(Context context, Intent intent)
		{
			String action = intent.getAction();
			if (UsbBase.ACTION_USB_PERMISSION.equals(action)) {
				synchronized (this) {
					if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
						if (g_iStatus1 == 1){
							switchBtn(new int[] {R.id.button3}, true);
							g_iStatus = g_iStatus1;
						}else{
							switchBtn(new int[] {R.id.button2, R.id.button3}, true);
							Button b = (Button) findViewById(R.id.button2);
							b.setText("停止读卡");
							g_iStatus = g_iStatus1;
						}

					}else{
						InitView();
						TextView v1 = (TextView)findViewById(R.id.textView9);
						v1.setText("读卡失败：打开设备失败");
					}
				}
			}
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		mPermissionIntent = PendingIntent.getBroadcast(this, 0, new Intent(UsbBase.ACTION_USB_PERMISSION), 0);
		IntentFilter filter = new IntentFilter(UsbBase.ACTION_USB_PERMISSION);
		registerReceiver(mUsbReceiver, filter);

		System.out.println("abc");
		long iTick = GetTickCount();
		SystemClock.sleep(100);
		iTick = GetTickCount()-iTick;

		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		for (int i=0; i< id.length; i++){
			Button b = (Button) findViewById(id[i]);
			b.setOnClickListener(this);
		}

		//getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		SharedPreferences sp = getSharedPreferences("set", Activity.MODE_PRIVATE);
		SharedPreferences.Editor edit = sp.edit();
		g_bFinger = sp.getBoolean("Finger", g_bFinger);

		g_iCardType = sp.getInt("CardType", 0);

		InitView();
		InitThread();
	};

	private invsIdCard CurCard;
	private byte[] CurPhoto;

	private void displayView_wgr(invsIdCard card)
	{
		TextView v = (TextView)findViewById(R.id.textView1);
		v.setText("英文名 " + card.address + "\r\n中文名 " + card.name);

		v = (TextView)findViewById(R.id.textView2);
		String szTmp = card.sex;
		if (szTmp.compareToIgnoreCase("女") == 0){
			szTmp = "女/F";
		}else{
			szTmp = "男/M";
		}
		v.setText("性别  " + szTmp);

		v = (TextView)findViewById(R.id.textView3);
		v.setText("出生  " + card.birth1);

		v = (TextView)findViewById(R.id.textView4);
		v.setText("国籍 " + card.nation1 + "/" + card.nation);

		v = (TextView)findViewById(R.id.textView5);
		v.setText("有效期限  " + card.start1 + "-" + card.end1);

		v = (TextView)findViewById(R.id.textView6);
		v.setText("签发机关  " + "公安部/Ministry of Public Security");

		v = (TextView)findViewById(R.id.textView7);
		v.setText("身份证号 " + card.idNo);

		v = (TextView)findViewById(R.id.textView8);
		v.setText("");

		v = (TextView)findViewById(R.id.textView9);
		String s = "读卡成功";

		if (card.id1 != ""){
			s = s + " 卡id：" + card.id1;
			if (card.bFinger){
				s = s + " 指纹:" + invsUtil.bytesToHexString(card.finger, 0, 8);
			}
		}else if (card.bFinger){
			s = s + " 指纹:" + invsUtil.bytesToHexString(card.finger, 0, 8);
		}

		s = s+"\r\n读卡成功次数:"+ g_iOkCardCount + "\r\n读卡失败次数:" + g_iErrorCardCount;
		v.setText(s);
		byte[] ZpData = invswlt.Wlt2Bmp(card.wlt);
		if (ZpData == null)
			return;

		CurPhoto = ZpData;
		CurCard = card;

		bm = BitmapFactory.decodeByteArray(ZpData, 0, 38862);
		ImageView mImageView = (ImageView)findViewById(R.id.imageView1);
		mImageView.setImageBitmap(bm);
	}

	private void Display(invsIdCard card)
	{
		byte c = card.address.toLowerCase().getBytes()[0];
		if ((c >= "a".getBytes()[0] && c <= "z".getBytes()[0])){
			displayView_wgr(card);
			return;
		}

		TextView v;
		v = (TextView)findViewById(R.id.textView1);
		v.setText("姓名  " + card.name);
		v = (TextView)findViewById(R.id.textView2);
		v.setText("性别  " + card.sex1);
		v = (TextView)findViewById(R.id.textView3);
		//v.setText("名族  " + card.nation1);
		String szTmp = card.nation1;
		if (szTmp.length() == 0){
			v.setText("通行证号码 " + card.txzNo + "   签发次数 " + card.txzCnt);
		}else{
			v.setText("名族  " + szTmp);
		}

		v = (TextView)findViewById(R.id.textView4);
		v.setText("出生  " + card.birth1);
		v = (TextView)findViewById(R.id.textView5);
		v.setText("住址  " + card.address);
		v = (TextView)findViewById(R.id.textView6);
		v.setText("公民身份证号  " + card.idNo);
		v = (TextView)findViewById(R.id.textView7);
		v.setText("签发机关  " + card.police);
		v = (TextView)findViewById(R.id.textView8);
		v.setText("有效期限  " + card.start1 + "-" + card.end1);

		v = (TextView)findViewById(R.id.textView9);

		String s = "读卡成功";

		if (card.id1 != ""){
			s = s + " 卡id：" + card.id1;
			if (card.bFinger){
				s = s + " 指纹:" + invsUtil.bytesToHexString(card.finger, 0, 8);
			}
		}else if (card.bFinger){
			s = s + " 指纹:" + invsUtil.bytesToHexString(card.finger, 0, 8);
		}

		s = s+"\r\n读卡成功次数:"+ g_iOkCardCount + "\r\n读卡失败次数:" + g_iErrorCardCount;
		v.setText(s);
		byte[] ZpData = invswlt.Wlt2Bmp(card.wlt);
		if (ZpData == null)
			return;

		CurPhoto = ZpData;
		CurCard = card;

		/*
		long tick =  GetTickCount();
		Bitmap photo = BitmapFactory.decodeByteArray(CurPhoto, 0, CurPhoto.length);
		photo = ImageControl.setAlpha(photo, 0);
		DisplayMetrics displaysMetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displaysMetrics);
		// TODO Auto-generated constructor stub
		IdPhoto photoInfo = ImageControl.initAllBitmap(this,CurCard,photo,displaysMetrics.density);
		//BmpTool.toBmp(photoInfo.getAllImage());
		tick =  GetTickCount() - tick;
		 */
		bm = BitmapFactory.decodeByteArray(ZpData, 0, 38862);
		ImageView mImageView = (ImageView)findViewById(R.id.imageView1);
		mImageView.setImageBitmap(bm);
	}

	private final Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			Bundle bundle = msg.getData();
			int iCode = bundle.getInt("code");
			TextView v = (TextView)findViewById(R.id.textView9);
			if (iCode == 0){
				Display(mCard);
			}else if (iCode == 1){
				InitView();

				String s = "卡id:" + invsUtil.bytesToHexString(cardId, 0, 8) + "\r\n读卡成功\r\n读卡成功次数:"+ g_iOkCardCount + "\r\n读卡失败次数:" + g_iErrorCardCount;
				v.setText(s);
			}else if (iCode == 2){
				InitView();
				String s = "卡id:" + invsUtil.bytesToHexString(cardId, 0, 9) + "\r\n读卡成功\r\n读卡成功次数:"+ g_iOkCardCount + "\r\n读卡失败次数:" + g_iErrorCardCount;
				v.setText(s);
			}else{
				InitView();
				String s = "读卡失败：" + invsUtil.GetErrMsg(iCode);
				s = s+"\r\n读卡成功次数:"+ g_iOkCardCount + "\r\n读卡失败次数:" + g_iErrorCardCount;
				v.setText(s);
			}

			int iCmd = bundle.getInt("cmd");
			if (iCmd == 0){//one read
				switchBtn(new int[] {}, false);
			}
			super.handleMessage(msg);
		}
	};

	public static final Object lock = new Object();
	private String getAddr()
	{
		SharedPreferences sp = getSharedPreferences("BindDevice", Activity.MODE_PRIVATE);
		return sp.getString("Address", "");
	}

	Intent mIntent = null;
	public class ReadCardThread extends Thread{
		Context mContext = null;
		private Handler mHandler = null;

		public ReadCardThread(UsbReadCard act, Handler handler) throws SecurityException, NoSuchMethodException, IOException{
			mContext = act;
			this.mHandler = handler;
		}

		public ReadCardThread(UsbReadCard act){

		}
		void SendMsg(int iRet)
		{
			Bundle bundle = new Bundle();
			bundle.putInt("code", iRet);
			if (g_iStatus == 1){
				g_iStatus = 0;
				bundle.putInt("cmd", 0);
			}else{
				bundle.putInt("cmd", 1);
			}

			Message msg = new Message();
			msg.setData(bundle);
			mHandler.sendMessage(msg);
			if (iRet != 0 && g_iStatus == 2){
				SystemClock.sleep(1000);
			}
		}

		void ReadCard()
		{
			UsbSam mTermb = new UsbSam();
			int iRet = 0;
			iRet = mTermb.InitComm(mContext);
			if (iRet != 1){
				g_iErrorCardCount++;
				SendMsg(invsUtil.ERR_OPENECOMM);
				mTermb.CloseComm();
				return;
			}

			iRet = mTermb.FindCardCmd();//128, 159
			if (iRet != 159 && !(g_iStatus==2 && iRet == 128)){
				g_iErrorCardCount++;
				SendMsg(invsUtil.CMD_FINDCARD);
				mTermb.CloseComm();
				return;
			}

			iRet = mTermb.SelCardCmd();
			if (iRet != 144 && !(g_iStatus==2 && iRet == 129)){
				g_iErrorCardCount++;
				SendMsg(invsUtil.CMD_SELCARD);
				mTermb.CloseComm();
				return;
			}

			iRet = mTermb.ReadCardCmd(g_bFinger, 300);
			if (iRet != 144){
				g_iErrorCardCount++;
				SendMsg(invsUtil.CMD_READCARDID);
				mTermb.CloseComm();
				return;
			}

			g_iOkCardCount++;
			mCard = mTermb.mCard;
			SendMsg(0);
			mTermb.CloseComm();
			return;
		}

		void ReadCard1()
		{
			long iTick = GetTickCount();
			UsbSam mTermb = new UsbSam();
			int iRet = mTermb.ReadCard(mContext, g_bFinger, 650);
			if (iRet == 0){
				g_iOkCardCount++;
			}else{
				g_iErrorCardCount++;
			}
			mCard = mTermb.mCard;
			SendMsg(iRet);
			iTick = GetTickCount()-iTick;
			return ;
		}

		void ReadCardA()
		{
			long iTick = GetTickCount();
			UsbSam mTermb = new UsbSam();
			int iRet = mTermb.InitComm(mContext);
			if (iRet != 1){
				g_iErrorCardCount++;
				SendMsg(invsUtil.ERR_OPENECOMM);
				mTermb.CloseComm();
				return;
			}

			iRet = mTermb.ReadACardId(cardId);
			if (iRet == 0){
				g_iOkCardCount++;
				SendMsg(2);
			}else{
				g_iErrorCardCount++;
				SendMsg(invsUtil.CMD_READCARDID);
			}

			iTick = GetTickCount()-iTick;
			mTermb.CloseComm();
			return ;
		}

		void ReadCardB()
		{
			long iTick = GetTickCount();
			UsbSam mTermb = new UsbSam();
			int iRet = mTermb.InitComm(mContext);
			if (iRet != 1){
				g_iErrorCardCount++;
				SendMsg(invsUtil.ERR_OPENECOMM);
				mTermb.CloseComm();
				return;
			}

			iRet = mTermb.ReadCardId(cardId);
			if (iRet == 0){
				g_iOkCardCount++;
				SendMsg(1);
			}else{
				g_iErrorCardCount++;
				SendMsg(invsUtil.CMD_READCARDID);
			}

			iTick = GetTickCount()-iTick;
			mTermb.CloseComm();
			return ;
		}

		public void run(){
			while (!g_bTerminate){
				SystemClock.sleep(50);
				while ((!g_bTerminate) && ((g_iStatus == 2) || (g_iStatus == 1) || (g_iStatus == 3))) {
					SystemClock.sleep(50);
					switch (g_iCardType){
						case 0:
							ReadCard1();
							break;
						case 1:
							ReadCardB();
							break;
						case 2:
							ReadCardA();
							break;
						default:
							break;
					}
				}
			}
		}
	}
}

