package com.example.edwin.ayllu;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.edwin.ayllu.Domain.Usuario;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    public ArrayList<Usuario> u;
    public static Usuario user;
    private EditText et1,et2;
    private static final String TAG = "ERRORES";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        et1=(EditText)findViewById(R.id.txtName);
        et2=(EditText)findViewById(R.id.txtPsw);

    }

    public void login(View v) {

        loginUser(et1.getText().toString(),et2.getText().toString());
        if (user!=null){
            String tipo = user.getTipo_usu();
            if(tipo.equals("A")){
                Log.i("TAG", "Bienvenido administrador!! ");
                //menuAdministrador();
            }
            else {
                Log.i("TAG", "Bienvenido monitor!! ");
                //menuMonitor();
            }
        }

    }
    void loginUser(String ide, String pw){
        final Retrofit retrofit = new Retrofit.Builder().baseUrl(RestClient.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create()).build();

        RestClient service = retrofit.create(RestClient.class);
        Call<ArrayList<Usuario>> requestUser = service.getUsuario(ide,pw);
        requestUser.enqueue(new Callback<ArrayList<Usuario>>() {
            @Override
            public void onResponse(Call<ArrayList<Usuario>>call, Response<ArrayList<Usuario>> response) {
                if (response.isSuccessful()) {
                    u = response.body();
                    user=u.get(0);
                    Log.i("TAG", "error " );

                } else {
                    int statusCode = response.code();
                    Log.i("TAG", "error " + response.code());

                    // handle request errors yourself
                    //ResponseBody errorBody = response.errorBody();
                }

            }

            @Override
            public void onFailure(Call<ArrayList<Usuario>> call, Throwable t) {
                Log.e(TAG,"Error al iniciar sesión!!!!"+ t.getMessage());
                Toast login = Toast.makeText(getApplicationContext(),
                        "Error al iniciar sesión", Toast.LENGTH_SHORT);
                login.show();
                //et3.setText("xxxxxx");
            }

        });

    }
}
