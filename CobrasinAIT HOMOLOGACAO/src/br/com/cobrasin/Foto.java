package br.com.cobrasin;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;   

import br.com.cobrasin.dao.FotoDAO;


import android.annotation.TargetApi;
import android.app.Activity;   
import android.content.Context;   
import android.graphics.PixelFormat;   
import android.hardware.Camera;   
import android.os.Build;
import android.os.Bundle;   
import android.util.Log;   
import android.view.SurfaceHolder;   
import android.view.SurfaceView;   
import android.view.View;   
import android.view.Window;   
import android.view.WindowManager;   
import android.view.View.OnClickListener;   
  
  
  
@TargetApi(Build.VERSION_CODES.FROYO)
public class Foto extends Activity implements SurfaceHolder.Callback,   
        OnClickListener {   
    static final int FOTO_MODE = 0;   
    private static final String TAG = "CameraTest";   
    Camera mCamera;   
    boolean mPreviewRunning = false;   
    private Context mContext = this;   
       
    long idAit = 0 ; 
  
    public void onCreate(Bundle icicle) {   
        super.onCreate(icicle);   
           
        // pega o Id do AIT 
        idAit = (Long) getIntent().getSerializableExtra("idAit");
        
       // Bundle extras = getIntent().getExtras();   
        
        getWindow().setFormat(PixelFormat.JPEG);   
        requestWindowFeature(Window.FEATURE_NO_TITLE);   
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);   
        
        setContentView(R.layout.fotografa1);   
        
        mSurfaceView = (SurfaceView) findViewById(R.id.surf1);   
        
        mSurfaceView.setOnClickListener(this);   
        mSurfaceHolder = mSurfaceView.getHolder();   
        mSurfaceHolder.addCallback(this);   
        mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);   

        /*
        mCamera.setParameters(mCamera.getParameters()); // pega os parametros default
        try {   
            mCamera.setPreviewDisplay(mSurfaceHolder);   
            mCamera.startPreview();   
        } catch (Exception e) {   
            System.out.println("Erro - " + e.getMessage());   
        } 
        */  
  
    }   
           
    @Override   
    protected void onRestoreInstanceState(Bundle savedInstanceState) {   
        super.onRestoreInstanceState(savedInstanceState);   
    }   
  
    Camera.PictureCallback mPictureCallback = new Camera.PictureCallback() {   
        public void onPictureTaken(byte[] imageData, Camera c) {   
  
            if (imageData != null) {   
  
            	
                //Intent mIntent = new Intent();   
  
                /*FileUtilities.StoreByteImage(mContext, imageData,  
                         50, "ImageName");*/   
                mCamera.startPreview();   

                //setResult(FOTO_MODE,mIntent);
                
                // grava a foto em um campo blob
                FotoDAO fotodao = new FotoDAO(getBaseContext());
                fotodao.gravaFoto(idAit, imageData);
                fotodao.close();
                
                finish();   
               
  
            }   
        }   
    };   
  
    protected void onResume() {   
        Log.e(TAG, "onResume");   
        super.onResume();   
    }   
  
    protected void onSaveInstanceState(Bundle outState) {   
        super.onSaveInstanceState(outState);   
    }   
  
    protected void onStop() {   
        Log.e(TAG, "onStop");   
        super.onStop();   
    }   
  
    public void surfaceCreated(SurfaceHolder holder) {   
        
    	Log.e(TAG, "surfaceCreated");   
        
        mCamera = Camera.open();   
        
        /*
        mCamera.setParameters(mCamera.getParameters()); // pega os parametros default   
        try {   
            mCamera.setPreviewDisplay(holder);   
            mCamera.startPreview();   
        } catch (Exception e) {   
            System.out.println("Erro - " + e.getMessage());   
        } 
        */  
    }   
  
    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {   
        Log.e(TAG, "surfaceChanged");   
  
        
        // XXX stopPreview() will crash if preview is not running   
        if (mPreviewRunning) {   
            mCamera.stopPreview();   
        }   
  
        Camera.Parameters p = mCamera.getParameters();
        
        Camera.Size cx = getBestPreviewSize(w,h);
        
        p.setPreviewSize(cx.width,cx.height);   
           
        mCamera.setParameters(p);
        
        try {
        	
            mCamera.setPreviewDisplay(holder);   
        } catch (IOException e) {   
            // TODO Auto-generated catch block   
            e.printStackTrace();   
        }   
        mCamera.startPreview();   
        mPreviewRunning = true;   
    }   
  
    public void surfaceDestroyed(SurfaceHolder holder) {   
        Log.e(TAG, "surfaceDestroyed");   
        mCamera.stopPreview();   
        mPreviewRunning = false;   
        mCamera.release();   
    }   
  
    private SurfaceView mSurfaceView;   
    private SurfaceHolder mSurfaceHolder;   
  
    public void onClick(View arg0) {   
  
        mCamera.takePicture(null, mPictureCallback, mPictureCallback);   
  
    }   
    
    private Camera.Size getBestPreviewSize(int width, int height)
    {
        Camera.Size result=null;    
        Camera.Parameters p = mCamera.getParameters();
        for (Camera.Size size :p.getSupportedJpegThumbnailSizes() ) { //p.getSupportedPreviewSizes()
            if (size.width<=width && size.height<=height) {
                if (result==null) {
                    result=size;
                } else {
                    int resultArea=result.width*result.height;
                    int newArea=size.width*size.height;

                    if (newArea>resultArea) {
                        result=size;
                    }
                }
            }
        }
        return result;
    }
  
}  
