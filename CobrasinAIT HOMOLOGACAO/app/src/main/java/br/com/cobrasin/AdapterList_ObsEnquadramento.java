package br.com.cobrasin;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import br.com.cobrasin.R;
import br.com.cobrasin.dao.EnquadramentoDAO;
import br.com.cobrasin.dao.ParametroDAO;
import br.com.cobrasin.tabela.Enquadramento;

public class AdapterList_ObsEnquadramento extends ArrayAdapter<Enquadramento > {

    boolean SemObrigatoriedade=false;
    Context cOrigem;
    private ListView listaObs;
    private List<Enquadramento> lstEnquadramento;
    private AdapterList_ObsEnquadramento adapter;

    public AdapterList_ObsEnquadramento(Context context, int textViewResourceId, List<Enquadramento> objects, boolean semObrigatoriedade) {
        super(context, textViewResourceId, objects);
        SemObrigatoriedade=semObrigatoriedade;
        cOrigem=context;
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
            convertView = inflater.inflate(R.layout.lst_obs_enquadramento, null);
            viewHolder = new ViewHolder();

            viewHolder.lblCodEnquadramento_lstObsEnquadramento = (TextView)convertView.findViewById(R.id.lblCodEnquadramento_lstObsEnquadramento);
            viewHolder.lblEnquadramento_lstObsEnquadramento = (TextView)convertView.findViewById(R.id.lblEnquadramento_lstObsEnquadramento);
            viewHolder.btnExcluir_lstObsEnquadramento = (Button) convertView.findViewById(R.id.btnExcluir_lstObsEnquadramento);

            if (SemObrigatoriedade){
                viewHolder.btnExcluir_lstObsEnquadramento.setText("Atribuir");
            }else{
                viewHolder.btnExcluir_lstObsEnquadramento = (Button) convertView.findViewById(R.id.btnExcluir_lstObsEnquadramento);
            }
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        final Enquadramento Ei = getItem(position);

        viewHolder.btnExcluir_lstObsEnquadramento.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(final View v) {
                // TODO Auto-generated method stub


                AlertDialog.Builder aviso = new AlertDialog.Builder(cOrigem);
                aviso.setIcon(android.R.drawable.ic_dialog_alert);
                if(SemObrigatoriedade)
                    aviso.setTitle("Atribuir Obrigatoriedade");
                else
                    aviso.setTitle("Excluir Obrigatoriedade");
                aviso.setMessage("Confirma ?");
                aviso.setNeutralButton("Não",null);
                aviso.setPositiveButton("Sim", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        boolean obs=true;
                        EnquadramentoDAO enquadramentoDAO = new EnquadramentoDAO(cOrigem);
                        ParametroDAO parametroDAO = new ParametroDAO(cOrigem);
                        parametroDAO.SetSincObsObrigatorio(Utilitarios.getDataHora(4));
                        if(SemObrigatoriedade){
                            enquadramentoDAO.UpdateEnquadramentoObsObrigatorio(Ei.getCodigo(),"1");
                            obs=true;
                            Toast.makeText(cOrigem, "Atribuido com sucesso !", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            obs=false;
                            enquadramentoDAO.UpdateEnquadramentoObsObrigatorio(Ei.getCodigo(),"0");
                            Toast.makeText(cOrigem, "Excluido com sucesso !", Toast.LENGTH_SHORT).show();
                        }

                        if (cOrigem instanceof obsEnquadramento) {
                            ((obsEnquadramento)cOrigem).carregaLista();
                        }
                    }
                });

                aviso.show();

            }
        });
        viewHolder.lblEnquadramento_lstObsEnquadramento.setText(Ei.getDescricao());
        viewHolder.lblCodEnquadramento_lstObsEnquadramento.setText(Ei.getCodigo()+" - ");
        return convertView;
    }

    private class ViewHolder {
        public Button btnExcluir_lstObsEnquadramento;
        public TextView lblEnquadramento_lstObsEnquadramento;
        public TextView lblCodEnquadramento_lstObsEnquadramento;
    }

    ArrayList<String> getSelecionado(){
        return Lista_Selecionados;
    }}
