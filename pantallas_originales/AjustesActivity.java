package cu.alexgi.youchat;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.PowerManager;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.slider.LabelFormatter;
import com.google.android.material.slider.Slider;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.vanniktech.emoji.EmojiTextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;

import cu.alexgi.youchat.BottomSheetDialogFragment.BottomSheetDialogFragment_Ayuda_ajustes;
import cu.alexgi.youchat.base_datos.BDConstantes;
import cu.alexgi.youchat.items.ItemChat;
import cu.alexgi.youchat.progressbar.DownloadProgressView;
import cu.alexgi.youchat.swipebackfragment.BaseSwipeBackFragment;
import cu.alexgi.youchat.views_GI.CheckBoxGI;
import cu.alexgi.youchat.views_GI.TextViewFontGenGI;

import static android.content.Context.POWER_SERVICE;
import static cu.alexgi.youchat.MainActivity.context;
import static cu.alexgi.youchat.MainActivity.mainActivity;

@SuppressLint("RestrictedApi")
public class AjustesActivity extends BaseSwipeBackFragment {

    private boolean modoPreguntar,entroAPerfil;
//    private Context context;
    private Activity activity;
    private AjustesActivity ajustesActivity;
    private View option_slider_chat_tam_limite, option_chat_tam_limite, option_slider_now_tam_limite, option_now_tam_limite,
            contenedor_notificacion, option_chat_emoji_anim, option_perfil_pub,
            option_post_ver_historial, option_post_historial, option_post_borrar_24, option_post_des_img,
            option_ir_ajustes_buzon, option_activar_buzon, option_chat_responder, option_slider_post,
            option_post_limite, option_chat_intro, option_post, option_burbuja_datos, option_slider_estados,
            ir_atras, option_perfil, option_actperfil, option_aviso_en_linea,
            option_segundo_plano, option_datos, option_bloqueados, option_bloqueados_post,
            option_estadisticas, option_copia_seguridad, option_lectura,
            option_animacion_chat, option_chat_dinamico,
            option_admin_estados, option_visibilidad_estados, option_progreso_estados,
            option_clear_inbox, option_saldo,
            option_donar, option_silenciar_notificaciones, option_silenciar_sonidos, option_chat_security;

    private Slider seekBar_slider_post, seekBar_tiempo_progreso, seekBar_slider_now_tam_limite,seekBar_slider_chat_tam_limite;
    private TextView slider_post, tv_calidad_img, tiempo_progreso, tv_mensaje_tam_limite,tv_chat_tam_limite;
    private TextView  tv_cant_msg_inbox, tv_visibilidad;
    private TextView texto_alerta,correo_ajustes;
    private EmojiTextView user_ajustes;
    private SwitchMaterial switch_chat_tam_limite, switch_now_tam_limite, switch_chat_emoji_anim, switch_perfil_pub, switch_post_historial,
            switch_post_borrar_24, switch_post_des_img, switch_activar_buzon,switch_chat_responder,
            switch_post_limite, switch_chat_intro, switch_post, switch_chat_security, switch_burbuja,
            switch_actperfil, switch_lectura, switch_notificaciones, switch_sonidos,
            switch_animacion_chat,switch_chat_dinamico, switch_aviso_en_linea, switch_segundo_plano,switch_estado_progreso;
    private  String nick,correo;
    private int theme=0,calidad;
    private AppCompatImageView icon_preguntar;
    private Dialog dialogoInbox;
    private ImageView imageView_page_ajustes;
    private ColorStateList stateListAccent, stateListNone;

    private CheckBoxGI checkbox_notificacion_chat, checkbox_notificacion_correos,
            checkbox_notificacion_now, checkbox_notificacion_reaccion;

    //BATERIA
    private MaterialCardView card_bateria;
    private View btn_bateria1, btn_bateria2;
    private CheckBoxGI checkBox_opt_bat;


//    private NavController navController;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return attachToSwipeBack(inflater.inflate(R.layout.activity_ajustes, container, false));
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        activity=getActivity();
        ajustesActivity = this;
        init(view);
    }

    private void init(View view){
        if(YouChatApplication.mostrarAvisoOptBatery){
            checkBox_opt_bat=view.findViewById(R.id.checkBox_opt_bat);
            checkBox_opt_bat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    YouChatApplication.setMostrarAvisoOptBatery(!isChecked);
                }
            });
            card_bateria=view.findViewById(R.id.card_bateria);
            btn_bateria1=view.findViewById(R.id.btn_bateria1);
            btn_bateria1.setOnClickListener(new View.OnClickListener() {
                @SuppressLint("BatteryLife")
                @RequiresApi(api = Build.VERSION_CODES.M)
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                    intent.setData(Uri.parse("package:"+ context.getPackageName()));
                    startActivity(intent);
//                    Utils.ShowToastAnimated(mainActivity, "Busque YouChat y desactive la optimización de batería", R.raw.chats_infotip);
                }
            });

            btn_bateria2=view.findViewById(R.id.btn_bateria2);
            btn_bateria2.setOnClickListener(new View.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.M)
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.setAction(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS);
                    startActivity(intent);
                    Utils.ShowToastAnimated(mainActivity, "Busque YouChat y desactive la optimización de batería", R.raw.chats_infotip);
                }
            });
        }
        stateListAccent = ColorStateList.valueOf(Color.parseColor(YouChatApplication.itemTemas.getColor_accento()));
        stateListNone = ColorStateList.valueOf(Color.parseColor(YouChatApplication.itemTemas.getFont_barra()));

        modoPreguntar=false;
        entroAPerfil=false;

        user_ajustes=view.findViewById(R.id.user_ajustes);
        correo_ajustes=view.findViewById(R.id.correo_ajustes);
        imageView_page_ajustes=view.findViewById(R.id.imageView_page_ajustes);
        cargarPreferencias();

        view.findViewById(R.id.root).setBackgroundColor(Color.parseColor(YouChatApplication.itemTemas.getColor_interior()));

        switch_activar_buzon=view.findViewById(R.id.switch_activar_buzon);
        switch_chat_responder=view.findViewById(R.id.switch_chat_responder);
        switch_post_limite=view.findViewById(R.id.switch_post_limite);
        switch_chat_intro=view.findViewById(R.id.switch_chat_intro);
        switch_post_des_img=view.findViewById(R.id.switch_post_des_img);
        switch_post_borrar_24=view.findViewById(R.id.switch_post_borrar_24);
        switch_post_historial=view.findViewById(R.id.switch_post_historial);
        switch_now_tam_limite=view.findViewById(R.id.switch_now_tam_limite);
        switch_chat_tam_limite=view.findViewById(R.id.switch_chat_tam_limite);
        switch_perfil_pub=view.findViewById(R.id.switch_perfil_pub);
        switch_chat_emoji_anim=view.findViewById(R.id.switch_chat_emoji_anim);
        switch_post=view.findViewById(R.id.switch_post);
        switch_chat_security=view.findViewById(R.id.switch_chat_security);
        switch_actperfil=view.findViewById(R.id.switch_actperfil);
        switch_lectura= view.findViewById(R.id.switch_lectura);
        switch_notificaciones= view.findViewById(R.id.switch_notificaciones);
        contenedor_notificacion= view.findViewById(R.id.contenedor_notificacion);
        checkbox_notificacion_chat= view.findViewById(R.id.checkbox_notificacion_chat);
        checkbox_notificacion_correos= view.findViewById(R.id.checkbox_notificacion_correos);
        checkbox_notificacion_now= view.findViewById(R.id.checkbox_notificacion_now);
        checkbox_notificacion_reaccion= view.findViewById(R.id.checkbox_notificacion_reaccion);
        switch_sonidos=view.findViewById(R.id.switch_sonidos);
        switch_animacion_chat=view.findViewById(R.id.switch_animacion_chat);
        switch_chat_dinamico=view.findViewById(R.id.switch_chat_dinamico);
        switch_aviso_en_linea=view.findViewById(R.id.switch_aviso_en_linea);
        switch_segundo_plano=view.findViewById(R.id.switch_segundo_plano);
        switch_estado_progreso=view.findViewById(R.id.switch_estado_progreso);

        option_burbuja_datos = view.findViewById(R.id.option_burbuja_datos);
        switch_burbuja = view.findViewById(R.id.switch_burbuja);

        option_slider_estados=view.findViewById(R.id.option_slider_estados);
        tiempo_progreso=view.findViewById(R.id.tiempo_progreso);
        tv_mensaje_tam_limite=view.findViewById(R.id.tv_mensaje_tam_limite);
        tv_chat_tam_limite=view.findViewById(R.id.tv_chat_tam_limite);
        slider_post=view.findViewById(R.id.slider_post);
        seekBar_tiempo_progreso=view.findViewById(R.id.seekBar_tiempo_progreso);
        seekBar_slider_now_tam_limite=view.findViewById(R.id.seekBar_slider_now_tam_limite);
        seekBar_slider_chat_tam_limite=view.findViewById(R.id.seekBar_slider_chat_tam_limite);
        seekBar_slider_post=view.findViewById(R.id.seekBar_slider_post);

