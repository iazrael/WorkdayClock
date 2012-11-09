/**
 * @author azraellong
 * @date 2012-11-9
 */
package com.imatlas.workdayclock;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.util.Log;

/**
 * @author azraellong
 *
 */
public class MusicService extends Service {

	private MediaPlayer mediaPlayer;
	
	/* (non-Javadoc)
	 * @see android.app.Service#onBind(android.content.Intent)
	 */
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public void ring(Context context) {
		Log.v("music-service", "ring.....");
		if (mediaPlayer == null) {
			mediaPlayer = MediaPlayer.create(context, R.raw.ring);
			mediaPlayer.setLooping(true);
		}
		if (mediaPlayer.isPlaying()) {
			mediaPlayer.stop();
		}
		mediaPlayer.start();
	}

	public void stopRing() {
		if(mediaPlayer == null){
			Log.v("music-service", "mediaplayer is null ");
		}
		if (mediaPlayer != null && mediaPlayer.isPlaying()) {
			mediaPlayer.stop();
		}
	}
	
	@Override
	public void onDestroy() {
		this.stopRing();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		this.ring(getApplicationContext());
		return START_STICKY;
	}


}
