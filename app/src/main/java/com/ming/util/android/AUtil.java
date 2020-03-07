package com.ming.util.android;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.List;

import com.ming.util.common.Util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.widget.Toast;

public class AUtil {






	public static int getStateBarHeight(Context context){
		int result = 0;
		int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
		if (resourceId > 0) {
			result = context.getResources().getDimensionPixelSize(resourceId);
		}
		return result;
	}


	public static String makeCachePath(Context context, String name) {
		String path = "";

		String cachepath = "";
		cachepath = 	context.getExternalFilesDir(null).getPath();
		if(!Util.isNull(cachepath))
		{
			if(!cachepath.endsWith("/"))
			{
				cachepath = cachepath+"/";
			}
			path = cachepath+name+"/";

			Util.debug("getCachePath----"+path);

			boolean isOk = new File(path).mkdirs();
			Util.debug("getCachePath----"+isOk);
		}
		return path;
	}



	
	public static StateListDrawable getImageStateListDrawable(Bitmap pressBitmap, Bitmap relessBitmap) {
		
		int pressed = android.R.attr.state_pressed;  
		int window_focused = android.R.attr.state_window_focused;  
		int focused = android.R.attr.state_focused;  
		int selected = android.R.attr.state_selected;  
		
		BitmapDrawable pd = new BitmapDrawable(pressBitmap);
		BitmapDrawable rd = new BitmapDrawable(relessBitmap);
		StateListDrawable stalistDrawable = new StateListDrawable();  
		stalistDrawable.addState(new int []{pressed , window_focused}, pd);  
		stalistDrawable.addState(new int []{pressed , -focused}, pd);  
		stalistDrawable.addState(new int []{selected }, pd);  
		stalistDrawable.addState(new int []{focused }, pd);  
		stalistDrawable.addState(new int []{}, rd);  		
		
		return stalistDrawable;
	}	 	
	
	
	public static Bitmap getScaleBitmap(Bitmap bitmap, float f) {
		int dstWidth = (int)(bitmap.getWidth()*f);
		int dstHeight = (int)(bitmap.getHeight()*f);
		return Bitmap.createScaledBitmap(bitmap, dstWidth, dstHeight, true);
	}	 	
	
	
	public static Bitmap getAssetsBitmap(Context context, String string) {
		byte[] data = AUtil.getAssetsData(context, string);
		Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
		data = null;
		return bmp;
	}	
	
	
	  public static boolean checkPackage(Context context,String packageName)
	  { 
		   boolean isOK = false;
		  	try
	        {
				PackageManager manager;
				PackageInfo info = null;
				manager = context.getPackageManager();
				info = manager.getPackageInfo(packageName, 0);
				String v = info.versionName;
				if(!Util.isNull(v))
				{
					isOK = true;
				}
	        }

	        catch (Exception e)
	        { 
	        	e.printStackTrace();
	        } 
	        
	        return isOK;

	    }	
	
	private static Handler handler;
	
	public static int getDip(Context context, int num)
	{
		// System.out.println(Util.getDeviceDpi(active));
		float ff = num * getDeviceDpi((Activity) context);
		return (int) ff;
	}

	public static float getDeviceDpi(Activity paramActivity)
	  {
	    DisplayMetrics localDisplayMetrics = new DisplayMetrics();
	    paramActivity.getWindowManager().getDefaultDisplay().getMetrics(localDisplayMetrics);
	    return localDisplayMetrics.density;
	  }	
	
