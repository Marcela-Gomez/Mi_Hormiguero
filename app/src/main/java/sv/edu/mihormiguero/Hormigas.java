package sv.edu.mihormiguero;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

// ----------------------------------------------------------------------
// 1. INTERFAZ DE ACCIÓN (A NIVEL SUPERIOR)
// ----------------------------------------------------------------------
/**
 * Interfaz para comunicar acciones (editar/eliminar) desde el Adaptador
 * de vuelta a la Activity principal (Hormigas).
 */
interface OnGastoActionListener {
    // CORRECCIÓN CLAVE: Usamos el nombre simple de la clase anidada.
    void onGastoEdited(Hormigas.GastoHormiga gasto);
    void onGastoDeleted(Hormigas.GastoHormiga gasto);
}

// CLASE PRINCIPAL: Hormigas Activity
// Implementa la interfaz de nivel superior OnGastoActionListener
public class Hormigas extends AppCompatActivity implements OnGastoActionListener { // ESTA LÍNEA AHORA FUNCIONARÁ

    private RecyclerView recyclerView;
    private GastoHormigaAdapter adapter;
    private List<GastoHormiga> gastosList;
    private TextView totalHormigasText;
    private TextView totalGastosText;
    private int nextId = 1; // Contador simple para IDs

    // ----------------------------------------------------------------------
    // 2. CLASE MODELO ANIDADA (PUBLIC STATIC)
    // ----------------------------------------------------------------------
    /**
     * Clase modelo para representar un Gasto Hormiga.
     */
    public static class GastoHormiga {
        private String id;
        private String nombre;
        private double cantidad;

        public GastoHormiga(String id, String nombre, double cantidad) {
            this.id = id;
            this.nombre = nombre;
            this.cantidad = cantidad;
        }

        // Getters
        public String getId() { return id; }
        public String getNombre() { return nombre; }
        public double getCantidad() { return cantidad; }

        // Setters
        public void setNombre(String nombre) { this.nombre = nombre; }
        public void setCantidad(double cantidad) { this.cantidad = cantidad; }
    }

    // ----------------------------------------------------------------------
    // 3. ADAPTADOR ANIDADO (Clase interna no estática)
    // ----------------------------------------------------------------------
    /**
     * Adaptador para el RecyclerView.
     */
    // Se cambia a 'static' para evitar que GastoHormigaAdapter mantenga una referencia implícita a la clase Hormigas,
    // lo cual puede causar fugas de memoria y contribuir al error de herencia cíclica que apareció en la consola.
    public static class GastoHormigaAdapter extends RecyclerView.Adapter<GastoHormigaAdapter.GastoViewHolder> {

        private List<GastoHormiga> gastosList;
        private Context context;
        // Referencia a la interfaz de nivel superior
        private OnGastoActionListener listener;

        public GastoHormigaAdapter(Context context, List<GastoHormiga> gastosList, OnGastoActionListener listener) {
            this.context = context;
            this.gastosList = gastosList;
            this.listener = listener;
        }

        @NonNull
        @Override
        public GastoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            // Asumiendo que el recurso R.layout.item_gasto_hormiga existe
            View view = LayoutInflater.from(context).inflate(R.layout.item_gasto_hormiga, parent, false);
            return new GastoViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull GastoViewHolder holder, int position) {
            GastoHormiga gasto = gastosList.get(position);
            holder.textNombre.setText("hormiga: " + gasto.getNombre());
            holder.textCantidad.setText(String.format("Cantidad: $%.2f", gasto.getCantidad()));

            // Botón de opciones (MANTENER DESHABILITADO)
            holder.buttonOptions.setVisibility(View.GONE);
        }

        @Override
        public int getItemCount() {
            return gastosList.size();
        }

        // ViewHolder anidado
        public static class GastoViewHolder extends RecyclerView.ViewHolder {
            TextView textNombre;
            TextView textCantidad;
            ImageButton buttonOptions;

            public GastoViewHolder(@NonNull View itemView) {
                super(itemView);
                // Asumiendo que los IDs existen en item_gasto_hormiga.xml
                textNombre = itemView.findViewById(R.id.text_nombre_hormiga);
                textCantidad = itemView.findViewById(R.id.text_cantidad_hormiga);
                buttonOptions = itemView.findViewById(R.id.button_options);
            }
        }

