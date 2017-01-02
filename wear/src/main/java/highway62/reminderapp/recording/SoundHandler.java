package highway62.reminderapp.recording;

import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.os.AsyncTask;
import android.util.Log;

import highway62.reminderapp.ViewRemindersActivity;
import highway62.reminderapp.constants.Consts;


/**
 * Handles the recording of audio data to be sent to the accompanying device(s)
 */
public class SoundHandler{

    private int bufferSize = 0;
    private AudioTrack at;
    private boolean isPlaying = false;
    ViewRemindersActivity parent = null;

    public SoundHandler(){}

    public SoundHandler(ViewRemindersActivity parent){
        this.parent = parent;
    }

    public void startPlaying(byte[] audio){
        if(!isPlaying()) {
            new SoundPlayer().execute(audio);
        }
    }

    private class SoundPlayer extends AsyncTask<byte[], Void, Void>{
        @Override
        protected Void doInBackground(byte[]... params) {
            for(byte[] audio : params){
                playAudio(audio);
            }
            return null;
        }
    }

    private void playAudio(byte[] audio){

        bufferSize = AudioRecord.getMinBufferSize(
                Consts.RECORDER_SAMPLERATE,
                Consts.RECORDER_CHANNELS_IN,
                Consts.RECORDER_AUDIO_ENCODING);

        at = new AudioTrack(
                AudioManager.STREAM_MUSIC,
                Consts.RECORDER_SAMPLERATE,
                Consts.RECORDER_CHANNELS_OUT,
                Consts.RECORDER_AUDIO_ENCODING,
                bufferSize,
                AudioTrack.MODE_STREAM);

        if (at != null) {
            at.play();
            isPlaying = true;
            // Write the byte array to the track
            at.write(audio, 0, audio.length);
            stopPlaying();
        }
        else
            Log.d("TCAudio", "audio track is not initialised ");
    }

    public void stopPlaying(){
        if(at != null){
            //at.stop();
            at.release();
            isPlaying = false;
        }
    }

    public boolean isPlaying(){
        return isPlaying;
    }

}
