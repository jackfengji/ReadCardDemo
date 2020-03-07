package com.guoTeng.usbReadCard;

import com.invs.invsIdCard;
import com.tony.ReadCardDemo.R;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.widget.ImageView;

public class DisplayIdCardPicActivity extends Activity {
	
	private int width;
	private ImageView posIv, negIv;
	
	private float getDensity(){
		DisplayMetrics displaysMetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displaysMetrics);
		// TODO Auto-generated constructor stub
		float density = displaysMetrics.density;
		width = displaysMetrics.widthPixels;
		return density;
	}
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        invsIdCard CurCard;
        byte[] CurPhoto;
        setContentView(R.layout.activity_display_idcard_pic);
        
		posIv = (ImageView) findViewById(R.id.idcard_pos_iv);
		negIv = (ImageView) findViewById(R.id.idcard_neg_iv);
		Bundle extras = getIntent().getExtras();
		CurCard = (invsIdCard) extras.get("CardInfo");
		CurPhoto =  extras.getByteArray("Photo");

		Bitmap photo = BitmapFactory.decodeByteArray(CurPhoto, 0, CurPhoto.length);
		photo = ImageControl.setAlpha(photo, 0);
		//IdPhoto photoInfo = ImageControl.initBitmap(this,CurCard,photo,getDensity());
		//posIv.setImageBitmap(ImageControl.scale(photoInfo.getPositiveImage(), width));
		//negIv.setImageBitmap(ImageControl.scale(photoInfo.getNegativeImage(), width));
		IdPhoto photoInfo = ImageControl.initAllBitmap(this,CurCard,photo,getDensity());
		posIv.setImageBitmap(ImageControl.scale(photoInfo.getAllImage(), width));
    }
    
    


	
	


}
