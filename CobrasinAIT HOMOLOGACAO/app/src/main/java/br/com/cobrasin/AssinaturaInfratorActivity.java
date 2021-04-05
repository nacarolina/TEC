package br.com.cobrasin;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.github.gcacace.signaturepad.views.SignaturePad;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class AssinaturaInfratorActivity extends Activity {

    private SignaturePad mSignaturePad;
    private Button mClearButton, mSaveButton;
    private ImageView image_signature;
    String idAit;
    File fileSignature_search = null;

    private RelativeLayout image_container, signature_container;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.assinatura_infrator);

        CreateTable();

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        idAit = String.valueOf(getIntent().getSerializableExtra("idAit"));
        mSignaturePad = (SignaturePad) findViewById(R.id.signature_pad);
        mSignaturePad.setOnSignedListener(new SignaturePad.OnSignedListener() {
            @Override
            public void onStartSigning() {
            }

            @Override
            public void onSigned() {
                mSaveButton.setEnabled(true);
                mClearButton.setEnabled(true);
            }

            @Override
            public void onClear() {
                mSaveButton.setEnabled(false);
                mClearButton.setEnabled(false);
            }
        });

        mClearButton = (Button) findViewById(R.id.clear_buttonInfrator);
        mSaveButton = (Button) findViewById(R.id.save_buttonInfrator);
        image_signature = (ImageView) findViewById(R.id.image_signatureInfrator);
        image_container = (RelativeLayout) findViewById(R.id.image_containerInfrator);
        signature_container = (RelativeLayout) findViewById(R.id.signature_pad_containerInfrator);

        mClearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder aviso = new AlertDialog.Builder(
                        AssinaturaInfratorActivity.this);
                aviso.setIcon(android.R.drawable.ic_dialog_alert);
                aviso.setTitle("Nova assinatura do Infrator");
                aviso.setMessage(" Deseja substituir a assinatura por uma nova?");
                aviso.setNeutralButton("Não", null);
                aviso.setPositiveButton("Sim",
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                // TODO Auto-generated method stub

                                fileSignature_search=null;
                                mSignaturePad.clear();
                                image_container.setVisibility(View.GONE);
                                signature_container.setVisibility(View.VISIBLE);
                            }
                        });

                aviso.show();
            }
        });

        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (fileSignature_search != null) {
                    if (fileSignature_search.exists()) {
                        Bitmap myBitmap = BitmapFactory.decodeFile(fileSignature_search.getAbsolutePath());
                        image_signature.setImageBitmap(myBitmap);
                        boolean resultado= SalvarArquivoAssinatura(fileSignature_search.getAbsolutePath());
                        if (resultado) {
                            Toast.makeText(AssinaturaInfratorActivity.this, "Assinatura salva com sucesso", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    } else
                        Toast.makeText(AssinaturaInfratorActivity.this, "Não foi possível salvar a assinatura", Toast.LENGTH_SHORT).show();
                } else {
                    Bitmap signatureBitmap = mSignaturePad.getSignatureBitmap();
                    if (addJpgSignatureToGallery(signatureBitmap)) {
                        Toast.makeText(AssinaturaInfratorActivity.this, "Assinatura salva com sucesso", Toast.LENGTH_SHORT).show();
                        finish();
                    } else
                        Toast.makeText(AssinaturaInfratorActivity.this, "Não foi possível salvar a assinatura", Toast.LENGTH_SHORT).show();

                }
            }
        });

        CarregaAssinaturaAit();
    }

    public void CarregaAssinaturaAit() {
        try {
            SQLiteDatabase Base = SQLiteDatabase.openDatabase( Environment.getExternalStorageDirectory().getAbsolutePath() + "/db/ait", null, 0);

            Cursor cursor = Base.rawQuery("Select arquivoAssInfrator from aitAssinatura Where idAit = " + idAit, null);
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                File imgFile = new File(cursor.getString(cursor.getColumnIndex("arquivoAssInfrator")));
                if (imgFile.exists()) {
                    Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                    fileSignature_search = imgFile;
                    image_signature.setImageBitmap(myBitmap);
                    mSaveButton.setEnabled(true);
                    mClearButton.setEnabled(true);
                }
                image_container.setVisibility(View.VISIBLE);
                signature_container.setVisibility(View.GONE);
            } else {
                String path= Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getPath()
                        +"/SignatureTec/Infrator/"+idAit+".jpg";

                File f =  new File(path);
                if (f.exists() ||  f.isDirectory()) {
                    final Uri imageUri = Uri.fromFile(new File(path));
                    fileSignature_search = new File(path);
                    final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                    final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                    image_signature.setImageBitmap(selectedImage);
                    image_container.setVisibility(View.VISIBLE);
                    signature_container.setVisibility(View.GONE);
                    mClearButton.setEnabled(true);
                    mSaveButton.setEnabled(true);
                }
                else {
                    image_container.setVisibility(View.GONE);
                    signature_container.setVisibility(View.VISIBLE);
                    mSaveButton.setEnabled(true);
                    mClearButton.setEnabled(false);
                }
            }
            cursor.close();
            Base.close();
        } catch (SQLiteException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void CreateTable() {
        try {
            SQLiteDatabase Base = SQLiteDatabase.openDatabase( Environment.getExternalStorageDirectory().getAbsolutePath() + "/db/ait", null, 0);

            Base.execSQL("CREATE TABLE IF NOT EXISTS aitAssinatura ( " +
                    "id                 INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "idAit              BIGINT  NULL," +
                    "arquivoAssInfrator TEXT  NULL," +
                    "arquivoAssinatura  TEXT    NULL);");

            Base.close();
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    public File getAlbumStorageDir() {
        // Get the directory for the user's public pictures directory.
        File file = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "SignatureTec/Infrator/");
        if (!file.mkdirs()) {
            Log.e("SignatureTec/Infrator/", "Directory not created");
        }
        return file;
    }

    public void saveBitmapToJPG(Bitmap bitmap, File photo) throws IOException {
        Bitmap newBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(newBitmap);
        canvas.drawColor(Color.WHITE);
        canvas.drawBitmap(bitmap, 0, 0, null);
        OutputStream stream = new FileOutputStream(photo);
        newBitmap.compress(Bitmap.CompressFormat.JPEG, 1, stream);
        stream.close();
    }

    public boolean addJpgSignatureToGallery(Bitmap signature) {
        boolean result = false;
            String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getPath()
                    + "/SignatureTec/Infrator/" + idAit + ".jpg";

            File f = new File(path);
            if (f.exists() || f.isDirectory()) {
                f.delete();
            }
            try {
                File photo = new File(getAlbumStorageDir(), idAit+".jpg");
                saveBitmapToJPG(signature, photo);
                scanMediaFile(photo);
                result = true;
                SalvarArquivoAssinatura(photo.getPath());
            } catch (IOException e) {
                e.printStackTrace();
            }

        return result;
    }

    private boolean SalvarArquivoAssinatura(String filePath) {
            try {
                SQLiteDatabase Base = SQLiteDatabase.openDatabase( Environment.getExternalStorageDirectory().getAbsolutePath() + "/db/ait", null, 0);

                Cursor cursor = Base.rawQuery("Select id from aitAssinatura Where idAit = " + idAit, null);
                if (cursor.getCount() > 0) {
                    cursor.moveToFirst();
                    Base.execSQL("UPDATE aitAssinatura SET arquivoAssInfrator='" + filePath + "' Where id = " + cursor.getString(cursor.getColumnIndex("id")));
                } else {
                    Base.execSQL("INSERT INTO aitAssinatura (idAit,arquivoAssInfrator) VALUES (" + idAit + ",'" + filePath + "')");
                }
                cursor.close();
                Base.close();
            } catch (SQLiteException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        return true;
    }

    private void scanMediaFile(File photo) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri contentUri = Uri.fromFile(photo);
        mediaScanIntent.setData(contentUri);
        AssinaturaInfratorActivity.this.sendBroadcast(mediaScanIntent);
    }

    @Override
    protected void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            try {
                final Uri imageUri = data.getData();
                fileSignature_search = new File(getPath(imageUri));
                final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                image_signature.setImageBitmap(selectedImage);
                image_container.setVisibility(View.VISIBLE);
                signature_container.setVisibility(View.GONE);
                mSaveButton.setEnabled(true);
                mClearButton.setEnabled(true);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    public String getPath(Uri uri) {
        String filePath = "";
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        if (cursor.getCount() > 0) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            filePath = cursor.getString(column_index);
            cursor.close();
        }
        return filePath;
    }
}
