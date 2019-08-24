package com.adfendo.beta.utilities;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.text.Layout;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.adfendo.beta.R;
import com.adfendo.beta.callback.ApiClient;
import com.adfendo.beta.callback.ApiInterface;
import com.adfendo.beta.model.IpLocation;

import java.text.DecimalFormat;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class Utils {
    public static String location = ",";
    IpLocation ipLocation;

    public void getLocation() {
        new LocationInBackground().execute();
    }

    private static final String TAG = "Utils";

    @SuppressLint("StaticFieldLeak")
    private class LocationInBackground extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            ApiInterface apiInterface = ApiClient.getApiClient().create(ApiInterface.class);
            Call<IpLocation> call = apiInterface.getLocation();
            call.enqueue(new Callback<IpLocation>() {
                @Override
                public void onResponse(Call<IpLocation> call, Response<IpLocation> response) {
                    ipLocation = response.body();
                    if (ipLocation != null) {
                        if (!ipLocation.getCountryLong().isEmpty()) {
                            location = ipLocation.getCity() +","+ ipLocation.getCountryLong();
                        }
                    } else {
                        location = ",";
                    }
                }
                @Override
                public void onFailure(Call<IpLocation> call, Throwable t) {
                    Log.d(TAG, "onFailure: " + t.getMessage());
                }
            });
            return null;
        }
    }

    public static String getRoughNumber(long value) {
        if (value <= 999) {
            return String.valueOf(value);
        }
        final String[] units = new String[]{"", "K", "M", "B", "T"};
        int digitGroups = (int) (Math.log10(value) / Math.log10(1000));
        return new DecimalFormat("#,##0.#").format(value / Math.pow(1000, digitGroups)) + "" + units[digitGroups];
    }

    public static String getAgentInfo(){
       return System.getProperty("http.agent");
    }

    public void ShowInfoDialog(final Context context) {
        final Dialog dialog = new Dialog(context); // Context, this, etc.
        dialog.setContentView(R.layout.info_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        Button privacy_button = dialog.findViewById(R.id.privacy_button);
        Button visit_button = dialog.findViewById(R.id.visit_button_dialog);

        privacy_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPrivacyPolicyPopup(context);
                dialog.dismiss();
            }
        });

        visit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(context.getString(R.string.adfendo_url)));
                context.startActivity(browserIntent);
            }
        });

        dialog.show();


    }

    private void showPrivacyPolicyPopup(Context context) {
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.privacy_policy_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);
        TextView privacy_text = dialog.findViewById(R.id.privacy_text);
        privacy_text.setMovementMethod(new ScrollingMovementMethod());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            privacy_text.setJustificationMode(Layout.JUSTIFICATION_MODE_INTER_WORD);
        }
        ImageView closeDialogButton = dialog.findViewById(R.id.close_dialog_button);
        closeDialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();

    }
}
