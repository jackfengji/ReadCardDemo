package com.guoTeng.usbReadCard;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import com.invs.invsIdCard;
import com.tony.ReadCardDemo.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.Bitmap.Config;
import android.os.Environment;
import android.util.DisplayMetrics;

public class ImageControl {
	
	public static Bitmap setAlpha(Bitmap sourceImg, int number) { 
		
		int[] argb = new int[sourceImg.getWidth() * sourceImg.getHeight()]; 
		
		sourceImg.getPixels(argb, 0, sourceImg.getWidth(), 0, 0,sourceImg.getWidth(), sourceImg.getHeight());// 获得图片的ARGB�?
		return setAlpha(argb,number,sourceImg.getWidth(),sourceImg.getHeight());
	}
	
	public static Bitmap setAlpha(int[] sourceImg, int number,int width,int height) {	

		int bgcolor = sourceImg[0];
		number = number * 255 / 100; 
		for (int i = 0; i < sourceImg.length; i++) { 
			if (bgcolor==sourceImg[i])
				sourceImg[i] = (number << 24) | (sourceImg[i] & 0x00FFFFFF);// [/i][i]修改�?��2[/i][i]位的�?
		} 
		Bitmap bp = Bitmap.createBitmap(sourceImg, width, height, Config.ARGB_8888);
		return bp; 
	}
	
	public static void saveBitmap(Bitmap bmp, String name)
	{
		/*
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
			name = Environment.getExternalStorageDirectory().getAbsolutePath() + name;
		}else{
			
		}*/
		
		File f = new File(name);
		FileOutputStream fOut = null;
		try{
			fOut = new FileOutputStream(f);
		}catch(FileNotFoundException e){
			return;
		}
		
		bmp.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
		try{
			fOut.close();
		}catch(IOException e){
			
		}
	}
	
	public static IdPhoto initBitmap(Context context,invsIdCard info,Bitmap photo,float density) {
		IdPhoto photoInfo = new IdPhoto();

		int index = 0;
		int startHeight = (int) (42 * density);
		int intervalHeight = (int) (24 * density);
		int startWidth = (int) (64 * density);
		int textSize = (int) (12 * density);
		Paint p = new Paint();
		p.setColor(Color.BLACK);// 设置红色
		
		Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(),
				R.drawable.cardfront).copy(Config.ARGB_8888, true);
		Canvas canvas = new Canvas(bitmap);
		p.setTextSize(textSize);
		p.setColor(Color.BLACK);// 设置红色
		// 底图
		canvas.drawBitmap(bitmap, 0, 0, p);
		// 姓名
		canvas.drawText(info.name, startWidth, startHeight, p);
		// 性别
		canvas.drawText(info.sex1 , startWidth, startHeight
				+ intervalHeight, p);
		// 民族
		canvas.drawText(info.nation1, startWidth * 2, startHeight
				+ intervalHeight, p);
		// 出生日期
		String birthDay = info.birth;
		String year = birthDay.substring(0, 4);
		String month = birthDay.substring(4, 6);
		String day = birthDay.substring(6, 8);
		if (month.startsWith("0")) {
			month = month.substring(1);
		}
		if (day.startsWith("0")) {
			day = day.substring(1);
		}
		// �?
		canvas.drawText(year, startWidth, startHeight + intervalHeight * 2, p);
		// �?
		canvas.drawText(month, startWidth + (int) (50 * density), startHeight
				+ intervalHeight * 2, p);
		// �?
		canvas.drawText(day, startWidth + (int) (80 * density), startHeight
				+ intervalHeight * 2, p);
		// 住址
		String address = info.address;
		while (address.length() > 11) {

			String subAddress = address.substring(0, 11);
			canvas.drawText(subAddress, startWidth, startHeight
					+ intervalHeight * 3 + 3 + index
					* (textSize + (int) (1.5 * density)), p);
			address = address.substring(11);
			index++;
		}

