package cu.alexgi.youchat;

import android.app.Dialog;
import android.content.ClipData;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.card.MaterialCardView;
import com.vanniktech.emoji.EmojiTextView;
import com.yanzhenjie.album.Action;
import com.yanzhenjie.album.Album;
import com.yanzhenjie.album.AlbumFile;
import com.yanzhenjie.album.api.widget.Widget;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import cu.alexgi.youchat.BottomSheetDialogFragment.BottomSheetDialogFragment_User_Estadisticas;
import cu.alexgi.youchat.Charts.animation.Easing;
import cu.alexgi.youchat.Charts.charts.PieChart;
import cu.alexgi.youchat.Charts.components.Legend;
import cu.alexgi.youchat.Charts.data.PieData;
import cu.alexgi.youchat.Charts.data.PieDataSet;
import cu.alexgi.youchat.Charts.data.PieEntry;
import cu.alexgi.youchat.Charts.formatter.PercentFormatter;
import cu.alexgi.youchat.Charts.utils.ColorTemplate;
import cu.alexgi.youchat.Charts.utils.MPPointF;
import cu.alexgi.youchat.items.ItemChat;
import cu.alexgi.youchat.items.ItemContacto;
import cu.alexgi.youchat.items.ItemEstadisticaPersonal;
import cu.alexgi.youchat.photoView.photoViewLibrary.Info;
import cu.alexgi.youchat.photoView.photoViewLibrary.PhotoView;
import cu.alexgi.youchat.swipebackfragment.BaseSwipeBackFragment;

import static cu.alexgi.youchat.MainActivity.context;
import static cu.alexgi.youchat.MainActivity.dbWorker;
import static cu.alexgi.youchat.MainActivity.permisos;

public class ViewPerfilActivity extends BaseSwipeBackFragment {

    private MaterialCardView fondo_btn;
    private TextView info_seg, btn_seguir;
    private Toolbar toolbar;
    private static Bundle mibundle;
    private CircleImageView img_perfil;
    private LinearLayout fondo_perfil;
    private View correo_copiar, fab, realizarLlamadaEnPerfil;
    private boolean lo_sigues;
    private String alias, nombre, correo, telefono, ruta_img_perfil, info, genero, provincia, fecha_nacimiento;
    private boolean vieneDeChat;
    private TextView info_perfil_seguidores;
    private ImageView verified_user, iv_fondo_view_user;
    private TextView info_perfil_correo, info_perfil_telefono, info_perfil_genero, info_perfil_fecha_nacimiento, info_perfil_provincia;
    private EmojiTextView info_perfil_nombre1, info_perfil_nombre2, info_perfil_informacion, info_perfil_alias;


    //estadisticas
    private View cardView_est_personal;
    private TextView tv_consumo_p_env, tv_consumo_p_rec;
    private PieChart chart;

    //img linda
    private View frameLayout_visorImg_perfil;
    private PhotoView photoView_visorImg_perfil;
    private Info info_photoView;
//    private TextView tv_cant_msj_enviados, tv_cant_msj_recibidos, tv_es_tu_seguidor, tv_cant_veces_reacciono_a_ti;

    public static ViewPerfilActivity newInstance(Bundle bundle) {
        ViewPerfilActivity fragment = new ViewPerfilActivity();
        mibundle = bundle;
        return fragment;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return attachToSwipeBack(inflater.inflate(R.layout.activity_view_perfil, container, false));
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        toolbar = view.findViewById(R.id.toolbar);
        toolbar.setTitle("");
        YouChatApplication.ViewPerfilActivity = this;
//        setSupportActionBar(toolbar);

        frameLayout_visorImg_perfil = view.findViewById(R.id.frameLayout_visorImg_perfil);
        photoView_visorImg_perfil = view.findViewById(R.id.photoView_visorImg_perfil);

        photoView_visorImg_perfil.enable();
        photoView_visorImg_perfil.enableRotate();
        photoView_visorImg_perfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ocultarImagen();
            }
        });

