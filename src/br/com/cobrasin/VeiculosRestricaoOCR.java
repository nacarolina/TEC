package br.com.cobrasin;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;

import org.opencv.imgproc.Imgproc;

import org.opencv.objdetect.CascadeClassifier;

import com.googlecode.tesseract.android.TessBaseAPI;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.AudioManager;
import android.media.ExifInterface;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

public class VeiculosRestricaoOCR extends Activity implements
		CvCameraViewListener2 {

	private ProgressDialog progress;

	List<Point> platePointList;
	TextView foundNumberPlate;
	private File mCascadeFile;
	private CascadeClassifier mJavaDetector;
	MatOfRect plates;
	private float mRelativePlateSize = 0.2f;
	private int mAbsolutePlateSize = 0;
	public static final String DATA_PATH = Environment
			.getExternalStorageDirectory().toString() + "/OCRInfortronics/";
	public static final String lang = "sti";
	private final static String TAG = "OpenCvTest::MainActivity";
	protected String _path;
	private Mat mRgba;
	private Mat mdetectou;
	private Mat mGray;

	private TextView lblPlacaTeste;
	private TextView lblPlaca;
	private TextView lblModelo;
	private TextView lblMarca;
	private TextView lblCor;
	private TextView lblAnoLicenciamento;

	private LinearLayout pnlDadosVeiculoOCR;

	private Handler handler = new Handler();
	boolean bBaixouDB, detectou = false;
	boolean fotografar = false;
	ProgressDialog dialog = null;
	Cursor c = null;
	String PlacaPesquisaWEB = "";
	LinearLayout pnlDetectou;
	ImageView imgPlaca, imgRetangulo, imgRetanguloEfeito;
	java.util.Date agora = new java.util.Date();
	String data1 = null;

	private ImageButton btnFechar;
	private Button btnPesquisarONOCR;
	
	private String agente = "";

	String PlacaSemTraco = "";
	String MarcaModeloDetectada = "";
	private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
		@Override
		public void onManagerConnected(int status) {
			switch (status) {
			case LoaderCallbackInterface.SUCCESS: {
				Log.i(TAG, "OpenCV loaded successfully");

				mOpenCvCameraView.setCameraIndex(0);// 0=camera de traz do
				// celular | 1= camera da
				// frente
				try {
					// Load Haar training result file from application resources
					// This file from opencv_traincascade tool.
					// Load res/cascade-europe.xml file
					InputStream is = getResources().openRawResource(
							R.raw.europerussia);

					File cascadeDir = getDir("cascade", Context.MODE_PRIVATE);
					mCascadeFile = new File(cascadeDir, "europe.xml"); // Load
																		// XML
																		// file
																		// according
																		// to
																		// R.raw.cascade
					FileOutputStream os = new FileOutputStream(mCascadeFile);

					byte[] buffer = new byte[4096];
					int bytesRead;
					while ((bytesRead = is.read(buffer)) != -1) {
						os.write(buffer, 0, bytesRead);
					}
					is.close();
					os.close();

					mJavaDetector = new CascadeClassifier(
							mCascadeFile.getAbsolutePath());
					if (mJavaDetector.empty()) {
						Log.e(TAG, "Failed to load cascade classifier");
						mJavaDetector = null;
					} else
						Log.i(TAG, "Loaded cascade classifier from "
								+ mCascadeFile.getAbsolutePath());

					cascadeDir.delete();

				} catch (IOException e) {
					e.printStackTrace();
					Log.e(TAG, "Failed to load cascade. Exception thrown: " + e);
				}
				// mOpenCvCameraView.enableFpsMeter();
				mOpenCvCameraView.enableView();
			}
				break;
			default: {
				super.onManagerConnected(status);
			}
				break;
			}

		}
	};
	private CameraBridgeViewBase mOpenCvCameraView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.veiculos_restricao_ocr);

		agente = (String) getIntent().getSerializableExtra("agente");
		
		btnFechar = (ImageButton) findViewById(R.id.btnFecharOCR);
		lblPlacaTeste = (TextView) findViewById(R.id.lblPlaca);
		lblPlaca = (TextView) findViewById(R.id.lblPlacaOCR);
		lblModelo = (TextView) findViewById(R.id.lblModeloOCR);
		lblMarca = (TextView) findViewById(R.id.lblMarcaOCR);
		lblCor = (TextView) findViewById(R.id.lblCorOCR);
		lblAnoLicenciamento = (TextView) findViewById(R.id.lblAnoLicenciamentoOCR);

		pnlDadosVeiculoOCR = (LinearLayout) findViewById(R.id.pnlDadosVeiculoOCR);

		// pnlPesquisarON.setVisibility(View.INVISIBLE);
		pnlDadosVeiculoOCR.setVisibility(View.INVISIBLE);

		pnlDetectou = (LinearLayout) findViewById(R.id.pnlCarro);
		Button btnNovoAitOCR = (Button) findViewById(R.id.btnNovoAitOCR);
		btnPesquisarONOCR = (Button) findViewById(R.id.btnPesquisarONOCR);

		imgPlaca = (ImageView) findViewById(R.id.imgPlaca);
		imgRetangulo = (ImageView) findViewById(R.id.imgRetangulo);
		imgRetanguloEfeito = (ImageView) findViewById(R.id.imgRetanguloEfeito);
		// imgRetangulo.setVisibility(View.INVISIBLE);
		// imgRetanguloEfeito.setVisibility(View.INVISIBLE);
		btnNovoAitOCR.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent i = new Intent(VeiculosRestricaoOCR.this,
						ListaTipoAit.class);
				i.putExtra("agente", agente);
				i.putExtra("PlacaDetectada",PlacaSemTraco);
				i.putExtra("MarcaModeloDetectada", MarcaModeloDetectada);
				startActivity(i);
				finish();
			}
		});

		btnFechar.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				pnlDetectou.setVisibility(View.INVISIBLE);
				detectou = false;
			}
		});
		btnPesquisarONOCR.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				ConsultarDados(PlacaPesquisaWEB);
			}
		});
		// lblDtVencimento = (TextView) findViewById(R.id.lblDtVencimento);

		// lblCor.setVisibility(View.INVISIBLE);
		// lblModelo.setVisibility(View.INVISIBLE);
		pnlDetectou.setVisibility(View.INVISIBLE);
		// lblDtVencimento.setVisibility(View.INVISIBLE);
		String[] paths = new String[] { DATA_PATH, DATA_PATH + "tessdata/" };

		for (String path : paths) {
			File dir = new File(path);
			if (!dir.exists()) {
				if (!dir.mkdirs()) {
					Log.v(TAG, "ERROR: Creation of directory " + path
							+ " on sdcard failed");
					return;
				} else {
					Log.v(TAG, "Created directory " + path + " on sdcard");
				}
			}

		}

		// lang.traineddata file with the app (in assets folder)
		// You can get them at:
		// http://code.google.com/p/tesseract-ocr/downloads/list
		// This area needs work and optimization
		if (!(new File(DATA_PATH + "tessdata/" + lang + ".traineddata"))
				.exists()) {
			try {

				AssetManager assetManager = getAssets();
				InputStream in = assetManager.open("tessdata/" + lang
						+ ".traineddata");
				// GZIPInputStream gin = new GZIPInputStream(in);
				OutputStream out = new FileOutputStream(DATA_PATH + "tessdata/"
						+ lang + ".traineddata");

				// Transfer bytes from in to out
				byte[] buf = new byte[1024];
				int len;
				// while ((lenf = gin.read(buff)) > 0) {
				while ((len = in.read(buf)) > 0) {
					out.write(buf, 0, len);
				}
				in.close();
				// gin.close();
				out.close();

				Log.v(TAG, "Copied " + lang + " traineddata");
			} catch (IOException e) {
				Log.e(TAG,
						"Was unable to copy " + lang + " traineddata "
								+ e.toString());
			}
		}

		_path = DATA_PATH + "/";

		mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.fd_activity_surface_view);
		mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
		mOpenCvCameraView.setCvCameraViewListener(this);
		platePointList = new ArrayList<Point>();

		/*
		 * File folder = new File("mnt/sdcard" +
		 * "/db_BlitzEletronica/PlacaOriginal"); if (!folder.exists()) {
		 * folder.mkdir(); }
		 */
		/*
		 * File folder = new File("mnt/sdcard" + "/db_BlitzEletronica"); if
		 * (!folder.exists()) { folder.mkdir(); } folder = new File("mnt/sdcard"
		 * + "/db_BlitzEletronica/PlacaNormal"); if (!folder.exists()) {
		 * folder.mkdir(); } folder = new File("mnt/sdcard" +
		 * "/db_BlitzEletronica/PlacaEfeito"); if (!folder.exists()) {
		 * folder.mkdir(); }
		 */

	}

	@Override
	public void onPause() {
		super.onPause();
		if (mOpenCvCameraView != null)
			mOpenCvCameraView.disableView();
	}

	public void onResume() {
		super.onResume();
		OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_7, this,
				mLoaderCallback);
		// retangulo.setOnClickListener(this);
	}

	public void onDestroy() {
		super.onDestroy();
		// if (mOpenCvCameraView != null) {
		mOpenCvCameraView.disableView();
		// }
	}

	@Override
	public void onCameraViewStarted(int width, int height) {
		// TODO Auto-generated method stub
		mRgba = new Mat(height, width, CvType.CV_8UC4);
		mGray = new Mat(height, width, CvType.CV_8UC1);

	}

	@Override
	public void onCameraViewStopped() {
		// TODO Auto-generated method stub
		mRgba.release();
		mGray.release();
	}

	public static final List<Object> compactarImagem(String imagePath) {
		// Lista de objetos que sera retornado pelo metodo.
		List<Object> listReturned = new ArrayList<Object>();

		Bitmap scaledBitmap = null;

		BitmapFactory.Options options = new BitmapFactory.Options();

		// Definindo este campo como true, os pixels reais do bitmap nao sao
		// carregados na memoria. Apenas os limites sao carregados. Se
		// voce tentar usar o bitmap aqui, vocï¿½ ira obter nulo.
		options.inJustDecodeBounds = true;
		Bitmap bmp = BitmapFactory.decodeFile(imagePath, options);

		int actualHeight = options.outHeight;
		int actualWidth = options.outWidth;

		// Os valores maximo de altura e largura da imagem comprimida sao em
		// torno de 816x612
		float maxHeight = 816.0f;
		float maxWidth = 612.0f;
		float imgRatio = actualWidth / actualHeight;
		float maxRatio = maxWidth / maxHeight;

		// Valores de largura e altura sao definidas mantendo a relacao de
		// aspecto da imagem
		if (actualHeight > maxHeight || actualWidth > maxWidth) {
			if (imgRatio < maxRatio) {
				imgRatio = maxHeight / actualHeight;
				actualWidth = (int) (imgRatio * actualWidth);
				actualHeight = (int) maxHeight;
			} else if (imgRatio > maxRatio) {
				imgRatio = maxWidth / actualWidth;
				actualHeight = (int) (imgRatio * actualHeight);
				actualWidth = (int) maxWidth;
			} else {
				actualHeight = (int) maxHeight;
				actualWidth = (int) maxWidth;
			}
		}

		// Definindo o valor de inSampleSize permite carregar uma versao
		// reduzida da imagem original.
		options.inSampleSize = calculateInSampleSize(options, actualWidth,
				actualHeight);

		// inJustDecodeBounds definida como false para carregar o bitmap real.
		options.inJustDecodeBounds = false;

		// Esta opcao permite que android reivindique a memeria do bitmap se ele
		// e executado com pouca memoria.
		options.inPurgeable = true;
		options.inInputShareable = true;
		options.inTempStorage = new byte[16 * 1024];

		try {
			// Carrega o bitmap.
			bmp = BitmapFactory.decodeFile(imagePath, options);
		} catch (OutOfMemoryError exception) {
			Log.e("ProcessaImagens",
					"Estouro de memÃ³ria ao tentar decodificar caminho de arquivo para bitmap.",
					exception);
		}
		try {
			scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight,
					Bitmap.Config.ARGB_8888);
		} catch (OutOfMemoryError exception) {
			Log.e("ProcessaImagens",
					"Estouro de memÃ³ria ao tentar criar bitmap escalado.",
					exception);
		}

		float ratioX = actualWidth / (float) options.outWidth;
		float ratioY = actualHeight / (float) options.outHeight;
		float middleX = actualWidth / 2.0f;
		float middleY = actualHeight / 2.0f;

		Matrix scaleMatrix = new Matrix();
		scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);

		Canvas canvas = new Canvas(scaledBitmap);
		canvas.setMatrix(scaleMatrix);
		canvas.drawBitmap(bmp, middleX - bmp.getWidth() / 2,
				middleY - bmp.getHeight() / 2, new Paint(
						Paint.FILTER_BITMAP_FLAG));
		// canvas.drawBitmap( bmp, middleX - bmp.getWidth() , middleY -
		// bmp.getHeight() , new Paint( Paint.FILTER_BITMAP_FLAG ) );

		// Checa a rotacao da imagem e a exibe corretamente.
		ExifInterface exif;
		try {
			exif = new ExifInterface(imagePath);

			int orientation = exif.getAttributeInt(
					ExifInterface.TAG_ORIENTATION, 0);
			Log.d("EXIF", "Exif: " + orientation);

			Matrix matrix = new Matrix();

			if (orientation == 6) {
				matrix.postRotate(90);
				Log.d("EXIF", "Exif: " + orientation);
			} else if (orientation == 3) {
				matrix.postRotate(180);
				Log.d("EXIF", "Exif: " + orientation);
			} else if (orientation == 8) {
				matrix.postRotate(270);
				Log.d("EXIF", "Exif: " + orientation);
			}

			scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0,
					scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix,
					true);
		} catch (IllegalArgumentException e) {
			Log.e("ProcessaImagens",
					"Se os valores de x, y, largura e altura estÃ£o fora das dimensÃµes do bitmap fonte.",
					e);
		} catch (IOException e) {
			Log.e("ProcessaImagens",
					"Erro na leitura das tags Exif do arquivo jpeg especificado.",
					e);
		}

		// Um OutputStream de array de bytes para escrever array de bytes nele.
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		// Escreve uma versï¿½o compactada do bitmap para o fluxo de saï¿½da
		// (outputStream) especificado.
		scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream);

		// Adiciona os objetos a lista.
		listReturned.add(scaledBitmap);
		listReturned.add(outputStream.toByteArray());

		return listReturned;
	}

	private static final int calculateInSampleSize(
			BitmapFactory.Options options, int reqWidth, int reqHeight) {
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {
			final int heightRatio = Math.round((float) height
					/ (float) reqHeight);
			final int widthRatio = Math.round((float) width / (float) reqWidth);
			inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
		}
		final float totalPixels = width * height;
		final float totalReqPixelsCap = reqWidth * reqHeight * 2;
		while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
			inSampleSize++;
		}

		return inSampleSize;
	}

	private Mat RecortaPlaca(Mat image, double PlacaAltura, double PlacaLargura) {
		// double QtdCortarTopo = PlacaAltura * 0.3538;
		double QtdCortarTopo = PlacaAltura * 0.3530;
		/*
		 * Point topLeft = new Point(PlacaLargura * 0.05, QtdCortarTopo - 0.58);
		 * Point bottomRight = new Point(image.width() - (PlacaLargura * 0.05),
		 * (PlacaAltura * 0.58) + QtdCortarTopo);
		 */
		Point topLeft = new Point(PlacaLargura, QtdCortarTopo - 0.58);
		Point bottomRight = new Point(image.width() - PlacaLargura,
				(PlacaAltura * 0.58) + QtdCortarTopo);

		Mat placa = new Mat(image, new Rect(topLeft, bottomRight));
		return placa;
	}

	@Override
	public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
		// TODO Auto-generated method stub
		// mGray = inputFrame.gray();
		// mRgba = inputFrame.rgba();
		if (detectou == true) {
			return mdetectou;
		} else {
			mGray = inputFrame.gray();
			mRgba = inputFrame.rgba();

			if (mAbsolutePlateSize == 0) {
				int heightGray = mGray.rows();
				if (Math.round(heightGray * mRelativePlateSize) > 0) {
					mAbsolutePlateSize = Math.round(heightGray
							* mRelativePlateSize);
				}
			}

			// This variable is used to to store the detected plates in the
			// result
			plates = new MatOfRect();

			if (mJavaDetector != null) {
				// mJavaDetector.detectMultiScale(mGray, plates,1.1, 2, 2, new
				// Size(200,mAbsolutePlateSize), new Size());
				mJavaDetector.detectMultiScale(mGray, plates, 1.4, 2, 2,
						new Size(mAbsolutePlateSize, mAbsolutePlateSize),
						new Size());
			}
			Rect[] facesArray = plates.toArray();
			// if(facesArray.length > 0){
			for (int i = 0; i < facesArray.length; i++) {

				Point p1 = new Point(facesArray[i].x + 10, facesArray[i].y + 20);
				Point p2 = new Point(
						facesArray[i].x + facesArray[i].width - 60,
						facesArray[i].y + facesArray[i].height - 15);
				// int i = 1;
				if (detectou == true) {
					return mdetectou;
				}

				/*
				 * Point p1 = new Point(facesArray[i].x -100, facesArray[i].y +
				 * 15); Point p2 = new Point(facesArray[i].x +
				 * facesArray[i].width +100, facesArray[i].y +
				 * facesArray[i].height - 15);
				 */

				// Point p1 = new Point(facesArray[i].x, facesArray[i].y);
				// Point p2 = new Point(facesArray[i].x + facesArray[i].width,
				// facesArray[i].y + facesArray[i].height);

				final Bitmap bmpOriginal = Bitmap.createBitmap(mRgba.cols(),
						mRgba.rows(), Bitmap.Config.ARGB_8888);
				Utils.matToBitmap(mRgba, bmpOriginal);
				Core.rectangle(mRgba, p1, p2, new Scalar(250, 40, 40), 3);

				try {

					Mat imagem = new Mat(mRgba, new Rect(p1, p2));
					Mat original = new Mat(mRgba, new Rect(p1, p2));
					Imgproc.cvtColor(imagem, imagem, Imgproc.COLOR_BGR2GRAY);
					Imgproc.threshold(imagem, imagem, 0, 255,
							Imgproc.THRESH_TRUNC | Imgproc.THRESH_OTSU);
					Imgproc.threshold(imagem, imagem, 0, 255,
							Imgproc.THRESH_BINARY | Imgproc.THRESH_OTSU);
					Imgproc.erode(imagem, imagem, Imgproc
							.getStructuringElement(Imgproc.MORPH_RECT,
									new Size(2, 2)));
					Imgproc.dilate(imagem, imagem, Imgproc
							.getStructuringElement(Imgproc.MORPH_RECT,
									new Size(2, 2)));
					// imagem = RecortaPlaca(imagem, imagem.height(),
					// imagem.width());
					final Bitmap bitmap = Bitmap.createBitmap(imagem.cols(),
							imagem.rows(), Bitmap.Config.ARGB_8888);
					Utils.matToBitmap(imagem, bitmap);
					final Bitmap bmp = Bitmap.createBitmap(original.cols(),
							original.rows(), Bitmap.Config.ARGB_8888);
					Utils.matToBitmap(original, bmp);
					String plc = "";
					try {
						plc = tesseractOCR(bitmap).replace(" ", "")
								.replace(".", "").replace("-", "")
								.replace("~", "");
					} catch (Exception e) {
						// TODO: handle exception
					}

					if (!(plc.length() == 7)) {
						plc = "";
					} else {
						String numeros = plc.substring(3, 7);
						String Letras = plc.substring(0, 3);

						Letras = Letras.replace("0", "O").replace("1", "I")
								.replace("2", "Z").replace("8", "B")
								.replace("0", "D").replace("6", "G")
								.replace("0", "Q").replace("1", "L")
								.replace("5", "S");

						numeros = numeros.replace("O", "0").replace("I", "1")
								.replace("Z", "2").replace("B", "8")
								.replace("D", "0").replace("G", "6")
								.replace("Q", "0").replace("L", "1")
								.replace("S", "6");

						plc = plc + ", " + Letras.toUpperCase() + numeros;

						int i2 = 0;
						boolean conseguiu = true;
						while (i2 <= 2) {
							if (conseguiu == true) {
								try {
									String caracter = Letras.substring(i2, 1);
									int numero = Integer.parseInt(caracter);
									conseguiu = false;
								} catch (Exception e) {
								}
							}

							i2++;
						}

						if (conseguiu == true) {
							try {
								// String caracter = Letras.substring(i - 1, i);
								int numero = Integer.parseInt(numeros);
								conseguiu = true;
								detectou = true;
								final String PlacaCerta = Letras + "-"
										+ numeros;
								PlacaSemTraco = Letras + numeros;
								PlacaPesquisaWEB = PlacaSemTraco;
								VeiculosRestricaoOCR.this
										.runOnUiThread(new Runnable() {
											@Override
											public void run() {
												String Placa = "";
												String Marca = "";
												String Modelo = "";
												String Cor = "";
												String AnoLicenciamento = "";
												String IdMarca = "";
												String IdCor = "";

												ToneGenerator toneG = new ToneGenerator(
														AudioManager.STREAM_ALARM,
														100);
												toneG.startTone(
														ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD,
														500);
												lblPlaca.setVisibility(View.VISIBLE);
												lblPlaca.setText("Placa: "
														+ PlacaCerta);
												Placa = PlacaCerta;
												pnlDetectou
														.setVisibility(View.VISIBLE);

												

												try {
													SQLiteDatabase s = SQLiteDatabase
														.openDatabase(
																"mnt/sdcard/veiculos_rodizio.SDB",
																null, 0);
													Cursor cus = null;

													// Pesquisa Veiculo
													cus = s.rawQuery(
															"Select * from veiculos where Placa = '"
																	+ PlacaSemTraco
																	+ "'", null);
													if (cus.getCount() > 0) {
														while (cus.moveToNext()) {
															Placa = cus
																	.getString(0);
															IdMarca = cus
																	.getString(1);
															IdCor = cus
																	.getString(cus
																			.getColumnIndex("IdCor"));
															AnoLicenciamento = cus
																	.getString(cus
																			.getColumnIndex("AnoLicenciamento"));
															// IdTipo =
															// cus.getString(2);
															// IdEspecie =
															// cus.getString(3);
														}

														// Pesquisa
														// Marca-------------------------------------------------------------------------
														cus = null;
														cus = s.rawQuery(
																"select * from marcas where Id= ?",
																new String[] { IdMarca });
														while (cus.moveToNext()) {
															Marca = cus
																	.getString(1);
															MarcaModeloDetectada = Marca;
														}

														// Pesquisa
														// Cor----------------------------------------------------------------------------

														try {
															cus = null;
															cus = s.rawQuery(
																	"select * from cor where Id = "
																			+ IdCor,
																	null);
															while (cus
																	.moveToNext()) {
																Cor = cus
																		.getString(cus
																				.getColumnIndex("Cor"));
															}
														} catch (Exception e) {
															// TODO: handle
															// exception
														}

														// lblPlaca.setText("Placa: "
														// + Placa);
														lblMarca.setText("| Marca: "
																+ Marca);
														lblModelo
																.setText("Modelo: ");
														lblCor.setText("| Cor: "
																+ Cor);

														lblAnoLicenciamento
																.setText("Ano Licenciamento: "
																		+ AnoLicenciamento);

														pnlDadosVeiculoOCR
																.setVisibility(View.VISIBLE);

														btnPesquisarONOCR
																.setVisibility(View.INVISIBLE);


														return;
													} else {
														pnlDadosVeiculoOCR
																.setVisibility(View.INVISIBLE);

														btnPesquisarONOCR
																.setVisibility(View.VISIBLE);

														AlertDialog.Builder aviso = new AlertDialog.Builder(
																VeiculosRestricaoOCR.this);
														aviso.setIcon(android.R.drawable.ic_dialog_alert);
														aviso.setTitle("TEC");
														aviso.setMessage("Veículo não encontrado! Deseja pesquisar os dados do veículo online?");
														aviso.setNeutralButton(
																"Não", null);
														aviso.setPositiveButton(
																"Sim",
																new DialogInterface.OnClickListener() {

																	@Override
																	public void onClick(
																			DialogInterface dialog,
																			int which) {
																		// TODO
																		// Auto-generated
																		// method
																		// stub
																		ConsultarDados(PlacaPesquisaWEB);
																	}
																});

														aviso.show();
													}
													s.close();
												} catch (SQLiteException e) {
													Log.e("Erro=",
															e.getMessage());

													pnlDadosVeiculoOCR
															.setVisibility(View.INVISIBLE);

													btnPesquisarONOCR
															.setVisibility(View.VISIBLE);

													AlertDialog.Builder aviso = new AlertDialog.Builder(
															VeiculosRestricaoOCR.this);
													aviso.setIcon(android.R.drawable.ic_dialog_alert);
													aviso.setTitle("TEC");
													aviso.setMessage("Banco offline não encontrado! Deseja pesquisar os dados do veículo online?");
													aviso.setNeutralButton(
															"Não", null);
													aviso.setPositiveButton(
															"Sim",
															new DialogInterface.OnClickListener() {

																@Override
																public void onClick(
																		DialogInterface dialog,
																		int which) {
																	// TODO
																	// Auto-generated
																	// method
																	// stub
																	ConsultarDados(PlacaPesquisaWEB);
																}
															});

													aviso.show();
												
												}
												// imgPlaca.setImageBitmap(bitmap);
											}
										});
								mdetectou = mRgba;
							} catch (Exception e) {
							}
						}

					}

					final String Placa = plc;
					VeiculosRestricaoOCR.this.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							lblPlacaTeste.setVisibility(View.VISIBLE);
							lblPlacaTeste.setText(Placa);
							imgPlaca.setImageBitmap(bitmap);
							imgRetangulo.setImageBitmap(bmp);
							imgRetanguloEfeito.setImageBitmap(bitmap);
						}
					});
				} catch (Exception e) {
					// TODO: handle exception
				}
			}

			/*
			 * boolean existe = true; SimpleDateFormat formata = new
			 * SimpleDateFormat("dd/MM/yyyy HH:mm:ss:SS"); data1 =
			 * formata.format(agora); String fname = data1.replace("/",
			 * "").replace(":","").replace(" ", "")+".jpg"; File file = new
			 * File("mnt/sdcard" + "/db_BlitzEletronica/PlacaNormal/", fname);
			 * if (file.exists()) { existe = true; int i2=0; while (existe ==
			 * true) { i2++; fname = data1.replace("/",
			 * "").replace(":","").replace(" ", "")+i2+".jpg"; file = new
			 * File("mnt/sdcard" + "/db_BlitzEletronica/PlacaNormal/", fname);
			 * if (file.exists()) { existe = true; } else { existe = false; } }
			 * } try { FileOutputStream out = new FileOutputStream(file);
			 * bmp.compress(Bitmap.CompressFormat.JPEG, 90, out); out.flush();
			 * out.close();// Imgproc.boundingRect(temp_largest); } } catch
			 * (Exception e1) { // TODO: handle exception
			 * 
			 * }
			 * 
			 * file = new File("mnt/sdcard" +
			 * "/db_BlitzEletronica/PlacaEfeito/", fname); if (file.exists()) {
			 * existe = true; int i2=0; while (existe == true) { i2++; fname =
			 * data1.replace("/", "").replace(":","").replace(" ",
			 * "")+i2+".jpg"; file = new File("mnt/sdcard" +
			 * "/db_BlitzEletronica/PlacaEfeito/", fname); if (file.exists()) {
			 * existe = true; } else { existe = false; } } } try {
			 * FileOutputStream out = new FileOutputStream(file);
			 * bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
			 * out.flush(); out.close();// Imgproc.boundingRect(temp_largest); }
			 * } catch (Exception e1) { // TODO: handle exception
			 * 
			 * }
			 * 
			 * file = new File("mnt/sdcard" +
			 * "/db_BlitzEletronica/PlacaOriginal/", fname); if (file.exists())
			 * { existe = true; int i2=0; while (existe == true) { i2++; fname =
			 * data1.replace("/", "").replace(":","").replace(" ",
			 * "")+i2+".jpg"; file = new File("mnt/sdcard" +
			 * "/db_BlitzEletronica/PlacaOriginal/", fname); if (file.exists())
			 * { existe = true; } else { existe = false; } } } try {
			 * FileOutputStream out = new FileOutputStream(file);
			 * bmpOriginal.compress(Bitmap.CompressFormat.JPEG, 90, out);
			 * out.flush(); out.close();// Imgproc.boundingRect(temp_largest); }
			 * } catch (Exception e1) { // TODO: handle exception
			 * 
			 * }
			 */
			// }

			// imgSource = ProcuraSobel(mGray);

			return mRgba;
		}
		// }
	}

	private String tesseractOCR(Bitmap bitmap) {
		TessBaseAPI tess = new TessBaseAPI();
		// tess.setVariable("tessedit_char_whitelist",
		// "ABCDEFGHIJKLMNOPRSTUVWXYZ1234567890");
		tess.init(DATA_PATH, "sti");
		tess.setImage(bitmap);
		tess.setDebug(true);
		String Texto = tess.getUTF8Text();
		tess.end();
		return Texto;
	}

	public void ConsultarDados(final String Placa) {

		progress = ProgressDialog.show(VeiculosRestricaoOCR.this, "Aguarde...",
				"Consultando Dados na Web!!!", true);
		// aviso = Toast.makeText(context, "Todos aits enviados com sucesso!",
		// Toast.LENGTH_LONG);

		new Thread(new Runnable() {
			public void run() {
				// TODO Auto-generated method stub

				try {
					Thread.sleep(1000);

				} catch (InterruptedException e) {
					throw new RuntimeException(e);
				}
				try {
					WebService web = new WebService();
					JSONArray dt = web
							.ExecuteReaderQuery("Select v.Placa,(Select C.Descricao from [veiculos_rodizio].[dbo].Cores C Where C.Cor = v.CorDEN) Cor,"
									+ "(Select Ma.Descricao from [veiculos_rodizio].[dbo].MarcasDENATRAN Ma WHERE Marca = v.MarcaDEN)'Marca'"
									+ "from [veiculos_rodizio].[dbo].veiculos v where v.Placa = '"
									+ PlacaSemTraco + "'");
					if (dt != null) {
						JSONObject dr = dt.getJSONObject(0);
						final String Placa = dr.getString("Placa");
						final String Marca = dr.getString("Marca");
						MarcaModeloDetectada = Marca;
						final String Cor = dr.getString("Cor");
						String AnoLicenciamento = "";
						try {
							JSONArray dt2 = web
									.ExecuteReaderQuery("Select Convert(Date,prd_veiculo.data_licenciamento,101)AnoLicenciamento "
											+ "from [prd_multas].[dbo].veiculo prd_veiculo "
											+ "WHERE placa_letra = '"
											+ dr.getString("Placa").substring(
													0, 3)
											+ "' and placa_numero = '"
											+ dr.getString("Placa").substring(
													3, 7) + "'");
							if (dt2 != null) {
								JSONObject dr2 = dt2.getJSONObject(0);
								AnoLicenciamento = dr2
										.getString("AnoLicenciamento");
							} else {
								AnoLicenciamento = "";
							}
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							AnoLicenciamento = "";
						}

						VeiculoEncontradoWeb(Placa, Marca, Cor,
								AnoLicenciamento);
					} else {
						mostraMensagem("Veículo não encontrado!");
						AtualizaDadosTela();
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					mostraMensagem("Veículo não encontrado!");
					AtualizaDadosTela();
				}

				progress.dismiss();

			}
		}).start();
	}

	private void VeiculoEncontradoWeb(final String Placa, final String Marca,
			final String Cor, final String AnoLicenciamento) {
		handler.post(new Runnable() {

			@Override
			public void run() {

				//lblPlaca.setText("Placa: " + Placa);
				lblMarca.setText("Marca: " + Marca);
				lblModelo.setText("Modelo: ");
				lblCor.setText("Cor: " + Cor);
				lblAnoLicenciamento.setText("Ano Licenciamento: "
						+ AnoLicenciamento);

				pnlDadosVeiculoOCR.setVisibility(View.VISIBLE);

			    btnPesquisarONOCR.setVisibility(View.INVISIBLE);

			}
		});
	}

	private void AtualizaDadosTela() {
		handler.post(new Runnable() {

			@Override
			public void run() {

				pnlDadosVeiculoOCR.setVisibility(View.INVISIBLE);

				btnPesquisarONOCR.setVisibility(View.VISIBLE);

			}
		});
	}

	private void mostraMensagem(final String mensagem) {
		handler.post(new Runnable() {

			@Override
			public void run() {

				AlertDialog.Builder aviso1 = new AlertDialog.Builder(
						VeiculosRestricaoOCR.this);
				aviso1.setIcon(android.R.drawable.ic_dialog_alert);
				aviso1.setTitle("TEC");
				aviso1.setMessage(mensagem);
				aviso1.setPositiveButton("OK", null);
				aviso1.show();

			}
		});
	}
}
