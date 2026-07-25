package cu.alexgi.youchat;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.HashSet;
import java.util.List;

import cu.alexgi.youchat.swipebackfragment.BaseSwipeBackFragment;

import static cu.alexgi.youchat.MainActivity.context;
import static cu.alexgi.youchat.MainActivity.mainActivity;

public class Acerca_de_Activity extends BaseSwipeBackFragment {

    private int cont;
    private String user;
    private View newTester;
    private TextView nameTester;
    private View view_link_apklis,view_link_telegram,view_link_youtube;

    public static Acerca_de_Activity newInstance() {
        Acerca_de_Activity fragment = new Acerca_de_Activity();
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_acerca_de, container, false);
        return attachToSwipeBack(view);
//        return
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        cont=0;
        newTester=view.findViewById(R.id.tester);
        user = YouChatApplication.alias;
        if(user.equals("")) user = YouChatApplication.correo;
        nameTester=view.findViewById(R.id.nameTester);
        view_link_apklis=view.findViewById(R.id.view_link_apklis);
        view_link_telegram=view.findViewById(R.id.view_link_telegram);
        view_link_youtube=view.findViewById(R.id.view_link_youtube);

        view.findViewById(R.id.root).setBackgroundColor(Color.parseColor(YouChatApplication.itemTemas.getColor_interior()));

        View back = view.findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainActivity.atrasFragment();
                getActivity().onBackPressed();
            }
        });
        View logo = view.findViewById(R.id.logo);
        logo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(YouChatApplication.calidad==70 && !YouChatApplication.notificacion) accion();
            }
        });

        view_link_apklis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.apklis.cu/application/cu.alexgi.youchat/")));
            }
        });
        view_link_telegram.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openTelegram(getActivity(),"youchat_oficial");
            }
        });
        view_link_youtube.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/c/alexgi")));
            }
        });

        nameTester.setText(user);
        if(YouChatApplication.es_beta_tester)
            newTester.setVisibility(View.VISIBLE);
    }

    private synchronized void accion() {
        cont++;
        if(cont==10){
            cont=0;
            if(!YouChatApplication.es_beta_tester) {

                Dialog dialogo = new Dialog(context);
                dialogo.requestWindowFeature(1);
                View mviewe=getLayoutInflater().inflate(R.layout.dialog_confirm_beta_tester,null);
                dialogo.setContentView(mviewe);

                EditText et_codigo=mviewe.findViewById(R.id.et_codigo);
                View btn_ok=mviewe.findViewById(R.id.btn_ok);
                View btn_cancel=mviewe.findViewById(R.id.btn_cancel);

                btn_ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialogo.dismiss();
                        String cad = et_codigo.getText().toString().trim();
                        if(!cad.equals(""))
                            verificarCodigo(cad);
                    }
                });
                btn_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialogo.dismiss();
                    }
                });

                dialogo.getWindow().setBackgroundDrawable(new ColorDrawable(0));
                dialogo.setCancelable(false);
                dialogo.show();
            }
            else {
                YouChatApplication.setEs_beta_tester(false);
                Utils.ShowToastAnimated(mainActivity,"Ya no eres un Beta Tester",R.raw.chats_infotip);
                newTester.setVisibility(View.GONE);
            }
        }
    }

    private void verificarCodigo(String cad) {
        String codigoGenerado = Convertidor.generarCodigo();
        if(cad.equals(codigoGenerado)){
            newTester.setVisibility(View.VISIBLE);
            YouChatApplication.setEs_beta_tester(true);
            Utils.ShowToastAnimated(mainActivity,"Ahora eres un Beta Tester",R.raw.contact_check);
        }
        else Utils.ShowToastAnimated(mainActivity,"Código incorrecto",R.raw.error);
    }

    public static void openTelegram (Activity activity, String userName) {
        Intent general = new Intent(Intent.ACTION_VIEW, Uri.parse("https://t.com/" + userName));
        HashSet<String> generalResolvers = new HashSet<>();
        List<ResolveInfo> generalResolveInfo = activity.getPackageManager().queryIntentActivities(general, 0);
        for (ResolveInfo info : generalResolveInfo) {
            if (info.activityInfo.packageName != null) {
                generalResolvers.add(info.activityInfo.packageName);
            }
        }
        Intent telegram = new Intent(Intent.ACTION_VIEW, Uri.parse("https://t.me/" + userName));
        int goodResolver = 0;
        List<ResolveInfo> resInfo = activity.getPackageManager().queryIntentActivities(telegram, 0);
        if (!resInfo.isEmpty()) {
            for (ResolveInfo info : resInfo) {
                if (info.activityInfo.packageName != null && !generalResolvers.contains(info.activityInfo.packageName)) {
                    goodResolver++;
                    telegram.setPackage(info.activityInfo.packageName);
                }
            }
        }
        if (goodResolver != 1) {
            telegram.setPackage(null);
        }
        if (telegram.resolveActivity(activity.getPackageManager()) != null) {
            activity.startActivity(telegram);
        }
    }
}