//        NavController navController = Navigation.findNavController(view);
        view.findViewById(R.id.root).setBackgroundColor(Color.parseColor(YouChatApplication.itemTemas.getColor_interior()));
        CollapsingToolbarLayout bar = view.findViewById(R.id.bar);
        bar.setContentScrimColor(Color.parseColor(YouChatApplication.itemTemas.getColor_barra()));

        view.findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
//                navController.navigateUp();
            }
        });

        fab = view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(vieneDeChat) getActivity().onBackPressed();
                else{
//                    navController.popBackStack();
//                    navController.navigate(R.id.chatsActivity,mibundle);
                    getActivity().onBackPressed();
                    Utils.runOnUIThread(()->{
                        if(mAddFragmentListener!=null) mAddFragmentListener.onAddFragment(YouChatApplication.principalActivity, ChatsActivity.newInstance(mibundle));
                    });
                    //getActivity().getSupportFragmentManager().beginTransaction().remove(ViewPerfilActivity.this);
                }
            }
        });

        realizarLlamadaEnPerfil = view.findViewById(R.id.realizarLlamadaEnPerfil);
        realizarLlamadaEnPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!telefono.equals(""))
                    if(permisos.requestPermissionTelefono())
                        startActivity(new Intent("android.intent.action.CALL", Uri.parse("tel:" + (telefono))));
            }
        });

        fondo_perfil=view.findViewById(R.id.fondo_perfil);
        correo_copiar=view.findViewById(R.id.correo_copiar);
        fondo_btn=view.findViewById(R.id.fondo_btn);
        btn_seguir=view.findViewById(R.id.btn_seguir);
        info_seg=view.findViewById(R.id.info_seg);
        iv_fondo_view_user = view.findViewById(R.id.iv_fondo_view_user);

        info_perfil_seguidores=view.findViewById(R.id.info_perfil_seguidores);
        lo_sigues=false;

        verified_user=view.findViewById(R.id.verified_user);

        Utils.cargarFondo(context,iv_fondo_view_user);

        info_perfil_nombre1 = view.findViewById(R.id.info_perfil_nombre1);
        info_perfil_nombre2 = view.findViewById(R.id.info_perfil_nombre2);
        info_perfil_alias = view.findViewById(R.id.info_perfil_alias);
        info_perfil_correo = view.findViewById(R.id.info_perfil_correo);
        info_perfil_informacion = view.findViewById(R.id.info_perfil_informacion);
        img_perfil = view.findViewById(R.id.img_perfil);
        info_perfil_telefono = view.findViewById(R.id.info_perfil_telefono);
        info_perfil_genero = view.findViewById(R.id.info_perfil_genero);
        info_perfil_fecha_nacimiento = view.findViewById(R.id.info_perfil_fecha_nacimiento);
        info_perfil_provincia = view.findViewById(R.id.info_perfil_provincia);

        cardView_est_personal = view.findViewById(R.id.cardView_est_personal);
        tv_consumo_p_env = view.findViewById(R.id.tv_consumo_p_env);
        tv_consumo_p_rec = view.findViewById(R.id.tv_consumo_p_rec);

        chart = view.findViewById(R.id.chart);

        chart.setUsePercentValues(true);
        chart.getDescription().setEnabled(false);
        chart.setExtraOffsets(5, 10, 5, 5);

        chart.setDragDecelerationFrictionCoef(0.95f);

//        chart.setCenterTextTypeface(tfLight);
//        chart.setCenterText(generateCenterSpannableText());

        chart.setDrawHoleEnabled(true);
        chart.setHoleColor(Color.WHITE);

        chart.setTransparentCircleColor(Color.WHITE);
        chart.setTransparentCircleAlpha(110);

        chart.setHoleRadius(58f);
        chart.setTransparentCircleRadius(61f);

        chart.setDrawCenterText(true);

        chart.setRotationAngle(0);
        // enable rotation of the chart by touch
        chart.setRotationEnabled(true);
        chart.setHighlightPerTapEnabled(true);

        // chart.setUnit(" €");
        // chart.setDrawUnitsInChart(true);

        // add a selection listener
