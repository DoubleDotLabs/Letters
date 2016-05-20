package com.doubledotlabs.letters;

import android.content.Context;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;

import java.io.IOException;
import java.util.List;

public class ImagePreview extends ViewGroup implements SurfaceHolder.Callback {
    SurfaceView sv;
    SurfaceHolder h;
    Camera.Size previewSize;
    List<Camera.Size> supportedSize;
    Camera camera;

    public ImagePreview(Context context) {
        super(context);
    }

    public ImagePreview(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public ImagePreview(Context context, AttributeSet attributeSet, int something) {
        super(context, attributeSet, something);
    }

    public ImagePreview(Context context, SurfaceView sv) {
        super(context);
        this.sv = sv;

        h = sv.getHolder();
        h.addCallback(this);
        h.setType(SurfaceHolder.SURFACE_TYPE_NORMAL);
    }

    public void setCamera(Camera camera) {
        this.camera = camera;
        if (camera != null) {
            supportedSize = camera.getParameters().getSupportedPreviewSizes();
            requestLayout();

            Camera.Parameters params = camera.getParameters();

            List<String> focusModes = params.getSupportedFocusModes();
            if (focusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO)) {
                params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
                camera.setParameters(params);
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int width = resolveSize(getSuggestedMinimumWidth(), widthMeasureSpec);
        final int height = resolveSize(getSuggestedMinimumHeight(), heightMeasureSpec);
        setMeasuredDimension(width, height);

        if (supportedSize != null) {
            previewSize = getOptimalPreviewSize(supportedSize, width, height);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (changed && getChildCount() > 0) {
            final View child = getChildAt(0);

            final int width = r - l;
            final int height = b - t;

            int pWidth = width;
            int pHeight = height;
            if (previewSize != null) {
                pWidth = previewSize.width;
                pHeight = previewSize.height;
            }

            if (width * pHeight > height * pWidth) {
                final int scaledChildWidth = pWidth * height / pHeight;
                child.layout((width - scaledChildWidth) / 2, 0,
                        (width + scaledChildWidth) / 2, height);
            } else {
                final int scaledChildHeight = pHeight * width / pWidth;
                child.layout(0, (height - scaledChildHeight) / 2,
                        width, (height + scaledChildHeight) / 2);
            }
        }
    }

    public void surfaceCreated(SurfaceHolder holder) {
        try {
            if (camera != null) {
                camera.setPreviewDisplay(holder);
            }
        } catch (IOException ignored) {
        }
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        if (camera != null) {
            camera.stopPreview();
        }
    }


    private Camera.Size getOptimalPreviewSize(List<Camera.Size> sizes, int w, int h) {
        final double ASPECT_TOLERANCE = 0.1;
        double targetRatio = (double) w / h;
        if (sizes == null) return null;

        Camera.Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;

        for (Camera.Size size : sizes) {
            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) continue;
            if (Math.abs(size.height - h) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - h);
            }
        }

        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Camera.Size size : sizes) {
                if (Math.abs(size.height - h) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - h);
                }
            }
        }
        return optimalSize;
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        if(camera != null) {
            Camera.Parameters parameters = camera.getParameters();
            parameters.setPreviewSize(previewSize.width, previewSize.height);
            requestLayout();

            camera.setParameters(parameters);
            camera.startPreview();
        }
    }

}

