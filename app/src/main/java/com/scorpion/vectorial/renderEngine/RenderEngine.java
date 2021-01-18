package com.scorpion.vectorial.renderEngine;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.media.MediaCodec;
import android.media.MediaCodec.BufferInfo;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.media.MediaMuxer;
import android.os.Build;
import android.util.Log;
import android.view.Surface;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

public class RenderEngine extends AppCompatActivity {

    private static final int BIT_RATE = 4000000;
    private static final int FRAMES_PER_SECOND = 30;
    static int HEIGHT = 1080;
    private static final int IFRAME_INTERVAL = 30;
    private static final String MIME_TYPE = "video/avc";
    private static final String TAG = "from_created";
    private static final boolean VERBOSE = true;
    static int WIDTH = 1920;
    public InterfaceRenderEngine interfaceRenderEngine;
    private BufferInfo mBufferInfo;
    private MediaCodec mEncoder;
    private long mFakePts;
    private Surface mInputSurface;
    private MediaMuxer mMuxer;
    private boolean mMuxerStarted;
    private int mTrackIndex;
    private int progress;

    
    public interface InterfaceRenderEngine {

        void onProgressChange(float f);

        void onRendered(File file);

        void onRenderFailed(String message);
    }

    public enum RenderMode {
        NATIVE,
        HYBRIDE
    }

    
    public void init(File file) {
    }

    public void setInterfaceRenderEngine(InterfaceRenderEngine interfaceRenderEngine2) {
        this.interfaceRenderEngine = interfaceRenderEngine2;
    }

