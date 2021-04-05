package br.com.cobrasin;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import br.com.cobrasin.tabela.Caracterizacao;

public class AdapterList_QFV extends ArrayAdapter<Caracterizacao> {

    public AdapterList_QFV(Context context, int textViewResourceId, List<Caracterizacao> objects) {
        super(context, textViewResourceId, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getViewOptimize(position, convertView, parent);
    }

    public View getViewOptimize(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.lst_caracterizacao_item, null);
            viewHolder = new ViewHolder();
            viewHolder.imgSilheta_lstCaracterizacao = (ImageView)convertView.findViewById(R.id.imgSilheta_lstCaracterizacao);
            viewHolder.lblGrupo_N_Eixos_lstCaracterizacao = (TextView)convertView.findViewById(R.id.lblGrupo_N_Eixos_lstCaracterizacao);
            viewHolder.lblPBT_PBTC_lstCaracterizacao = (TextView)convertView.findViewById(R.id.lblPBT_PBTC_lstCaracterizacao);
            viewHolder.lblCaracterizacaoTitulo_lstCaracterizacao = (TextView)convertView.findViewById(R.id.lblCaracterizacaoTitulo_lstCaracterizacao);
            viewHolder.lblCaracterizacaoDesc_lstCaracterizacao =  (TextView)convertView.findViewById(R.id.lblCaracterizacaoDesc_lstCaracterizacao);
            viewHolder.lblClasse_lstCaracterizacao = (TextView)convertView.findViewById(R.id.lblClasse_lstCaracterizacao);
            viewHolder.lblCodigo_lstCaracterizacao = (TextView)convertView.findViewById(R.id.lblCodigo_lstCaracterizacao);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        Caracterizacao ca = getItem(position);
        viewHolder.lblGrupo_N_Eixos_lstCaracterizacao.setText(ca.getGrupo_N_Eixos());
        viewHolder.lblPBT_PBTC_lstCaracterizacao.setText(ca.getPBT_PBTC());
        viewHolder.lblCaracterizacaoTitulo_lstCaracterizacao.setText(ca.getCaracterizacao_Titulo());
        viewHolder.lblCaracterizacaoDesc_lstCaracterizacao.setText(ca.getCaracterizacao_Desc());
        viewHolder.lblClasse_lstCaracterizacao.setText(ca.getClasse());
        viewHolder.lblCodigo_lstCaracterizacao.setText(ca.getCodigo());
        
        byte buffer[][] = new byte[1][];
        try {
			byte data[] = ca.getSilhueta_Foto();
			buffer[0] = new byte[data.length];
			buffer[0] = data;

			Bitmap bm = BitmapFactory.decodeByteArray(buffer[0], 0,
					buffer[0].length);

			viewHolder.imgSilheta_lstCaracterizacao.setImageBitmap(bm);
			viewHolder.imgSilheta_lstCaracterizacao.setMinimumWidth(370);
			viewHolder.imgSilheta_lstCaracterizacao.setMinimumHeight(224);
			//imgSilheta.setScaleType(ScaleType.FIT_XY);
		} catch (Exception e) {
			// TODO: handle exception
		}
        
        return convertView;
    }

    private class ViewHolder {
    	public ImageView imgSilheta_lstCaracterizacao;
        public TextView lblGrupo_N_Eixos_lstCaracterizacao;
        public TextView lblPBT_PBTC_lstCaracterizacao;
        public TextView lblCaracterizacaoTitulo_lstCaracterizacao;
        public TextView lblCaracterizacaoDesc_lstCaracterizacao;
        public TextView lblClasse_lstCaracterizacao;
        public TextView lblCodigo_lstCaracterizacao;       
    }
}