package jp.co.nextninja.billing.util;

import java.util.HashSet;
import java.util.Set;

import jp.co.nextninja.hs8.AbstractActivity;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

public class Bill extends AbstractActivity {
//    private static final String TAG = "Bill";
//    private static final String DB_INITIALIZED = "db_initialized";

    public static final int ERROR_CANCELED = -1;
    public static final int ERROR_OK = 0;
    public static final int ERROR_CANNOT_CONNECT = 1;
    public static final int ERROR_BILLING_NOT_SUPPORTED = 2;
    public static final int ERROR_WAITING = 99;
    public static final int ERROR_ERROR = 100;

    private static final String SKU_PREMIUM = "";
    private static final String SKU_PREMIUM_SUBSCRIPTION = "";
    private static final int REQUEST_CODE_PURCHASE_PREMIUM = 10001;

    public Activity context;
    public BillCallback callback;
    public boolean recievedStat;
    public Set<String> mOwnedItems;
    private String mRecentItem;

    private boolean mIsPremium = false;
    private boolean mIsSubscriber = false;

    public Bill(Activity context,BillCallback callback) {
        this.context = context;
        this.callback = callback;
        this.recievedStat = false;
        this.mOwnedItems = new HashSet<String>();

    }

    public int billItem(String productId, String developerPayload) {
//        if (!mBillingService.checkBillingSupported()) {
//            return ERROR_BILLING_NOT_SUPPORTED;
//        }
//        if (!mBillingService.requestPurchase(productId, developerPayload)) {
//            return ERROR_CANNOT_CONNECT;
//        }

        return ERROR_OK;
    }

    public int getBuyedItem(Set<String> item) {
        if(recievedStat){
            item.addAll(mOwnedItems);
            return ERROR_OK;
        }
        return ERROR_WAITING;
    }

    public String getRecentItem() {
        return mRecentItem;
    }

    public void setupBilling() {
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

    public void tearDownBilling() {
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

    public void requestBilling(String itemId) {
        mBillingHelper.launchPurchaseFlow(this, itemId,
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
