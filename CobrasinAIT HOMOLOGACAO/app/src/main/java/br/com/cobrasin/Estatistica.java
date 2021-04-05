package br.com.cobrasin;

import java.util.List;

import br.com.cobrasin.dao.AitDAO;
import br.com.cobrasin.tabela.Ait;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class Estatistica extends Activity{
	
	private int QtdAitValido;
	private int QtdTodosAit;
	private int QtdAitCancelado;
	private String QtdAitTransferido;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.estatistica);
		
		Button btRetorna = (Button) findViewById(R.id.btRetornaEstatistica);
		TextView txvAitValido = (TextView)findViewById(R.id.txvAitValido);
		TextView txvAitTransferido = (TextView)findViewById(R.id.txvAitTransferidos);
		TextView txvAitCancelado = (TextView)findViewById(R.id.txvAitCancelado);
		TextView txvTotalAit = (TextView)findViewById(R.id.txvTotalAit);
		
	     AitDAO a = new AitDAO(Estatistica.this);
		 List<Ait> ls= a.getListaAitPrint(getIntent().getStringExtra("agente"));
			 QtdAitValido = ls.size();
		 
		List<Ait>  ls1= a.getLista(getIntent().getStringExtra("agente"));
			QtdTodosAit = ls1.size();
			QtdAitCancelado = (QtdTodosAit - QtdAitValido);
			QtdAitTransferido = a.ObtemTotalTransferido();
			
			txvAitValido.setText(txvAitValido.getText() + Integer.toString(QtdAitValido));
			txvAitTransferido.setText(txvAitTransferido.getText() + QtdAitTransferido);
			txvAitCancelado.setText(txvAitCancelado.getText() + Integer.toString(QtdAitCancelado));
			txvTotalAit.setText(txvTotalAit.getText() + Integer.toString(QtdTodosAit));
			
		btRetorna.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				finish();
				
			}
		});
		}

}
