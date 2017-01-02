package highway62.reminderapp.recording;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Handles the recording of audio data to be sent to the accompanying device(s)
 */
public class AudioRecorder {

    private static final int RECORDER_SAMPLERATE = 8000;
    private static final int RECORDER_CHANNELS = AudioFormat.CHANNEL_IN_MONO;
    private static final int RECORDER_AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT;
    private AudioRecord recorder = null;
    private Thread recordingThread = null;
    private boolean isRecording = false;
    private int bufferSize = 0;
    private ByteArrayOutputStream bOutputStream;

    int BufferElements2Rec = 1024; // want to play 2048 (2K) since 2 bytes we use only 1024
    int BytesPerElement = 2; // 2 bytes in 16bit format

    AudioRecorder(){
        bufferSize = AudioRecord.getMinBufferSize(RECORDER_SAMPLERATE, RECORDER_CHANNELS, RECORDER_AUDIO_ENCODING);
        bOutputStream = null;
    }

    public void startRecording() {

        if(recorder == null){
            recorder = new AudioRecord(MediaRecorder.AudioSource.MIC,
                    RECORDER_SAMPLERATE, RECORDER_CHANNELS,
                    RECORDER_AUDIO_ENCODING, BufferElements2Rec * BytesPerElement);
        }else{
            recorder.release();
            recorder = new AudioRecord(MediaRecorder.AudioSource.MIC,
                    RECORDER_SAMPLERATE, RECORDER_CHANNELS,
                    RECORDER_AUDIO_ENCODING, BufferElements2Rec * BytesPerElement);
        }


        Log.d("AUDIO", "Recorder instantiated: " + recorder.toString());
        Log.d("AUDIO", "Recorder state: " + recorder.getState());
        if(recorder.getState() == AudioRecord.STATE_INITIALIZED){
            recorder.startRecording();
            isRecording = true;
            recordingThread = new Thread(new Runnable() {
                public void run() {
                    storeAudioData();
                }
            }, "AudioRecorder Thread");
            recordingThread.start();
        }else{
            // Audio recorder failed to initialise
            Log.e("AUDIO", "Audio Recorder failed to initialise");
        }

    }

    public byte[] stopRecording() {
        // stops the recording activity
        if (recorder != null) {
            isRecording = false;
            recorder.stop();
            recorder.release();
            recorder = null;
            recordingThread = null;
            byte[] audioBytes = bOutputStream.toByteArray();

            try {
                bOutputStream.reset();
                bOutputStream.close();
                Log.d("RECORDER", "bOutputstream closed");
            } catch (IOException e) {
                Log.d("RECORDER", "Output stream could not be closed or reset");
                e.printStackTrace();
            }
            if(audioBytes != null){
                return audioBytes;
            }else{
                return null;
            }
        } else {
            return null;
        }
    }

    private void storeAudioData() {
        // Store the audio in bytes

        short sData[] = new short[BufferElements2Rec];

        bOutputStream = new ByteArrayOutputStream();

        while (isRecording) {
            // Get the voice output from microphone and store in byte format in byte output stream
            recorder.read(sData, 0, BufferElements2Rec);
            try {
                // Converts short data to byte array
                // Stores in byte array output stream
                byte bData[] = short2byte(sData);
                bOutputStream.write(bData);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //convert short to byte
    private byte[] short2byte(short[] sData) {
        int shortArrsize = sData.length;
        byte[] bytes = new byte[shortArrsize * 2];
        for (int i = 0; i < shortArrsize; i++) {
            bytes[i * 2] = (byte) (sData[i] & 0x00FF);
            bytes[(i * 2) + 1] = (byte) (sData[i] >> 8);
            sData[i] = 0;
        }
        return bytes;
    }

    public boolean isRecording(){
        return isRecording;
    }

    public void release(){
        if(recorder != null){
            recorder.release();
        }
    }
}