//        navController=Navigation.findNavController(view);
        ir_atras = view.findViewById(R.id.ir_atras);
        option_perfil = view.findViewById(R.id.option_perfil);
        option_actperfil = view.findViewById(R.id.option_actperfil);
        option_aviso_en_linea = view.findViewById(R.id.option_aviso_en_linea);
        option_segundo_plano = view.findViewById(R.id.option_segundo_plano);
        option_datos = view.findViewById(R.id.option_datos);
        option_ir_ajustes_buzon = view.findViewById(R.id.option_ir_ajustes_buzon);
        option_bloqueados = view.findViewById(R.id.option_bloqueados);
        option_bloqueados_post = view.findViewById(R.id.option_bloqueados_post);
        option_estadisticas = view.findViewById(R.id.option_estadisticas);
        option_copia_seguridad = view.findViewById(R.id.option_copia_seguridad);
        option_lectura = view.findViewById(R.id.option_lectura);
        option_animacion_chat = view.findViewById(R.id.option_animacion_chat);
        option_chat_dinamico = view.findViewById(R.id.option_chat_dinamico);
        option_admin_estados = view.findViewById(R.id.option_admin_estados);
        option_visibilidad_estados = view.findViewById(R.id.option_visibilidad_estados);
        option_progreso_estados = view.findViewById(R.id.option_progreso_estados);
        option_clear_inbox = view.findViewById(R.id.option_clear_inbox);
        option_saldo = view.findViewById(R.id.option_saldo);
        option_donar = view.findViewById(R.id.option_donar);
        option_silenciar_notificaciones = view.findViewById(R.id.option_silenciar_notificaciones);
        option_silenciar_sonidos = view.findViewById(R.id.option_silenciar_sonidos);
        option_slider_post = view.findViewById(R.id.option_slider_post);
        option_chat_responder = view.findViewById(R.id.option_chat_responder);
        option_activar_buzon = view.findViewById(R.id.option_activar_buzon);

        option_chat_security = view.findViewById(R.id.option_chat_security);
        option_post = view.findViewById(R.id.option_post);
        option_post_des_img = view.findViewById(R.id.option_post_des_img);
        option_post_borrar_24 = view.findViewById(R.id.option_post_borrar_24);
        option_post_historial = view.findViewById(R.id.option_post_historial);
        option_post_ver_historial = view.findViewById(R.id.option_post_ver_historial);
        option_chat_intro = view.findViewById(R.id.option_chat_intro);
        option_perfil_pub = view.findViewById(R.id.option_perfil_pub);
        option_chat_emoji_anim = view.findViewById(R.id.option_chat_emoji_anim);
        option_now_tam_limite = view.findViewById(R.id.option_now_tam_limite);
        option_slider_now_tam_limite = view.findViewById(R.id.option_slider_now_tam_limite);
        option_chat_tam_limite = view.findViewById(R.id.option_chat_tam_limite);
        option_slider_chat_tam_limite = view.findViewById(R.id.option_slider_chat_tam_limite);

        option_chat_intro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(modoPreguntar){
                    modoPreguntar=false;
                    icon_preguntar.setSupportImageTintList(stateListNone);
                    BottomSheetDialogFragment bsdFragment = BottomSheetDialogFragment_Ayuda_ajustes
                            .newInstance("Enviar con tecla Intro", getResources().getString(R.string.explicarIntro));
                    bsdFragment.show(getActivity().getSupportFragmentManager(), "BSDialogAyudaAjustes");
                }
                else{
                    YouChatApplication.setEnviarEnter(!YouChatApplication.enviarEnter);
                    switch_chat_intro.setChecked(YouChatApplication.enviarEnter);

                }
            }
        });

        option_chat_responder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(modoPreguntar){
                    modoPreguntar=false;
                    icon_preguntar.setSupportImageTintList(stateListNone);
                    BottomSheetDialogFragment bsdFragment = BottomSheetDialogFragment_Ayuda_ajustes
                            .newInstance("Invertir respondido chat", getResources().getString(R.string.explicarInvertirRespondido));
                    bsdFragment.show(getActivity().getSupportFragmentManager(), "BSDialogAyudaAjustes");
                }
                else{
                    YouChatApplication.setInvertirResChat(!YouChatApplication.invertirResChat);
                    switch_chat_responder.setChecked(YouChatApplication.invertirResChat);
                }
            }
        });
        option_activar_buzon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(modoPreguntar){
                    modoPreguntar=false;
                    icon_preguntar.setSupportImageTintList(stateListNone);
                    BottomSheetDialogFragment bsdFragment = BottomSheetDialogFragment_Ayuda_ajustes
                            .newInstance("Activar buzón", getResources().getString(R.string.explicarActivarBuzon));
                    bsdFragment.show(getActivity().getSupportFragmentManager(), "BSDialogAyudaAjustes");
                }
                else{
                    YouChatApplication.setActivarBuzon(!YouChatApplication.activarBuzon);
                    switch_activar_buzon.setChecked(YouChatApplication.activarBuzon);
                }
            }
        });

        option_post_limite = view.findViewById(R.id.option_post_limite);
        option_post_limite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(modoPreguntar){
                    modoPreguntar=false;
                    icon_preguntar.setSupportImageTintList(stateListNone);
                    BottomSheetDialogFragment bsdFragment = BottomSheetDialogFragment_Ayuda_ajustes
                            .newInstance("Limitar cantidad de Post", getResources().getString(R.string.explicarLimitePost));
                    bsdFragment.show(getActivity().getSupportFragmentManager(), "BSDialogAyudaAjustes");
                }
                else{
                    YouChatApplication.setLimitarPost(!YouChatApplication.limitarPost);
                    if(YouChatApplication.limitarPost){
                        switch_post_limite.setChecked(true);
                        option_slider_post.setVisibility(View.VISIBLE);
                    }
                    else {
                        switch_post_limite.setChecked(false);
                        option_slider_post.setVisibility(View.GONE);
                    }
                }
            }
        });

        option_chat_security.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(modoPreguntar){
                    modoPreguntar=false;
                    icon_preguntar.setSupportImageTintList(stateListNone);
                    BottomSheetDialogFragment bsdFragment = BottomSheetDialogFragment_Ayuda_ajustes
                            .newInstance("Encriptar mensajes", getResources().getString(R.string.explicarEncript));
                    bsdFragment.show(getActivity().getSupportFragmentManager(), "BSDialogAyudaAjustes");
                }
                else{
                    YouChatApplication.setChat_security(!YouChatApplication.chat_security);
                    switch_chat_security.setChecked(YouChatApplication.chat_security);

                }
            }
        });

        option_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(modoPreguntar){
                    modoPreguntar=false;
                    icon_preguntar.setSupportImageTintList(stateListNone);
                    BottomSheetDialogFragment bsdFragment = BottomSheetDialogFragment_Ayuda_ajustes
                            .newInstance("Post", getResources().getString(R.string.explicarPost));
                    bsdFragment.show(getActivity().getSupportFragmentManager(), "BSDialogAyudaAjustes");
                }
                else{
                    YouChatApplication.setActivePost(!YouChatApplication.activePost);
                    switch_post.setChecked(YouChatApplication.activePost);

                    if(YouChatApplication.principalActivity!=null)
                        YouChatApplication.principalActivity.reloadTab();
                    else {
                        MainActivity.mainActivity.finish();
                        startActivity(new Intent(context, MainActivity.class));
                    }
                }
            }
        });

        option_post_des_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(modoPreguntar){
                    modoPreguntar=false;
                    icon_preguntar.setSupportImageTintList(stateListNone);
                    BottomSheetDialogFragment bsdFragment = BottomSheetDialogFragment_Ayuda_ajustes
                            .newInstance("Descargar imágenes automáticamente", getResources().getString(R.string.explicarPostDesImg));
                    bsdFragment.show(getActivity().getSupportFragmentManager(), "BSDialogAyudaAjustes");
                }
                else{
                    YouChatApplication.setActivePostDesImg(!YouChatApplication.activePostDesImg);
                    switch_post_des_img.setChecked(YouChatApplication.activePostDesImg);
                }
            }
        });
        option_post_borrar_24.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(modoPreguntar){
                    modoPreguntar=false;
                    icon_preguntar.setSupportImageTintList(stateListNone);
                    BottomSheetDialogFragment bsdFragment = BottomSheetDialogFragment_Ayuda_ajustes
                            .newInstance("Eliminar cada 24 horas", getResources().getString(R.string.explicarPostBorrar24));
                    bsdFragment.show(getActivity().getSupportFragmentManager(), "BSDialogAyudaAjustes");
                }
                else{
                    YouChatApplication.setActivePostBorrar24(!YouChatApplication.activePostBorrar24);
                    switch_post_borrar_24.setChecked(YouChatApplication.activePostBorrar24);
                }
            }
        });
        option_chat_emoji_anim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(modoPreguntar){
                    modoPreguntar=false;
                    icon_preguntar.setSupportImageTintList(stateListNone);
                    BottomSheetDialogFragment bsdFragment = BottomSheetDialogFragment_Ayuda_ajustes
                            .newInstance("Emojis animados en el chat", getResources().getString(R.string.explicarEmojiAnimChat));
                    bsdFragment.show(getActivity().getSupportFragmentManager(), "BSDialogAyudaAjustes");
                }
                else{
                    YouChatApplication.setActiveEmojisAnimChat(!YouChatApplication.activeEmojisAnimChat);
                    switch_chat_emoji_anim.setChecked(YouChatApplication.activeEmojisAnimChat);
                }
            }
        });
        option_perfil_pub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(modoPreguntar){
                    modoPreguntar=false;
                    icon_preguntar.setSupportImageTintList(stateListNone);
                    BottomSheetDialogFragment bsdFragment = BottomSheetDialogFragment_Ayuda_ajustes
                            .newInstance("Perfil público", getResources().getString(R.string.explicarHacerPerfilPublico));
                    bsdFragment.show(getActivity().getSupportFragmentManager(), "BSDialogAyudaAjustes");
                }
                else{
                    if(YouChatApplication.estaAndandoChatService() && YouChatApplication.chatService.hayConex){
                        String info = YouChatApplication.obtenerStringInfoPerfil();
                        if(!info.isEmpty()){
                            YouChatApplication.setActivePerfilPub(!YouChatApplication.activePerfilPub);
                            switch_perfil_pub.setChecked(YouChatApplication.activePerfilPub);
                            ItemChat msg = new ItemChat("",(YouChatApplication.activePerfilPub?"1":"0"));
                            msg.setMensaje(info);
                            YouChatApplication.chatService.enviarMensaje(msg,SendMsg.CATEGORY_PERFIL_PUBLICO);
                        }
                        else Utils.ShowToastAnimated(mainActivity,"Ha ocurrido un error", R.raw.error);
                    }
                    else Utils.mostrarToastDeConexion(mainActivity);
                }
            }
        });
        option_post_historial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(modoPreguntar){
                    modoPreguntar=false;
                    icon_preguntar.setSupportImageTintList(stateListNone);
                    BottomSheetDialogFragment bsdFragment = BottomSheetDialogFragment_Ayuda_ajustes
                            .newInstance("Habilitar historial", getResources().getString(R.string.explicarPostHistorial));
                    bsdFragment.show(getActivity().getSupportFragmentManager(), "BSDialogAyudaAjustes");
                }
                else{
                    YouChatApplication.setActivePostHistorial(!YouChatApplication.activePostHistorial);

                    if(YouChatApplication.activePostHistorial){
                        option_post_ver_historial.setVisibility(View.VISIBLE);
                        switch_post_historial.setChecked(true);
                    }
                    else {
                        option_post_ver_historial.setVisibility(View.GONE);
                        switch_post_historial.setChecked(false);
                    }
                }
            }
        });
        option_now_tam_limite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(modoPreguntar){
                    modoPreguntar=false;
                    icon_preguntar.setSupportImageTintList(stateListNone);
                    BottomSheetDialogFragment bsdFragment = BottomSheetDialogFragment_Ayuda_ajustes
                            .newInstance("Descargar imágenes automáticamente",
                                    getResources().getString(R.string.explicarDescargarImagenNowAutomatico));
                    bsdFragment.show(getActivity().getSupportFragmentManager(), "BSDialogAyudaAjustes");
                }
                else{
                    YouChatApplication.setDescargaAutImagenNow(!YouChatApplication.descargaAutImagenNow);
                    if (YouChatApplication.descargaAutImagenNow) {
                        switch_now_tam_limite.setChecked(true);
                        option_slider_now_tam_limite.setVisibility(View.VISIBLE);
                    } else {
                        switch_now_tam_limite.setChecked(false);
                        option_slider_now_tam_limite.setVisibility(View.GONE);
                    }
                }
            }
        });
        option_chat_tam_limite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(modoPreguntar){
                    modoPreguntar=false;
                    icon_preguntar.setSupportImageTintList(stateListNone);
                    BottomSheetDialogFragment bsdFragment = BottomSheetDialogFragment_Ayuda_ajustes
                            .newInstance("Descargar multimedia automáticamente",
                                    getResources().getString(R.string.explicarDescargarMultimediaChatAutomatico));
                    bsdFragment.show(getActivity().getSupportFragmentManager(), "BSDialogAyudaAjustes");
                }
                else{
                    YouChatApplication.setDescargaAutMultimediaChat(!YouChatApplication.descargaAutMultimediaChat);
                    if (YouChatApplication.descargaAutMultimediaChat) {
                        switch_chat_tam_limite.setChecked(true);
                        option_slider_chat_tam_limite.setVisibility(View.VISIBLE);
                    } else {
                        switch_chat_tam_limite.setChecked(false);
                        option_slider_chat_tam_limite.setVisibility(View.GONE);
                    }
                }
            }
        });
        option_post_ver_historial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(modoPreguntar){
                    modoPreguntar=false;
                    icon_preguntar.setSupportImageTintList(stateListNone);
                    BottomSheetDialogFragment bsdFragment = BottomSheetDialogFragment_Ayuda_ajustes
                            .newInstance("Ver historial", getResources().getString(R.string.explicarPostVerHistorial));
                    bsdFragment.show(getActivity().getSupportFragmentManager(), "BSDialogAyudaAjustes");
                }
                else{
                    if(mAddFragmentListener!=null)
                        mAddFragmentListener.onAddFragment(AjustesActivity.this, new HistorialPostFragment());
                }
            }
        });


        ir_atras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(modoPreguntar){
                    modoPreguntar=false;
                    icon_preguntar.setSupportImageTintList(stateListNone);
                    BottomSheetDialogFragment bsdFragment = BottomSheetDialogFragment_Ayuda_ajustes
                            .newInstance("Botón de ir atrás", "Con este botón se regresa a la pantalla de conversaciones");
                    bsdFragment.show(getActivity().getSupportFragmentManager(), "BSDialogAyudaAjustes");
                }
                else getActivity().onBackPressed();
                //Navigation.findNavController(v).navigateUp();
            }
        });
        option_perfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(modoPreguntar){
                    modoPreguntar=false;
                    icon_preguntar.setSupportImageTintList(stateListNone);
                    BottomSheetDialogFragment bsdFragment = BottomSheetDialogFragment_Ayuda_ajustes
                            .newInstance("Editar perfil", getResources().getString(R.string.explicarPerfil));
                    bsdFragment.show(getActivity().getSupportFragmentManager(), "BSDialogAyudaAjustes");
                }
                else {
                    if(mAddFragmentListener!=null) mAddFragmentListener.onAddFragment(AjustesActivity.this, new ViewYouPerfilActivity());
                    //navController.navigate(R.id.viewYouPerfilActivity);
                }
            }
        });
        option_actperfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(modoPreguntar){
                    modoPreguntar=false;
                    icon_preguntar.setSupportImageTintList(stateListNone);
                    BottomSheetDialogFragment bsdFragment = BottomSheetDialogFragment_Ayuda_ajustes
                            .newInstance("Actualizaciones de perfil", getResources().getString(R.string.explicarActPerfil));
                    bsdFragment.show(getActivity().getSupportFragmentManager(), "BSDialogAyudaAjustes");
                }
                else{
                    YouChatApplication.setActualizar_perfil(!YouChatApplication.actualizar_perfil);
                    switch_actperfil.setChecked(YouChatApplication.actualizar_perfil);

                }
            }
        });
        option_aviso_en_linea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(modoPreguntar){
                    modoPreguntar=false;
                    icon_preguntar.setSupportImageTintList(stateListNone);
                    BottomSheetDialogFragment bsdFragment = BottomSheetDialogFragment_Ayuda_ajustes
                            .newInstance("Aviso en línea", getResources().getString(R.string.explicarEnLinea));
                    bsdFragment.show(getActivity().getSupportFragmentManager(), "BSDialogAyudaAjustes");
                }
                else {
                    YouChatApplication.setAvisar_en_linea(!YouChatApplication.avisar_en_linea);
                    switch_aviso_en_linea.setChecked(YouChatApplication.avisar_en_linea);
                }
            }
        });
        option_segundo_plano.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(modoPreguntar){
                    modoPreguntar=false;
                    icon_preguntar.setSupportImageTintList(stateListNone);
                    BottomSheetDialogFragment bsdFragment = BottomSheetDialogFragment_Ayuda_ajustes
                            .newInstance("Forzar segundo plano", getResources().getString(R.string.explicarSegundoPlano));
                    bsdFragment.show(getActivity().getSupportFragmentManager(), "BSDialogAyudaAjustes");
                }
                else{
                    try{
                        YouChatApplication.setEsta_segundo_plano(!YouChatApplication.esta_segundo_plano);
                        if(YouChatApplication.esta_segundo_plano){
                            switch_segundo_plano.setChecked(true);
                            if(YouChatApplication.estaAndandoChatService())
                                YouChatApplication.chatService.activarSegundoPlano();
                        }
                        else {
                            switch_segundo_plano.setChecked(false);
                            if(YouChatApplication.estaAndandoChatService())
                                YouChatApplication.chatService.detenerSegundoPlano();
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                        YouChatApplication.setEsta_segundo_plano(false);
                        Utils.ShowToastAnimated(activity,"El trabajo en segundo plano aún no está disponible para su android",R.raw.chats_infotip);
                    }
                }
            }
        });
        option_datos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(modoPreguntar){
                    modoPreguntar=false;
                    icon_preguntar.setSupportImageTintList(stateListNone);
                    BottomSheetDialogFragment bsdFragment = BottomSheetDialogFragment_Ayuda_ajustes
                            .newInstance("Calidad de imágenes", getResources().getString(R.string.explicarDatos));
                    bsdFragment.show(getActivity().getSupportFragmentManager(), "BSDialogAyudaAjustes");
                }
                else{
                    Dialog dialog = new Dialog(context);
                    dialog.requestWindowFeature(1);
                    View mview=getLayoutInflater().inflate(R.layout.dialog_option_datos,null);
                    dialog.setContentView(mview);

                    ImageView img_muestra=mview.findViewById(R.id.img_muestra);
                    TextView tam_aprox=mview.findViewById(R.id.tam_aprox);
                    View btn_aceptar=mview.findViewById(R.id.btn_aceptar);
                    final Slider seekBar_calidad_img = mview.findViewById(R.id.seekBar_calidad_img);
                    final TextView text_seek = mview.findViewById(R.id.text_seek);
                    calidad=(YouChatApplication.calidad/10)*10;

                    int x = calidad/10;
                    if(x==1){
                        img_muestra.setImageResource(R.drawable.cal10);
                        tam_aprox.setText("12.19 KB");
                    }
                    else if(x==2){
                        img_muestra.setImageResource(R.drawable.cal20);
                        tam_aprox.setText("17.06 KB");
                    }
                    else if(x==3){
                        img_muestra.setImageResource(R.drawable.cal30);
                        tam_aprox.setText("21.29 KB");
                    }
                    else if(x==4){
                        img_muestra.setImageResource(R.drawable.cal40);
                        tam_aprox.setText("24.91 KB");
                    }
                    else if(x==5){
                        img_muestra.setImageResource(R.drawable.cal50);
                        tam_aprox.setText("28.41 KB");
                    }
                    else if(x==6){
                        img_muestra.setImageResource(R.drawable.cal60);
                        tam_aprox.setText("32.21 KB");
                    }
                    else if(x==7){
                        img_muestra.setImageResource(R.drawable.cal70);
                        tam_aprox.setText("38.12 KB");
                    }
                    else if(x==8){
                        img_muestra.setImageResource(R.drawable.cal80);
                        tam_aprox.setText("47.92 KB");
                    }
                    else if(x==9){
                        img_muestra.setImageResource(R.drawable.cal90);
                        tam_aprox.setText("71.12 KB");
                    }
                    else if(x==10){
                        img_muestra.setImageResource(R.drawable.cal100);
                        tam_aprox.setText("240.56 KB");
                    }
                    seekBar_calidad_img.setValue(calidad);
                    text_seek.setText(calidad+"%");
                    btn_aceptar.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                            calidad=(int)seekBar_calidad_img.getValue();
                            if(calidad<10) calidad=10;
                            else if(calidad>100) calidad=100;
                            YouChatApplication.setCalidad(calidad);
                            tv_calidad_img.setText(YouChatApplication.calidad+"%");
                        }
                    });

                    seekBar_calidad_img.addOnChangeListener(new Slider.OnChangeListener() {
                        @Override
                        public void onValueChange(@NonNull Slider slider, float value, boolean fromUser) {
                            if(seekBar_calidad_img.getValue()<10) slider.setValue(10);
                            String str=seekBar_calidad_img.getValue()+"%";
                            text_seek.setText(str);
                        }
                    });
                    seekBar_calidad_img.addOnSliderTouchListener(new Slider.OnSliderTouchListener() {
                        @Override
                        public void onStartTrackingTouch(@NonNull Slider slider) {}
                        @Override
                        public void onStopTrackingTouch(@NonNull Slider slider) {
                            int progress=(int)seekBar_calidad_img.getValue();
                            int x = Math.round((float)progress/10);
                            if(x==1){
                                img_muestra.setImageResource(R.drawable.cal10);
                                tam_aprox.setText("12.19 KB");
                            }
                            else if(x==2){
                                img_muestra.setImageResource(R.drawable.cal20);
                                tam_aprox.setText("17.06 KB");
                            }
                            else if(x==3){
                                img_muestra.setImageResource(R.drawable.cal30);
                                tam_aprox.setText("21.29 KB");
                            }
                            else if(x==4){
                                img_muestra.setImageResource(R.drawable.cal40);
                                tam_aprox.setText("24.91 KB");
                            }
                            else if(x==5){
                                img_muestra.setImageResource(R.drawable.cal50);
                                tam_aprox.setText("28.41 KB");
                            }
                            else if(x==6){
                                img_muestra.setImageResource(R.drawable.cal60);
                                tam_aprox.setText("32.21 KB");
                            }
                            else if(x==7){
                                img_muestra.setImageResource(R.drawable.cal70);
                                tam_aprox.setText("38.12 KB");
                            }
                            else if(x==8){
                                img_muestra.setImageResource(R.drawable.cal80);
                                tam_aprox.setText("47.92 KB");
                            }
                            else if(x==9){
                                img_muestra.setImageResource(R.drawable.cal90);
                                tam_aprox.setText("71.12 KB");
                            }
                            else if(x==10){
                                img_muestra.setImageResource(R.drawable.cal100);
                                tam_aprox.setText("240.56 KB");
                            }
                        }
                    });
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
                    dialog.setCancelable(true);
                    dialog.show();
                }
            }
        });
        option_ir_ajustes_buzon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(modoPreguntar){
                    modoPreguntar=false;
                    icon_preguntar.setSupportImageTintList(stateListNone);
                    BottomSheetDialogFragment bsdFragment = BottomSheetDialogFragment_Ayuda_ajustes
                            .newInstance("Ajustes del buzón", getResources().getString(R.string.explicarAjustesBuzon));
                    bsdFragment.show(getActivity().getSupportFragmentManager(), "BSDialogAyudaAjustes");
                }
                else {
                    if(mAddFragmentListener!=null)
                        mAddFragmentListener.onAddFragment(AjustesActivity.this, new AjustesBuzonFragment());
                }
            }
        });
        option_bloqueados.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(modoPreguntar){
                    modoPreguntar=false;
                    icon_preguntar.setSupportImageTintList(stateListNone);
                    BottomSheetDialogFragment bsdFragment = BottomSheetDialogFragment_Ayuda_ajustes
                            .newInstance("Bloqueados", getResources().getString(R.string.explicarBloqueados));
                    bsdFragment.show(getActivity().getSupportFragmentManager(), "BSDialogAyudaAjustes");
                }
                else {
                    if(mAddFragmentListener!=null) mAddFragmentListener.onAddFragment(AjustesActivity.this, new BloqueadosActivity());

                }
            }
        });
        option_bloqueados_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(modoPreguntar){
                    modoPreguntar=false;
                    icon_preguntar.setSupportImageTintList(stateListNone);
                    BottomSheetDialogFragment bsdFragment = BottomSheetDialogFragment_Ayuda_ajustes
                            .newInstance("Bloqueados Post", getResources().getString(R.string.explicarBloqueadosPost));
                    bsdFragment.show(getActivity().getSupportFragmentManager(), "BSDialogAyudaAjustes");
                }
                else {
                    if(mAddFragmentListener!=null) mAddFragmentListener.onAddFragment(AjustesActivity.this, new BloqueadosPostFragment());
                }
            }
        });
        option_estadisticas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(modoPreguntar){
                    modoPreguntar=false;
                    icon_preguntar.setSupportImageTintList(stateListNone);
                    BottomSheetDialogFragment bsdFragment = BottomSheetDialogFragment_Ayuda_ajustes
                            .newInstance("Estadísticas", getResources().getString(R.string.explicarEstadisticas));
                    bsdFragment.show(getActivity().getSupportFragmentManager(), "BSDialogAyudaAjustes");
                }
                else if(mAddFragmentListener!=null)
                    mAddFragmentListener.onAddFragment(AjustesActivity.this, new EstadisticasFragment());
