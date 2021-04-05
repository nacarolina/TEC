package br.com.cobrasin;

import java.util.ArrayList;
import java.util.List;

import android.R.bool;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.TextView;
import br.com.cobrasin.tabela.Eixo;

public class AdapterList_Eixo extends ArrayAdapter<Eixo> {

	private boolean MostraCheckSel = true;
    public AdapterList_Eixo(Context context, int textViewResourceId, List<Eixo> objects, boolean mostraCheckSel) {
        super(context, textViewResourceId, objects);
        MostraCheckSel = mostraCheckSel;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getViewOptimize(position, convertView, parent);
    }

    ArrayList<String> Lista_Selecionados = new ArrayList<String>();
    
    public View getViewOptimize(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.lst_eixo_item, null);
            viewHolder = new ViewHolder();
            viewHolder.imgEixo_lstEixo = (ImageView)convertView.findViewById(R.id.imgEixo_lstEixo);
            viewHolder.lblTitulo_lstEixo = (TextView)convertView.findViewById(R.id.lblTitulo_lstEixo);
            viewHolder.lblDesc_lstEixo = (TextView)convertView.findViewById(R.id.lblDesc_lstEixo);
            viewHolder.lblPeso_lstEixo = (TextView)convertView.findViewById(R.id.lblPeso_lstEixo);
            viewHolder.chkSel_lstEixo = (CheckBox)convertView.findViewById(R.id.chkSel_lstEixo);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        
       if(MostraCheckSel == true)
       {
    	   viewHolder.chkSel_lstEixo.setVisibility(View.VISIBLE);
       }
       else
       {
    	   viewHolder.chkSel_lstEixo.setVisibility(View.INVISIBLE);
       }
        
        final Eixo Ei = getItem(position);
        
        viewHolder.chkSel_lstEixo.setOnCheckedChangeListener(new OnCheckedChangeListener()
        {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                if ( isChecked )
                {
                	Lista_Selecionados.add(Ei.getId());
                }else{
                	Lista_Selecionados.remove(Ei.getId());
                }
            }
        });
        
        
        viewHolder.lblTitulo_lstEixo.setText(Ei.getEixo_Titulo());
        viewHolder.lblDesc_lstEixo.setText(Ei.getEixo_Desc());
        viewHolder.lblPeso_lstEixo.setText(Ei.getEixo_Peso());
        
        byte buffer[][] = new byte[1][];
        try {
			byte data[] = Ei.getFoto();
			buffer[0] = new byte[data.length];
			buffer[0] = data;

			Bitmap bm = BitmapFactory.decodeByteArray(buffer[0], 0,
					buffer[0].length);

			viewHolder.imgEixo_lstEixo.setImageBitmap(bm);
			viewHolder.imgEixo_lstEixo.setMinimumWidth(370);
			viewHolder.imgEixo_lstEixo.setMinimumHeight(224);
			//imgSilheta.setScaleType(ScaleType.FIT_XY);
		} catch (Exception e) {
			// TODO: handle exception
		}
        
        return convertView;
    }

    private class ViewHolder {
    	public CheckBox chkSel_lstEixo;
    	public ImageView imgEixo_lstEixo;
        public TextView lblTitulo_lstEixo;
        public TextView lblDesc_lstEixo;
        public TextView lblPeso_lstEixo;  
    }
    
    ArrayList<String> getSelecionado(){
    	  return Lista_Selecionados;
    	}
}