package cu.alexgi.youchat;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.vanniktech.emoji.EmojiEditText;
import com.vanniktech.emoji.EmojiPopup;
import com.vanniktech.emoji.listeners.OnEmojiPopupDismissListener;
import com.vanniktech.emoji.listeners.OnEmojiPopupShownListener;

import java.util.Calendar;

import cu.alexgi.youchat.swipebackfragment.BaseSwipeBackFragment;
import cu.alexgi.youchat.swipebackfragment.SwipeBackLayout;

public class EditPerfilActivity extends BaseSwipeBackFragment {

//    private NavController navController;
    private LinearLayout alias,info,phone,genero,calendario;
    private ScrollView provincia;
    private TextView edit_titulo;
    private TextView text_ayuda;
    private TextView cant_limite_char;
    private static TextView text_calendario;
    private TextView borrar_edit_perfil;
    private EmojiEditText campo_alias,campo_info;
    private EditText campo_telefono;
    private EmojiPopup emojiPopup;
    private ImageView emoji_alias,emoji_info;
    private static String fechCumple;
    private RadioButton masculino,femenino,ninguno;
    private RadioButton prov0,prov1,prov2,prov3,prov4,prov5,prov6,prov7,prov8,prov9,prov10,prov11,prov12,prov13,prov14,prov15,prov16;
    private String action,valor="";
    private View ContentView, ir_atras, guardar;
    private boolean esCalendario,esGenero,esProvincia;
    private static Bundle bundle;

    public static EditPerfilActivity newInstance(Bundle b) {
        EditPerfilActivity fragment = new EditPerfilActivity();
        bundle = b;
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return attachToSwipeBack(inflater.inflate(R.layout.activity_edit_perfil, container, false));
    }