	public static void showTips(final Context context,final String tips) {
		if (handler == null) {
			handler = new Handler(Looper.getMainLooper());
		}

		handler.post(new Runnable() {
			@SuppressLint("WrongConstant")
			public void run() {
				try {
					Toast.makeText(context, tips, 60).show();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}		  
	
	
	  private static int BUFFER_SIZE = 100*1024;
	  public static void copyAssetsDataToFile(Context paramContext,String url,String path)
	  {
	    String str = url;
	    try
	    {
	      if (url.startsWith("assets:")) 
	      {
	          str = url.substring(9);
	      }		
		      
          int size = 0;
          byte[] buf = new byte[BUFFER_SIZE];
          
//          Util.debug("url----------"+url);
//          Util.debug("path----------"+path);

	      FileOutputStream fos = new FileOutputStream(path);
          InputStream bis = paramContext.getAssets().open(str);
          while ((size = bis.read(buf)) != -1) {
              fos.write(buf, 0, size);
          }
          fos.close();
          bis.close();	      
//          Util.debug("size----------"+new File(path).length());
          
	    }
	    catch (Exception e)
	    {
	      e.printStackTrace();
	    }
	  }		
	
	
	  public static byte[] readAppFileData(Context paramContext, String paramString)
	  {
		  byte[] data = null;
	    try
	    {
	      FileInputStream localFileOutputStream = paramContext.openFileInput(paramString);
	      data = Util.inputStreamToBytes(localFileOutputStream);
	    }
	    catch (Exception localException)
	    {
	      localException.printStackTrace();
	    }
	      return data;
	  }		
	
	
	public static String getAppMain(Context context,String className)
	{
		String isMain = "";
		try
		{
			Intent intent = new Intent(Intent.ACTION_MAIN, null);
			intent.addCategory(Intent.CATEGORY_LAUNCHER);

			List<ResolveInfo> list = context.getPackageManager().queryIntentActivities(intent,PackageManager.GET_ACTIVITIES);			
			
			for (int i = 0; i < list.size(); i++) 
			{
				ActivityInfo act = list.get(i).activityInfo;
//				System.out.println("act.name-------------------------:"+act.name+"----------package:"+act.packageName);
				if(act.packageName.indexOf(className)!=-1)
				{
					isMain = act.name;
					break;
				}
			}

		}
		catch(Exception e)
		{
			e.printStackTrace();
			
		}
		return isMain;
		
	}	
	
	
	  private static int id_index = 4660;
	  public static int getId()
	  {
	    id_index = 1 + id_index;
	    return id_index;
	  }
	
	
	
	public static boolean checkSDCARD() {
		try {
			boolean bool = Environment.getExternalStorageState().equals(
					"mounted");
			return bool;
		} catch (Exception localException) {
			localException.printStackTrace();
		}
		return false;
	}


	public static String urlToAssets(String paramString)
	{
		String str = paramString;
		str = Util.replaceAll(str, "\\", "/", true);
		if (paramString.startsWith("assets:"))
		{
			str = paramString.substring(9);
		}
		return str;
	}
	
	  public static byte[] getAssetsData(Context paramContext, String paramString)
	  {
	    String str = urlToAssets(paramString);
	    try
	    {
	      byte[] arrayOfByte = Util.inputStreamToBytes(paramContext.getAssets().open(str));
	      return arrayOfByte;
	    }
	    catch (Exception localException)
	    {
	      localException.printStackTrace();
	    }
	    return null;
	  }	
	
	
	  public static boolean writeAppFileData(Context paramContext, String paramString, byte[] paramArrayOfByte)
	  {
	    try
	    {
	      FileOutputStream localFileOutputStream = paramContext.openFileOutput(paramString, Context.MODE_PRIVATE);
	      localFileOutputStream.write(paramArrayOfByte);
	      localFileOutputStream.close();
	      return true;
	    }
	    catch (Exception localException)
	    {
	      localException.printStackTrace();
	    }
	    return false;
	  }	
	
	  
		public static int getDeviceWidth(Context context)
		  {
		    return getDeviceWidth((Activity)context);
		  }
		  
		  

		public static int getDeviceHeight(Context context)
		  {
		    return getDeviceHeight((Activity)context);
		  }	
			
		public static int getDeviceHeight(Activity paramActivity)
		  {
		    DisplayMetrics localDisplayMetrics = new DisplayMetrics();
		    paramActivity.getWindowManager().getDefaultDisplay().getMetrics(localDisplayMetrics);
		    return localDisplayMetrics.heightPixels;
		  }
		  
		public static int getDeviceWidth(Activity paramActivity)
		  {
		    DisplayMetrics localDisplayMetrics = new DisplayMetrics();
		    paramActivity.getWindowManager().getDefaultDisplay().getMetrics(localDisplayMetrics);
		    return localDisplayMetrics.widthPixels;
		  }

		
		
		  public static void copyAssetsDataToAppFile(Context paramContext,String url,String path)
		  {
		    String str = url;
		    try
		    {
		      if (url.startsWith("assets:")) 
		      {
		          str = url.substring(9);
		      }		
			      
	          int size = 0;
	          byte[] buf = new byte[BUFFER_SIZE];
	          

		      FileOutputStream fos = paramContext.openFileOutput(path, Context.MODE_PRIVATE );
	          InputStream bis = paramContext.getAssets().open(str);
	          while ((size = bis.read(buf)) != -1) {
	              fos.write(buf, 0, size);
	          }
	          fos.close();
	          bis.close();	      
	         
	          
		    }
		    catch (Exception e)
		    {
		      e.printStackTrace();
		    }
		  }


		public static void copyFileToAppFile(Context context, String url, String path) {
			
			try 
			{
		          InputStream bis = null;
		          if(url.startsWith("assets:")) 
		          {
				      String str = url;
				      if (url.startsWith("assets:")) 
				      {
				          str = url.substring(9);
				      }		
				      bis = context.getAssets().open(str);
		          }
		          else 
		          {
		        	  bis = new FileInputStream(url); 
		          }
		          
		          int size = 0;
		          byte[] buf = new byte[BUFFER_SIZE];
		          context.deleteFile(path);
			      FileOutputStream fos = context.openFileOutput(path, Context.MODE_PRIVATE );
		          while ((size = bis.read(buf)) != -1) {
		              fos.write(buf, 0, size);
		          }
		          fos.close();
		          bis.close();	      
				
			}
			catch(Exception e) 
			{
				e.printStackTrace();
			}
			
		}			

		

	public static final Bitmap bitmapToGery(Bitmap bitmap) {
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();

		Bitmap faceIconGreyBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

		Canvas canvas = new Canvas(faceIconGreyBitmap);
		Paint paint = new Paint();
		ColorMatrix colorMatrix = new ColorMatrix();
		colorMatrix.setSaturation(0);
		ColorMatrixColorFilter colorMatrixFilter = new ColorMatrixColorFilter(colorMatrix);
		paint.setColorFilter(colorMatrixFilter);
		canvas.drawBitmap(bitmap, 0, 0, paint);
		return faceIconGreyBitmap;
	}


	public static void delAppFile(Context context, String name) {
		context.deleteFile(name);
		// TODO Auto-generated method stub
		
	}	
 
	  

}
