package com.vipplayer;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "MainActivity";
    private static final String TEST_URL =
            "http://v.youku.com/v_show/id_XMjc2ODIyNjcxMg==.html?spm=a2h03.8164468.2069756.6";

    private WebView mWebView;
    private Spinner mUrlSpinner;
    private EditText mUrlEt;
    private Button mPlayBtn;
    private TextView mPlaceHolderTv;
    private ProgressDialog mWaitDialog;
    private GridView mVideoWebGv;

    private int mPosition;
    private boolean isLoadError;
    private List<String> mUrls = new ArrayList<>();
    private List<String> mWebsiteNames = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        initData();

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.url_spinner_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mUrlSpinner.setAdapter(adapter);
        mUrlSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mPosition = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mPosition = 0;
            }
        });

        WebViewClient webViewClient = new WebViewClient() {
            @Override
            public void onLoadResource(WebView view, String url) {
                super.onLoadResource(view, url);
                Log.d(TAG, "onLoadResource: url is " + url);
            }

            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
                Log.d(TAG, "shouldInterceptRequest: url is " + url);
                return super.shouldInterceptRequest(view, url);
            }

            @Override
            public void onReceivedError(WebView view, int errorCode,
                                        String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                Log.d(TAG, "errorCode is " + errorCode);
                Log.d(TAG, "description is " + description);
                Toast.makeText(MainActivity.this, "加载失败，请确保网络连接正常！",
                        Toast.LENGTH_SHORT).show();
                mWebView.setVisibility(View.GONE);
                mPlaceHolderTv.setVisibility(View.VISIBLE);
                isLoadError = true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if (!isLoadError) {
                    mWebView.setVisibility(View.VISIBLE);
                    mPlaceHolderTv.setVisibility(View.GONE);
                }
                mWaitDialog.dismiss();
            }
        };
        mWebView.setWebViewClient(webViewClient);
        WebChromeClient webChromeClient = new WebChromeClient();
        mWebView.setWebChromeClient(webChromeClient);
        mWebView.getSettings().setJavaScriptEnabled(true);

        WebsiteAdapter WebsiteAdapter = new WebsiteAdapter(this, mWebsiteNames);
        mVideoWebGv.setNumColumns(3);
        mVideoWebGv.setAdapter(WebsiteAdapter);
        mVideoWebGv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String websiteUrl = mWebsiteNames.get(position).split(MyConstants.SEPARATOR)[1];
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(websiteUrl));
                startActivity(intent);
            }
        });
    }

    private void initData() {
        //顺序不能变，应该和spinner的adapter的数组的顺序一致。
        mUrls.add(getString(R.string.num1));
        mUrls.add(getString(R.string.num2));
        mUrls.add(getString(R.string.num3));
        mUrls.add(getString(R.string.num4));
        mUrls.add(getString(R.string.num5));
        mUrls.add(getString(R.string.num6));
        mUrls.add(getString(R.string.num7));

        mWebsiteNames.add("优酷" + MyConstants.SEPARATOR + "http://www.youku.com");
        mWebsiteNames.add("爱奇艺" + MyConstants.SEPARATOR + "http://www.iqiyi.com");
        mWebsiteNames.add("腾讯" + MyConstants.SEPARATOR + "https://v.qq.com");
        mWebsiteNames.add("乐视" + MyConstants.SEPARATOR + "http://www.le.com");
        mWebsiteNames.add("搜狐" + MyConstants.SEPARATOR + "http://tv.sohu.com");
        mWebsiteNames.add("土豆" + MyConstants.SEPARATOR + "http://www.tudou.com");
    }

    private void initView() {
        mWebView = (WebView) findViewById(R.id.player_web_view_main_activity);
        mUrlSpinner = (Spinner) findViewById(R.id.player_url_spinner_main_activity);
        mUrlEt = (EditText) findViewById(R.id.player_url_et_main_activity);
        mPlayBtn = (Button) findViewById(R.id.play_btn_main_activity);
        mPlayBtn.setOnClickListener(this);
        mPlaceHolderTv = (TextView) findViewById(R.id.placeholder_tv_main_activity);
        mWaitDialog = new ProgressDialog(this);
        mVideoWebGv = (GridView) findViewById(R.id.video_url_gv_main_activity);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.play_btn_main_activity:
                playVideo();
                break;
            default:
                break;
        }
    }

    private void playVideo() {
        String videoUrl = mUrlEt.getText().toString().trim();
        if (TextUtils.isEmpty(videoUrl)) {
            Toast.makeText(this, "url 不允许为空", Toast.LENGTH_SHORT).show();
            return;
        }
        isLoadError = false;
        String prefix = mUrls.get(mPosition);
        String realUrl = prefix + videoUrl;
        mWebView.loadUrl(realUrl);
        mWaitDialog.setCancelable(false);
        mWaitDialog.setMessage("稍等");
        mWaitDialog.show();
        dismissKeyBoard();
    }

    private void dismissKeyBoard() {
        InputMethodManager manager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        if (manager.isActive(mUrlEt)) {
            manager.hideSoftInputFromWindow(mUrlEt.getWindowToken(), 0);
        }
    }
}