    public void setResolution(int height,int width) {
        HEIGHT = height;
        WIDTH = width;
        Log.i(TAG, "setResolution: height= "+HEIGHT);
        Log.i(TAG, "setResolution: width= "+WIDTH);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public void prepareEncoder(File file) throws IOException {

        this.mBufferInfo = new BufferInfo();
        if (WIDTH%2!=0){
            WIDTH=WIDTH-1;
        }
        if (HEIGHT%2!=0){
            HEIGHT=HEIGHT-1;
        }
        MediaFormat createVideoFormat = MediaFormat.createVideoFormat(MIME_TYPE, WIDTH, HEIGHT);

        createVideoFormat.setInteger(MediaFormat.KEY_COLOR_FORMAT,
                MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface);
        createVideoFormat.setInteger(MediaFormat.KEY_BIT_RATE, BIT_RATE);
        createVideoFormat.setInteger(MediaFormat.KEY_FRAME_RATE, FRAMES_PER_SECOND);
        createVideoFormat.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, IFRAME_INTERVAL);

        this.mEncoder = MediaCodec.createEncoderByType(MIME_TYPE);
        this.mEncoder.configure(createVideoFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
        this.mInputSurface = this.mEncoder.createInputSurface();
        this.mEncoder.start();

        this.mMuxer = new MediaMuxer(file.toString(), MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);
        this.mTrackIndex = -1;
        this.mMuxerStarted = false;

    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public void releaseEncoder() {
        Log.d(TAG, "releasing encoder objects");
        MediaCodec mediaCodec = this.mEncoder;

        if (mediaCodec != null) {
            mediaCodec.stop();
            this.mEncoder.release();
            this.mEncoder = null;
        }

        if (mInputSurface != null) {
            mInputSurface.release();
            this.mInputSurface = null;
        }

        if (mMuxer != null) {
            mMuxer.stop();
            mMuxer.release();
            mMuxer = null;
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public void drainEncoder(boolean z) {
        String sb2 = "drainEncoder(" + z + ")";
        String str = TAG;
        if (z) {
            this.mEncoder.signalEndOfInputStream();
        }
        ByteBuffer[] outputBuffers = this.mEncoder.getOutputBuffers();
        while (true) {
            int dequeueOutputBuffer = this.mEncoder.dequeueOutputBuffer(this.mBufferInfo, 10000);
            if (dequeueOutputBuffer == -1) {
                if (z) {
                    Log.d(str, "no output available, spinning to await EOS");
                } else {
                    return;
                }
            } else if (dequeueOutputBuffer == -3) {
                outputBuffers = this.mEncoder.getOutputBuffers();
            } else if (dequeueOutputBuffer == -2) {
                if (!this.mMuxerStarted) {
                    MediaFormat outputFormat = this.mEncoder.getOutputFormat();
                    StringBuilder sb3 = new StringBuilder();
                    sb3.append("encoder output format changed: ");
                    sb3.append(outputFormat);
                    Log.d(str, sb3.toString());
                    this.mTrackIndex = this.mMuxer.addTrack(outputFormat);
                    this.mMuxer.start();
                    this.mMuxerStarted = VERBOSE;
                } else {
                    throw new RuntimeException("format changed twice");
                }
            } else if (dequeueOutputBuffer < 0) {
                StringBuilder sb4 = new StringBuilder();
                sb4.append("unexpected result from encoder.dequeueOutputBuffer: ");
                sb4.append(dequeueOutputBuffer);
                Log.w(str, sb4.toString());
            } else {
                ByteBuffer byteBuffer = outputBuffers[dequeueOutputBuffer];
                if (byteBuffer != null) {
                    if ((this.mBufferInfo.flags & 2) != 0) {
                        Log.d(str, "ignoring BUFFER_FLAG_CODEC_CONFIG");
                        this.mBufferInfo.size = 0;
                    }
                    if (this.mBufferInfo.size != 0) {
                        if (this.mMuxerStarted) {
                            byteBuffer.position(this.mBufferInfo.offset);
                            byteBuffer.limit(this.mBufferInfo.offset + this.mBufferInfo.size);
                            BufferInfo bufferInfo = this.mBufferInfo;
                            long j = this.mFakePts;
                            bufferInfo.presentationTimeUs = j;
                            this.mFakePts = j + 33333;
                            this.mMuxer.writeSampleData(this.mTrackIndex, byteBuffer, bufferInfo);
                            StringBuilder sb5 = new StringBuilder();
                            sb5.append("sent ");
                            sb5.append(this.mBufferInfo.size);
                            sb5.append(" bytes to muxer");
                            Log.d(str, sb5.toString());
                        } else {
                            throw new RuntimeException("muxer hasn't started");
                        }
                    }
                    this.mEncoder.releaseOutputBuffer(dequeueOutputBuffer, false);
                    if ((this.mBufferInfo.flags & 4) != 0) {
                        if (!z) {
                            Log.w(str, "reached end of stream unexpectedly");
                            return;
                        } else {
                            Log.d(str, "end of stream reached");
                            return;
                        }
                    }
                } else {
                    StringBuilder sb6 = new StringBuilder();
                    sb6.append("encoderOutputBuffer ");
                    sb6.append(dequeueOutputBuffer);
                    sb6.append(" was null");
                    throw new RuntimeException(sb6.toString());
                }
            }
        }
    }

    RelativeLayout mainparent;

    public void setLayout(RelativeLayout constraintLayout){
        mainparent=constraintLayout;
    }

    public void generateFrame(Bitmap bitmap) {
        Canvas lockCanvas = this.mInputSurface.lockCanvas(null);
        try {
            lockCanvas.drawBitmap(bitmap, 0.0f, 0.0f, null);
        } finally {
            this.mInputSurface.unlockCanvasAndPost(lockCanvas);
        }
    }

    public Bitmap renderer(View view) {

        Log.i(TAG, "bitmaptime: start ");
        Bitmap createBitmap = Bitmap.createBitmap(mainparent.getWidth(), mainparent.getHeight(), Config.ARGB_8888);
        view.draw(new Canvas(createBitmap));
        Log.i(TAG, "renderer:231 height = "+mainparent.getHeight());
        Log.i(TAG, "renderer:231 width = "+mainparent.getWidth());
        Log.i(TAG, "bitmaptime: end ");
        //todo free memory if memory issues are found
        return createBitmap;
    }

    public float calcFramePercentage(int i, int i2) {
        if (i2 == 0) {
            return 0.0f;
        }
        return (float) ((i * 100) / i2);
    }

//    public void extractFramesFromVideo(Context context, String str) {
//        StringBuilder sb = new StringBuilder();
//        sb.append("extractFramesFromVideo: ");
//        sb.append(str);
//        Log.i(TAG, sb.toString());
//        StringBuilder sb2 = new StringBuilder();
//        sb2.append(Environment.getExternalStorageDirectory().getAbsolutePath());
//        sb2.append("/temp_Pixel_flow");
//        String sb3 = sb2.toString();
//        File file = new File(sb3);
//        deleteAllFilesFromDir(file);
//        file.mkdirs();
//        if (FFmpeg.getInstance(context).isSupported()) {
//            FFmpeg instance = FFmpeg.getInstance(context);
//            StringBuilder sb4 = new StringBuilder();
//            sb4.append(sb3);
//            sb4.append("/temp_img%01d.jpg");
//            instance.execute(new String[]{"-t", "10", "-i", str, "-r", "30", "-qscale:v", "5", sb4.toString()},
//                    new FFcommandExecuteResponseHandler() {
//                public void onSuccess(String str) {
//                    StringBuilder sb = new StringBuilder();
//                    sb.append("onSuccess: ");
//                    sb.append(str);
//                    Log.i(RenderEngine.TAG, sb.toString());
//                }
//
//                public void onProgress(String str) {
//                    StringBuilder sb = new StringBuilder();
//                    sb.append("onProgress: ");
//                    sb.append(str);
//                    Log.i(RenderEngine.TAG, sb.toString());
//                }
//
//                public void onFailure(String str) {
//                    StringBuilder sb = new StringBuilder();
//                    sb.append("onFailure: ");
//                    sb.append(str);
//                    Log.i(RenderEngine.TAG, sb.toString());
//                }
//
//                public void onStart() {
//                    Log.i(RenderEngine.TAG, "onStart: started");
//                }
//
//                public void onFinish() {
//                    Log.i(RenderEngine.TAG, "onFinish: finished");
//                }
//            });
//            return;
//        }
//        Helper.show(context, "FFmpeg is not supported");
//    }

    public void deleteAllFilesFromDir(File file) {
        if (file.isDirectory()) {
            for (File delete : file.listFiles()) {
                delete.delete();
            }
        }
    }
}
