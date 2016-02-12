package com.exelerus.cordova.audioinputcapture;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import java.util.Arrays;

//import java.io.UnsupportedEncodingException;
//import android.util.Base64;

public class AudioInputReceiver extends Thread {

    private int sampleRateInHz = 44100;

    private int channelConfig = AudioFormat.CHANNEL_IN_MONO;
    //private int channelConfig = AudioFormat.CHANNEL_IN_STEREO;

    //private int audioFormat = AudioFormat.ENCODING_PCM_8BIT;
    private int audioFormat = AudioFormat.ENCODING_PCM_16BIT;

    private int bufferSize = AudioRecord.getMinBufferSize(sampleRateInHz, channelConfig, audioFormat);

    private AudioRecord recorder;

    private Handler handler;

    private Message message;

    private Bundle messageBundle = new Bundle();

    public AudioInputReceiver() {
        recorder = new AudioRecord(MediaRecorder.AudioSource.VOICE_RECOGNITION, sampleRateInHz, channelConfig, audioFormat, bufferSize);
    }

    public AudioInputReceiver(int sampleRate, int bufferSizeInBytes, int channels, String format) {

		sampleRateInHz = sampleRate;

        if (bufferSizeInBytes > bufferSize) {
            bufferSize = bufferSizeInBytes;
        }

		switch (channels) {
            case 2:
                channelConfig = AudioFormat.CHANNEL_IN_STEREO;
                break;
			case 1:
            default:
                channelConfig = AudioFormat.CHANNEL_IN_MONO;
                break;
        }

		if(format == "PCM_8BIT") {
			audioFormat = AudioFormat.ENCODING_PCM_8BIT;
		}
		else {
			audioFormat = AudioFormat.ENCODING_PCM_16BIT;
		}

        recorder = new AudioRecord(MediaRecorder.AudioSource.VOICE_RECOGNITION, sampleRateInHz, channelConfig, audioFormat, bufferSize);
    }

    public void setHandler(Handler handler) {
        this.handler = handler;
    }

    @Override
    public void run() {
        int numReadBytes = 0;
        short audioBuffer[] = new short[bufferSize];

        synchronized(this)
        {
            recorder.startRecording();

            while (!isInterrupted()) {
                numReadBytes = recorder.read(audioBuffer, 0, bufferSize);

                if (numReadBytes > 0) {

					try {

						String decoded = Arrays.toString(audioBuffer);

					/*
						// Convert Short audioBuffer to Byte array
						byte[] audioBytes = ShortToByte_Twiddle_Method(audioBuffer);

						try {
							//String decoded = Base64.encodeToString(audioBytes, Base64.NO_WRAP);
						}
						catch(UnsupportedEncodingException uex) {
						}

					*/

	                    // send data to handler
	                    message = handler.obtainMessage();
	                    messageBundle.putString("data", decoded);
	                    message.setData(messageBundle);
	                    handler.sendMessage(message);
                    }
                    catch(Exception ex) {
	                    message = handler.obtainMessage();
	                    messageBundle.putString("error", ex.toString());
	                    messageBundle.putString("data", "");
	                    message.setData(messageBundle);
	                    handler.sendMessage(message);
                    }
                }
            }

            if (recorder.getRecordingState() == AudioRecord.RECORDSTATE_RECORDING) {
                recorder.stop();
            }

            recorder.release();
            recorder = null;
        }
    }
}
