package jp.co.nextninja.hs8;

import java.util.HashMap;

import jp.co.nextninja.billing.util.IabHelper;
import jp.co.nextninja.billing.util.IabResult;
import jp.co.nextninja.billing.util.Inventory;
import jp.co.nextninja.billing.util.Purchase;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.webkit.ConsoleMessage;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gcm.GCMRegistrar;



@SuppressLint({ "SetJavaScriptEnabled", "JavascriptInterface" })
public class MainActivity extends AbstractActivity {

//    private static final String TAG = null;
    private ProgressDialog progress;
    private String uId;

    private static final String SKU_PREMIUM = "";
    private static final String SKU_PREMIUM_SUBSCRIPTION = "";
    private static final int REQUEST_CODE_PURCHASE_PREMIUM = 10001;


    private boolean mIsPremium = false;
    private boolean mIsSubscriber = false;

    private String base64EncodedPublicKey;
    private IabHelper mBillingHelper = null;

    private WebView wv;

    final Activity _this = this;

//    private IInAppBillingService billingService = null;
//    private ServiceConnection serviceConnextion = null;

//     private static final String SENDER_ID = "1078258322280";

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        setUrlBase();
        final String loadurl = url_base;

        super.onCreate(savedInstanceState);

        // タイトルバーを削除する
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.main);

        base64EncodedPublicKey = getString(R.string.public_key);
        mBillingHelper = new IabHelper(this, base64EncodedPublicKey);
        // 製品版の場合falseに
        mBillingHelper.enableDebugLogging(true);
        mBillingHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            @Override
            public void onIabSetupFinished(IabResult result) {
                if (!result.isSuccess()) {
                    // Log.d(TAG, "Problem setting up In-app Billing: " +
                    // result);
                }
                // Hooray, IAB is fully set up!
            }
        });

        final Resources resource = getResources();

        progress = new ProgressDialog(this);
        progress.setMessage(resource.getString(R.string.msg_nowloading));
        progress.setIndeterminate(false);
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setCancelable(false);

        final LinearLayout al = new LinearLayout(this);
        al.setOrientation(LinearLayout.VERTICAL);

        wv = (WebView) findViewById(R.id.wv);
        wv.setVerticalScrollbarOverlay(true);
        wv.getSettings().setJavaScriptEnabled(true);
        wv.getSettings().setLoadWithOverviewMode(true);
        wv.getSettings().setUseWideViewPort(true);
        wv.setWebViewClient(new WebViewClient() {

            private String loginCookie = "";

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return false;
            }

            // ページ読み込み開始時の処理
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                Toast.makeText(MainActivity.this, R.string.msg_nowloading,
                        Toast.LENGTH_SHORT).show();
            }

            // ページ読み込みエラー時の処理
            @Override
            public void onReceivedError(WebView view, int errorCode,
                    String description, String url) {
                Toast.makeText(MainActivity.this, "通信エラー", Toast.LENGTH_LONG)
                        .show();
            }

            @SuppressWarnings("unused")
            public String getLoginCookie() {
                return loginCookie;
            }

            @SuppressWarnings("unused")
            public void setLoginCookie(String loginCookie) {
                this.loginCookie = loginCookie;
            }
        });

        wv.setWebChromeClient(new WebChromeClient() {
            @Override
            public Bitmap getDefaultVideoPoster() {
                return null;
            }

            @Override
            public View getVideoLoadingProgressView() {
                return null;
            }

            @Override
            public void onShowCustomView(View view,
                    WebChromeClient.CustomViewCallback callback) {
            }

            @Override
            public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
                return true;
            }

            @Override
            public boolean onJsAlert(WebView view, String url, String message,
                    JsResult result) {
                return true;
            }

            @Override
            public boolean onJsBeforeUnload(WebView view, String url,
                    String message, JsResult result) {
                return true;
            }

            @Override
            public boolean onJsConfirm(WebView view, String url,
                    String message, JsResult result) {
                return true;
            }

            @Override
            public boolean onJsPrompt(WebView view, String url, String message,
                    String defaultValue, JsPromptResult result) {
                return true;
            }

            @Override
            public boolean onJsTimeout() {
                return true;
            }

            @Override
            public void onProgressChanged(WebView view, int progress) {
                _this.setProgress(progress * 100);
            }
        });

//        bindService(new
//                Intent("com.android.vending.billing.InAppBillingService.BIND"),
//                    serviceConnextion, Context.BIND_AUTO_CREATE);

        wv.addJavascriptInterface(this, "billing");

        setupBilling();
        SharedPreferences pref = getSharedPreferences("ID",
                Activity.MODE_PRIVATE);
        uId = pref.getString("id", "");
        extraHeaders = new HashMap<String, String>();
        extraHeaders.put("USER_HASH", uId);
