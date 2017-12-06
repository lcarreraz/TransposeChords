package com.milmedios.transposechords;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.text.Html;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;

/**
 * A simple {@link Activity} that embeds an AdView.
 */
public class TransposeActivity extends Activity {

	private static final String FOLDER = "tabSong";
	String filename, data;
	int sdk = android.os.Build.VERSION.SDK_INT;
	EditText contenedor;
	TextView pitch_v, note_original, note_actual;

	String[] items;
	int flag2, pitch;
	String name, type_format;
	String files[];
	private String nuevo, first_note;
	String[] notas;

	private static final int NUMBERS_OF_NOTES = 12;
	private String chord_regex = "^[A-G](b|#)?((m(aj|in)?|M|aug|dim|sus|[2-7]|9|13)([2-7]|9|13)?)?(\\(13\\)?)?(\\/[A-G](b|#)?)?$";

	// Original
	// private String
	// chord_regex="^[A-G](b|#)?((m(aj)?|M|aug|dim|sus|[2-7]|9|13)([2-7]|9|13)?)?(\\/[A-G](b|#)?)?$"

	/** The view to show the ad. */
	private AdView adView;

	/* Your ad unit id. Replace with your actual ad unit id. */
	private static final String AD_UNIT_ID = "a150aae9c3499e9";

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		File folder = new File(Environment.getExternalStorageDirectory() + "/"
				+ FOLDER);
		boolean success = true;
		if (!folder.exists()) {
			success = folder.mkdir();
		}
		if (success) {
			// Do something on success
		} else {
			// Do something else on failure
		}

		contenedor = (EditText) this.findViewById(R.id.tab);
		pitch_v = (TextView) this.findViewById(R.id.pitch);
		note_original = (TextView) this.findViewById(R.id.note_original);
		note_actual = (TextView) this.findViewById(R.id.note_actual);
		notas = "C C# D D# E F F# G G# A A# B".split(" ");
		first_note = "C";
		initial_values();
		filename = "";

		// Create an ad.
		adView = new AdView(this);
		adView.setAdSize(AdSize.BANNER);
		adView.setAdUnitId(AD_UNIT_ID);

		// Add the AdView to the view hierarchy. The view will have no size
		// until the ad is loaded.
		LinearLayout layout = (LinearLayout) findViewById(R.id.linearLayout);
		layout.addView(adView);

		// Create an ad request. Check logcat output for the hashed device ID to
		// get test ads on a physical device.
		AdRequest adRequest = new AdRequest.Builder()
				.addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
				.addTestDevice("34ED6D1D0C2C6A82").build();