        public void updateList(List<GastoHormiga> newList) {
            this.gastosList = newList;
            notifyDataSetChanged();
        }
    }

    // ----------------------------------------------------------------------
    // 4. METODOS DEL ACTIVITY PRINCIPAL
    // ----------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hormigas);

        // 1. Inicializar UI
        totalHormigasText = findViewById(R.id.text_total_hormigas_valor);
        totalGastosText = findViewById(R.id.text_total_gastos);
        recyclerView = findViewById(R.id.recycler_gastos_hormiga);

        // 2. Simulación de datos iniciales
        gastosList = new ArrayList<>();
        // Agregar un gasto de ejemplo para poblar la lista inicialmente
        gastosList.add(new GastoHormiga(String.valueOf(nextId++), "comprar frutsi", 1.25));
        gastosList.add(new GastoHormiga(String.valueOf(nextId++), "boleto de autobús", 3.00));

        // 3. Configurar RecyclerView
        adapter = new GastoHormigaAdapter(this, gastosList, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        // 4. Configurar Botón Agregar (+)
        ImageButton buttonAdd = findViewById(R.id.button_add);
        buttonAdd.setOnClickListener(v -> showAddDialogSimple());

        // 5. Actualizar totales
        updateTotals();
    }

    /**
     * Lógica de Diálogo AGREGAR (+) - VERSIÓN SIMPLE (sin archivos XML de diálogo)
     */
    private void showAddDialogSimple() {
        Context context = this;
        // Asegurarse de usar la clase completamente cualificada para evitar errores de símbolos
        android.widget.LinearLayout container = new android.widget.LinearLayout(context);
        container.setOrientation(android.widget.LinearLayout.VERTICAL);
        int paddingInPx = (int) (30 * getResources().getDisplayMetrics().density);
        container.setPadding(paddingInPx, paddingInPx, paddingInPx, 0);

        android.widget.LinearLayout.LayoutParams layoutParams = new android.widget.LinearLayout.LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT
        );
        layoutParams.setMargins(0, 0, 0, paddingInPx / 2);

        final EditText editNombre = new EditText(context);
        editNombre.setHint("Nombre de la hormiga");
        container.addView(editNombre, layoutParams);

        final EditText editCantidad = new EditText(context);
        editCantidad.setHint("Cantidad ($)");
        editCantidad.setInputType(android.text.InputType.TYPE_CLASS_NUMBER | android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL);
        container.addView(editCantidad, layoutParams);

        new AlertDialog.Builder(this)
                .setTitle("AGREGAR GASTO HORMIGA")
                .setView(container)
                .setPositiveButton("ACEPTAR", (dialog, which) -> {
                    try {
                        String nombre = editNombre.getText().toString().trim();
                        double cantidad = Double.parseDouble(editCantidad.getText().toString());

                        if (nombre.isEmpty()) {
                            Toast.makeText(this, "El nombre no puede estar vacío.", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        GastoHormiga nuevoGasto = new GastoHormiga(String.valueOf(nextId++), nombre, cantidad);
                        gastosList.add(0, nuevoGasto);
                        adapter.updateList(gastosList);
                        updateTotals();

                        Toast.makeText(this, "Gasto Hormiga agregado.", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    } catch (NumberFormatException e) {
                        Toast.makeText(this, "Por favor, ingrese una cantidad válida (número).", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("CANCELAR", (dialog, which) -> dialog.cancel())
                .show();
    }

    // Implementación de la Interfaz del Adaptador (cuando un gasto es Editado)
    @Override
    public void onGastoEdited(GastoHormiga gasto) {
        adapter.notifyDataSetChanged();
        updateTotals();
    }

    // Implementación de la Interfaz del Adaptador (cuando un gasto es Eliminado)
    @Override
    public void onGastoDeleted(GastoHormiga gasto) {
        gastosList.remove(gasto);
        adapter.updateList(gastosList);
        updateTotals();
    }

    // Lógica de Totales
    private void updateTotals() {
        // 1. Actualiza el número total de hormigas (gastos)
        totalHormigasText.setText(String.valueOf(gastosList.size()));

        // 2. Calcular y actualizar el Total de Gastos
        double totalGastado = 0;
        for (GastoHormiga gasto : gastosList) {
            totalGastado += gasto.getCantidad();
        }
        totalGastosText.setText(String.format("$%.2f", totalGastado));
    }
}
