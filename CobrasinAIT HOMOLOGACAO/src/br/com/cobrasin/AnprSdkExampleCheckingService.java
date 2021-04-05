package br.com.cobrasin;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AsyncPlayer;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.widget.Toast;

public class AnprSdkExampleCheckingService extends Service 
{
	static AsyncPlayer asp;
	Context context;
	
	class IncomingHandler extends Handler 
	{
        @Override
        public void handleMessage(Message msg) 
        {
        	Bundle b = msg.getData();
        	String numberPlate = b.getString("NumberPlate");	// NumberPlate data
        	boolean r = CheckCar(numberPlate);	// check the car
        	String res = "";
        	if (r == true)
        	{
            	res = " OK";	// car is OK
        	}
        	else
        	{
        		res = " is not payed";	//car is not OK
        		int sound = context.getResources().getIdentifier("alert", "raw", context.getPackageName());
    			asp.play(context, Uri.parse("android.resource://" + context.getPackageName() + "/" + sound), false, AudioManager.STREAM_MUSIC);
        	}
            Toast.makeText(getApplicationContext(), numberPlate + res, Toast.LENGTH_SHORT).show(); // show the checking result
        }
    }
	
	final Messenger mMessenger = new Messenger(new IncomingHandler());

	
	@Override
	public IBinder onBind(Intent arg0) 
	{
	    return mMessenger.getBinder();
	}

	
	
	private boolean CheckCar(String aNumberPlate)	// car checking function 
	{
		boolean ret = false;
		char c = aNumberPlate.charAt(aNumberPlate.length() - 1);
		if (c == 2 * (int)(c / 2))
		{
			ret = true;
		}
		return ret;
	}
	
	
    @Override
    public void onCreate() 
    {
		context = getApplicationContext();
    	asp = new AsyncPlayer("t");
    }

	
	
}
