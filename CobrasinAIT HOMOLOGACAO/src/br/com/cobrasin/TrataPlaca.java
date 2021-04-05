package br.com.cobrasin;

import android.content.Context; 
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.AttributeSet; 
import android.widget.EditText;
import android.widget.Toast;

public class TrataPlaca extends EditText {
	
	private boolean isUpdating; 

	public TrataPlaca(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		//this.setOnKeyListener(this);
		initialize();
	}

	public TrataPlaca(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		initialize();
		//this.setOnKeyListener(this);
	}

	public TrataPlaca(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		initialize();
		//this.setOnKeyListener(this);
	}

	
	private void initialize() { 
		 
		final int maxNumberLength = 7; 

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
				 
				 if ( placa.length() >= 3 )
					 TrataPlaca.this.setInputType(InputType.TYPE_CLASS_NUMBER);
				 else
					 TrataPlaca.this.setInputType(InputType.TYPE_CLASS_TEXT);
					 
				 
				 boolean erro = false;
				 for ( int nx = 0 ;nx <= (placa.length()-1) ;nx++)
				 {
					 caractere = placa.charAt(nx);
					 
					 // BVX 6348
					 // 012 3456
					 
					 if (nx < 3)
					 {
						 // 3 letras
						 if (!Character.isLetter(caractere)) erro = true;
					 }
					 else
					 {
						 // 4
						 if (!Character.isDigit(caractere)) erro = true;
					 }
				 }
				 
				
				 
				if (erro)
				{
					 // limpa
					 if (placa.length() > 1) 
					 {
						 TrataPlaca.this.setText("");
						 TrataPlaca.this.setInputType(InputType.TYPE_CLASS_TEXT);
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
					 
					 TrataPlaca.this.setText(pedaco);
					 TrataPlaca.this.setSelection(selecao);
 
				 }
				 
				 isUpdating = false;
			}
		}); 

	} 
	
}

	
