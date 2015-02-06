package jp.co.nextninja.hs8;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MotionEvent;
import android.widget.ImageView;

import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

public class StartActivity extends AbstractActivity {
    private String id;

    private Handler handler = new Handler();
    private Runnable splashTask = new Runnable() {
        @Override
        public void run() {
            // ネットワーク接続チェック
            ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
            // 圏外ならダイアログを出して終了
            if(isConnected(cm) == false) {
                AlertDialog.Builder builder = new AlertDialog.Builder(StartActivity.this);
                builder.setTitle("通信エラー");
                builder.setMessage("通信状態を確認の上再度おためしください。");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
                builder.show();
            } else {
                setUrlBase();
                // 圏内ならIDチェック
                id = getID();
                if(!id.equals("")) {	// IDあり
                    handler.post(nextTask);
                } else {	// IDなし
                    RequestQueue queue = Volley.newRequestQueue(StartActivity.this);
                    // ID取得処理
                    String url_auth = url_base + getString(R.string.url_auth);
                    JsonObjectRequest request = new JsonObjectRequest(Method.GET, url_auth, null,
                            new Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject jsonRoot) {
                                    try {
                                        id = jsonRoot.getString("user_hash");
                                        SharedPreferences pref = getSharedPreferences("ID", Activity.MODE_PRIVATE);
                                        Editor edit = pref.edit();
                                        edit.putString("id", id);
                                        edit.commit();
                                        handler.post(nextTask);
                                    } catch (JSONException e) {
                                    }
                                }
                            }, null);

                    queue.add(request);
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageView pushEnter = (ImageView)findViewById(R.id.ivEnter);
        pushEnter.setImageResource(R.drawable.pushenter);
//        pushEnter.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v){
//                handler.postDelayed(splashTask, 500);
//            }
//        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private Runnable nextTask = new Runnable() {
        @Override
        public void run() {
            Intent intent = new Intent(StartActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    };

    private String getID() {
        SharedPreferences pref = getSharedPreferences("ID", Activity.MODE_PRIVATE);
        String id = pref.getString("id", "");
        return id;
    }

    public static boolean isConnected(ConnectivityManager manager){
        NetworkInfo info = manager.getActiveNetworkInfo();
        if (info == null ) {
            return false;
        }
        return info.isConnected();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        handler.postDelayed(splashTask, 500);
        return true;
    }
}
