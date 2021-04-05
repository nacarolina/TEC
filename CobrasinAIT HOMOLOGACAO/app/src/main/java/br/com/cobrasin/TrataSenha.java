package br.com.cobrasin;

import android.content.Context;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.widget.EditText;
import android.widget.Toast;

public class TrataSenha extends EditText {


	private boolean isUpdating;
	
	public TrataSenha(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		initialize();
	}


	public TrataSenha(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		initialize();
	}


	public TrataSenha(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		initialize();
	}


	private void initialize() { 
		 
		final int maxNumberLength = 6; 

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
				 
				 // forca 
				 TrataSenha.this.setInputType(InputType.TYPE_TEXT_VARIATION_NORMAL );
				 
				 boolean erro = false;
				 for ( int nx = 0 ;nx <= (descricao.length()-1) ;nx++)
				 {
					 caractere = descricao.charAt(nx);

					 // nao é digito
					 if (!Character.isDigit(caractere)) erro = true;
				 }
				 
				
				 
				if (erro)
				{
					 // limpa
					 TrataSenha.this.setText("");
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
					 
					 TrataSenha.this.setText(pedaco);
					 TrataSenha.this.setSelection(selecao);
 
				 }
				 
				 isUpdating = false;
			}
		}); 

	} 
	


}