//        wv.loadUrl("file:///android_asset/bill.html");
        wv.loadUrl(loadurl, extraHeaders);

        // GCM PUSH通知
        // // デバイス・マニフェストの確認
        // GCMRegistrar.checkDevice(this);
        // GCMRegistrar.checkManifest(this);
        // // 登録済かどうかを判別
        // String regId = GCMRegistrar.getRegistrationId(this);
        // if (TextUtils.isEmpty(regId)) {
        // // 未登録
        // GCMRegistrar.register(getApplicationContext(), SENDER_ID);
        // }

    }

    public void goBill(View v) {
        wv.loadUrl("file:///android_asset/html/bill.html");
    }

    public void onClickTop(View v) {
        // wv.loadUrl("javascript:document.write('JavaScriptが実行されました！');");
//        wv.loadUrl("file:///android_asset/html/bill.html");
        wv.loadUrl(url_base + getString(R.string.url_top));
    }

    public void onClickMyPage(View v) {
        wv.loadUrl(url_base + getString(R.string.url_mypage));
    }

    public void onClickGacha(View v) {
        wv.loadUrl(url_base + getString(R.string.url_gacha));
    }

    public void onClickShop(View v) {
        wv.loadUrl(url_base + getString(R.string.url_shop));
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode ==
        // KeyEvent.KEYCODE_BACK && wv.canGoBack() == true) {
        if (keyCode == KeyEvent.KEYCODE_BACK && wv.canGoBack() == true) {
            wv.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onDestroy() {
        if (wv != null) {
            wv.stopLoading();
            wv.setWebChromeClient(null);
            wv.setWebViewClient(null);
            this.unregisterForContextMenu(wv);
            // ((LinearLayout) findViewById(R.id.ll)).removeView(wv);
            wv.removeAllViews();
            wv.destroy();
            wv = null;
        }
        if (progress != null) {
            progress.dismiss();
        }
        progress = null;
        // bill.unbind();

        if (mBillingHelper != null) {
            mBillingHelper.dispose();
        }

//        if (serviceConnextion != null) {
//            unbindService(serviceConnextion);
//        }

        tearDownBilling();
        mBillingHelper = null;
        GCMRegistrar.onDestroy(this);
        super.onDestroy();
    }

    // @Override
    // public boolean onCreateOptionsMenu(Menu menu) {
    // // Inflate the menu; this adds items to the action bar if it is present.
    // getMenuInflater().inflate(R.menu.main, menu);
    // return true;
    // }

    private void setupBilling() {
        mBillingHelper = new IabHelper(this, base64EncodedPublicKey);
        mBillingHelper.enableDebugLogging(true); // Remove before release
        mBillingHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            public void onIabSetupFinished(IabResult result) {
                Log.d("billing", "Setup finished.");
                if (result.isFailure())
                    return;
                Log.d("billing", "Setup successful. Querying inventory.");
                mBillingHelper.queryInventoryAsync(mGotInventoryListener);
            }
        });
    }

    private void tearDownBilling() {
        if (mBillingHelper != null)
            mBillingHelper.dispose();
        mBillingHelper = null;
    }

    private IabHelper.QueryInventoryFinishedListener mGotInventoryListener = new IabHelper.QueryInventoryFinishedListener() {
        public void onQueryInventoryFinished(IabResult result,
                Inventory inventory) {
            Log.d("billing", "Query inventory finished.");
            if (result.isFailure())
                return;
            Log.d("billing", "Query inventory was successful.");
            mIsPremium = inventory.hasPurchase(SKU_PREMIUM);
            Log.d("billing", "User is "
                    + (mIsPremium ? "PREMIUM" : "NOT PREMIUM"));
        }
    };

    private IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
        public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
            Log.d("billing", "Purchase finished: " + result + ", purchase: "
                    + purchase);
            if (result.isFailure())
                return;
            Log.d("billing", "Purchase successful.");
            if (purchase.getSku().equals(SKU_PREMIUM)) {
                Log.d("billing",
                        "Purchase is premium upgrade. Congratulating user.");
                mIsPremium = true;
            }
            if (purchase.getSku().equals(SKU_PREMIUM_SUBSCRIPTION)) {
                Log.d("billing", "Purchase is new subscribing. Congratulating.");
                mIsSubscriber = true;
            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!mBillingHelper.handleActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    protected boolean isPremiumUser() {
        return this.mIsPremium;
    }

    protected boolean isSubscriber() {
        return this.mIsSubscriber;
    }

    public void requestBilling(View v) {
        mBillingHelper.launchPurchaseFlow(this, "hp_recover_005",
                REQUEST_CODE_PURCHASE_PREMIUM, mPurchaseFinishedListener);
    }

    protected void requestSubscriptionBilling() {
        if (mBillingHelper.subscriptionsSupported()) {
            mBillingHelper.launchSubscriptionPurchaseFlow(this,
                    SKU_PREMIUM_SUBSCRIPTION, REQUEST_CODE_PURCHASE_PREMIUM,
                    mPurchaseFinishedListener);
        }
    }

    protected void cancelSubscription() {
        startActivity(new Intent(Intent.ACTION_VIEW,
                Uri.parse("market://details?id=" + getPackageName())));
    }

}
