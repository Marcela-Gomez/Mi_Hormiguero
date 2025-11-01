package sv.edu.mihormiguero;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class metas extends AppCompatActivity {

    RecyclerView rvMetas;
    ArrayList<metasC> lista = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_metas);

        rvMetas = findViewById(R.id.rvMetas);
        rvMetas.setLayoutManager(new LinearLayoutManager(this));

        // 👉 Datos de prueba
        lista.add(new metasC("Viaje", "$200", "70%", "10/11/2025"));
        lista.add(new metasC("Laptop", "$700", "30%", "25/12/2025"));
        lista.add(new metasC("Moto", "$1500", "10%", "10/06/2026"));

        MetaAdapter adapter = new MetaAdapter(lista);
        rvMetas.setAdapter(adapter);
    }
}
