package com.example.edwin.ayllu.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import com.example.edwin.ayllu.R;

public class AdminUserTransactionActivity extends AppCompatActivity {

    /**
     * =============================================================================================
     * METODO:
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_user_transaction);

        //Inflamos el layout para el Fragmento AdminUserFormFragment
        getSupportFragmentManager().beginTransaction()
                .add(R.id.transaction_principal_context, new AdminUserFormFragment())
                .commit();

    }
}
