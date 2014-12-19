package com.example.tenki;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class MainActivity extends Activity {
	String appId = "dj0zaiZpPUFGQklVTWRvSXFpNCZzPWNvbnN1bWVyc2VjcmV0Jng9OGQ-";
	String locationURL = "http://contents.search.olp.yahooapis.jp/OpenLocalPlatform/V1/contentsGeoCoder";
	String weatherURL = "http://weather.olp.yahooapis.jp/v1/place";
	AsyncHttpClient client;
	TextView tv1, tv2;
	EditText editText;
	ListView listView;
	ArrayAdapter<String> adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		
		tv1 = (TextView)findViewById(R.id.textView1);
		tv2 = (TextView)findViewById(R.id.textView2);
		editText = (EditText)findViewById(R.id.editText1);
		listView = (ListView)findViewById(R.id.list);
		
		client = new AsyncHttpClient();
		
		adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
		listView.setAdapter(adapter);
	}
	public void getlocation(View view){
		// EditTextに入力された文字を取得し、変数に代入
		String query = editText.getText().toString();
		// RequestParamsを設定
		RequestParams requestParams = new RequestParams();
		requestParams.put("appid", appId); // appid :id(一番上で定義した変数)
		requestParams.put("output", "json"); // output:json(今回は扱いやすいようにjson)
		requestParams.put("query", query); // query :検索対象
		// WebAPIを使用する(getメソッド)
		client.get(locationURL, requestParams, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers , JSONObject response) {
				super.onSuccess(statusCode , headers, response);
				try{
					// responseから、key=“Features” のvalue(Json配列)を取り出す
					JSONArray features = response.getJSONArray("Feature");
					// features(Json配列)から、0番目の要素を取り出す
					JSONObject feature = features.getJSONObject(0);
					// featureから、key=“Geometry”のvalue(JsonObject)を取り出す
					JSONObject geometry = feature.getJSONObject("Geometry");
					// geometryから、key=“Coordinates”のvalue(String)を取り出す
					String coordinates = geometry.getString("Coordinates");
					// featureから、key=“Name”のvalue(String)を取り出してtv1にセット
					tv1.setText(feature.getString("Name"));
					// 緯度、経度の情報をtv2にセット
					tv2.setText(coordinates);
					// 緯度、経度から気象情報を取得する
					getWeatherInformation(coordinates);
					}catch(JSONException e){
					e.printStackTrace();
					}
			}
		});
	}
		
			public void getWeatherInformation(String coordinates) {
				// TODO Auto-generated method stub
				// Request Params
			RequestParams requestParams = new RequestParams();
				requestParams.put("appid", appId); // appid: id
				requestParams.put("output", "json"); // output: json
				requestParams.put("coordinates", coordinates); // coordinates: 検索座標
				// 気象情報APIに対してリクエストを送る(URL: weatherURL)
				client.get(weatherURL, requestParams, new JsonHttpResponseHandler() {
				@Override
				public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
					super.onSuccess(statusCode,  headers, response);
					try{
						JSONArray features = response.getJSONArray("Feature");
					JSONObject feature = features.getJSONObject(0);
					JSONObject property = feature.getJSONObject("Property");
					JSONObject weatherList = property.getJSONObject("WeatherList"); 
					JSONArray weathers = weatherList.getJSONArray("Weather");
					adapter.clear();
					for(int i = 0; i < weathers.length(); i++){
					JSONObject weather = weathers.getJSONObject(i);
					String date = weather.getString("Date");
					String type = weather.getString("Type");
					float strong = Float.valueOf(weather.getString("Rainfall"));
					String result = "date: " + date + // 日付
					"\ntype: " + type + // タイプ
					"\nstrong: " + strong; // 降水強度
					adapter.add(result);
					}
					}catch(JSONException e){
					e.printStackTrace();
					}
			}
		}); 
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