//                    navController.navigate(R.id.estadisticasFragment);
            }
        });
        option_copia_seguridad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(modoPreguntar){
                    modoPreguntar=false;
                    icon_preguntar.setSupportImageTintList(stateListNone);
                    BottomSheetDialogFragment bsdFragment = BottomSheetDialogFragment_Ayuda_ajustes
                            .newInstance("Copia de seguridad", getResources().getString(R.string.explicarCopiadeSeguridad));
                    bsdFragment.show(getActivity().getSupportFragmentManager(), "BSDialogAyudaAjustes");
                }
                else {
                    if(mAddFragmentListener!=null)
                        mAddFragmentListener.onAddFragment(AjustesActivity.this, new CopiaSeguridadFragment());
                }
//                else{
//                    final Dialog dialog = new Dialog(context);
//                    dialog.requestWindowFeature(1);
//                    View mview=getLayoutInflater().inflate(R.layout.dialog_confirm_animado,null);
//                    dialog.setContentView(mview);
//
//                    LottieAnimationView animation=mview.findViewById(R.id.animation);
//                    TextView text_icono = mview.findViewById(R.id.text_icono);
//                    TextView text_eliminar = mview.findViewById(R.id.text_eliminar);
//                    TextView btn_cancel=mview.findViewById(R.id.btn_cancel);
//                    TextView btn_ok=mview.findViewById(R.id.btn_ok);
//
//                    animation.setAnimation(R.raw.filter_new);
//                    text_icono.setText("Copia de seguridad");
//                    text_eliminar.setText("Realice una salva de sus datos con frecuencia para no perder sus chats y seguidores nunca más.");
//                    btn_cancel.setText("GUARDAR");
//                    btn_ok.setText("CARGAR");
//
//                    String bdSalva = YouChatApplication.RUTA_COPIA_BASE_DATOS+"YouChat_BDatos.dbyc";
//                    File existBD = new File(bdSalva);
//                    if(!existBD.exists()) btn_ok.setVisibility(View.GONE);
//
//                    btn_cancel.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            dialog.dismiss();
//                            exportarBaseDatos();
//                        }
//                    });
//
//                    btn_ok.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            dialog.dismiss();
//                            importarBaseDatos();
//                        }
//                    });
//
//                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
//                    dialog.setCancelable(true);
//                    dialog.show();
//                }
            }
        });
        option_burbuja_datos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(modoPreguntar){
                    modoPreguntar=false;
                    icon_preguntar.setSupportImageTintList(stateListNone);
                    BottomSheetDialogFragment bsdFragment = BottomSheetDialogFragment_Ayuda_ajustes
                            .newInstance("Burbuja de datos", getResources().getString(R.string.explicarBurbujaDatos));
                    bsdFragment.show(getActivity().getSupportFragmentManager(), "BSDialogAyudaAjustes");
                }
                else{
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(context)) {
                        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + context.getPackageName()));
                        startActivity(intent);
                    }
                    else if(YouChatApplication.estaAndandoChatService()){
                        YouChatApplication.setBurbuja_datos(!YouChatApplication.burbuja_datos);
                        if(YouChatApplication.burbuja_datos) {
                            if(YouChatApplication.chatService.hayConex)
                                YouChatApplication.chatService.buildWidget();
                            switch_burbuja.setChecked(true);
                        }
                        else {
                            YouChatApplication.chatService.removeWidget();
                            switch_burbuja.setChecked(false);
                        }
                    }
                }
            }
        });
        option_lectura.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(modoPreguntar){
                    modoPreguntar=false;
                    icon_preguntar.setSupportImageTintList(stateListNone);
                    BottomSheetDialogFragment bsdFragment = BottomSheetDialogFragment_Ayuda_ajustes
                            .newInstance("Acuse de recibo", getResources().getString(R.string.explicarLectura));
                    bsdFragment.show(getActivity().getSupportFragmentManager(), "BSDialogAyudaAjustes");
                }
                else{
                    YouChatApplication.setLectura(!YouChatApplication.lectura);
                    switch_lectura.setChecked(YouChatApplication.lectura);
                }
            }
        });
        option_animacion_chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(modoPreguntar){
                    modoPreguntar=false;
                    icon_preguntar.setSupportImageTintList(stateListNone);
                    BottomSheetDialogFragment bsdFragment = BottomSheetDialogFragment_Ayuda_ajustes
                            .newInstance("Animaciones en el chat", getResources().getString(R.string.explicarAnimaciones));
                    bsdFragment.show(getActivity().getSupportFragmentManager(), "BSDialogAyudaAjustes");
                }
                else {

                    YouChatApplication.setAnimaciones_chat(!YouChatApplication.animaciones_chat);
                    switch_animacion_chat.setChecked(YouChatApplication.animaciones_chat);

                }
            }
        });
        option_chat_dinamico.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(modoPreguntar){
                    modoPreguntar=false;
                    icon_preguntar.setSupportImageTintList(stateListNone);
                    BottomSheetDialogFragment bsdFragment = BottomSheetDialogFragment_Ayuda_ajustes
                            .newInstance("Chat dinámico", getResources().getString(R.string.explicarDinamico));
                    bsdFragment.show(getActivity().getSupportFragmentManager(), "BSDialogAyudaAjustes");
                }
                else {
                    YouChatApplication.setEstado_personal(!YouChatApplication.estado_personal);
                    switch_chat_dinamico.setChecked(YouChatApplication.estado_personal);
                }
            }
        });
        option_admin_estados.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(modoPreguntar){
                    modoPreguntar=false;
                    icon_preguntar.setSupportImageTintList(stateListNone);
                    BottomSheetDialogFragment bsdFragment = BottomSheetDialogFragment_Ayuda_ajustes
                            .newInstance("Administrar estados", getResources().getString(R.string.explicarAdminEstados));
                    bsdFragment.show(getActivity().getSupportFragmentManager(), "BSDialogAyudaAjustes");
                }
                else{
                    if(mAddFragmentListener!=null) mAddFragmentListener.onAddFragment(AjustesActivity.this, new AdminEstadosActivity());
//                    navController.navigate(R.id.adminEstadosActivity);
                }
            }
        });
        option_visibilidad_estados.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(modoPreguntar){
                    modoPreguntar=false;
                    icon_preguntar.setSupportImageTintList(stateListNone);
                    BottomSheetDialogFragment bsdFragment = BottomSheetDialogFragment_Ayuda_ajustes
                            .newInstance("Aceptar solicitud", getResources().getString(R.string.explicarVisibilidadEstados));
                    bsdFragment.show(getActivity().getSupportFragmentManager(), "BSDialogAyudaAjustes");
                }
                else {
                    YouChatApplication.setSon_privados_estados(!YouChatApplication.son_privados_estados);
                    if(YouChatApplication.son_privados_estados) tv_visibilidad.setText("Manual");
                    else tv_visibilidad.setText("Automático");
                }
            }
        });
        option_progreso_estados.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(modoPreguntar){
                    modoPreguntar=false;
                    icon_preguntar.setSupportImageTintList(stateListNone);
                    BottomSheetDialogFragment bsdFragment = BottomSheetDialogFragment_Ayuda_ajustes
                            .newInstance("Progreso de estado", getResources().getString(R.string.explicarProgresoEstados));
                    bsdFragment.show(getActivity().getSupportFragmentManager(), "BSDialogAyudaAjustes");
                }
                else {
                    YouChatApplication.setActivar_progreso(!YouChatApplication.activar_progreso);
                    if(YouChatApplication.activar_progreso){
                        option_slider_estados.setVisibility(View.VISIBLE);
                        switch_estado_progreso.setChecked(true);
                    }
                    else {
                        option_slider_estados.setVisibility(View.GONE);
                        switch_estado_progreso.setChecked(false);
                    }
                }
            }
        });
        option_clear_inbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(modoPreguntar){
                    modoPreguntar=false;
                    icon_preguntar.setSupportImageTintList(stateListNone);
                    BottomSheetDialogFragment bsdFragment = BottomSheetDialogFragment_Ayuda_ajustes
                            .newInstance("Vaciar bandeja de entrada", getResources().getString(R.string.explicarVaciarBandeja));
                    bsdFragment.show(getActivity().getSupportFragmentManager(), "BSDialogAyudaAjustes");
                }
                else {
                    Dialog dialogo = new Dialog(context);
                    dialogo.requestWindowFeature(1);
                    View mview=getLayoutInflater().inflate(R.layout.dialog_confirm,null);
                    dialogo.setContentView(mview);

                    ImageView img=mview.findViewById(R.id.icono_eliminar);
                    TextView text_icono=mview.findViewById(R.id.text_icono);
                    TextView text_eliminar=mview.findViewById(R.id.text_eliminar);
                    View btn_cancel=mview.findViewById(R.id.btn_cancel);
                    View btn_ok=mview.findViewById(R.id.btn_ok);

                    img.setImageResource(R.drawable.option_inbox);
                    text_icono.setText("Vaciar bandeja");
                    text_eliminar.setText("¿Desea eliminar todos los correos de su bandeja de entrada?");

                    if(YouChatApplication.estaAndandoChatService() && YouChatApplication.chatService.hayConex){
                        tv_cant_msg_inbox.setVisibility(View.VISIBLE);
                        tv_cant_msg_inbox.setText(""+YouChatApplication.cant_msg_inbox);
                    }
                    else tv_cant_msg_inbox.setVisibility(View.GONE);

                    btn_ok.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(YouChatApplication.estaAndandoChatService()
                                    && YouChatApplication.chatService.hayConex){
                                dialogo.dismiss();

                                dialogoInbox = new Dialog(context);
                                dialogoInbox.requestWindowFeature(1);
                                View mviewe=getLayoutInflater().inflate(R.layout.dialog_clear_inbox_progress,null);
                                dialogoInbox.setContentView(mviewe);

                                TextViewFontGenGI texto_cant = mviewe.findViewById(R.id.texto_cant);
                                DownloadProgressView progressbar_vaciar_inbox = mviewe.findViewById(R.id.progressbar_vaciar_inbox);

                        /*texto_alerta=mviewe.findViewById(R.id.texto_alerta);
                        texto_alerta.setText("Vaciando bandeja de entrada...");*/

                                dialogoInbox.getWindow().setBackgroundDrawable(new ColorDrawable(0));
                                dialogoInbox.setCancelable(false);
                                dialogoInbox.show();

                                progressbar_vaciar_inbox.quitarClick();
                                progressbar_vaciar_inbox.setDownloading(true);
                                progressbar_vaciar_inbox.setProgress(0f);

                                YouChatApplication.chatService.VaciarInbox(ajustesActivity,
                                        texto_cant, progressbar_vaciar_inbox);
                            }
                            else Utils.ShowToastAnimated(activity,"Compruebe su conexión",R.raw.ic_ban);
                        }
                    });
                    btn_cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialogo.dismiss();
                        }
                    });

                    dialogo.getWindow().setBackgroundDrawable(new ColorDrawable(0));
                    dialogo.setCancelable(true);
                    dialogo.show();
                }
            }
        });
        option_saldo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(modoPreguntar){
                    modoPreguntar=false;
                    icon_preguntar.setSupportImageTintList(stateListNone);
                    BottomSheetDialogFragment bsdFragment = BottomSheetDialogFragment_Ayuda_ajustes
                            .newInstance("Plan de datos", getResources().getString(R.string.explicarPlan));
                    bsdFragment.show(getActivity().getSupportFragmentManager(), "BSDialogAyudaAjustes");
                }
                else {
                    startActivity(new Intent("android.intent.action.CALL", Uri.parse("tel:" + ("*222*328" + Uri.encode("#")))));
                }
            }
        });
        option_donar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(modoPreguntar){
                    modoPreguntar=false;
                    icon_preguntar.setSupportImageTintList(stateListNone);
                    BottomSheetDialogFragment bsdFragment = BottomSheetDialogFragment_Ayuda_ajustes
                            .newInstance("Haga una donación", getResources().getString(R.string.explicarDonación));
                    bsdFragment.show(getActivity().getSupportFragmentManager(), "BSDialogAyudaAjustes");
                }
                else {
                    Dialog dialog = new Dialog(context);
                    dialog.requestWindowFeature(1);
                    View mview=getLayoutInflater().inflate(R.layout.dialog_option_donar,null);
                    dialog.setContentView(mview);

                    View btn_cancel=mview.findViewById(R.id.btn_cancel);
                    View btn_ok=mview.findViewById(R.id.btn_ok);
                    final EditText monto=mview.findViewById(R.id.input_monto);
                    final EditText pin=mview.findViewById(R.id.input_pin);

                    btn_ok.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                            startActivity(new Intent("android.intent.action.CALL", Uri.parse("tel:" + ("*234*1*56166352*"+pin.getText().toString()+"*"+monto.getText().toString()+ Uri.encode("#")))));
                        }
                    });
                    btn_cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
                    dialog.setCancelable(true);
                    dialog.show();
                }
            }
        });
        option_silenciar_notificaciones.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(modoPreguntar){
                    modoPreguntar=false;
                    icon_preguntar.setSupportImageTintList(stateListNone);
                    BottomSheetDialogFragment bsdFragment = BottomSheetDialogFragment_Ayuda_ajustes
                            .newInstance("Notificaciones", getResources().getString(R.string.explicarNotificacion));
                    bsdFragment.show(getActivity().getSupportFragmentManager(), "BSDialogAyudaAjustes");
                }
                else {
                    YouChatApplication.setNotificacion(!YouChatApplication.notificacion);
                    if(YouChatApplication.notificacion){
                        contenedor_notificacion.setVisibility(View.VISIBLE);
                        switch_notificaciones.setChecked(true);
                    }
                    else{
                        contenedor_notificacion.setVisibility(View.GONE);
                        switch_notificaciones.setChecked(false);
                    }
                }
            }
        });
        option_silenciar_sonidos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(modoPreguntar){
                    modoPreguntar=false;
                    icon_preguntar.setSupportImageTintList(stateListNone);
                    BottomSheetDialogFragment bsdFragment = BottomSheetDialogFragment_Ayuda_ajustes
                            .newInstance("Sonidos", getResources().getString(R.string.explicarSonidos));
                    bsdFragment.show(getActivity().getSupportFragmentManager(), "BSDialogAyudaAjustes");
                }
                else {
                    YouChatApplication.setSonido(!YouChatApplication.sonido);
                    switch_sonidos.setChecked(YouChatApplication.sonido);

                }
            }
        });

        tv_chat_tam_limite.setText(Utils.convertirBytes(YouChatApplication.tam_max_descarga_chat*1024));
        seekBar_slider_chat_tam_limite.setValue(YouChatApplication.tam_max_descarga_chat);
        seekBar_slider_chat_tam_limite.setLabelFormatter(new LabelFormatter() {
            @NonNull
            @Override
            public String getFormattedValue(float value) {
                int x = (int) value;
                return Utils.convertirBytes(x*1024);
            }
        });
        seekBar_slider_chat_tam_limite.addOnSliderTouchListener(new Slider.OnSliderTouchListener() {
            @Override
            public void onStartTrackingTouch(@NonNull Slider slider) {
            }

            @Override
            public void onStopTrackingTouch(@NonNull Slider slider) {
                int x = (int) slider.getValue();
                YouChatApplication.setTam_max_descarga_chat(x);
                tv_chat_tam_limite.setText(Utils.convertirBytes(x*1024));
            }
        });

        tv_mensaje_tam_limite.setText(Utils.convertirBytes(YouChatApplication.tam_max_descarga_now*1024));
        seekBar_slider_now_tam_limite.setValue(YouChatApplication.tam_max_descarga_now);
        seekBar_slider_now_tam_limite.setLabelFormatter(new LabelFormatter() {
            @NonNull
            @Override
            public String getFormattedValue(float value) {
                int x = (int) value;
                return Utils.convertirBytes(x*1024);
            }
        });
        seekBar_slider_now_tam_limite.addOnSliderTouchListener(new Slider.OnSliderTouchListener() {
            @Override
            public void onStartTrackingTouch(@NonNull Slider slider) {
            }

            @Override
            public void onStopTrackingTouch(@NonNull Slider slider) {
                int x = (int) slider.getValue();
                YouChatApplication.setTam_max_descarga_now(x);
                tv_mensaje_tam_limite.setText(Utils.convertirBytes(x*1024));
            }
        });

        tiempo_progreso.setText(String.format("%02d seg.",YouChatApplication.tiempo_progreso));
        seekBar_tiempo_progreso.setValue(YouChatApplication.tiempo_progreso);

        seekBar_tiempo_progreso.setLabelFormatter(new LabelFormatter() {
            @NonNull
            @Override
            public String getFormattedValue(float value) {
                int x=(int)value;
                return x+"seg.";
            }
        });
        seekBar_tiempo_progreso.addOnSliderTouchListener(new Slider.OnSliderTouchListener() {
            @Override
            public void onStartTrackingTouch(@NonNull Slider slider) {}

            @Override
            public void onStopTrackingTouch(@NonNull Slider slider) {
                YouChatApplication.setTiempo_progreso((int)slider.getValue());
                tiempo_progreso.setText(String.format("%02d seg.",(int)slider.getValue()));
            }
        });


        if(YouChatApplication.cantLimitePost<100) slider_post.setText(" "+YouChatApplication.cantLimitePost);
        else slider_post.setText(""+YouChatApplication.cantLimitePost);
        seekBar_slider_post.setValue(YouChatApplication.cantLimitePost);

        seekBar_slider_post.setLabelFormatter(new LabelFormatter() {
            @NonNull
            @Override
            public String getFormattedValue(float value) {
                int x=(int)value;
                return x+"";
            }
        });
        seekBar_slider_post.addOnSliderTouchListener(new Slider.OnSliderTouchListener() {
            @Override
            public void onStartTrackingTouch(@NonNull Slider slider) {}

            @Override
            public void onStopTrackingTouch(@NonNull Slider slider) {
                int x =(int)slider.getValue();
                YouChatApplication.setCantLimitePost(x);
                if(x<100) slider_post.setText(" "+x);
                else slider_post.setText(""+x);
            }
        });

        icon_preguntar=view.findViewById(R.id.icon_pregunta);
        tv_visibilidad=view.findViewById(R.id.tv_visibilidad);

        icon_preguntar.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onClick(View v) {
                v.setEnabled(false);
                modoPreguntar=!modoPreguntar;
                if(modoPreguntar){

                    icon_preguntar.setSupportImageTintList(stateListAccent);
                    // icon_preguntar.setBackgroundResource(R.drawable.shape_icon_verde);
                    Utils.ShowToastAnimated(activity,"Elija una opción para obtener su explicación",R.raw.chats_infotip);
                }
                else icon_preguntar.setSupportImageTintList(stateListNone);
                //icon_preguntar.setSupportImageTintList(stateListNone);
                v.setEnabled(true);
            }
        });

        if(YouChatApplication.limitarPost){
            switch_post_limite.setChecked(true);
            option_slider_post.setVisibility(View.VISIBLE);
        }
        else {
            switch_post_limite.setChecked(false);
            option_slider_post.setVisibility(View.GONE);
        }

        switch_activar_buzon.setChecked(YouChatApplication.activarBuzon);
        switch_chat_responder.setChecked(YouChatApplication.invertirResChat);
        switch_chat_intro.setChecked(YouChatApplication.enviarEnter);
        switch_post.setChecked(YouChatApplication.activePost);
        switch_post_des_img.setChecked(YouChatApplication.activePostDesImg);
        switch_post_borrar_24.setChecked(YouChatApplication.activePostBorrar24);
        switch_chat_security.setChecked(YouChatApplication.chat_security);
        switch_burbuja.setChecked(YouChatApplication.burbuja_datos);
        switch_actperfil.setChecked(YouChatApplication.actualizar_perfil);
        switch_lectura.setChecked(YouChatApplication.lectura);
        switch_sonidos.setChecked(YouChatApplication.sonido);
        switch_animacion_chat.setChecked(YouChatApplication.animaciones_chat);
        switch_chat_dinamico.setChecked(YouChatApplication.estado_personal);
        switch_aviso_en_linea.setChecked(YouChatApplication.avisar_en_linea);
        switch_segundo_plano.setChecked(YouChatApplication.esta_segundo_plano);
        switch_perfil_pub.setChecked(YouChatApplication.activePerfilPub);
        switch_chat_emoji_anim.setChecked(YouChatApplication.activeEmojisAnimChat);
        if(YouChatApplication.activePostHistorial){
            option_post_ver_historial.setVisibility(View.VISIBLE);
            switch_post_historial.setChecked(true);
        }
        else {
            option_post_ver_historial.setVisibility(View.GONE);
            switch_post_historial.setChecked(false);
        }
        if(YouChatApplication.descargaAutImagenNow){
            option_slider_now_tam_limite.setVisibility(View.VISIBLE);
            switch_now_tam_limite.setChecked(true);
        }
        else {
            option_slider_now_tam_limite.setVisibility(View.GONE);
            switch_now_tam_limite.setChecked(false);
        }
        if (YouChatApplication.descargaAutMultimediaChat) {
            switch_chat_tam_limite.setChecked(true);
            option_slider_chat_tam_limite.setVisibility(View.VISIBLE);
        } else {
            switch_chat_tam_limite.setChecked(false);
            option_slider_chat_tam_limite.setVisibility(View.GONE);
        }
        if(YouChatApplication.notificacion){
            contenedor_notificacion.setVisibility(View.VISIBLE);
            switch_notificaciones.setChecked(true);
        }
        else{
            contenedor_notificacion.setVisibility(View.GONE);
            switch_notificaciones.setChecked(false);
        }
        if(YouChatApplication.son_privados_estados) tv_visibilidad.setText("Manual");
        else tv_visibilidad.setText("Automático");
        if(YouChatApplication.activar_progreso) switch_estado_progreso.setChecked(true);
        else {
            option_slider_estados.setVisibility(View.GONE);
            switch_estado_progreso.setChecked(false);
        }

        checkbox_notificacion_chat.setChecked(YouChatApplication.notiMenChat);
        checkbox_notificacion_correos.setChecked(YouChatApplication.notiCorreoEnt);
        checkbox_notificacion_now.setChecked(YouChatApplication.notiNowEnt);
        checkbox_notificacion_reaccion.setChecked(YouChatApplication.notiReacNow);

        checkbox_notificacion_chat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                YouChatApplication.setNotiMenChat(isChecked);
                checkbox_notificacion_chat.setChecked(isChecked);
            }
        });
        checkbox_notificacion_correos.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                YouChatApplication.setNotiCorreoEnt(isChecked);
                checkbox_notificacion_correos.setChecked(isChecked);
            }
        });
        checkbox_notificacion_now.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                YouChatApplication.setNotiNowEnt(isChecked);
                checkbox_notificacion_now.setChecked(isChecked);
            }
        });
        checkbox_notificacion_reaccion.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                YouChatApplication.setNotiReacNow(isChecked);
                checkbox_notificacion_reaccion.setChecked(isChecked);
            }
        });

        tv_calidad_img=view.findViewById(R.id.tv_calidad_img);
        tv_cant_msg_inbox=view.findViewById(R.id.tv_cant_msg_inbox);

        tv_calidad_img.setText(YouChatApplication.calidad+"%");

        if(YouChatApplication.estaAndandoChatService() && YouChatApplication.chatService.hayConex){
            tv_cant_msg_inbox.setVisibility(View.VISIBLE);
            tv_cant_msg_inbox.setText(""+YouChatApplication.cant_msg_inbox);
        }
        else tv_cant_msg_inbox.setVisibility(View.GONE);
    }

    private void comprobarOptimizacionBateria() {
        Utils.runOnUIThread(()->{
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                PowerManager pm = (PowerManager) context.getSystemService(POWER_SERVICE);
                if(!pm.isIgnoringBatteryOptimizations(context.getPackageName())){
                    if(card_bateria.getVisibility()==View.GONE) {
                        Animation anim = AnimationUtils.loadAnimation(context, R.anim.fade_in_fast);
                        anim.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {}

                            @Override
                            public void onAnimationEnd(Animation animation) {
                                animation = AnimationUtils.loadAnimation(context, R.anim.anim_rebote);
                                animation.setDuration(500);
                                animation.setRepeatMode(Animation.REVERSE);
                                animation.setRepeatCount(2);
                                card_bateria.startAnimation(animation);
                                Utils.vibrate(context, 80);
                            }
                            @Override
                            public void onAnimationRepeat(Animation animation) { }
                        });
                        Utils.vibrate(context, 80);
                        card_bateria.setVisibility(View.VISIBLE);
                        card_bateria.startAnimation(anim);
                    }
                }
                else if(card_bateria.getVisibility()==View.VISIBLE){
                    Animation anim = AnimationUtils.loadAnimation(context, R.anim.fade_out);
                    anim.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {}

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            card_bateria.setVisibility(View.GONE);
                        }
                        @Override
                        public void onAnimationRepeat(Animation animation) { }
                    });
                    card_bateria.startAnimation(anim);
                }
            }
        }, 3000);
    }

    public static String getCad() {
        return "óÅßéÂËÞ\u0098";
    }

