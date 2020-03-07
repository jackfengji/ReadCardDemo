package com.tony.ReadCardDemo;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.guoTeng.usbReadCard.UsbReadCard;
import com.invs.UsbBase;
import com.invs.UsbSam;
import com.invs.invsIdCard;
import com.invs.invsUtil;
import com.invs.invswlt;

import java.io.IOException;

public class MainActivity extends Activity {


    ReadCardThread mReadCardThread = null;
    public int g_iOkCardCount = 0;
    public int g_iStatus = 2;//0，空闲 1、单次读卡 2、循环读卡
    public int g_iErrorCardCount = 0;
    public boolean g_bFinger = false;
    public invsIdCard mCard;
    byte[] cardId = new byte[10];
    public boolean g_bTerminate = false;
    public int g_iCardType = 0;//0,身份证，1，身份证id，2，a卡id
//    private Bitmap bm;//图片资源Bitmap


    MainView mainView;
    private PendingIntent mPermissionIntent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        mPermissionIntent = PendingIntent.getBroadcast(this, 0, new Intent(UsbBase.ACTION_USB_PERMISSION), 0);
        IntentFilter filter = new IntentFilter(UsbBase.ACTION_USB_PERMISSION);
        registerReceiver(mUsbReceiver, filter);


        mainView = new MainView(this);
        setContentView(mainView);

        InitThread();
    }



    private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            String action = intent.getAction();
            if (UsbBase.ACTION_USB_PERMISSION.equals(action)) {
//                synchronized (this) {
//                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
//                        if (g_iStatus1 == 1){
//                            switchBtn(new int[] {R.id.button3}, true);
//                            g_iStatus = g_iStatus1;
//                        }else{
//                            switchBtn(new int[] {R.id.button2, R.id.button3}, true);
//                            Button b = (Button) findViewById(R.id.button2);
//                            b.setText("停止读卡");
//                            g_iStatus = g_iStatus1;
//                        }
//
//                    }else{
//                        InitView();
//                        TextView v1 = (TextView)findViewById(R.id.textView9);
//                        v1.setText("读卡失败：打开设备失败");
//                    }
//                }
            }
        }
    };

    private invsIdCard CurCard;
    private byte[] CurPhoto;

    private void displayView_wgr(invsIdCard card)
    {
//        v.setText("英文名 " + card.address + "\r\n中文名 " + card.name);

        String szTmp = card.sex;
        if (szTmp.compareToIgnoreCase("女") == 0){
            szTmp = "女/F";
        }else{
            szTmp = "男/M";
        }
        mainView.genderView.setText(szTmp);

        mainView.birthdayView.setText(card.birth1);

        mainView.nationView.setText(card.nation1 + "/" + card.nation);

        mainView.verifyTimeView.setText(card.start1 + "-" + card.end1);

        mainView.publishDepartmentView.setText("公安部/Ministry of Public Security");

        mainView.codeView.setText(card.idNo);


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
        byte[] ZpData = invswlt.Wlt2Bmp(card.wlt);
        if (ZpData == null)
            return;

        CurPhoto = ZpData;
        CurCard = card;

        Bitmap bm = BitmapFactory.decodeByteArray(ZpData, 0, 38862);
        mainView.photoView.setImageBitmap(bm);
        mainView.setPhotoBitmap(bm);
    }


    private void Display(invsIdCard card)
    {
        byte c = card.address.toLowerCase().getBytes()[0];
        if ((c >= "a".getBytes()[0] && c <= "z".getBytes()[0])){
            displayView_wgr(card);
            return;
        }

        mainView.nameView.setText(card.name);

        mainView.genderView.setText(card.sex1);


        String szTmp = card.nation1;
        if (szTmp.length() == 0){
//            v.setText("通行证号码 " + card.txzNo + "   签发次数 " + card.txzCnt);
        }else{
            mainView.nationView.setText(szTmp);
        }

        mainView.birthdayView.setText(card.birth1);

        mainView.addressView.setText(card.address);

        mainView.codeView.setText(card.idNo);

        mainView.publishDepartmentView.setText(card.police);

        mainView.verifyTimeView.setText(card.start1 + "-" + card.end1);



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
//        bm = BitmapFactory.decodeByteArray(ZpData, 0, 38862);
//        ImageView mImageView = (ImageView)findViewById(R.id.imageView1);
//        mImageView.setImageBitmap(bm);

        Bitmap bm = BitmapFactory.decodeByteArray(ZpData, 0, 38862);
        mainView.photoView.setImageBitmap(bm);
        mainView.setPhotoBitmap(bm);
    }


    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Bundle bundle = msg.getData();
            int iCode = bundle.getInt("code");
            if (iCode == 0){
                Display(mCard);
            }else if (iCode == 1){
                String s = "卡id:" + invsUtil.bytesToHexString(cardId, 0, 8) + "\r\n读卡成功\r\n读卡成功次数:"+ g_iOkCardCount + "\r\n读卡失败次数:" + g_iErrorCardCount;
            }else if (iCode == 2){
                String s = "卡id:" + invsUtil.bytesToHexString(cardId, 0, 9) + "\r\n读卡成功\r\n读卡成功次数:"+ g_iOkCardCount + "\r\n读卡失败次数:" + g_iErrorCardCount;
            }else{
                String s = "读卡失败：" + invsUtil.GetErrMsg(iCode);
                s = s+"\r\n读卡成功次数:"+ g_iOkCardCount + "\r\n读卡失败次数:" + g_iErrorCardCount;
            }
            int iCmd = bundle.getInt("cmd");
            if (iCmd == 0){//one read
            }
            super.handleMessage(msg);
        }
    };



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

    class ReadCardThread extends Thread{
        Context mContext = null;
        private Handler mHandler = null;

        public ReadCardThread(Context act, Handler handler) throws SecurityException, NoSuchMethodException, IOException {
            mContext = act;
            this.mHandler = handler;
        }

        long GetTickCount()
        {
            return SystemClock.uptimeMillis();
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
