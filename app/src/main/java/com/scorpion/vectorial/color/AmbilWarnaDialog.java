package com.scorpion.vectorial.color;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.scorpion.vectorial.R;


public class AmbilWarnaDialog {
    final Dialog dialog;
    final OnAmbilWarnaListener listener;
    final View viewHue;
    final AmbilWarnaSquare viewSatVal;
    final ImageView viewCursor;
    final ImageView viewAlphaCursor;
    final View viewOldColor;
    final View viewNewColor;
    final View viewAlphaOverlay;
    final ImageView viewTarget;
    final ImageView viewAlphaCheckered;
    final ViewGroup viewContainer;
    final float[] currentColorHsv = new float[3];
    private final boolean supportsAlpha;
    ImageView ivclose;
    ImageView ivok, ivcancel;
    ImageView ivarrow, ivtrans;
    TextView tvtitle;
    int alpha;
    int width, height;
    LinearLayout lmain;
    LinearLayout ambilState;
    LinearLayout linearbtn;
    RelativeLayout reltitle, reltrans;
    FrameLayout fb1, fb2;
    View vline;
    Boolean transparent;
    /**
     * Create an GROWUP_AmbilWarnaDialog.
     *
     * @param context  activity context
     * @param color    current color
     * @param listener an OnAmbilWarnaListener, allowing you to get back error or OK
     */
    public AmbilWarnaDialog(final Context context, Boolean trans, int color, OnAmbilWarnaListener listener) {
        this(context, trans, color, false, listener);
    }