		canvas.drawText(address, startWidth, startHeight + intervalHeight * 3
				+ 3 + index * (textSize + (int) (1.5 * density)), p);
		// 身份证号
		String certNo = info.idNo;
		Typeface font = Typeface.create("����ϸ��", Typeface.BOLD);
		p.setTypeface(font);
		for (int i = 0; i < certNo.length(); i++) {
			canvas.drawText(
					certNo.substring(i, i + 1),
					120 * density + textSize * 3 / 4 * i-10,
					(float) (bitmap.getHeight() - intervalHeight + 0.5 * density-3),
					p);
		}
		// 照片
		canvas.drawBitmap(scale(photo, density, startHeight, intervalHeight, textSize), bitmap.getWidth() / 2 + (int) (28 * density)+10, startHeight - textSize + 2 * density, p);
		
		//canvas.drawBitmap(photo, bitmap.getWidth() / 2 + (int) (28 * density), startHeight - textSize + density, p);
		
		p.setColor(Color.BLACK);
		font = Typeface.create("����ϸ��", Typeface.NORMAL);
		p.setTypeface(font);
		photoInfo.setPositiveImage(bitmap);
		
		
		//saveBitmap(bitmap, "/sdcard/1.jpg");
		Bitmap bitmap2 = BitmapFactory.decodeResource(context.getResources(),
				R.drawable.cardback).copy(Config.ARGB_8888, true);
		Canvas canvas2 = new Canvas(bitmap2);
		// 签发机关
		canvas2.drawText(
				info.police,
				bitmap2.getWidth() * 7 / 16,
				(float) (bitmap2.getHeight() - intervalHeight * 2 + 1.5 * density - 2),
				p);
		// 有效日期
		String effDate = info.start;
		String expDate = info.end;

		canvas2.drawText(
				effDate.substring(0, 4) + "." + effDate.substring(4, 6) + "."
						+ effDate.substring(6, 8) + "-"
						+ expDate.substring(0, 4) + "."
						+ expDate.substring(4, 6) + "."
						+ expDate.substring(6, 8), bitmap2.getWidth() * 7 / 16,
				(float) (bitmap2.getHeight() - intervalHeight + 1.5 * density - 2), p);
		photoInfo.setNegativeImage(bitmap2);
		//saveBitmap(bitmap2, "/sdcard/2.jpg");
		
