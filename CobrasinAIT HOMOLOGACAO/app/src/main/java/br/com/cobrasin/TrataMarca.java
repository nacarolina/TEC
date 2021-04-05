package br.com.cobrasin;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.widget.EditText;
import android.widget.Toast;

public class TrataMarca extends EditText {


	private boolean isUpdating;
	
	public TrataMarca(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		initialize();
	}


	public TrataMarca(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		initialize();
	}


	public TrataMarca(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		initialize();
	}


	private void initialize() { 
		 
		final int maxNumberLength = 25; 

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

				String descricao = s.toString(); 
				
				 if (isUpdating) { 
					 isUpdating = false; 
					 return; 
				 
					} 

				 isUpdating = true;
				 
				 Character caractere ;
				 
				 boolean erro = false;
				 //for ( int nx = 0 ;nx <= (descricao.length()-1) ;nx++)
				 //{
				//	 caractere = descricao.charAt(nx);
				//	
					 // nao é digito ou caractere
				//	 if ( (!Character.isLetter(caractere)) && (!Character.isDigit(caractere))) erro = true;
				// }
				 //******************************************************************************************
				 // retirado em 03.05.2012 devido a pesquisa no webtrans... Retorna fabricante/modelo e da 
				 // erro na barra
				 //******************************************************************************************
				 
				if (erro)
				{
					 // limpa
					 TrataMarca.this.setText("");
					 Toast.makeText( getContext() , ">Entrada inválida!",Toast.LENGTH_SHORT).show();
				}
				 else
					 
				 {	
					 String pedaco;
					 int selecao = 0;
					 // ultrapassou
					 if (descricao.length() > maxNumberLength)
					 {
						 selecao = maxNumberLength;
						 pedaco = descricao.substring(0, maxNumberLength);
					 }
					 else
					 {
						 pedaco = descricao.substring(0, descricao.length());
						 selecao = descricao.length();
					 }

					 pedaco = pedaco.toUpperCase();
					 
					 TrataMarca.this.setText(pedaco);
					 TrataMarca.this.setSelection(selecao);
 
				 }
				 
				 isUpdating = false;
			}
		}); 

	} 
	


}