    /**
     * Create an GROWUP_AmbilWarnaDialog.
     *
     * @param context       activity context
     * @param color         current color
     * @param supportsAlpha whether alpha/transparency controls are enabled
     * @param listener      an OnAmbilWarnaListener, allowing you to get back error or OK
     */
    public AmbilWarnaDialog(final Context context, Boolean trans, int color, boolean supportsAlpha, OnAmbilWarnaListener listener) {
        this.supportsAlpha = supportsAlpha;
        this.listener = listener;
        transparent = trans;

        if (!supportsAlpha) { // remove alpha if not supported
            color = color | 0xff000000;
        }

        Color.colorToHSV(color, currentColorHsv);
        alpha = Color.alpha(color);

        final View view = LayoutInflater.from(context).inflate(R.layout.color_dialog, null);
        viewHue = view.findViewById(R.id.ambilwarna_viewHue);
        viewSatVal = (AmbilWarnaSquare) view.findViewById(R.id.ambilwarna_viewSatBri);
        viewCursor = (ImageView) view.findViewById(R.id.ambilwarna_cursor);
        viewOldColor = view.findViewById(R.id.ambilwarna_oldColor);
        viewNewColor = view.findViewById(R.id.ambilwarna_newColor);
        viewTarget = (ImageView) view.findViewById(R.id.ambilwarna_target);
        viewContainer = (ViewGroup) view.findViewById(R.id.ambilwarna_viewContainer);
        viewAlphaOverlay = view.findViewById(R.id.ambilwarna_overlay);
        viewAlphaCursor = (ImageView) view.findViewById(R.id.ambilwarna_alphaCursor);
        viewAlphaCheckered = (ImageView) view.findViewById(R.id.ambilwarna_alphaCheckered);
        ivok = view.findViewById(R.id.ivok);
        ivcancel = view.findViewById(R.id.ivcancel);
        ivclose = (ImageView) view.findViewById(R.id.ivclose);
        ivarrow = (ImageView) view.findViewById(R.id.ivcarrow);
        lmain = (LinearLayout) view.findViewById(R.id.lineardialog);
        reltitle = (RelativeLayout) view.findViewById(R.id.reltitle);
        tvtitle = (TextView) view.findViewById(R.id.tvtitle);
        fb1 = (FrameLayout) view.findViewById(R.id.framebox1);
        fb2 = (FrameLayout) view.findViewById(R.id.framebox2);
        ambilState = (LinearLayout) view.findViewById(R.id.ambilwarna_state);
        vline = (View) view.findViewById(R.id.viewline);
        linearbtn = (LinearLayout) view.findViewById(R.id.linearbtn);
        reltrans = (RelativeLayout) view.findViewById(R.id.reltrans);
        ivtrans = (ImageView) view.findViewById(R.id.ivtrans);

        width = context.getResources().getDisplayMetrics().widthPixels;
        height = context.getResources().getDisplayMetrics().heightPixels;

        { // hide/show alpha
            viewAlphaOverlay.setVisibility(supportsAlpha ? View.VISIBLE : View.GONE);
            viewAlphaCursor.setVisibility(supportsAlpha ? View.VISIBLE : View.GONE);
            viewAlphaCheckered.setVisibility(supportsAlpha ? View.VISIBLE : View.GONE);
        }

        viewSatVal.setHue(getHue());
        viewOldColor.setBackgroundColor(color);
        viewNewColor.setBackgroundColor(color);

        viewHue.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_MOVE
                        || event.getAction() == MotionEvent.ACTION_DOWN
                        || event.getAction() == MotionEvent.ACTION_UP) {

                    float y = event.getY();
                    if (y < 0.f) y = 0.f;
                    if (y > viewHue.getMeasuredHeight()) {
                        y = viewHue.getMeasuredHeight() - 0.001f; // to avoid jumping the cursor from bottom to top.
                    }
                    float hue = 360.f - 360.f / viewHue.getMeasuredHeight() * y;
                    if (hue == 360.f) hue = 0.f;
                    setHue(hue);

                    // update view
                    viewSatVal.setHue(getHue());
                    moveCursor();
                    viewNewColor.setBackgroundColor(getColor());
                    updateAlphaView();
                    return true;
                }
                return false;
            }
        });

        if (supportsAlpha) viewAlphaCheckered.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if ((event.getAction() == MotionEvent.ACTION_MOVE)
                        || (event.getAction() == MotionEvent.ACTION_DOWN)
                        || (event.getAction() == MotionEvent.ACTION_UP)) {

                    float y = event.getY();
                    if (y < 0.f) {
                        y = 0.f;
                    }
                    if (y > viewAlphaCheckered.getMeasuredHeight()) {
                        y = viewAlphaCheckered.getMeasuredHeight() - 0.001f; // to avoid jumping the cursor from bottom to top.
                    }
                    final int a = Math.round(255.f - ((255.f / viewAlphaCheckered.getMeasuredHeight()) * y));
                    AmbilWarnaDialog.this.setAlpha(a);

                    // update view
                    moveAlphaCursor();
                    int col = AmbilWarnaDialog.this.getColor();
                    int c = a << 24 | col & 0x00ffffff;
                    viewNewColor.setBackgroundColor(c);
                    return true;
                }
                return false;
            }
        });
        viewSatVal.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_MOVE
                        || event.getAction() == MotionEvent.ACTION_DOWN
                        || event.getAction() == MotionEvent.ACTION_UP) {

                    float x = event.getX(); // touch event are in dp units.
                    float y = event.getY();

                    if (x < 0.f) x = 0.f;
                    if (x > viewSatVal.getMeasuredWidth()) x = viewSatVal.getMeasuredWidth();
                    if (y < 0.f) y = 0.f;
                    if (y > viewSatVal.getMeasuredHeight()) y = viewSatVal.getMeasuredHeight();

                    setSat(1.f / viewSatVal.getMeasuredWidth() * x);
                    setVal(1.f - (1.f / viewSatVal.getMeasuredHeight() * y));

                    // update view
                    moveTarget();
                    viewNewColor.setBackgroundColor(getColor());

                    return true;
                }
                return false;
            }
        });

        dialog = new Dialog(context, R.style.Theme_Transparent);
        dialog.setContentView(view);

        ivtrans.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (AmbilWarnaDialog.this.listener != null) {
                    AmbilWarnaDialog.this.listener.onOk(AmbilWarnaDialog.this, Color.TRANSPARENT);
                }

            }
        });

        ivok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (AmbilWarnaDialog.this.listener != null) {
                    AmbilWarnaDialog.this.listener.onOk(AmbilWarnaDialog.this, getColor());
                }

            }
        });

        ivclose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (AmbilWarnaDialog.this.listener != null) {
                    AmbilWarnaDialog.this.listener.onCancel(AmbilWarnaDialog.this);
                }

            }
        });

        ivcancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (AmbilWarnaDialog.this.listener != null) {
                    AmbilWarnaDialog.this.listener.onCancel(AmbilWarnaDialog.this);
                }

            }
        });

        forUI();

        // move cursor & target on first draw
        ViewTreeObserver vto = view.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                moveCursor();
                if (AmbilWarnaDialog.this.supportsAlpha) moveAlphaCursor();
                moveTarget();
                if (AmbilWarnaDialog.this.supportsAlpha) updateAlphaView();
                view.getViewTreeObserver().removeGlobalOnLayoutListener(this);
            }
        });
    }

    private void forUI() {

        int main_height = 1376;
        int btn_top = 145;
        int btn_side = 60;

        if (!transparent) {

            vline.setVisibility(View.GONE);
            reltrans.setVisibility(View.GONE);
            lmain.setBackgroundResource(R.drawable.dialog_color_small);

            main_height = 1059;
            btn_top = 45;
            btn_side = 50;

        }

        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams
                (width * 969 / 1080, height * main_height / 1920);

        param.gravity = Gravity.CENTER;

        lmain.setLayoutParams(param);

        LinearLayout.LayoutParams paramt = new LinearLayout.LayoutParams
                (width * 969 / 1080, height * 161 / 1920);

//        reltitle.setLayoutParams(paramt);

//        reltitle.setPadding(0, height * 40 / 1920, width * 40 / 1080, 0);

        RelativeLayout.LayoutParams paramtv = new RelativeLayout.LayoutParams
                (LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        paramtv.setMargins(0, height * 30 / 1920,
                0, 0);

        paramtv.addRule(RelativeLayout.CENTER_HORIZONTAL);

//        tvtitle.setLayoutParams(paramtv);

        RelativeLayout.LayoutParams param1 = new RelativeLayout.LayoutParams
                (width * 90 / 1080, height * 90 / 1920);

        param1.setMargins(0, height * 20 / 1920,
                width * 25 / 1080, 0);

        param1.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);

//        ivclose.setLayoutParams(param1);
//
//        ivclose.setPadding(0, 0, 0, 0);


        RelativeLayout.LayoutParams parambri = new RelativeLayout.LayoutParams
                (width * 580 / 1080, height * 400 / 1920);

        parambri.setMargins(0, height * 30 / 1920,
                width * 30 / 1080, 0);

        viewSatVal.setLayoutParams(parambri);

        RelativeLayout.LayoutParams paramhue = new RelativeLayout.LayoutParams
                (width * 115 / 1080, height * 400 / 1920);

        paramhue.setMargins(width * 30 / 1080, height * 30 / 1920,
                0, 0);

        paramhue.addRule(RelativeLayout.RIGHT_OF, R.id.ambilwarna_viewSatBri);

        viewHue.setLayoutParams(paramhue);


        RelativeLayout.LayoutParams paramstate = new RelativeLayout.LayoutParams
                (LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        paramstate.setMargins(0, height * 60 / 1920,
                0, height * 60 / 1920);

        paramstate.addRule(RelativeLayout.CENTER_HORIZONTAL);
        paramstate.addRule(RelativeLayout.BELOW, R.id.ambilwarna_viewSatBri);

        ambilState.setLayoutParams(paramstate);


        LinearLayout.LayoutParams paramb1 = new LinearLayout.LayoutParams
                (width * 190 / 1080, height * 90 / 1920);

//        paramb1.setMargins(width * 80 / 1080, 0,
//                width * 80 / 1080, 0);

        fb1.setLayoutParams(paramb1);
        fb2.setLayoutParams(paramb1);

        LinearLayout.LayoutParams parama = new LinearLayout.LayoutParams
                (width * 100 / 1080, height * 49 / 1920);

        parama.setMargins(width * 60 / 1080, 0,
                width * 60 / 1080, 0);

        ivarrow.setLayoutParams(parama);

        LinearLayout.LayoutParams paraml = new LinearLayout.LayoutParams
                (width * 820 / 1080, height * 3 / 1920);

//        paraml.setMargins(width * 70 / 1080, 0,
//                width * 100 / 1080, 0);
        paraml.gravity = Gravity.CENTER_HORIZONTAL;

        paraml.setMargins(0, 0,
                0, height * 60 / 1920);

        vline.setLayoutParams(paraml);


        LinearLayout.LayoutParams paramtr = new LinearLayout.LayoutParams
                (width * 810 / 1080, height * 140 / 1920);

        paramtr.gravity = Gravity.CENTER_HORIZONTAL;

        reltrans.setLayoutParams(paramtr);

        RelativeLayout.LayoutParams paramtrans = new RelativeLayout.LayoutParams
                (width * 106 / 1080, width * 106 / 1080);

        paramtrans.setMargins(0, 0,
                width * 20 / 1080, 0);

        paramtrans.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        paramtrans.addRule(RelativeLayout.CENTER_VERTICAL);

        ivtrans.setLayoutParams(paramtrans);


        LinearLayout.LayoutParams parambl = new LinearLayout.LayoutParams
                (LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        parambl.setMargins(width * btn_side / 1080, height * btn_top / 1920,
                width * btn_side / 1080, 0);
        parambl.gravity = Gravity.CENTER_VERTICAL;

        linearbtn.setLayoutParams(parambl);

        LinearLayout.LayoutParams parambtn = new LinearLayout.LayoutParams
                (width * 316 / 1080, height * 116 / 1920);

//        parambtn.setMargins(width * 70 / 1080, 0,
//                width * 100 / 1080, 0);
        parambtn.weight = 1;

//        ivok.setLayoutParams(parambtn);
//        ivcancel.setLayoutParams(parambtn);

    }

    protected void moveCursor() {
        float y = viewHue.getMeasuredHeight() - (getHue() * viewHue.getMeasuredHeight() / 360.f);
        if (y == viewHue.getMeasuredHeight()) y = 0.f;
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) viewCursor.getLayoutParams();
        layoutParams.leftMargin = (int) (viewHue.getLeft() - Math.floor(viewCursor.getMeasuredWidth() / 2) - viewContainer.getPaddingLeft());
        layoutParams.topMargin = (int) (viewHue.getTop() + y - Math.floor(viewCursor.getMeasuredHeight() / 2) - viewContainer.getPaddingTop());
        viewCursor.setLayoutParams(layoutParams);
    }

    protected void moveTarget() {
        float x = getSat() * viewSatVal.getMeasuredWidth();
        float y = (1.f - getVal()) * viewSatVal.getMeasuredHeight();
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) viewTarget.getLayoutParams();
        layoutParams.leftMargin = (int) (viewSatVal.getLeft() + x - Math.floor(viewTarget.getMeasuredWidth() / 2) - viewContainer.getPaddingLeft());
        layoutParams.topMargin = (int) (viewSatVal.getTop() + y - Math.floor(viewTarget.getMeasuredHeight() / 2) - viewContainer.getPaddingTop());
        viewTarget.setLayoutParams(layoutParams);
    }

    protected void moveAlphaCursor() {
        final int measuredHeight = this.viewAlphaCheckered.getMeasuredHeight();
        float y = measuredHeight - ((this.getAlpha() * measuredHeight) / 255.f);
        final RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) this.viewAlphaCursor.getLayoutParams();
        layoutParams.leftMargin = (int) (this.viewAlphaCheckered.getLeft() - Math.floor(this.viewAlphaCursor.getMeasuredWidth() / 2) - this.viewContainer.getPaddingLeft());
        layoutParams.topMargin = (int) ((this.viewAlphaCheckered.getTop() + y) - Math.floor(this.viewAlphaCursor.getMeasuredHeight() / 2) - this.viewContainer.getPaddingTop());

        this.viewAlphaCursor.setLayoutParams(layoutParams);
    }

    private int getColor() {
        final int argb = Color.HSVToColor(currentColorHsv);
        return alpha << 24 | (argb & 0x00ffffff);
    }

    private float getHue() {
        return currentColorHsv[0];
    }

    private void setHue(float hue) {
        currentColorHsv[0] = hue;
    }

    private float getAlpha() {
        return this.alpha;
    }

    private void setAlpha(int alpha) {
        this.alpha = alpha;
    }

    private float getSat() {
        return currentColorHsv[1];
    }

    private void setSat(float sat) {
        currentColorHsv[1] = sat;
    }

    private float getVal() {
        return currentColorHsv[2];
    }

    private void setVal(float val) {
        currentColorHsv[2] = val;
    }

    public void setTitle(String s) {
        dialog.setTitle(s);
    }

    public void show() {
        dialog.show();
    }

    public Dialog getDialog() {
        return dialog;
    }

    private void updateAlphaView() {
        final GradientDrawable gd = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, new int[]{
                Color.HSVToColor(currentColorHsv), 0x0
        });
        viewAlphaOverlay.setBackgroundDrawable(gd);
    }

    public interface OnAmbilWarnaListener {
        void onCancel(AmbilWarnaDialog dialog);

        void onOk(AmbilWarnaDialog dialog, int color);
    }
}