//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//    }

    public void exportarBaseDatos() {
        if(!new Permisos(activity,context).requestPermissionAlmacenamiento())
            return;
        File sd = new File(YouChatApplication.RUTA_COPIA_BASE_DATOS);
        boolean exist = sd.exists();
        if(!exist)
            exist = sd.mkdirs();
        if(exist){
            String nombreBd = BDConstantes.NOMBRE_BASE_DATOS;
            try {
                File data = Environment.getDataDirectory();
                if (sd.canWrite()) {
                    String currentDBPath = "//data//"+getActivity().getPackageName()+"//databases//"+nombreBd+"";
                    String backupDBPath = "YouChat_BDatos.dbyc";
                    File currentDB = new File(data, currentDBPath);
                    File backupDB = new File(sd, backupDBPath);
                    Utils.borrarFile(backupDB);

                    if (currentDB.exists()) {
                        String pass = Utils.MD5(YouChatApplication.correo+"YouChat");
                        if(pass!=null){
                            boolean exito = Utils.comprimirArchivo(currentDB,backupDB, pass);
                            if(exito) Utils.ShowToastAnimated(activity,"Base de datos guardada con éxito",R.raw.contact_check);
                            else Utils.ShowToastAnimated(activity,"Falló al intentar guardar la base de datos",R.raw.error);
                        } else Utils.ShowToastAnimated(activity,"Falló al intentar guardar la base de datos",R.raw.error);
//                        FileChannel src = new FileInputStream(currentDB).getChannel();
//                        FileChannel dst = new FileOutputStream(backupDB).getChannel();
//                        dst.transferFrom(src, 0, src.size());
//                        src.close();
//                        dst.close();
//                        Toast.makeText(context, "Base de datos guardada con éxito en "
//                                +YouChatApplication.RUTA_COPIA_BASE_DATOS+"YouChat_BDatos.db", Toast.LENGTH_LONG).show();
                    }
                    else Utils.ShowToastAnimated(activity,"No existe ninguna base de datos para guardar",R.raw.chats_infotip);

                }
            } catch (Exception e) {
                Utils.ShowToastAnimated(activity,"Falló al intentar guardar la base de datos",R.raw.error);
                e.printStackTrace();
            }
        }
        else Utils.ShowToastAnimated(activity,"Falló al intentar guardar la base de datos",R.raw.error);
    }

    public void exportarBaseDatos2() {
        if(!new Permisos(activity,context).requestPermissionAlmacenamiento())
            return;
        File sd = new File(YouChatApplication.RUTA_COPIA_BASE_DATOS);
        boolean exist = sd.exists();
        if(!exist)
            exist = sd.mkdirs();
        if(exist){
            String nombreBd = BDConstantes.NOMBRE_BASE_DATOS;
            try {
                File data = Environment.getDataDirectory();

                if (sd.canWrite()) {
                    String currentDBPath = "//data//"+getActivity().getPackageName()+"//databases//"+nombreBd+"";
                    String backupDBPath = "YouChat_BDatos.dbyc";
                    File currentDB = new File(data, currentDBPath);
                    File backupDB = new File(sd, backupDBPath);

                    if (currentDB.exists()) {
                        FileChannel src = new FileInputStream(currentDB).getChannel();
                        FileChannel dst = new FileOutputStream(backupDB).getChannel();
                        dst.transferFrom(src, 0, src.size());
                        src.close();
                        dst.close();
                        Utils.ShowToastAnimated(activity,"Base de datos guardada con éxito",R.raw.contact_check);
//                        Toast.makeText(context, "Base de datos guardada con éxito en "
//                                +YouChatApplication.RUTA_COPIA_BASE_DATOS+"YouChat_BDatos.db", Toast.LENGTH_LONG).show();
                    } else Utils.ShowToastAnimated(activity,"No existe ninguna base de datos para guardar",R.raw.chats_infotip);

                }
            } catch (Exception e) {
                Utils.ShowToastAnimated(activity,"Falló al intentar guardar la base de datos",R.raw.error);
                e.printStackTrace();
            }
        }
        else Utils.ShowToastAnimated(activity,"Falló al intentar guardar la base de datos",R.raw.error);
    }

    public void importarBaseDatos(){
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(1);
        View mview=getLayoutInflater().inflate(R.layout.dialog_confirm_db,null);
        dialog.setContentView(mview);

        View btn_cancel=mview.findViewById(R.id.btn_cancel);
        View btn_ok=mview.findViewById(R.id.btn_ok);

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if(!new Permisos(activity,context).requestPermissionAlmacenamiento()) return;
                File sd = new File(YouChatApplication.RUTA_COPIA_BASE_DATOS);
                boolean exist = sd.exists();
                if(!exist)
                    exist = sd.mkdirs();
                if(exist){
                    String nombreBd = BDConstantes.NOMBRE_BASE_DATOS;
                    try {
//                        File data = Environment.getDataDirectory();
                        if (sd.canWrite()) {
//                            String currentDBPath = "//data//"+getActivity().getPackageName()+"//databases//"+nombreBd+"";
                            String currentDBPath = "/data/"+getActivity().getPackageName()+"/databases/";
                            String backupDBPath = "YouChat_BDatos.dbyc";
//                            File currentDB = new File(data, currentDBPath);
                            File backupDB = new File(sd, backupDBPath);
                            if (backupDB.exists()) {
                                String pass = Utils.MD5(YouChatApplication.correo+"YouChat");
                                if(pass!=null){
                                    int result = Utils.descomprimirArchivo(backupDB, Environment.getDataDirectory()+currentDBPath,nombreBd, pass);
                                    if(result==1){
                                        LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent("ACTUALIZAR_USUARIOS"));
                                        Utils.ShowToastAnimated(activity,"Base de datos cargada con éxito",R.raw.contact_check);
                                    }
                                    else if(result==2)
                                        Utils.ShowToastAnimated(activity,"Esta base de datos no le pertenece a este correo",R.raw.error);
                                    else if(result==3)
                                        Utils.ShowToastAnimated(activity,"Base de datos no encontrada o archivo dañado",R.raw.error);
                                    else
                                        Utils.ShowToastAnimated(activity,"Falló al intentar cargar la base de datos",R.raw.error);
                                } else Utils.ShowToastAnimated(activity,"Falló al intentar guardar la base de datos",R.raw.error);
                            } else Utils.ShowToastAnimated(activity,"No existe ninguna base de datos para cargar",R.raw.chats_infotip);
                        }
                    } catch (Exception e) {
                        Utils.ShowToastAnimated(activity,"Falló al intentar cargar la base de datos",R.raw.error);
                        e.printStackTrace();
                    }
                }
                else Utils.ShowToastAnimated(activity,"Falló al intentar cargar la base de datos",R.raw.error);
            }
        });

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        dialog.setCancelable(true);
        dialog.show();
    }

    public void importarBaseDatos2(){
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(1);
        View mview=getLayoutInflater().inflate(R.layout.dialog_confirm_db,null);
        dialog.setContentView(mview);

        View btn_cancel=mview.findViewById(R.id.btn_cancel);
        View btn_ok=mview.findViewById(R.id.btn_ok);

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if(!new Permisos(activity,context).requestPermissionAlmacenamiento()) return;
                File sd = new File(YouChatApplication.RUTA_COPIA_BASE_DATOS);
                boolean exist = sd.exists();
                if(!exist)
                    exist = sd.mkdirs();
                if(exist){
                    String nombreBd = BDConstantes.NOMBRE_BASE_DATOS;
                    try {
                        File data = Environment.getDataDirectory();

                        if (sd.canWrite()) {
                            String currentDBPath = "//data//"+getActivity().getPackageName()+"//databases//"+nombreBd+"";
                            String backupDBPath = "YouChat_BDatos.dbyc";
                            File currentDB = new File(data, currentDBPath);
                            File backupDB = new File(sd, backupDBPath);

                            if (currentDB.exists()) {
                                FileChannel src = new FileOutputStream(currentDB).getChannel();
                                FileChannel dst = new FileInputStream(backupDB).getChannel();
                                src.transferFrom(dst, 0, dst.size());
                                src.close();
                                dst.close();
                                Utils.ShowToastAnimated(activity,"Base de datos cargada con éxito",R.raw.contact_check);
                            } else Utils.ShowToastAnimated(activity,"No existe ninguna base de datos para cargar",R.raw.chats_infotip);
                        }
                    } catch (Exception e) {
                        Utils.ShowToastAnimated(activity,"Falló al intentar cargar la base de datos",R.raw.error);
                        e.printStackTrace();
                    }
                }
                else Utils.ShowToastAnimated(activity,"Falló al intentar cargar la base de datos",R.raw.error);
            }
        });

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        dialog.setCancelable(true);
        dialog.show();
    }

    public void cargarPreferencias() {
        correo = YouChatApplication.correo;
        nick = YouChatApplication.alias;
        if(nick.replace(" ","").equals("")) nick=correo;

        user_ajustes.setText(nick);
        correo_ajustes.setText(correo);

        String ruta_img_perfil = YouChatApplication.ruta_img_perfil;
        Glide.with(this).load(ruta_img_perfil).error(R.drawable.profile_white).into(imageView_page_ajustes);

    }

    @Override
    public void onResume() {
        super.onResume();
        if(entroAPerfil){
            entroAPerfil=false;
            cargarPreferencias();
        }
        if(YouChatApplication.mostrarAvisoOptBatery)
            comprobarOptimizacionBateria();
    }

    public void vaciadoInboxFinalizado(int cant){
        if (dialogoInbox != null){
            if(dialogoInbox.isShowing()) dialogoInbox.dismiss();
            dialogoInbox = null;
            if (YouChatApplication.estaAndandoChatService()
                    && YouChatApplication.chatService.hayConex) {
                tv_cant_msg_inbox.setVisibility(View.VISIBLE);
                tv_cant_msg_inbox.setText("" + YouChatApplication.cant_msg_inbox);
            }
            else tv_cant_msg_inbox.setVisibility(View.GONE);
            if (cant == -1) Utils.ShowToastAnimated(activity,"Ha ocurrido un error",R.raw.error);
            else if (cant == 0) Utils.ShowToastAnimated(activity,"Vaciado finalizado",R.raw.chats_unarchive);
            else if (cant == 1) Utils.ShowToastAnimated(activity,"Vaciado finalizado, 1 mensaje eliminado",R.raw.chats_unarchive);
            else Utils.ShowToastAnimated(activity,"Vaciado finalizado, "+ cant +" mensajes eliminados",R.raw.chats_unarchive);
        }
    }

    public void vaciadoInboxFinalizado(int cant, int cantTodos){
        if (dialogoInbox != null){
            if(dialogoInbox.isShowing()) dialogoInbox.dismiss();
            dialogoInbox = null;

            if (YouChatApplication.estaAndandoChatService()
                    && YouChatApplication.chatService.hayConex) {
                tv_cant_msg_inbox.setVisibility(View.VISIBLE);
                tv_cant_msg_inbox.setText("" + YouChatApplication.cant_msg_inbox);
            }
            else tv_cant_msg_inbox.setVisibility(View.GONE);
            if (cant == -1) Utils.ShowToastAnimated(activity,"Ha ocurrido un error",R.raw.error);
            else if (cant == 0) Utils.ShowToastAnimated(activity,"Vaciado finalizado",R.raw.chats_unarchive);
            else if (cant == 1) Utils.ShowToastAnimated(activity,"Vaciado finalizado, 1 mensaje eliminado",R.raw.chats_unarchive);
            else Utils.ShowToastAnimated(activity,"Vaciado finalizado, "+ cant +" mensajes eliminados",R.raw.chats_unarchive);

            if (cantTodos == -1) Utils.ShowToastAnimated(activity,"Ha ocurrido un error al eliminar de todos",R.raw.error);
            else if (cantTodos == 0) Utils.ShowToastAnimated(activity,"Vaciado finalizado de carpeta todos",R.raw.chats_unarchive);
            else if (cantTodos == 1) Utils.ShowToastAnimated(activity,"Vaciado finalizado, 1 mensaje eliminado de carpeta todos",R.raw.chats_unarchive);
            else Utils.ShowToastAnimated(activity,"Vaciado finalizado, "+ cantTodos +" mensajes eliminados de carpeta todos",R.raw.chats_unarchive);
        }
    }
}

