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
        this.setText("");
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

                Character caracter;

                TrataPlaca.this.setInputType(InputType.TYPE_CLASS_TEXT);

                boolean erro = false;
                for (int nx = 0; nx <= (placa.length() - 1); nx++) {
                    caracter = placa.charAt(nx);
                    erro = Character.isLetterOrDigit(caracter) ? false : true;
                    if (Character.isLetter(caracter)) {
                        //para as autuações de placa MERCOSUL a resolução especifica que os segundo numero que agora serão letras valera
                        // A=0, B=1, C=2, D=3, E=4, F=5, G=6, H=7, I=8 J=9, PORTANTO A LETRA DESTE SEGUNDO CAMPO SO PODERA SER DA LETRA A ate A LETRA J,
                        // porem ate agora o sistema permite letras acima de J.
                        String letra = caracter.toString().toUpperCase();

                        if(placa.length()==5) {
                            if (letra.equals("A") == false && letra.equals("B") == false && letra.equals("C") == false && letra.equals("D") == false && letra.equals("E") == false
                                    && letra.equals("F") == false && letra.equals("G") == false && letra.equals("H") == false && letra.equals("I") == false && letra.equals("J") == false) {
                                erro = true;
                            }
                        }
                    }
                }


                if (erro) {
                    TrataPlaca.this.setText("");
                    TrataPlaca.this.setInputType(InputType.TYPE_CLASS_TEXT);
                    Toast.makeText(getContext(), ">Placa inválida!", Toast.LENGTH_SHORT).show();

                } else {
                    String pedaco;
                    int selecao = 0;
                    // ultrapassou
                    if (placa.length() > maxNumberLength) {
                        // bvx6348
                        selecao = maxNumberLength;
                        pedaco = placa.substring(0, maxNumberLength);
                    } else {
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

	
