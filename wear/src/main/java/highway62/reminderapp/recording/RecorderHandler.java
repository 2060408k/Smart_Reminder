package highway62.reminderapp.recording;

import android.util.Log;

/**
 * Created by Highway62 on 15/09/2016.
 */
public class RecorderHandler {

    byte[] reminderAudio;
    AudioRecorder recorder;

    public RecorderHandler(){
        this.recorder = new AudioRecorder();
    }

    public void startRecording(){
        Log.e("AUDIO", "startRecording() called in audio handler");
        if(!recorder.isRecording()){
            recorder.startRecording();
        }else{
            Log.e("AUDIO", "startRecording() called when audio already recording");
        }
    }

    public void stopRecording(){
        Log.e("AUDIO", "stop recording called in audio handler");
        if(recorder.isRecording()){
            reminderAudio = recorder.stopRecording();
        }else{
            Log.e("AUDIO", "stopRecording() called when audio already stopped");
        }
    }

    public byte[] getReminderAudio(){
        Log.e("AUDIO", "get audio called in audio handler");
        if(reminderAudio != null && reminderAudio.length > 0){
            return reminderAudio;
        }else{
            return null;
        }
    }

    public void clearReminderAudio(){
        Log.e("AUDIO", "clear audio called in audio handler");
        reminderAudio = null;
    }
}