/*
{
                    Dialog dialog = new Dialog(context);
                    dialog.requestWindowFeature(1);
                    View mview=getLayoutInflater().inflate(R.layout.dialog_option_descargar,null);
                    dialog.setContentView(mview);

                    View btn_aceptar=mview.findViewById(R.id.btn_ok);
                    final Slider seekBar_tam_max = mview.findViewById(R.id.seekBar_tam_max);
                    final TextView text_seek = mview.findViewById(R.id.text_seek);

                    long size=YouChatApplication.tam_max_descarga;
                    if(size>2048){
                        size = 2048;
                        YouChatApplication.setTam_max_descarga(size);
                    }
                    if(size%64!=0){
                        size=64;
                        YouChatApplication.setTam_max_descarga(size);
                    }

                    String ext = "Kb";
                    double realSize = (double) size;
                    if(realSize >= 1024){
                        ext = "Mb";
                        realSize = (double) realSize/1024;
                    }
                    realSize = (double)Math.round(realSize*1000)/1000;
                    text_seek.setText(realSize+" "+ext);
                    seekBar_tam_max.setValue(size);
                    //seekBar_tam_max.setProgress((int)size);

                    btn_aceptar.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                            long tamMax = (long) seekBar_tam_max.getValue();
                            //long tamMax=seekBar_tam_max.getProgress();
                            //if(tamMax<50) tamMax=50;
//                    else if(tamMax>30000) tamMax=30000;
                            YouChatApplication.setTam_max_descarga(tamMax);
                            String ext = "Kb";
                            double realSize = (double) tamMax;
                            if(realSize >= 1024){
                                ext = "Mb";
                                realSize = (double) realSize/1024;
                            }
                            realSize = (double)Math.round(realSize*1000)/1000;
                            tv_max_descargar.setText(realSize+" "+ext);
                        }
                    });

                    seekBar_tam_max.addOnChangeListener(new Slider.OnChangeListener() {
                        @Override
                        public void onValueChange(@NonNull Slider slider, float value, boolean fromUser) {
                            //if(seekBar_tam_max.getValue()<50) slider.setValue(50);
                            long size = (long) seekBar_tam_max.getValue();
                            String ext = "Kb";
                            double realSize = (double) size;
                            if(realSize > 1024){
                                ext = "Mb";
                                realSize = (double) realSize/1024;
                            }
                            realSize = (double)Math.round(realSize*1000)/1000;
                            text_seek.setText(realSize+" "+ext);
                        }
                    });
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
                            dialog.setCancelable(true);
                            dialog.show();
                            }
 */