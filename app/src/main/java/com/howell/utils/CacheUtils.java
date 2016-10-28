package com.howell.utils;

import android.graphics.Bitmap;
import android.os.Environment;
import android.os.StatFs;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

/**
 * @author 霍之昊 
 *
 * 类说明
 */
public class CacheUtils {
	
	public static String getSDCardPath(){
		return Environment.getExternalStorageDirectory().getAbsolutePath();
	}
	
	public static String getBitmapCachePath(){
		return getSDCardPath() + File.separator + "howell" + File.separator + "maps_cache" + File.separator;
	}
	
	public static String getPictureCachePath(){
		return getSDCardPath() + File.separator + "howell" + File.separator + "pictures_cache" + File.separator;
	}
	
	public static void createBitmapDir(){
		File eCameraDir = new File(getSDCardPath() + "/howell");
		if (!eCameraDir.exists()) {
			eCameraDir.mkdirs();
		}
		File bitmapCacheDir = new File(getSDCardPath() + "/howell/maps_cache");
		if (!bitmapCacheDir.exists()) {
			bitmapCacheDir.mkdirs();
		}
		File pictureCacheDir = new File(getSDCardPath() + "/howell/pictures_cache");
		if (!pictureCacheDir.exists()) {
			pictureCacheDir.mkdirs();
		}
	}
	
	/** 
	 * 计算sdcard上的剩余空间 
	 * @return 
	 */  
	public static int freeSpaceOnSd() {  
	    StatFs stat = new StatFs(getSDCardPath());  
	    double sdFreeMB = ((double)stat.getAvailableBlocks() * (double) stat.getBlockSize()) / ( 1024 *1024 );  
	    return (int) sdFreeMB;  
	}  
	
	/** 
	 * 修改文件的最后修改时间 
	 *
	 * @param filePath
	 */  
	@SuppressWarnings("unused")
	private void updateFileTime(String filePath) {  
	    File file = new File(filePath);         
	    long newModifiedTime = System.currentTimeMillis();  
	    file.setLastModified(newModifiedTime);  
	}  
	
	/** 
	 *计算存储目录下的文件大小，当文件总大小大于规定的CACHE_SIZE或者sdcard剩余空间小于FREE_SD_SPACE_NEEDED_TO_CACHE的规定 
	 * 那么删除40%最近没有被使用的文件 
	 * @param dirPath 
	 *
	 */  
//	static int CACHE_SIZE = 50;
//	static int MB = 1024 * 1024;
//	static int FREE_SD_SPACE_NEEDED_TO_CACHE = 50 ;
	public static void removeCache(String dirPath) {  
	    File dir = new File(dirPath);  
	    File[] files = dir.listFiles();  
	    if (files == null) {  
	        return;  
	    }  
	    Log.i("", "文件个数："+files.length);  
//	    int dirSize = 0;  
//	    for (int i = 0; i < files.length;i++) {  
//	        dirSize += files[i].length();  
//	    }  
//	    if (dirSize > CACHE_SIZE * MB ||FREE_SD_SPACE_NEEDED_TO_CACHE > freeSpaceOnSd()) {  
//	        int removeFactor = (int) ((0.4 *files.length) + 1);  
//	        Log.i("", "Clear some expiredcache files ");  
//	        for (int i = files.length ; i > removeFactor; i--) {  
//	            files[i].delete();               
//	        }  
//	    }  
        for (int i = 0 ; i < files.length; i++) {  
            files[i].delete();               
        }  
	}  
	
	static final int neededCacheSpace = 50;
	
	public static void saveBmpToSd(Bitmap bm, String filename) {  
        if (bm == null) {  
        	Log.e("saveBmpToSd","Bitmap is null");
            return;  
        }  
         //判断sdcard上的空间  
        if (neededCacheSpace >freeSpaceOnSd()) {  
        	removeCache(getSDCardPath() + File.separator + "howell" + File.separator + "maps_cache" + File.separator);
            return;  
        }  
        File file = new File(getSDCardPath() + File.separator + "howell" + File.separator + "maps_cache" + File.separator + filename);  
        try {  
            file.createNewFile();  
            OutputStream outStream = new FileOutputStream(file);  
            bm.compress(Bitmap.CompressFormat.JPEG, 100, outStream);  
            outStream.flush();  
            outStream.close();  
        } catch (FileNotFoundException e) {  
        	Log.e("saveBmpToSd","FileNotFoundException");
        } catch (IOException e) {  
        	Log.e("saveBmpToSd","IOException");
        }  
        Log.i("saveBmpToSd","create " + filename + " success");
    }  
	
