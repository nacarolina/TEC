package br.com.cobrasin;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;

import java.util.ArrayList;

public class PermissionConfig extends Activity {
    private static final int REQUEST_PERMISSION_GROUP = 1;
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            PermissionConfiguation();
        else {
            startActivity(new Intent(PermissionConfig.this, DownloadFTP.class));
            finish();
        }

        setContentView(R.layout.activity_permission_config);
    }

    String[] permissionsRequired = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.INTERNET,Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.BLUETOOTH_ADMIN,  Manifest.permission.BLUETOOTH,  Manifest.permission.CAMERA};

    private void PermissionConfiguation() {
        ArrayList<String> permissionsNeeded = new ArrayList<>();

        for (String permission : permissionsRequired) {
            if (ContextCompat.checkSelfPermission(PermissionConfig.this, permission) != PackageManager.PERMISSION_GRANTED)
                permissionsNeeded.add(permission);
        }

        if (permissionsNeeded.size() > 0)
            RequestPermissionGroup(permissionsNeeded);
        else {
            startActivity(new Intent(PermissionConfig.this, DownloadFTP.class));
            finish();
        }
    }

    private void RequestPermissionGroup(ArrayList<String> permissions) {
        String[] permissionList = new String[permissions.size()];
        permissions.toArray(permissionList);
        ActivityCompat.requestPermissions(PermissionConfig.this, permissionList, REQUEST_PERMISSION_GROUP);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            return;
        else
            mostraMensagem("Não é possível fazer os downloads necessários sem a permisão de acesso!", "Acesso Negado!");
        return;
    }

    public void RequiredPermission(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            boolean hasPermission = true;
            for (String permission : permissionsRequired) {
                if (ContextCompat.checkSelfPermission(PermissionConfig.this, permission) != PackageManager.PERMISSION_GRANTED){
                    hasPermission = false;
                    break;
                }
            }
            if (!hasPermission)
                PermissionConfiguation();
            else
                mostraMensagem("Suas Permissões já estão configuradas!", "Permissões Configuradas");
        } else
            mostraMensagem("Suas Permissões já estão configuradas!", "Permissões Configuradas");
    }

    private void mostraMensagem(final String mens, final String titulo) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                //String mensagem = "Erro na instalação...";
                AlertDialog.Builder aviso = new AlertDialog.Builder(
                        PermissionConfig.this);
                aviso.setIcon(android.R.drawable.ic_dialog_alert);
                aviso.setTitle(titulo);
                aviso.setMessage(mens);
                aviso.setPositiveButton("OK", null);
                aviso.show();
            }
        });
    }

    public void AcessarApk(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            boolean hasPermission = true;
            for (String permission : permissionsRequired) {
                if (ContextCompat.checkSelfPermission(PermissionConfig.this, permission) != PackageManager.PERMISSION_GRANTED){
                    hasPermission = false;
                    break;
                }
            }
            if (!hasPermission)
                mostraMensagem("Para acessar o TEC é necessário aceitar as permissões de acesso requirida!", "Permissões de Acesso");
            else {
                startActivity(new Intent(PermissionConfig.this, DownloadFTP.class));
                finish();
            }
        } else {
            startActivity(new Intent(PermissionConfig.this, DownloadFTP.class));
            finish();
        }
    }
}
