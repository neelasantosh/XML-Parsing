package com.example.xmlparsing;

import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.xml.sax.Parser;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import android.app.Activity;
import android.content.pm.ApplicationInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

public class MainActivity extends Activity {

	ProgressBar progress;
	ListView listViewData;
	ArrayList<String> listData = new ArrayList<String>();
	ArrayAdapter<String> adapter;
	String xmlFilePath = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home);
		progress = (ProgressBar) findViewById(R.id.progressBar1);
		listViewData = (ListView) findViewById(R.id.listView1);

		adapter = new ArrayAdapter<String>(MainActivity.this,
				android.R.layout.simple_list_item_1, listData);
		listViewData.setAdapter(adapter);
	}// eof oncreate

	void parseXML() {
		// instantiate pull parser with file path
		String data = "";
		try {
			FileReader reader = new FileReader(xmlFilePath);
			XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
			XmlPullParser parser = factory.newPullParser();
			parser.setInput(reader);

			int tokentype = parser.next();
			while (tokentype != XmlPullParser.END_DOCUMENT) {
				tokentype = parser.next();
				if (tokentype == XmlPullParser.START_TAG) {
					if (parser.getName().equals("BOTANICAL")) {
						tokentype = parser.next();// to read to text
						if (tokentype == XmlPullParser.TEXT) {
							 data += parser.getText();
							

						}// eof if
					}// eof if
				}// eof if
				if (tokentype == XmlPullParser.START_TAG) {
					if (parser.getName().equals("PRICE")) {
						tokentype = parser.next();// to read to text
						if (tokentype == XmlPullParser.TEXT) {
							data  += parser.getText();
							listData.add(data);
							data="";
						}// eof if
					}// eof if
				}
			}// eof while
			reader.close();
			adapter.notifyDataSetChanged();

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	class LoadXMLTask extends AsyncTask<String, Void, String> {
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();

			progress.setVisibility(View.VISIBLE);

		}

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			String url = params[0];
			HttpGet getRequest = new HttpGet(url);
			HttpClient client = new DefaultHttpClient();
			String output = "";
			try {
				HttpResponse response = client.execute(getRequest);
				InputStream is = response.getEntity().getContent();
				// save i/p stream data in a file
				// create filepath
				ApplicationInfo appInfo = getApplicationInfo();
				xmlFilePath = appInfo.dataDir + "/data.xml";
				// read from input stream and write to file
				FileOutputStream fileOut = new FileOutputStream(xmlFilePath);

				while (true) {
					int ch = is.read();
					if (ch == -1)// eof data
					{
						break;

					} else {
						fileOut.write(ch);
					}
				}// eof while

				fileOut.flush();
				fileOut.close();
				is.close();

			} catch (Exception ex) {
				ex.printStackTrace();
			}
			return "done";
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			Toast.makeText(MainActivity.this, result, 5).show();
			listData.clear();
			parseXML();

		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.home_actions, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.option_refres) {

			// load xml file and save in data directory
			String xmlUrl = "http://www.w3schools.com/xml/plant_catalog.xml";
			LoadXMLTask task = new LoadXMLTask();
			task.execute(xmlUrl);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