		return photoInfo;
	}
	
	public static IdPhoto initAllBitmap(Context context,invsIdCard info,Bitmap photo,float density) {
		IdPhoto photoInfo = new IdPhoto();

		int index = 0;
		int startHeight = (int) (42 * density);
		int intervalHeight = (int) (24 * density);
		int startWidth = (int) (64 * density);
		int textSize = (int) (12 * density);
		Paint p = new Paint();
		p.setColor(Color.BLACK);// 设置红色
		
		Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(),
				R.drawable.all).copy(Config.ARGB_8888, true);
		Canvas canvas = new Canvas(bitmap);
		p.setTextSize(textSize);
		p.setColor(Color.BLACK);// 设置红色
		// 底图
		canvas.drawBitmap(bitmap, 0, 0, p);
		// 姓名
		canvas.drawText(info.name, startWidth, startHeight, p);
		// 性别
		canvas.drawText(info.sex1 , startWidth, startHeight
				+ intervalHeight, p);
		// 民族
		canvas.drawText(info.nation1, startWidth * 2, startHeight
				+ intervalHeight, p);
		// 出生日期
		String birthDay = info.birth;
		String year = birthDay.substring(0, 4);
		String month = birthDay.substring(4, 6);
		String day = birthDay.substring(6, 8);
		if (month.startsWith("0")) {
			month = month.substring(1);
		}
		if (day.startsWith("0")) {
			day = day.substring(1);
		}
		// �?
		canvas.drawText(year, startWidth, startHeight + intervalHeight * 2, p);
		// �?
		canvas.drawText(month, startWidth + (int) (50 * density), startHeight
				+ intervalHeight * 2, p);
		// �?
		canvas.drawText(day, startWidth + (int) (80 * density), startHeight
				+ intervalHeight * 2, p);
		// 住址
		String address = info.address;
		while (address.length() > 11) {

			String subAddress = address.substring(0, 11);
			canvas.drawText(subAddress, startWidth, startHeight
					+ intervalHeight * 3 + 3 + index
					* (textSize + (int) (1.5 * density)), p);
			address = address.substring(11);
			index++;
		}

		canvas.drawText(address, startWidth, startHeight + intervalHeight * 3
				+ 3 + index * (textSize + (int) (1.5 * density)), p);
		// 身份证号
		String certNo = info.idNo;
		Typeface font = Typeface.create("����ϸ��", Typeface.BOLD);
		p.setTypeface(font);
		for (int i = 0; i < certNo.length(); i++) {
			canvas.drawText(
					certNo.substring(i, i + 1),
					120 * density + textSize * 3 / 4 * i-10,
					180,
					p);
		}
		// 照片
		canvas.drawBitmap(scale(photo, density, startHeight, intervalHeight, textSize), bitmap.getWidth() / 2 + (int) (28 * density)+10, startHeight - textSize + 2 * density, p);
		
		//canvas.drawBitmap(photo, bitmap.getWidth() / 2 + (int) (28 * density), startHeight - textSize + density, p);
		
		p.setColor(Color.BLACK);
		font = Typeface.create("����ϸ��", Typeface.NORMAL);
		p.setTypeface(font);
		
		// 签发机关
		canvas.drawText(
				info.police,
				bitmap.getWidth() * 7 / 16,
				(float) (bitmap.getHeight() - intervalHeight * 2 + 1.5 * density - 2),
				p);
		// 有效日期
		String effDate = info.start;
		String expDate = info.end;

		canvas.drawText(
				effDate.substring(0, 4) + "." + effDate.substring(4, 6) + "."
						+ effDate.substring(6, 8) + "-"
						+ expDate.substring(0, 4) + "."
						+ expDate.substring(4, 6) + "."
						+ expDate.substring(6, 8), bitmap.getWidth() * 7 / 16,
				(float) (bitmap.getHeight() - intervalHeight + 1.5 * density - 2), p);
		photoInfo.setAllImage(bitmap);
		return photoInfo;
	}
	
	/*
	public static IdPhoto initBitmap(Context context,invsIdCard info,Bitmap photo,float density) {
		IdPhoto photoInfo = new IdPhoto();

		int index = 0;
		int startHeight = (int) (30 * density);
		int intervalHeight = (int) (17.5 * density);
		int startWidth = (int) (47.5 * density);
		int textSize = (int) (8 * density);
		Paint p = new Paint();
		p.setColor(Color.BLACK);// 设置红色
		
		Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(),
				R.drawable.cardfront).copy(Bitmap.Config.ARGB_8888, true);
		Canvas canvas = new Canvas(bitmap);
		p.setTextSize(textSize);
		p.setColor(Color.BLACK);// 设置红色
		// 底图
		canvas.drawBitmap(bitmap, 0, 0, p);
		// 姓名
		canvas.drawText(info.name, startWidth, startHeight, p);
		// 性别
		canvas.drawText(info.sex1 , startWidth, startHeight
				+ intervalHeight, p);
		// 民族
		canvas.drawText(info.nation1, startWidth * 2, startHeight
				+ intervalHeight, p);
		// 出生日期
		String birthDay = info.birth;
		String year = birthDay.substring(0, 4);
		String month = birthDay.substring(4, 6);
		String day = birthDay.substring(6, 8);
		if (month.startsWith("0")) {
			month = month.substring(1);
		}
		if (day.startsWith("0")) {
			day = day.substring(1);
		}
		// �?
		canvas.drawText(year, startWidth, startHeight + intervalHeight * 2, p);
		// �?
		canvas.drawText(month, startWidth + (int) (32.5 * density), startHeight
				+ intervalHeight * 2, p);
		// �?
		canvas.drawText(day, startWidth + (int) (52.5 * density), startHeight
				+ intervalHeight * 2, p);
		// 住址
		String address = info.address;
		while (address.length() > 11) {

			String subAddress = address.substring(0, 11);
			canvas.drawText(subAddress, startWidth, startHeight
					+ intervalHeight * 3 + 3 + index
					* (textSize + (int) (1.5 * density)), p);
			address = address.substring(11);
			index++;
		}

		canvas.drawText(address, startWidth, startHeight + intervalHeight * 3
				+ 3 + index * (textSize + (int) (1.5 * density)), p);
		// 身份证号
		String certNo = info.idNo;
		Typeface font = Typeface.create("����ϸ��", Typeface.BOLD);
		p.setTypeface(font);
		for (int i = 0; i < certNo.length(); i++) {
			canvas.drawText(
					certNo.substring(i, i + 1),
					86 * density + textSize * 3 / 4 * i,
					(float) (bitmap.getHeight() - intervalHeight + 0.5 * density),
					p);
		}
		// 照片
		canvas.drawBitmap(scale(photo, density, startHeight, intervalHeight, textSize), bitmap.getWidth() / 2 + (int) (28 * density), startHeight - textSize + 2 * density, p);
		
		//canvas.drawBitmap(photo, bitmap.getWidth() / 2 + (int) (28 * density), startHeight - textSize + density, p);
		
		p.setColor(Color.BLACK);
		font = Typeface.create("����ϸ��", Typeface.NORMAL);
		p.setTypeface(font);
		photoInfo.setPositiveImage(bitmap);
		
		
		saveBitmap(bitmap, "/sdcard/1.jpg");
		Bitmap bitmap2 = BitmapFactory.decodeResource(context.getResources(),
				R.drawable.cardback).copy(Bitmap.Config.ARGB_8888, true);
		Canvas canvas2 = new Canvas(bitmap2);
		// 签发机关
		canvas2.drawText(
				info.police,
				bitmap2.getWidth() * 7 / 16,
				(float) (bitmap2.getHeight() - intervalHeight * 2 + 1.5 * density),
				p);
		// 有效日期
		String effDate = info.start;
		String expDate = info.end;

		canvas2.drawText(
				effDate.substring(0, 4) + "." + effDate.substring(4, 6) + "."
						+ effDate.substring(6, 8) + "-"
						+ expDate.substring(0, 4) + "."
						+ expDate.substring(4, 6) + "."
						+ expDate.substring(6, 8), bitmap2.getWidth() * 7 / 16,
				(float) (bitmap2.getHeight() - intervalHeight + 1.5 * density),
				p);
		photoInfo.setNegativeImage(bitmap2);
		saveBitmap(bitmap2, "/sdcard/2.jpg");
		
		return photoInfo;
	}*/
    
    public static Bitmap scale(Bitmap bitmap, int width) {

		Matrix matrix = new Matrix();
		float scale = Float.valueOf(width) / bitmap.getWidth();

		System.out.println(scale + "," + bitmap.getWidth() + "," + width);
		matrix.postScale(scale, scale); // 长和宽放大缩小的比例
		Bitmap resizeBmp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
				bitmap.getHeight(), matrix, true);
		return resizeBmp;
	}

    public static Bitmap scale(Bitmap bitmap, float density, int startHeight,
			int intervalHeight, int textSize) {

		int newHeight = startHeight + intervalHeight * 2 + 2 * textSize;
		Matrix matrix = new Matrix();

		matrix.postScale(Float.valueOf(newHeight) / bitmap.getHeight(),
				Float.valueOf(newHeight) / bitmap.getHeight()); // 长和宽放大缩小的比例
		Bitmap resizeBmp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
				bitmap.getHeight(), matrix, true);
		return resizeBmp;
	}

}
