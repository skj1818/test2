package jp.co.nextninja.hs8;

import java.util.Map;

import jp.co.nextninja.billing.util.IabHelper;
import android.app.Activity;
import android.webkit.WebView;

/**
 *
 * @author yamaguchi
 */
public class AbstractActivity extends Activity {

  protected String url_base;
  protected Map<String, String> extraHeaders;
  protected WebView wv;

  public String base64EncodedPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA3DJw7tCBTkP8UR5PpJAdVQFFFVJyv6Vh/aY/r2AkMEgbumneGPhOrGkFI9uUMFKTZMw/9pv5qrnFRnnuOSsLXmzUVX0jXRPjAqHzhD8bJsvsv/MGkw6gpywco6RqikAH7z7pufZ+QxTwFvFVZpl6XdAl7+MtFMu8pB6XIjX9V6BjcroGNX0Y8pgyzEMfMyUUFPtsA0Mq2xskRynFi/W78UxAAb/l0j2qi+2JPCFs1ZBUc0A3AxDrK2yFIa3j7URFPF80vVHctqZSfZaNZ/tYL3+UkM29DfSXXtn19E/tQ9ck0bxsE9E7XMGVT4PaLOZGlYo135GEi10CzpLJ7In6/QIDAQAB";
  public IabHelper mBillingHelper = null;

  public void sendHeaderLoadUrl(String url) {
    wv.loadUrl(url, extraHeaders);
  }

  protected void setUrlBase() {
    url_base = "http://" + getString(R.string.url_base);
  }
}
