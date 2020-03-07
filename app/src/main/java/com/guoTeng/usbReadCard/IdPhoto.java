package com.guoTeng.usbReadCard;

import android.graphics.Bitmap;

public class IdPhoto {
	private Bitmap mPositiveImage;
	private Bitmap mNegativeImage;
	private Bitmap mAllImage;
	
	public  Bitmap getPositiveImage() {
		return mPositiveImage;
	}

	public Bitmap getNegativeImage() {
		return mNegativeImage;
	}

	public void setPositiveImage(Bitmap pos) {
		mPositiveImage = pos;
	}

	public void setNegativeImage(Bitmap neg) {
		mNegativeImage = neg;
	}
	
	public  Bitmap getAllImage() {
		return mAllImage;
	}

	public void setAllImage(Bitmap pos) {
		mAllImage = pos;
	}
}