		// Start loading the ad in the background.
		adView.loadAd(adRequest);
	}

	public void initial_values() {

		name = "";
		pitch = 0;
		type_format = "new";
		flag2 = 1;

		final CharSequence cs = contenedor.getText();
		contenedor.setFocusable(false);
		pitch_v.setText("" + pitch);

		String note = first_note(cs);
		note_original.setText(note);
		note_actual.setText(note);

	}

	@SuppressLint("NewApi")
	public void onPaste(View view) {
		if (pitch % NUMBERS_OF_NOTES == 0)
			pitch = 0;
		pitch_v.setText("" + pitch);

		String pasteText;

		// TODO Auto-generated method stub
		if (sdk < android.os.Build.VERSION_CODES.HONEYCOMB) {
			android.text.ClipboardManager clipboard = (android.text.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
			pasteText = clipboard.getText().toString();
			contenedor.setText(pasteText);

		} else {

			ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
			if (clipboard.hasPrimaryClip() == true) {
				ClipData.Item item = clipboard.getPrimaryClip().getItemAt(0);
				pasteText = item.getText().toString();
				contenedor.setText(pasteText);

			} else {

				Toast.makeText(getApplicationContext(),
						getString(R.string.nPaste), Toast.LENGTH_SHORT).show();

			}
		}

		initial_values();

	}

	// visualizar en html
	public void saveChordAs() {

		data = contenedor.getText().toString();
		int len = data.length();

		if (len == 0) {
			Toastmessage(getString(R.string.file5));

		} else {

			final EditText input = new EditText(this);
			input.setHint(getString(R.string.file7));
			input.setSingleLine();
			new AlertDialog.Builder(this)
					.setTitle(getString(R.string.file6))
					.setView(input)
					.setPositiveButton("Ok",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {
									filename = input.getText().toString();
									try {
										filename = filename + ".txt";
										File myFile = new File(Environment
												.getExternalStorageDirectory()
												+ "/" + FOLDER + "/" + filename);

										if (myFile.exists()) {
											// Do somehting
											Toastmessage(getString(R.string.file1)
													+ " " + filename);
										} else {
											myFile.createNewFile();
											FileOutputStream fos = new FileOutputStream(
													myFile);

											fos.write(data.getBytes());
											fos.close();

											Toastmessage(getString(R.string.file3)
													+ " " + filename);
										}

									} catch (FileNotFoundException e) {

										e.printStackTrace();
									} catch (IOException e) {

										e.printStackTrace();
									}

								}
							})
					.setNegativeButton(getString(R.string.Cancel),
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {
									// Do nothing.
								}
							}).show();

		}

	}

	public void newChord() {
		contenedor.setFocusableInTouchMode(true);
		contenedor.setText("");
		pitch = 0;
		pitch_v.setText("" + pitch);
		type_format = "new";
		flag2 = 1;
		filename = "";
	}

	public void onEdit(View view) {
		contenedor.setFocusableInTouchMode(true);
		type_format = "new";
		flag2 = 1;
	}

	public void onLess(View view) {
		contenedor.setFocusable(false);
		pitch--;
		if (pitch % NUMBERS_OF_NOTES == 0)
			pitch = 0;
		proccess(-1);
		pitch_v.setText("" + pitch);
		final CharSequence cs = contenedor.getText();
		note_actual.setText(first_note(cs));
	}

	public void onMore(View view) {
		contenedor.setFocusable(false);
		pitch++;
		if (pitch % NUMBERS_OF_NOTES == 0)
			pitch = 0;
		proccess(1);
		pitch_v.setText("" + (pitch));
		final CharSequence cs = contenedor.getText();
		note_actual.setText(first_note(cs));
	}

	public void onReset(View view) {
		contenedor.setFocusable(false);
		pitch = 0;

		final CharSequence no = note_original.getText();
		final CharSequence na = note_actual.getText();

		String original_note = first_note(no);
		String actual_note = first_note(na);

		int index_no = getIndiceArray(notas, original_note);
		int index_na = getIndiceArray(notas, actual_note);

		proccess(index_no - index_na);
		pitch_v.setText("" + (pitch));
		final CharSequence cs = contenedor.getText();
		note_actual.setText(first_note(cs));
	}

	public void onMirar(View view) {

		String tab = Html.toHtml(contenedor.getText());
		Intent intent = new Intent(this, webViewActivity.class);
		Bundle b = new Bundle();
		b.putString("tab", tab);
		intent.putExtras(b);
		startActivity(intent);

	}

	public int getIndiceArray(String[] arr, String cad) {

		int index = -1;
		for (int i = 0; i < arr.length; i++) {
			if (arr[i] == null ? cad == null : arr[i].equals(cad)) {
				index = i;
				break;
			}
		}
		return index;
	}

	public String extract_note(String chord) {

		Pattern patron = Pattern.compile("([A-G](b|#)?)");
		Matcher matcher = patron.matcher(chord);
		matcher.find();
		return matcher.group(1);
	}

	public String sufij_note(String chord) {

		String[] f = chord.split("([A-G](b|#)?)");

		if (f.length == 0) {
			return "";
		} else {
			return f[1];
		}

	}

	public void proccess(int pitch) {

		final CharSequence cs = contenedor.getText();

		if ((type_format == "new") && (flag2 == 1)) {
			String note = first_note(cs);
			note_original.setText(note);
			note_actual.setText(note);
			flag2 = 2;
		}

		String[] linea = cs.toString().split("\n");
		String[] vals;

		Pattern pattern = Pattern.compile(chord_regex);

		String new_tab = "";

		for (int j = 0; j < linea.length; j++) {
			nuevo = "";
			vals = linea[j].split(" ");
			for (int i = 0; i < vals.length; i++) {
				Matcher matcher = pattern.matcher(vals[i].toString());

				if (matcher.find()
						&& (pattern.matcher(vals[0].toString()).find() || pattern
								.matcher(vals[vals.length - 1].toString())
								.find())) {
					nuevo = nuevo
							+ transpose(extract_note(vals[i].toString()), pitch)
							+ sufij_note(vals[i].toString()) + " ";
				} else {
					nuevo = nuevo + vals[i].toString() + " ";
				}
			}
			new_tab = new_tab + nuevo + "\n";
		}
		contenedor.setText(new_tab);

	}

	private String transpose(String string, int pitch) {
		// TODO Auto-generated method stub

		int nueva = getIndiceArray(notas, string);
		int valor;
		if (((nueva + pitch) % NUMBERS_OF_NOTES) < 0) {
			valor = NUMBERS_OF_NOTES + (nueva + pitch) % NUMBERS_OF_NOTES;
		} else {
			valor = (nueva + pitch) % NUMBERS_OF_NOTES;
		}
		return notas[valor];
	}

	private String first_note(CharSequence cs) {

		String[] linea = cs.toString().split("\n");
		String[] vals;
		int a = 1;
		Pattern pattern = Pattern.compile(chord_regex);
		int j = 0;
		int i;

		while (j < linea.length && a == 1) {
			vals = linea[j].split(" ");
			i = 0;
			while (i < vals.length && a == 1) {
				Matcher matcher = pattern.matcher(vals[i].toString());
				if (matcher.find()) {
					first_note = extract_note(vals[i].toString());
					a = 2;
				}
				i = i + 1;
			}
			j = j + 1;
		}
		return first_note;
	}

	void Toastmessage(String msg) {
		Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
	}

	public void openChord() {

		File f2 = new File(Environment.getExternalStorageDirectory() + "/"
				+ FOLDER);

		if (f2.isDirectory()) {
			files = f2.list();
		}

		items = new String[] {};

		items = files;

		Arrays.sort(items);

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(getString(R.string.file8));
		builder.setItems(items, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int item) {

				StringBuilder salida = new StringBuilder();

				try {

					File f = new File(Environment.getExternalStorageDirectory()
							+ "/" + FOLDER + "/" + items[item]);

					filename = items[item].toString();

					FileInputStream fileIS = new FileInputStream(f);
					BufferedReader buf = new BufferedReader(
							new InputStreamReader(fileIS, "ISO-8859-1"));
					String readString = new String();
					while ((readString = buf.readLine()) != null) {
						salida.append(readString);
						salida.append('\n');
					}

				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}

				contenedor.setText(salida);
				initial_values();

			}
		});
		AlertDialog alert = builder.create();
		alert.show();

	}

	// visualizar en html
	public void onFile(View view) {

		final CharSequence[] items = { getString(R.string.newFile),
				getString(R.string.save), getString(R.string.saveAs),
				getString(R.string.openFile) };

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(getString(R.string.file));
		builder.setItems(items, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int item) {

				switch (item) {
				case 0:
					newChord();
					initial_values();
					break;
				case 1:
					saveChord();
					initial_values();
					break;
				case 2:
					saveChordAs();
					initial_values();
					break;
				case 3:
					openChord();
					initial_values();
					break;
				default:
					break;
				}

			}
		});
		AlertDialog alert = builder.create();
		alert.show();


	}

	// visualizar en html
	public void saveChord() {

		if (!(filename.equals(""))) {
			data = contenedor.getText().toString();
			int len = data.length();

			if (len == 0) {
				Toastmessage(getString(R.string.file5));

			} else {

				try {
					File myFile = new File(
							Environment.getExternalStorageDirectory() + "/"
									+ FOLDER + "/" + filename);

					if (myFile.exists()) {

						myFile.createNewFile();
						FileOutputStream fos = new FileOutputStream(myFile);

						fos.write(data.getBytes());
						fos.close();

						Toastmessage(getString(R.string.file4) + " " + filename);

					} else {
						saveChordAs();
					}

					// load_bt.setVisibility(View.VISIBLE);

				} catch (FileNotFoundException e) {

					e.printStackTrace();
				} catch (IOException e) {

					e.printStackTrace();
				}

			}
		} else {

			saveChordAs();

		}

	}

	@Override
	public void onResume() {
		super.onResume();
		if (adView != null) {
			adView.resume();
		}
	}

	@Override
	public void onPause() {
		if (adView != null) {
			adView.pause();
		}
		super.onPause();
	}

	/** Called before the activity is destroyed. */
	@Override
	public void onDestroy() {
		// Destroy the AdView.
		if (adView != null) {
			adView.destroy();
		}
		super.onDestroy();
	}

}