    @Override
    public void onDestroyView() {
        Utils.ocultarKeyBoard(MainActivity.mainActivity);
        super.onDestroyView();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getSwipeBackLayout().addSwipeListener(new SwipeBackLayout.OnSwipeListener() {
            @Override
            public void onDragStateChange(int state) {
                if(state==1)
                {
                    Utils.ocultarKeyBoard(MainActivity.mainActivity);
                }
            }
            @Override
            public void onEdgeTouch(int oritentationEdgeFlag){}
            @Override
            public void onDragScrolled(float scrollPercent){}
        });
//        navController = Navigation.findNavController(view);
        ContentView=view.findViewById(R.id.root);
        valor="";
        fechCumple="";

        view.findViewById(R.id.root).setBackgroundColor(Color.parseColor(YouChatApplication.itemTemas.getColor_fondo()));

        ir_atras = view.findViewById(R.id.ir_atras);
        ir_atras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
//                navController.navigateUp();
            }
        });
        guardar = view.findViewById(R.id.guardar);
        guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(guardarPreferencia(action)){
                    ViewYouPerfilActivity.ActualizarInfo();
                    getActivity().onBackPressed();
//                    navController.navigateUp();
                }
            }
        });

        esCalendario=esGenero=esProvincia=false;

        alias=view.findViewById(R.id.edit_alias);
        info=view.findViewById(R.id.edit_info);
        phone=view.findViewById(R.id.edit_telefono);
        genero=view.findViewById(R.id.edit_genero);
        calendario=view.findViewById(R.id.edit_calendario);
        provincia=view.findViewById(R.id.edit_provincia);

        calendario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new DatePickerFragment();
                newFragment.show(getChildFragmentManager(), "YouChat Calendario");
            }
        });

        edit_titulo=view.findViewById(R.id.edit_titulo);
        text_ayuda=view.findViewById(R.id.text_ayuda);
        cant_limite_char=view.findViewById(R.id.cant_limite_char);
        text_calendario=view.findViewById(R.id.text_calendario);
        borrar_edit_perfil=view.findViewById(R.id.borrar_edit_perfil);
        borrar_edit_perfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                campo_alias.setText("");
                campo_info.setText("");
                campo_telefono.setText("");
            }
        });

        masculino=view.findViewById(R.id.masculino);
        femenino=view.findViewById(R.id.femenino);
        ninguno=view.findViewById(R.id.ninguno);

        prov0=view.findViewById(R.id.prov0);
        prov1=view.findViewById(R.id.prov1);
        prov2=view.findViewById(R.id.prov2);
        prov3=view.findViewById(R.id.prov3);
        prov4=view.findViewById(R.id.prov4);
        prov5=view.findViewById(R.id.prov5);
        prov6=view.findViewById(R.id.prov6);
        prov7=view.findViewById(R.id.prov7);
        prov8=view.findViewById(R.id.prov8);
        prov9=view.findViewById(R.id.prov9);
        prov10=view.findViewById(R.id.prov10);
        prov11=view.findViewById(R.id.prov11);
        prov12=view.findViewById(R.id.prov12);
        prov13=view.findViewById(R.id.prov13);
        prov14=view.findViewById(R.id.prov14);
        prov15=view.findViewById(R.id.prov15);
        prov16=view.findViewById(R.id.prov16);

        campo_alias=view.findViewById(R.id.campo_alias);
        campo_info=view.findViewById(R.id.campo_info);
        campo_telefono=view.findViewById(R.id.campo_telefono);

        emoji_alias=view.findViewById(R.id.emoji_alias);
        emoji_info=view.findViewById(R.id.emoji_info);

        setUpEmojiPopupAlias();
        setUpEmojiPopupInfo();

        emoji_alias.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                emojiPopup.toggle();
            }
        });
        emoji_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                emojiPopup.toggle();
            }
        });

        new Thread(()->{
//            Bundle bundle= getArguments();
            if(bundle!=null) {
                action = bundle.getString("key");
                if(action==null) getActivity().onBackPressed();//navController.navigateUp();

                else if(action.equals("alias"))
                {
                    alias.setVisibility(View.VISIBLE);
                    valor=YouChatApplication.alias;
                    edit_titulo.setText("Editar alias");
                    if(!valor.replace(" ","").equals("")) campo_alias.setText(valor);
                    text_ayuda.setText(R.string.edit_alias);
                }
                else if(action.equals("info"))
                {
                    info.setVisibility(View.VISIBLE);
                    valor=YouChatApplication.info;
                    edit_titulo.setText("Editar información");
                    if(!valor.equals("")){
                        campo_info.setText(valor);
                        cant_limite_char.setText(valor.length()+"/160");
                    }
                    else cant_limite_char.setText("0/160");

                    text_ayuda.setText(R.string.edit_info);
                }
                else if(action.equals("telefono"))
                {
                    phone.setVisibility(View.VISIBLE);
                    valor=YouChatApplication.telefono;
                    edit_titulo.setText("Editar número");

                    if(!valor.replace(" ","").equals("")) campo_telefono.setText(valor);
                    text_ayuda.setText(R.string.edit_phone);
                }
                else if(action.equals("genero"))
                {
                    genero.setVisibility(View.VISIBLE);
                    valor=YouChatApplication.genero;
                    edit_titulo.setText("Editar género");
                    esGenero=true;
                    if(valor.equals("Masculino"))
                        masculino.setChecked(true);
                    else if(valor.equals("Femenino"))
                        femenino.setChecked(true);
                    else ninguno.setChecked(true);

                    text_ayuda.setText(R.string.edit_genero);
                    borrar_edit_perfil.setVisibility(View.GONE);
                }
                else if(action.equals("fecha_nacimiento"))
                {
                    calendario.setVisibility(View.VISIBLE);
                    valor=YouChatApplication.fecha_nacimiento;
                    edit_titulo.setText("Editar fecha de nacimiento");
                    if(!valor.replace(" ","").equals("")) text_calendario.setText(valor);
                    esCalendario=true;

                    text_ayuda.setText(R.string.edit_calendario);
                    borrar_edit_perfil.setVisibility(View.GONE);
                }
                else if(action.equals("provincia"))
                {
                    provincia.setVisibility(View.VISIBLE);
                    valor=YouChatApplication.provincia;
                    edit_titulo.setText("Editar provincia");
                    esProvincia=true;


                    if(valor.equals("Pinar del Río"))prov1.setChecked(true);
                    else if(valor.equals("Artemisa"))prov2.setChecked(true);
                    else if(valor.equals("Mayabeque"))prov3.setChecked(true);
                    else if(valor.equals("La Habana"))prov4.setChecked(true);
                    else if(valor.equals("Matanzas"))prov5.setChecked(true);
                    else if(valor.equals("Cienfuegos"))prov6.setChecked(true);
                    else if(valor.equals("Villa Clara"))prov7.setChecked(true);
                    else if(valor.equals("Sancti Spíritus"))prov8.setChecked(true);
                    else if(valor.equals("Ciego de Ávila"))prov9.setChecked(true);
                    else if(valor.equals("Camagüey"))prov10.setChecked(true);
                    else if(valor.equals("Las Tunas"))prov11.setChecked(true);
                    else if(valor.equals("Holguín"))prov12.setChecked(true);
                    else if(valor.equals("Granma"))prov13.setChecked(true);
                    else if(valor.equals("Santiago de Cuba"))prov14.setChecked(true);
                    else if(valor.equals("Guantánamo"))prov15.setChecked(true);
                    else if(valor.equals("Isla de la Juventud"))prov16.setChecked(true);
                    else prov0.setChecked(true);

                    text_ayuda.setText(R.string.edit_provincia);
                    borrar_edit_perfil.setVisibility(View.GONE);
                }
            }
        }).run();


        campo_info.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String espacio = s.toString();
                cant_limite_char.setText(espacio.length()+"/160");
            }
        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void setUpEmojiPopupAlias() {
        emojiPopup = EmojiPopup.Builder.fromRootView(ContentView)
                .setOnEmojiPopupShownListener(new OnEmojiPopupShownListener() {
                    @Override
                    public void onEmojiPopupShown() {
                        emoji_alias.setImageResource(R.drawable.input_keyboard);
                    }
                })
                .setOnEmojiPopupDismissListener(new OnEmojiPopupDismissListener() {
                    @Override
                    public void onEmojiPopupDismiss() {
                        emoji_alias.setImageResource(R.drawable.emoji);
                    }
                })
                .setKeyboardAnimationStyle(R.style.emoji_fade_animation_style)
                .setPageTransformer(new PageTransformer())
                .build(campo_alias,false);
    }

    private void setUpEmojiPopupInfo() {
        emojiPopup = EmojiPopup.Builder.fromRootView(ContentView)
                .setOnEmojiPopupShownListener(new OnEmojiPopupShownListener() {
                    @Override
                    public void onEmojiPopupShown() {
                        emoji_info.setImageResource(R.drawable.input_keyboard);
                    }
                })
                .setOnEmojiPopupDismissListener(new OnEmojiPopupDismissListener() {
                    @Override
                    public void onEmojiPopupDismiss() {
                        emoji_info.setImageResource(R.drawable.emoji);
                    }
                })
                .setKeyboardAnimationStyle(R.style.emoji_fade_animation_style)
                .setPageTransformer(new PageTransformer())
                .build(campo_info,false);
    }

    public synchronized boolean guardarPreferencia(String key){

        SharedPreferences preferencias=getActivity().getSharedPreferences("Memoria", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=preferencias.edit();
        String valor_modificado;

        if(esCalendario){
            valor_modificado = text_calendario.getText().toString();
            if(!valor_modificado.equals(valor)){
                YouChatApplication.setFecha_cumpleanos(fechCumple);
                editor.putString(key, valor_modificado);
                editor.apply();
                YouChatApplication.setVersion_info();
                YouChatApplication.cargarPreferencias();
                return true;
            }
        }

        else if(esGenero){
            if(masculino.isChecked()) valor_modificado="Masculino";
            else if(femenino.isChecked()) valor_modificado="Femenino";
            else valor_modificado="";
            if(!valor_modificado.equals(valor)){
                editor.putString(key, valor_modificado);
                editor.apply();
                YouChatApplication.setVersion_info();
                YouChatApplication.cargarPreferencias();
                return true;
            }
        }
        else if(esProvincia){
                 if(prov0.isChecked()) valor_modificado =  "";
            else if(prov1.isChecked()) valor_modificado =  "Pinar del Río";
            else if(prov2.isChecked()) valor_modificado =  "Artemisa";
            else if(prov3.isChecked()) valor_modificado =  "Mayabeque";
            else if(prov4.isChecked()) valor_modificado =  "La Habana";
            else if(prov5.isChecked()) valor_modificado =  "Matanzas";
            else if(prov6.isChecked()) valor_modificado =  "Cienfuegos";
            else if(prov7.isChecked()) valor_modificado =  "Villa Clara";
            else if(prov8.isChecked()) valor_modificado =  "Sancti Spíritus";
            else if(prov9.isChecked()) valor_modificado =  "Ciego de Ávila";
            else if(prov10.isChecked()) valor_modificado = "Camagüey";
            else if(prov11.isChecked()) valor_modificado = "Las Tunas";
            else if(prov12.isChecked()) valor_modificado = "Holguín";
            else if(prov13.isChecked()) valor_modificado = "Granma";
            else if(prov14.isChecked()) valor_modificado = "Santiago de Cuba";
            else if(prov15.isChecked()) valor_modificado = "Guantánamo";
            else valor_modificado = "Isla de la Juventud";

            if(!valor_modificado.equals(valor)){
                editor.putString(key, valor_modificado);
                editor.apply();
                YouChatApplication.setVersion_info();
                YouChatApplication.cargarPreferencias();
                return true;
            }
        }
        else {
            if(action.equals("alias")) valor_modificado = campo_alias.getText().toString().trim();
            else if(action.equals("info")) valor_modificado = campo_info.getText().toString().trim();
            else valor_modificado = campo_telefono.getText().toString();
            if(!valor_modificado.equals(valor)){
                editor.putString(key, valor_modificado);
                editor.apply();
                YouChatApplication.setVersion_info();
                YouChatApplication.cargarPreferencias();
                return true;
            }
        }
        return false;
    }

    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getContext(), R.style.PickerDialog, this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {

            fechCumple="";
            if(day<=9) fechCumple="0"+day;
            else fechCumple=""+day;
            int m=month+1;
            if(m<=9) fechCumple=fechCumple+"/0"+m;
            else fechCumple=fechCumple+"/"+m;

            String dd = ""+day;
            String aa = ""+year;
            String mm ="";
            switch (month){
                case 0: mm=" de enero de "; break;
                case 1: mm=" de febrero de "; break;
                case 2: mm=" de marzo de "; break;
                case 3: mm=" de abril de "; break;
                case 4: mm=" de mayo de "; break;
                case 5: mm=" de junio de "; break;
                case 6: mm=" de julio de "; break;
                case 7: mm=" de agosto de "; break;
                case 8: mm=" de septiembre de "; break;
                case 9: mm=" de octubre de "; break;
                case 10: mm=" de noviembre de "; break;
                case 11: mm=" de diciembre de "; break;
                default: mm=" del calendario apocalíptico de ";
            }
            text_calendario.setText(dd+mm+aa);
        }
    }
}
