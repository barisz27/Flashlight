package com.comxa.mysqlandroidproject.flashlight.activities;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.comxa.mysqlandroidproject.flashlight.R;
import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.builder.ColorPickerClickListener;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;

public class ScreenLight extends AppCompatActivity {

    private RelativeLayout mBackground;
    private int mInıtColor;
    private boolean mON = false;
    private float curBrightnessValue;

    private ImageView ivPower;
    private ImageView ivColorPicker;
    private ImageView ivBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screen_light);
        initValues();
        initVariables();
        changeImageViewsColor();
    }

    public void powerOnClick(View view) {
        if (!mON) {
            setViewsAlpha();
            mON = true;

        } else {
            restoreViewsAlpha();
            mON = false;
        }
    }

    public void colorPickerOnClick(View view) {
        ColorPickerDialogBuilder
                .with(ScreenLight.this)
                .setTitle(getResources().getString(R.string.pallet_choose_color))
                .initialColor(mInıtColor)
                .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                .density(12)
                .setPositiveButton(getResources().getString(R.string.pallet_ok), new ColorPickerClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int selectedColor, Integer[] allColors) {
                        changeBackgroundColor(selectedColor);
                    }
                })
                .setNegativeButton(getResources().getString(R.string.pallet_cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .build()
                .show();
    }

    public void backOnClick(View view) {
        ScreenLight.this.finish();
    }

    private void changeBackgroundColor(int colorTo) {
        int colorFrom = mInıtColor;
        ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
        colorAnimation.setDuration(500); // milliseconds
        colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                mBackground.setBackgroundColor((int) animator.getAnimatedValue());
            }

        });
        colorAnimation.start();

        mInıtColor = colorTo;
    }

    // fullscreen etkinleştiriliyor
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                mBackground.setSystemUiVisibility(
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                                | View.SYSTEM_UI_FLAG_FULLSCREEN
                                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
            }
        }
    }

    private void setViewsAlpha() {
        ivPower.setImageAlpha(75);
        ivColorPicker.setImageAlpha(0);
        setFullBrightness();
    }

    private void restoreViewsAlpha() {
        ivPower.setImageAlpha(255);
        ivColorPicker.setImageAlpha(255);
        restoreFullBrightness();
    }

    private void setFullBrightness() {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.screenBrightness = 100 / 100.0f;
        getWindow().setAttributes(lp);
    }

    private void restoreFullBrightness() {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.screenBrightness = 100 / curBrightnessValue;
        getWindow().setAttributes(lp);
    }

    private void saveFirstBrightness() {
            curBrightnessValue = android.provider.Settings.System.getInt(getContentResolver(), android.provider.Settings.System.SCREEN_BRIGHTNESS,-1);
    }

    private void initVariables() {
        mBackground = (RelativeLayout) findViewById(R.id.rlBackground);
        ivPower = (ImageView) findViewById(R.id.ivPower);
        ivColorPicker = (ImageView) findViewById(R.id.ivColorPicker);
        ivBack = (ImageView) findViewById(R.id.ivBack);
    }

    private void initValues() {
        mInıtColor = Color.WHITE;
        saveFirstBrightness();
    }

    private void changeImageViewsColor() {
        Drawable myPowerIcon = getResources().getDrawable(R.drawable.power_button);
        Drawable myBackIcon = getResources().getDrawable(R.drawable.back);

        /*
        Random randomNum = new Random();

        int r = randomNum.nextInt(265);
        int g = randomNum.nextInt(265);
        int b = randomNum.nextInt(265);
        */

        ColorFilter filter = new LightingColorFilter(Color.rgb(0, 0, 0), Color.rgb(0, 0, 0));
        if (myBackIcon != null && myPowerIcon != null) {
            myPowerIcon.setColorFilter(filter);
            myBackIcon.setColorFilter(filter);
        }
        ivPower.setImageDrawable(myPowerIcon);
        ivBack.setImageDrawable(myBackIcon);
    }

}
