


package br.com.cobrasin;

import br.com.cobrasin.dao.FotoDAO;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class MostraFotos extends Activity {

	int  qtdeFotos = 0 ; 
	long idAit = 0 ; 
	private Bitmap bm = null;
	private int imagem = 0;
	
	// armarzena as 3 fotos
	byte buffer[][] = new byte[3][];
	
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        
        setContentView(R.layout.exibeimagem);
        
        // pega o Id do AIT 
        idAit =  (Long) getIntent().getSerializableExtra("idAit");
     	
        // levanta qtde de fotos
        FotoDAO fotodao = new FotoDAO(getBaseContext());    
        qtdeFotos = fotodao.getQtde(idAit);
        
        Cursor cx = fotodao.getImagens(idAit);
        
       final ImageView img1 = (ImageView) findViewById(R.id.imgExpandir);
       ImageButton imgProximo = (ImageButton) findViewById(R.id.imgbtnProximo);
       ImageButton imgAnterior = (ImageButton) findViewById(R.id.imgbtnAnterior);
     //  final TextView txvNrmImagem = (TextView) findViewById(R.id.txvnrimagem);
        
        int pos = 0 ;
        while (cx.moveToNext())
        {
        	// aloca e preenche
        	byte data [] = cx.getBlob(cx.getColumnIndex("imagem"));
        	buffer[pos] = new byte[data.length];
            buffer[pos] = data; 
        	pos++;
       }
    	Bitmap bm =  BitmapFactory.decodeByteArray(buffer[imagem], 0, buffer[imagem].length);
    	
    	img1.setImageBitmap(bm);
    //	txvNrmImagem.setText("Primeira 1");
		//bm.recycle();
    // 	img1.setScaleType(ImageView.ScaleType.FIT_CENTER);
    	
    	imgProximo.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
            	if (imagem == 1) 
            	{
            		try {
				     	Bitmap bm =  BitmapFactory.decodeByteArray(buffer[2], 0, buffer[2].length);
                	
                  	img1.setImageBitmap(bm);
              		//bm.recycle();
                  	img1.setScaleType(ImageView.ScaleType.FIT_CENTER);
                	//txvNrmImagem.setText("Terçeira Imagem");
                  	imagem = 2;
					} catch (Exception e) {
						// TODO: handle exception
						imagem = 1;
					}

				}
            	if (imagem == 0) {
    	  try {
			      	Bitmap bm =  BitmapFactory.decodeByteArray(buffer[1], 0, buffer[1].length);
    	
      	img1.setImageBitmap(bm);
  		//bm.recycle();
      	img1.setScaleType(ImageView.ScaleType.FIT_CENTER);
      	//txvNrmImagem.setText("Segunda Imagem");
      	imagem = 1;
		} catch (Exception e) {
			// TODO: handle exception
			imagem = 0;
		}

	}
            }
    });
    	
    	imgAnterior.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
            	            	if (imagem == 1) 
            	{
                  	Bitmap bm =  BitmapFactory.decodeByteArray(buffer[0], 0, buffer[0].length);
                	
                  	img1.setImageBitmap(bm);
              		//bm.recycle();
                  	img1.setScaleType(ImageView.ScaleType.FIT_CENTER);
                //	txvNrmImagem.setText("Primeira Imagem");
                  	imagem = 0;
				}
            	 if (imagem == 2) 
            	{
                  	Bitmap bm =  BitmapFactory.decodeByteArray(buffer[1], 0, buffer[1].length);
                	
                  	img1.setImageBitmap(bm);
              		//bm.recycle();
                  	img1.setScaleType(ImageView.ScaleType.FIT_CENTER);
                //	txvNrmImagem.setText("Segunda Imagem");
                  	imagem = 1;
				}

            }
    });
        
        cx.close();
        fotodao.close();
        

        
        /* Find the gallery defined in the main.xml 
         * Apply a new (custom) ImageAdapter to it. */
       // ((Gallery) findViewById(R.id.gallery1))
        //		.setAdapter(new ImageAdapter(this));
        
        
        
    }

    public class ImageAdapter extends BaseAdapter {
    	/** The parent context */
        private Context myContext;

        
        /** Simple Constructor saving the 'parent' context. */
        public ImageAdapter(Context c) { this.myContext = c; }

        /** Returns the amount of images we have defined. */
        public int getCount() { return qtdeFotos; }

        /* Use the array-Positions as unique IDs */
        public Object getItem(int position) { return position; }
        public long getItemId(int position) { return position; }

        /** Returns a new ImageView to 
         * be displayed, depending on 
         * the position passed. */
        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView i = new ImageView(this.myContext);
           // BitmapFactory.Options b=null;
            //b=new BitmapFactory.Options();
           // b.inSampleSize=2;
        	Bitmap bm =  BitmapFactory.decodeByteArray(buffer[position], 0, buffer[position].length);
        	
			i.setImageBitmap(bm);
			//bm.recycle();
            /* Image should be scaled as width/height are set. */
            i.setScaleType(ImageView.ScaleType.FIT_CENTER);
            /* Set the Width/Height of the ImageView. */
            i.setLayoutParams(new Gallery.LayoutParams(350,350));
            return i;
        }

        /** Returns the size (0.0f to 1.0f) of the views
         * depending on the 'offset' to the center. */
        public float getScale(boolean focused, int offset) {
        	/* Formula: 1 / (2 ^ offset) */
            return Math.max(0, 1.0f / (float)Math.pow(2, Math.abs(offset)));
        }
    }
}