//        chart.setOnChartValueSelectedListener(this);

        chart.animateY(1400, Easing.EaseInOutQuad);
        // chart.spin(2000, 0, 360);

        Legend l = chart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setDrawInside(false);
        l.setXEntrySpace(7f);
        l.setYEntrySpace(0f);
        l.setYOffset(0f);

        // entry label styling
        chart.setEntryLabelColor(Color.WHITE);
//        chart.setEntryLabelTypeface(tfRegular);
        chart.setEntryLabelTextSize(12f);

//        mibundle=getArguments();
        if(mibundle!=null){
            nombre = mibundle.getString("usuario","");
            correo = mibundle.getString("correo","");

            if(MainActivity.dbWorker.existeSeguidor(correo)) info_seg.setText("Te sigue");
            else info_seg.setText("No te sigue");

            ItemContacto contacto = dbWorker.obtenerContacto(correo);
            if(contacto!=null){
                if(nombre.equals("") || nombre.equals(correo))
                    nombre = contacto.getNombreMostrar();
                alias = contacto.getAlias();
                telefono = contacto.getTelefono();
                ruta_img_perfil = contacto.getRuta_img();
                info = contacto.getInfo();
                genero = contacto.getGenero();
                provincia = contacto.getProvincia();
                fecha_nacimiento = contacto.getFecha_nac();
                info_perfil_seguidores.setText(""+contacto.getCant_seguidores());

                lo_sigues=dbWorker.existeSiguiendoA(correo);
                if(lo_sigues){
                    btn_seguir.setText("siguiendo");
                    btn_seguir.setTextColor(Color.parseColor(YouChatApplication.itemTemas.getColor_btn()));
                    fondo_btn.setCardBackgroundColor(Color.TRANSPARENT);
                }
                else {
                    btn_seguir.setText("seguir");
                    btn_seguir.setTextColor(Color.WHITE);
                    fondo_btn.setCardBackgroundColor(Color.parseColor(YouChatApplication.itemTemas.getColor_btn()));
                }

                if(YouChatApplication.comprobarOficialidad(contacto.getCorreo())){
                    verified_user.setImageResource(R.drawable.verified_profile);
                    verified_user.setVisibility(View.VISIBLE);
                    verified_user.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Utils.ShowToastAnimated(getActivity(),"Usuario oficial de YouChat",R.raw.contact_check);
                        }
                    });
                }
                else if(contacto.getCant_seguidores()>=YouChatApplication.usuMayor){
                    verified_user.setImageResource(R.drawable.vip_crown_line);
                    verified_user.setVisibility(View.VISIBLE);
                    verified_user.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Utils.ShowToasty(getActivity(),"Influencer en YouChat",R.drawable.vip_crown_line);
                        }
                    });
                }
                else if(contacto.getCant_seguidores()>=YouChatApplication.usuMedio){
                    verified_user.setImageResource(R.drawable.vip_diamond_line);
                    verified_user.setVisibility(View.VISIBLE);
                    verified_user.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Utils.ShowToasty(getActivity(),"Micro influencer en YouChat",R.drawable.vip_diamond_line);
                        }
                    });
                }
                else if(contacto.getCant_seguidores()>=YouChatApplication.usuMenor){
                    verified_user.setImageResource(R.drawable.award_line);
                    verified_user.setVisibility(View.VISIBLE);
                    verified_user.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Utils.ShowToasty(getActivity(),"Usuario popular en YouChat",R.drawable.award_line);
                        }
                    });
                }
            }
            else {
                if(nombre.trim().equals(""))
                    nombre=correo;
                telefono=ruta_img_perfil=info=genero=provincia=fecha_nacimiento="";
                info_perfil_seguidores.setText("0");
                if(YouChatApplication.comprobarOficialidad(correo)){
                    verified_user.setImageResource(R.drawable.verified_profile);
                    verified_user.setVisibility(View.VISIBLE);
                    verified_user.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Utils.ShowToastAnimated(getActivity(),"Usuario oficial de YouChat",R.raw.contact_check);
                        }
                    });
                }
            }

            ItemEstadisticaPersonal estadisticaPersonal = dbWorker.obtenerEstadisticaPersonal(correo);
            if(estadisticaPersonal==null) estadisticaPersonal = new ItemEstadisticaPersonal(correo);
            tv_consumo_p_env.setText(Utils.convertirBytes(estadisticaPersonal.obtenerTotalEnviado()));
            tv_consumo_p_rec.setText(Utils.convertirBytes(estadisticaPersonal.obtenerTotalRecibido()));
            ItemEstadisticaPersonal finalEstadisticaPersonal = estadisticaPersonal;
            setData(finalEstadisticaPersonal);
            cardView_est_personal.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showBSD(nombre,ruta_img_perfil, finalEstadisticaPersonal);
                }
            });

            correo_copiar.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    copiarCorreo();
                    return false;
                }
            });

            vieneDeChat = mibundle.getBoolean("vieneDeChat", false);

            info_perfil_nombre1.setText(nombre);
            info_perfil_nombre2.setText(nombre);
            info_perfil_alias.setText(alias);
            info_perfil_correo.setText(correo);
            info_perfil_informacion.setText(info);
            info_perfil_telefono.setText(telefono);
            info_perfil_genero.setText(genero);
            info_perfil_fecha_nacimiento.setText(fecha_nacimiento);
            info_perfil_provincia.setText(provincia);

            if(correo.equals(YouChatApplication.idOficial)){
                YouChatApplication.ponerIconOficial(img_perfil);
            }
            else {
                File file = new File(ruta_img_perfil);
                if(!file.exists()) ruta_img_perfil="";
                Glide.with(this).load(ruta_img_perfil).error(R.drawable.profile_white).into(img_perfil);
            }
        }
        else getActivity().onBackPressed();
            //Navigation.findNavController(view).navigateUp();

        img_perfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!ruta_img_perfil.equals("")) previewImage();
            }
        });

        fondo_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(YouChatApplication.estaAndandoChatService()){
                    if(YouChatApplication.chatService.hayConex){
                        if(lo_sigues){
                            Dialog dialog = new Dialog(context);
                            dialog.requestWindowFeature(1);
                            View mview=getLayoutInflater().inflate(R.layout.dialog_confirm,null);
                            dialog.setContentView(mview);

                            LinearLayout header=mview.findViewById(R.id.header);
                            ImageView icono_eliminar = mview.findViewById(R.id.icono_eliminar);
                            TextView text_icono = mview.findViewById(R.id.text_icono);
                            TextView text_eliminar = mview.findViewById(R.id.text_eliminar);
                            TextView btn_ok=mview.findViewById(R.id.btn_ok);
                            View btn_cancel=mview.findViewById(R.id.btn_cancel);

//                            header.setBackgroundResource(R.color.primary);
                            header.setBackgroundResource(YouChatApplication.colorTemaActual);

                            icono_eliminar.setImageResource(R.drawable.remove_seguir);
                            text_icono.setText("Dejar de seguir");
                            text_eliminar.setText("¿Quieres dejar de seguir a "+dbWorker.obtenerNombre(correo)+"? Se eliminarán todos " +
                                    "sus estados y se eliminará de su lista de seguidores.");
                            btn_ok.setText("ACEPTAR");

                            btn_ok.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dialog.dismiss();
                                    dbWorker.eliminarTodosLosEstadosDe(correo);
                                    dbWorker.eliminarSiguiendoA(correo);
                                    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS", Locale.getDefault());
                                    Date date = new Date();
                                    String fechaEntera = sdf.format(date);
                                    String hora = Convertidor.conversionHora(fechaEntera);
                                    String fecha = Convertidor.conversionFecha(fechaEntera);
                                    ItemChat solicitud = new ItemChat(correo,"0");
                                    solicitud.setId("-ss-");
                                    solicitud.setHora(hora);
                                    solicitud.setFecha(fecha);
                                    YouChatApplication.chatService.enviarMensaje(solicitud,SendMsg.CATEGORY_SOL_SEGUIR);
                                    Utils.ShowToastAnimated(getActivity(),"Solicitud enviada",R.raw.contact_check);
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
                        } else {
                            Dialog dialog = new Dialog(context);
                            dialog.requestWindowFeature(1);
                            View mview=getLayoutInflater().inflate(R.layout.dialog_confirm,null);
                            dialog.setContentView(mview);

                            LinearLayout header=mview.findViewById(R.id.header);
                            ImageView icono_eliminar = mview.findViewById(R.id.icono_eliminar);
                            TextView text_icono = mview.findViewById(R.id.text_icono);
                            TextView text_eliminar = mview.findViewById(R.id.text_eliminar);
                            TextView btn_ok=mview.findViewById(R.id.btn_ok);
                            View btn_cancel=mview.findViewById(R.id.btn_cancel);

                            header.setBackgroundResource(YouChatApplication.colorTemaActual);
                            icono_eliminar.setImageResource(R.drawable.add_user);
                            text_icono.setText("Seguir");
                            text_eliminar.setText("¿Quieres seguir a "+dbWorker.obtenerNombre(correo)+"? Se enviará una solicitud pidiendo ser " +
                                    "parte de su lista de seguidores, estará en sus manos aceptarte o no.");
                            btn_ok.setText("ACEPTAR");

                            btn_ok.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dialog.dismiss();
                                    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS", Locale.getDefault());
                                    Date date = new Date();
                                    String fechaEntera = sdf.format(date);
                                    String hora = Convertidor.conversionHora(fechaEntera);
                                    String fecha = Convertidor.conversionFecha(fechaEntera);
                                    ItemChat solicitud = new ItemChat(correo,"1");
                                    solicitud.setId("-ss-");
                                    solicitud.setHora(hora);
                                    solicitud.setFecha(fecha);
                                    YouChatApplication.chatService.enviarMensaje(solicitud,SendMsg.CATEGORY_SOL_SEGUIR);
                                    Utils.ShowToastAnimated(getActivity(),"Solicitud enviada",R.raw.contact_check);
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
                    } else {
                        Utils.ShowToastAnimated(getActivity(),"Compruebe su conexión",R.raw.ic_ban);
                    }
                }
            }
        });

        if(correo!=null && correo.equals(YouChatApplication.idOficial)){
            info_perfil_alias.setText("YouChat");
            info_perfil_informacion.setText("Canal oficial para informar todo acerca del desarrollo de YouChat.");
            view.findViewById(R.id.follower).setVisibility(View.GONE);
            view.findViewById(R.id.estadisticas).setVisibility(View.GONE);
        }
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setExitTransition(new MaterialSharedAxis(MaterialSharedAxis.X, true ));
//        setReenterTransition(new MaterialSharedAxis(MaterialSharedAxis.X, false ));
    }

    private void copiarCorreo(){
        String clip = correo;
        ClipData c = ClipData.newPlainText("YouChatCopy", clip);
        YouChatApplication.clipboard.setPrimaryClip(c);
        Utils.ShowToastAnimated(getActivity(),"Correo copiado al portapapeles",R.raw.voip_invite);
    }

    private void previewImage() {
        File file =  new File(ruta_img_perfil);
        if(!file.exists()) return;
        info_photoView = PhotoView.getImageViewInfo(img_perfil);
        img_perfil.setVisibility(View.INVISIBLE);
        frameLayout_visorImg_perfil.setVisibility(View.VISIBLE);
        Glide.with(context).load(ruta_img_perfil).error(R.drawable.placeholder).into(photoView_visorImg_perfil);
        photoView_visorImg_perfil.animaFrom(info_photoView);
    }

    private void previewImage2() {
        File file =  new File(ruta_img_perfil);
        if(!file.exists()) return;

        ArrayList<AlbumFile> imagenesChatAlbumFile=new ArrayList<>();
        AlbumFile albumFile = new AlbumFile();
        albumFile.setPath(ruta_img_perfil);
        imagenesChatAlbumFile.add(albumFile);

        Album.galleryAlbum(this)
                .checkable(false)
                .checkedList(imagenesChatAlbumFile)
                .currentPosition(0)
                .widget(
                        Widget.newDarkBuilder(context)
                                .title("Imagen de perfil")
                                .build()
                )
                .onResult(new Action<ArrayList<AlbumFile>>() {
                    @Override
                    public void onAction(@NonNull ArrayList<AlbumFile> result) {
                    }
                })
                .start();
    }

    public void showBSD(String nombre, String ruta, ItemEstadisticaPersonal itemEstadisticaPersonal){
        BottomSheetDialogFragment bsdFragment = BottomSheetDialogFragment_User_Estadisticas
                .newInstance(context, nombre, ruta, itemEstadisticaPersonal);
        bsdFragment.show(getChildFragmentManager(), "BSDialog");
    }


    ///////////////////////////////////////////

    private void setData(ItemEstadisticaPersonal item) {
        ArrayList<PieEntry> entries = new ArrayList<>();

        // NOTE: The order of the entries when being added to the entries array determines their position around the center of
        // the chart.
        int [] colors = new int[5];


        colors[0] = ContextCompat.getColor(getContext(), R.color.card1);
        colors[1] = ContextCompat.getColor(getContext(), R.color.card2);
        colors[2] = ContextCompat.getColor(getContext(), R.color.card3);
        colors[3] = ContextCompat.getColor(getContext(), R.color.card4);
        colors[4] = ContextCompat.getColor(getContext(), R.color.card5);

        final String[] parties = new String[] {"Mensajes", "Imágenes", "Audios", "Archivos", "Stickers"};

//        for (int i = 0; i < count ; i++) {
//            entries.add(new PieEntry((float) ((Math.random() * range) + range / 5),
//                    parties[i % parties.length]));
//        }

        int sumMSG = item.getCant_msg_env() + item.getCant_msg_rec();
        int sumIMG = item.getCant_img_env() + item.getCant_img_rec();
        int sumAUD = item.getCant_aud_env() + item.getCant_aud_rec();
        int sumARC = item.getCant_arc_env() + item.getCant_arc_rec();
        int sumSTI = item.getCant_sti_env() + item.getCant_sti_rec();

        if(sumMSG > 0) entries.add(new PieEntry((float) sumMSG, parties[0]));

        if(sumIMG > 0) entries.add(new PieEntry((float) sumIMG, parties[1]));

        if(sumAUD > 0) entries.add(new PieEntry((float) sumAUD, parties[2]));

        if(sumARC > 0) entries.add(new PieEntry((float) sumARC, parties[3]));

        if(sumSTI > 0) entries.add(new PieEntry((float) sumSTI, parties[4]));

        PieDataSet dataSet = new PieDataSet(entries, "");

        dataSet.setDrawIcons(false);

        dataSet.setSliceSpace(2f);
        dataSet.setIconsOffset(new MPPointF(0, 40));
        dataSet.setSelectionShift(5f);

        // add a lot of colors

//        ArrayList<Integer> colors = new ArrayList<>();
//
//        for (int c : ColorTemplate.VORDIPLOM_COLORS)
//            colors.add(c);
//
//        for (int c : ColorTemplate.JOYFUL_COLORS)
//            colors.add(c);
//
//        for (int c : ColorTemplate.COLORFUL_COLORS)
//            colors.add(c);
//
//        for (int c : ColorTemplate.LIBERTY_COLORS)
//            colors.add(c);
//
//        for (int c : ColorTemplate.PASTEL_COLORS)
//            colors.add(c);
//
//        colors.add(ColorTemplate.getHoloBlue());

        dataSet.setColors(colors);
        //dataSet.setSelectionShift(0f);

        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(11f);
        data.setValueTextColor(Color.WHITE);
        chart.setData(data);

        // undo all highlights
        chart.highlightValues(null);

        chart.invalidate();
    }


    /*@Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.viewGithub: {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse("https://github.com/PhilJay/MPAndroidChart/blob/master/MPChartExample/src/com/xxmassdeveloper/mpchartexample/PieChartActivity.java"));
                startActivity(i);
                break;
            }
            case R.id.actionToggleValues: {
                for (IDataSet<?> set : chart.getData().getDataSets())
                    set.setDrawValues(!set.isDrawValuesEnabled());

                chart.invalidate();
                break;
            }
            case R.id.actionToggleIcons: {
                for (IDataSet<?> set : chart.getData().getDataSets())
                    set.setDrawIcons(!set.isDrawIconsEnabled());

                chart.invalidate();
                break;
            }
            case R.id.actionToggleHole: {
                if (chart.isDrawHoleEnabled())
                    chart.setDrawHoleEnabled(false);
                else
                    chart.setDrawHoleEnabled(true);
                chart.invalidate();
                break;
            }
            case R.id.actionToggleMinAngles: {
                if (chart.getMinAngleForSlices() == 0f)
                    chart.setMinAngleForSlices(36f);
                else
                    chart.setMinAngleForSlices(0f);
                chart.notifyDataSetChanged();
                chart.invalidate();
                break;
            }
            case R.id.actionToggleCurvedSlices: {
                boolean toSet = !chart.isDrawRoundedSlicesEnabled() || !chart.isDrawHoleEnabled();
                chart.setDrawRoundedSlices(toSet);
                if (toSet && !chart.isDrawHoleEnabled()) {
                    chart.setDrawHoleEnabled(true);
                }
                if (toSet && chart.isDrawSlicesUnderHoleEnabled()) {
                    chart.setDrawSlicesUnderHole(false);
                }
                chart.invalidate();
                break;
            }
            case R.id.actionDrawCenter: {
                if (chart.isDrawCenterTextEnabled())
                    chart.setDrawCenterText(false);
                else
                    chart.setDrawCenterText(true);
                chart.invalidate();
                break;
            }
            case R.id.actionToggleXValues: {

                chart.setDrawEntryLabels(!chart.isDrawEntryLabelsEnabled());
                chart.invalidate();
                break;
            }
            case R.id.actionTogglePercent:
                chart.setUsePercentValues(!chart.isUsePercentValuesEnabled());
                chart.invalidate();
                break;
            case R.id.animateX: {
                chart.animateX(1400);
                break;
            }
            case R.id.animateY: {
                chart.animateY(1400);
                break;
            }
            case R.id.animateXY: {
                chart.animateXY(1400, 1400);
                break;
            }
            case R.id.actionToggleSpin: {
                chart.spin(1000, chart.getRotationAngle(), chart.getRotationAngle() + 360, Easing.EaseInOutCubic);
                break;
            }
            case R.id.actionSave: {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    saveToGallery();
                } else {
                    requestStoragePermission(chart);
                }
                break;
            }
        }
        return true;
    }*/


    private SpannableString generateCenterSpannableText() {

        SpannableString s = new SpannableString("MPAndroidChart\ndeveloped by Philipp Jahoda");
        s.setSpan(new RelativeSizeSpan(1.7f), 0, 14, 0);
        s.setSpan(new StyleSpan(Typeface.NORMAL), 14, s.length() - 15, 0);
        s.setSpan(new ForegroundColorSpan(Color.GRAY), 14, s.length() - 15, 0);
        s.setSpan(new RelativeSizeSpan(.8f), 14, s.length() - 15, 0);
        s.setSpan(new StyleSpan(Typeface.ITALIC), s.length() - 14, s.length(), 0);
        s.setSpan(new ForegroundColorSpan(ColorTemplate.getHoloBlue()), s.length() - 14, s.length(), 0);
        return s;
    }

    private void ocultarImagen() {
        photoView_visorImg_perfil.animaTo(info_photoView, new Runnable() {
            @Override
            public void run() {
                img_perfil.setVisibility(View.VISIBLE);
                frameLayout_visorImg_perfil.setVisibility(View.GONE);
            }
        });
    }

    public void atras() {
        if(frameLayout_visorImg_perfil.getVisibility()==View.VISIBLE)
            ocultarImagen();
        else {
            YouChatApplication.ViewPerfilActivity = null;
            getActivity().onBackPressed();
        }
    }
}
