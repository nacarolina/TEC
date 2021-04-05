package br.com.cobrasin;

import android.content.Context; 
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.AttributeSet; 
import android.widget.EditText;
import android.widget.Toast;

public class TrataPlacaEst extends EditText {
	
	private boolean isUpdating; 

	public TrataPlacaEst(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		//this.setOnKeyListener(this);
		initialize();
	}

	public TrataPlacaEst(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		initialize();
		//this.setOnKeyListener(this);
	}

	public TrataPlacaEst(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		initialize();
		//this.setOnKeyListener(this);
	}

	
	private void initialize() { 
		 
		final int maxNumberLength = 10; 

		//this.setKeyListener(keylistenerNumber); 
		 
		
		this.setText(""); 
	
		//this.setSelection(1); 
	 
		this.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				String placa = s.toString(); 
				//placa = placa.toUpperCase();
				
				
				 if (isUpdating) { 
					 isUpdating = false; 
					 return; 
				 
					} 

				 isUpdating = true;
				 
				 Character caractere ;
				
				 /*
				 if ( placa.length() >= 3 )
					 TrataPlacaEst.this.setInputType(InputType.TYPE_CLASS_NUMBER);
				 else
					 TrataPlacaEst.this.setInputType(InputType.TYPE_CLASS_TEXT);
					*/ 
				 
				 boolean erro = false;
				 for ( int nx = 0 ;nx <= (placa.length()-1) ;nx++)
				 {
					 caractere = placa.charAt(nx);
					 
					
					//if (Character.isLetter(caractere)) erro = false;
					//if (!Character.isDigit(caractere)) erro = false;
					 
				 }
				
				 
				 if (erro)
				 {
					 // limpa
					 if (placa.length() > 1) 
					 {
						 TrataPlacaEst.this.setText("");
						 TrataPlacaEst.this.setInputType(InputType.TYPE_CLASS_TEXT);
						 //TrataPlaca.this.setText( placa.substring(0, placa.length()-1));
					 }
					 
					 Toast.makeText( getContext() , ">Placa inválida!",Toast.LENGTH_SHORT).show();
					 
					 
				}
				else
				{	
					 String pedaco;
					 int selecao = 0;
					 // ultrapassou
					 if (placa.length() > maxNumberLength)
					 {
						 // bvx6348
						 selecao = maxNumberLength;
						 pedaco = placa.substring(0, maxNumberLength);
					 }
					 else
					 {
						 pedaco = placa.substring(0, placa.length());
						 selecao = placa.length();
					 }

					 pedaco = pedaco.toUpperCase();
					 
					 TrataPlacaEst.this.setText(pedaco);
					 TrataPlacaEst.this.setSelection(selecao);
 
				}
				 
				 isUpdating = false;
			}
		}); 

	} 
	
}

	