	public static void cachePictures(Bitmap bm, String filename) {  
        if (bm == null) {  
        	Log.e("saveBmpToSd","Bitmap is null");
            return;  
        }  
         //判断sdcard上的空间  
        //if (neededCacheSpace >freeSpaceOnSd()) {  
        //	removeCache(getSDCardPath() + File.separator + "howell" + File.separator + "pictures_cache" + File.separator);
        //    return;  
        //}  
        File file = new File(getSDCardPath() + File.separator + "howell" + File.separator + "pictures_cache" + File.separator + filename);  
        try {  
            file.createNewFile();  
            OutputStream outStream = new FileOutputStream(file);  
            bm.compress(Bitmap.CompressFormat.JPEG, 100, outStream);  
            outStream.flush();  
            outStream.close();  
        } catch (FileNotFoundException e) {  
        	Log.e("cachePictures","FileNotFoundException");
        } catch (IOException e) {  
        	Log.e("cachePictures","IOException");
        }  
        Log.i("cachePictures","create " + filename + " success");
    }  
	
	public static boolean isBitmapExist(String filename){
		if(filename == null){
			Log.e("isBitmapExist","filename == null");
			return false;
		}
		File f = new File(getBitmapCachePath() + filename);
		Log.e("isBitmapExist",f.exists()+"");
		return f.exists();
	}
	// 从sd卡获取图片资源
	public static ArrayList<String> getMapPathFromSD() {
		// 图片列表
		ArrayList<String> picList = new ArrayList<String>();
		// 得到该路径文件夹下所有的文件
		File mfile = new File(getBitmapCachePath());
		File[] files = mfile.listFiles();
		// 将所有的文件存入ArrayList中,并过滤所有图片格式的文件
		for (int i = 0; i < files.length; i++) {
			picList.add(files[i].getPath());
		}
		// 返回得到的图片列表
		return picList;
	}
	
	// 从sd卡获取图片资源
	public static String getMapPathFromSD(String mapId) {
		// 得到该路径文件夹下所有的文件
		File mfile = new File(getBitmapCachePath());
		File[] files = mfile.listFiles();
		// 将所有的文件存入ArrayList中,并过滤所有图片格式的文件
		for (int i = 0; i < files.length; i++) {
			if(files[i].getName().equals(mapId)){
				return files[i].getPath();
			}
		}
		// 返回得到的图片列表
		return "";
	}
	
	// 从sd卡获取地图Id
	public static ArrayList<String> getMapIdFromSD() {
		// 图片列表
		ArrayList<String> picList = new ArrayList<String>();
		// 得到该路径文件夹下所有的文件
		File mfile = new File(getBitmapCachePath());
		File[] files = mfile.listFiles();
		// 将所有的文件存入ArrayList中,并过滤所有图片格式的文件
		for (int i = 0; i < files.length; i++) {
			picList.add(files[i].getName());
		}
		// 返回得到的图片列表
		return picList;
	}
	
	public static void createFile(String name, String type, byte[] body) {
		byte[] bodyBity = body;
		String path = null;
		try {

		if (type.equalsIgnoreCase("JPG") || type.equalsIgnoreCase("PNG")
		|| type.equalsIgnoreCase("gpeg")
		|| type.equalsIgnoreCase("gif")
		|| type.equalsIgnoreCase("bmp")) {
			path = "/sdcard/";
		}else{
			path = "/sdcard/other/";
		}
		FileOutputStream fos = new FileOutputStream(path
		+ name + "." + type);
		BufferedOutputStream bufOutputStream = new BufferedOutputStream(fos,20480);
		bufOutputStream.write(bodyBity);
		bufOutputStream.flush();
		bufOutputStream.close();
		} catch (FileNotFoundException e1) {
		Log.i("archermind", "--------------------------------------------------------" + e1);
		} catch (IOException e) {
		Log.i("archermind", "--------------------------------------------------------" + e);
		}

	}
	
	public static byte[] getByteArrayFromFile(String fileName) {
        File file = null;
        try {
                file = new File(fileName);
        } catch (Exception e) {
                e.printStackTrace();
                return null;
        }

        if (!file.exists() || !file.isFile() || !file.canRead()) {
                return null;
        }

        byte[] byteArray = null;

        try {
                FileInputStream fis = new FileInputStream(file);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();

                int count;
                byte buffer[] = new byte[1024];
                while ((count = fis.read(buffer)) > 0) {
                        baos.write(buffer, 0, count);
                }
                byteArray = baos.toByteArray();
                fis.close();
                baos.flush();
                baos.close();
        } catch (Exception e) {
                e.printStackTrace();
        }

        return byteArray;
}
	
}
