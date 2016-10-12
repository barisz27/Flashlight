package com.comxa.mysqlandroidproject.flashlight.activities;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.hardware.Camera;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.comxa.mysqlandroidproject.flashlight.R;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener {

    private Camera camera;
    private Camera.Parameters camParamaters;
    private ImageView ivFlashlight;
    private RelativeLayout mRoot;
    private TextView tvTime;
    private boolean mFlash = true;
    private boolean mON = false;
    private boolean mPaused = false;
    private boolean isTmrStopped = true;

    // static variables
    private static int sTime;
    private static int bTime;
    private static boolean isTimer = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        onFinishInflate();
        checkFlashAvailableOrNot();
        updateTextView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_screenlight) {
            startActivity(new Intent(this, ScreenLight.class));
            return false;
        } else if (item.getItemId() == R.id.menu_timer) {
            showDialog();
            return false;
        } else if (item.getItemId() == R.id.menu_preferences) {
            startActivity(new Intent(MainActivity.this, Preferences.class));
            return false;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        // açma kapama butonuna tıklandığında ele alınacak olaylar...
        if (mFlash) {
            if (!mON) {
                // flaşı açıyoruz
                if (isTimer && isTmrStopped) {
                    isTmrStopped = false;
                    startTimer();
                }
                startOrStopCamera();
                uiChanges();
                mON = true;
            } else {
                // kapıyoruz
                if (isTimer && !isTmrStopped) {
                    stopTimer();
                }
                startOrStopCamera();
                uiChanges();
                mON = false;
            }

        } else {
            showAlertDialog();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mPaused = true;
        if (isTimer && !isTmrStopped) {
            stopTimer();
        }
        uiChanges();
        mON = false;
        if (camera != null && getApplicationContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)) {
            camera.release();
            camera = null;
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        mPaused = false;
        if (camera == null && getApplicationContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)) {
            camera = Camera.open();
            camParamaters = camera.getParameters();
        }
    }

    private void checkFlashAvailableOrNot() {
        if (getApplicationContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)) {
            camera = Camera.open();
            camParamaters = camera.getParameters();
            mFlash = true;
        }
    }

    private void showAlertDialog() {
        new AlertDialog.Builder(MainActivity.this)
                .setTitle(getResources().getString(R.string.dialog_error))
                .setCancelable(false)
                .setMessage(getResources().getString(R.string.dialog_no_flash))
                .setPositiveButton(getResources().getString(R.string.dialog_ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        MainActivity.this.finish();
                    }
                })
                .show();
    }

    private void editImageView() {
        Bitmap on = BitmapFactory.decodeResource(getResources(), R.drawable.flashlight_on);
        ivFlashlight.setImageBitmap(on);
    }

    private void restoreImageView() {
        Bitmap off = BitmapFactory.decodeResource(getResources(), R.drawable.flashlight_off);
        ivFlashlight.setImageBitmap(off);
    }

    private void animateColors() {
        if (!mON) {
            // ekran karartma animasyonu
            int colorFrom = Color.WHITE;
            int colorTo = getResources().getColor(R.color.gray);
            ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
            colorAnimation.setDuration(1000); // milliseconds
            colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animator) {
                    mRoot.setBackgroundColor((int) animator.getAnimatedValue());
                }
            });
            colorAnimation.start();
            // textview animasyonu
            if (sTime > 3) {
                int colorFrom1 = tvTime.getCurrentTextColor();
                int colorTo1 = Color.WHITE;
                ValueAnimator colorAnimation1 = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom1, colorTo1);
                colorAnimation1.setDuration(250); // milliseconds
                colorAnimation1.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animator) {
                        tvTime.setTextColor((int) animator.getAnimatedValue());
                    }
                });
                colorAnimation1.start();
            }
        } else {
            int colorFrom = getResources().getColor(R.color.gray);
            int colorTo = Color.WHITE;
            ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
            colorAnimation.setDuration(1000); // milliseconds
            colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

                @Override
                public void onAnimationUpdate(ValueAnimator animator) {
                    mRoot.setBackgroundColor((int) animator.getAnimatedValue());
                }

            });
            colorAnimation.start();

            if (sTime > 3) {
                // textview animasyonu
                int colorFrom1 = Color.WHITE;
                int colorTo1 = Color.BLACK;
                ValueAnimator colorAnimation1 = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom1, colorTo1);
                colorAnimation1.setDuration(250); // milliseconds
                colorAnimation1.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animator) {
                        tvTime.setTextColor((int) animator.getAnimatedValue());
                    }
                });
                colorAnimation1.start();
            }
        }
    }

    private void uiChanges() {
        // butona tıklandığında yapılacak arayüz değişimleri..
        if (!mON) {
            if (!mPaused) {
                editImageView();
                animateColors();
            }
        } else {
            restoreImageView();
            animateColors();
        }
    }

    private void startOrStopCamera() {
        if (!mON) {
            camParamaters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
            camera.setParameters(camParamaters);
            camera.startPreview();
        } else {
            camParamaters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
            camera.setParameters(camParamaters);
            camera.startPreview();
        }
    }

    private void onFinishInflate() {
        ivFlashlight = (ImageView) findViewById(R.id.ivFlashlight);
        ivFlashlight.setOnClickListener(this);
        ivFlashlight.setOnLongClickListener(this);
        mRoot = (RelativeLayout) findViewById(R.id.rlRoot);
        tvTime = (TextView) findViewById(R.id.tvTime);
    }

    private void showDialog() {
        MyDialog dialog = new MyDialog();
        dialog.show(getSupportFragmentManager(), "MyDialog");
    }

    @Override
    public boolean onLongClick(View v) {
        if (!mON)
        showDialog();
        return false;
    }

    public static class MyDialog extends DialogFragment {
        @NonNull
        @Override
        public Dialog onCreateDialog(final Bundle savedInstanceState) {
            final String[] intervals = getResources().getStringArray(R.array.timer_interval);

            final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(getResources().getString(R.string.timer_body_text));
            builder.setIcon(R.drawable.menu_timer_black);
            builder.setSingleChoiceItems(intervals, -1, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int item) {
                    if (item == 0) {
                        sTime = 3;
                        isTimer = true;
                    } else if (item == 1) {
                        sTime = 5;
                        isTimer = true;
                    } else if (item == 2) {
                        sTime = 15;
                        isTimer = true;
                    } else if (item == 3) {
                        sTime = 30;
                        isTimer = true;
                    } else if (item == 4) {
                        sTime = 60;
                        isTimer = true;
                    }
                    bTime = sTime;
                    dialogInterface.dismiss();
                }
            });
            builder.setCancelable(false);

            return builder.create();
        }
    }

    private void stopTimer() {
        isTmrStopped = true;
    }

    private void startTimer() {
        final Thread timer = new Thread() {
            public void run() {
                while (0 < sTime) {
                    try {
                        sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    if (isTmrStopped) {
                        break;
                    }
                    sTime--;
                }
            }

        };
        timer.start();
    }

    private void updateTextView() {
        Thread t = new Thread() {
            @Override
            public void run() {
                try {
                    while (!isInterrupted()) {
                        Thread.sleep(125);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // update TextView here!
                                if (isTimer) {
                                    if (0 < sTime && sTime <= 3) {
                                        tvTime.setTextColor(Color.RED);
                                    } else if (sTime == 0) {
                                        tvTime.setTextColor(Color.BLACK);
                                    }
                                    tvTime.setVisibility(View.VISIBLE);
                                    tvTime.setText(String.valueOf(sTime));
                                    if (sTime == 0 && mON) {
                                        startOrStopCamera();
                                        uiChanges();
                                        mON = false;
                                        isTimer = false;
                                        isTmrStopped = true;

                                        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
                                        if (prefs.getBoolean("PREF_SHOW_RESET_TIMER_DIALOG", true))
                                            showReTimerDialog();
                                    }
                                }
                            }
                        });
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        t.start();
    }

    private void showReTimerDialog() {
        new AlertDialog.Builder(MainActivity.this)
                .setCancelable(true)
                .setMessage(getResources().getString(R.string.timer_dialog_confirm))
                .setPositiveButton(getResources().getString(R.string.timer_dialog_confirm_yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        sTime = bTime;
                        isTimer = true;
                    }
                })
                .setNegativeButton(getResources().getString(R.string.timer_dialog_confirm_no), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .show();
    }
}