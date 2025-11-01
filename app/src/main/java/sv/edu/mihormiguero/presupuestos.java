package sv.edu.mihormiguero;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
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
 * Interface to communicate actions (edit/delete) from the Adapter
 * back to the main Activity (Presupuesto).
 */
interface OnTransaccionActionListener {
    // Usamos 'presupuestos.Transaccion' para asegurar la referencia a la clase anidada
    void onTransaccionEdited(presupuestos.Transaccion transaccion);
    void onTransaccionDeleted(presupuestos.Transaccion transaccion);
}

// CLASE PRINCIPAL: presupuestos Activity
public class presupuestos extends AppCompatActivity implements OnTransaccionActionListener {

    private RecyclerView recyclerGastos;
    // private RecyclerView recyclerIngresos; // Se elimina porque la sección de Ingresos ahora es estática en el XML.
    private TransaccionAdapter gastosAdapter;
    // private TransaccionAdapter ingresosAdapter; // Se elimina porque no es necesario para la lista estática.

    private List<Transaccion> gastosList;
    private List<Transaccion> ingresosList; // Se mantiene solo para datos estáticos iniciales, pero no se usa en el UI
    private int nextId = 1; // Simple counter for IDs

    // ----------------------------------------------------------------------
    // 2. CLASE MODELO ANIDADA (PUBLIC STATIC)
    // ----------------------------------------------------------------------
    /**
     * Data model class for transactions (Income and Expense).
     */
    public static class Transaccion {
        private String id;
        private String nombre;
        private double cantidad;
        private String tipo; // "INGRESO" or "GASTO"

        public Transaccion(String id, String nombre, double cantidad, String tipo) {
            this.id = id;
            this.nombre = nombre;
            this.cantidad = cantidad;
            this.tipo = tipo;
        }

        // Getters
        public String getId() { return id; }
        public String getNombre() { return nombre; }
        public double getCantidad() { return cantidad; }
        public String getTipo() { return tipo; }

        // Setters
        public void setNombre(String nombre) { this.nombre = nombre; }
        public void setCantidad(double cantidad) { this.cantidad = cantidad; }
    }

    // ----------------------------------------------------------------------
    // 3. ADAPTADOR ANIDADO (PUBLIC STATIC)
    // ----------------------------------------------------------------------
    /**
     * Adapter for the RecyclerView, handling both Income and Expense items.
     */
    public static class TransaccionAdapter extends RecyclerView.Adapter<TransaccionAdapter.TransaccionViewHolder> {

        private List<Transaccion> transaccionList;
        private Context context;
        private OnTransaccionActionListener listener;

        public TransaccionAdapter(Context context, List<Transaccion> transaccionList, OnTransaccionActionListener listener) {
            this.context = context;
            this.transaccionList = transaccionList;
            this.listener = listener;
        }

        @NonNull
        @Override
        public TransaccionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            // Assuming R.layout.item_transaccion exists
            View view = LayoutInflater.from(context).inflate(R.layout.item_transaccion, parent, false);
            return new TransaccionViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull TransaccionViewHolder holder, int position) {
            Transaccion transaccion = transaccionList.get(position);

            // Display transaction name (e.g., "Agua", "Salario")
            holder.textNombre.setText(transaccion.getNombre());

            // Display amount (Optional: You can differentiate color here based on transaccion.getTipo())
            holder.textCantidad.setText(String.format("$%.2f", transaccion.getCantidad()));

            // Enable the options button for Edit/Delete
            holder.buttonOptions.setVisibility(View.VISIBLE);
            holder.buttonOptions.setOnClickListener(v -> showPopupMenu(v, transaccion));
        }

        @Override
        public int getItemCount() {
            return transaccionList.size();
        }

        /**
         * Shows the Edit and Delete options menu when the '...' button is tapped.
         */
        private void showPopupMenu(View view, final Transaccion transaccion) {
            PopupMenu popup = new PopupMenu(context, view);

            // Menu Item 1: Edit
            popup.getMenu().add(0, 1, 0, "Editar");
            // Menu Item 2: Delete
            popup.getMenu().add(0, 2, 0, "Eliminar");

            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    if (listener == null) return true;

                    switch (item.getItemId()) {
                        case 1: // Editar
                            // Se realiza un cast explícito a 'presupuestos' para acceder al método
                            ((presupuestos)context).showEditDialogSimple(transaccion);
                            return true;
                        case 2: // Eliminar
                            ((presupuestos)context).showDeleteConfirmationDialog(transaccion);
                            return true;
                        default:
                            return false;
                    }
                }
            });
            popup.show();
        }

        // ViewHolder anidado
        public static class TransaccionViewHolder extends RecyclerView.ViewHolder {
            TextView textNombre;
            TextView textCantidad;
            ImageButton buttonOptions;

            public TransaccionViewHolder(@NonNull View itemView) {
                super(itemView);
                // Assuming R.id.text_nombre, R.id.text_cantidad, R.id.button_options exist in item_transaccion.xml
                textNombre = itemView.findViewById(R.id.text_nombre);
                textCantidad = itemView.findViewById(R.id.text_cantidad);
                buttonOptions = itemView.findViewById(R.id.button_options);
            }
        }

        public void updateList(List<Transaccion> newList) {
            this.transaccionList = newList;
            notifyDataSetChanged();
        }
    }

    // ----------------------------------------------------------------------
    // 4. METODOS DEL ACTIVITY PRINCIPAL
    // ----------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // CORRECCIÓN: Se usa R.layout.activity_presupuesto (singular)
        setContentView(R.layout.activity_presupuestos);

        // 1. Inicializar listas
        gastosList = new ArrayList<>();
        ingresosList = new ArrayList<>();

        // Simulación de datos iniciales
        // GASTOS (Expense)
        gastosList.add(new Transaccion(String.valueOf(nextId++), "Agua", 25.50, "GASTO"));
        gastosList.add(new Transaccion(String.valueOf(nextId++), "Luz", 45.00, "GASTO"));
        gastosList.add(new Transaccion(String.valueOf(nextId++), "Colegiatura", 120.00, "GASTO"));

        // INGRESOS (Income) - Solo para datos estáticos del modelo, no para UI dinámico
        ingresosList.add(new Transaccion(String.valueOf(nextId++), "Salario", 800.00, "INGRESO"));
        ingresosList.add(new Transaccion(String.valueOf(nextId++), "Emprendimiento", 150.00, "INGRESO"));


        // 2. Inicializar RecyclerView de GASTOS
        // CORRECCIÓN: Solo se inicializa el RecyclerView de Gastos
        recyclerGastos = findViewById(R.id.recycler_view_gastos);

        // 3. Configurar RecyclerView de GASTOS
        gastosAdapter = new TransaccionAdapter(this, gastosList, this);
        recyclerGastos.setLayoutManager(new LinearLayoutManager(this));
        recyclerGastos.setAdapter(gastosAdapter);

        // 4. Configurar Botón Agregar GASTO (+)
        ImageButton buttonAddGasto = findViewById(R.id.button_add_gasto);
        buttonAddGasto.setOnClickListener(v -> showAddDialogSimple("GASTO"));

        // 5. Configurar Botón Agregar INGRESO (+) - Deshabilitado por requisito
        ImageButton buttonAddIngreso = findViewById(R.id.button_add_ingreso);
        // Deshabilita la funcionalidad de Ingresos
        buttonAddIngreso.setOnClickListener(v -> Toast.makeText(this, "La funcionalidad de ingresos no está disponible aún.", Toast.LENGTH_SHORT).show());
    }

    // ----------------------------------------------------------------------
    // 5. MÉTODOS DE DIÁLOGO
    // ----------------------------------------------------------------------

    /**
     * Shows a simple Add dialog for a new GASTO.
     */
    private void showAddDialogSimple(String tipo) {
        Context context = this;
        // The container holds the two EditText fields
        android.widget.LinearLayout container = buildFormContainer(context);
        final EditText editNombre = (EditText) container.getChildAt(0);
        final EditText editCantidad = (EditText) container.getChildAt(1);

        new AlertDialog.Builder(this)
                .setTitle("AGREGAR " + tipo)
                .setView(container)
                .setPositiveButton("ACEPTAR", (dialog, which) -> {
                    try {
                        String nombre = editNombre.getText().toString().trim();
                        double cantidad = Double.parseDouble(editCantidad.getText().toString());

                        if (nombre.isEmpty()) {
                            Toast.makeText(this, "El nombre no puede estar vacío.", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        // Create and add the new transaction
                        Transaccion nuevaTransaccion = new Transaccion(String.valueOf(nextId++), nombre, cantidad, tipo);

                        if (tipo.equals("GASTO")) {
                            gastosList.add(0, nuevaTransaccion);
                            gastosAdapter.updateList(gastosList);
                        }
                        // No implementation for INGRESO yet

                        Toast.makeText(this, tipo + " agregado.", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    } catch (NumberFormatException e) {
                        Toast.makeText(this, "Por favor, ingrese una cantidad válida (número).", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("CANCELAR", (dialog, which) -> dialog.cancel())
                .show();
    }

    /**
     * Shows a simple Edit dialog for an existing GASTO.
     */
    public void showEditDialogSimple(final Transaccion transaccion) {
        Context context = this;
        android.widget.LinearLayout container = buildFormContainer(context);
        final EditText editNombre = (EditText) container.getChildAt(0);
        final EditText editCantidad = (EditText) container.getChildAt(1);

        // Pre-fill fields with existing data
        editNombre.setText(transaccion.getNombre());
        editCantidad.setText(String.valueOf(transaccion.getCantidad()));

        new AlertDialog.Builder(this)
                .setTitle("EDITAR " + transaccion.getTipo())
                .setView(container)
                .setPositiveButton("ACTUALIZAR", (dialog, which) -> {
                    try {
                        String nombre = editNombre.getText().toString().trim();
                        double cantidad = Double.parseDouble(editCantidad.getText().toString());

                        if (nombre.isEmpty()) {
                            Toast.makeText(this, "El nombre no puede estar vacío.", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        // Update the existing transaction object
                        transaccion.setNombre(nombre);
                        transaccion.setCantidad(cantidad);

                        // Notify the adapter to refresh the list item
                        onTransaccionEdited(transaccion);

                        Toast.makeText(this, transaccion.getTipo() + " actualizado.", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    } catch (NumberFormatException e) {
                        Toast.makeText(this, "Por favor, ingrese una cantidad válida (número).", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("CANCELAR", (dialog, which) -> dialog.cancel())
                .show();
    }

    /**
     * Shows a confirmation dialog before deleting a GASTO.
     */
    public void showDeleteConfirmationDialog(final Transaccion transaccion) {
        new AlertDialog.Builder(this)
                .setTitle("¿DESEA ELIMINAR ESTE " + transaccion.getTipo() + "?")
                .setMessage(transaccion.getNombre() + ": $" + String.format("%.2f", transaccion.getCantidad()))
                .setPositiveButton("SÍ", (dialog, which) -> {
                    onTransaccionDeleted(transaccion);
                    Toast.makeText(this, transaccion.getTipo() + " eliminado.", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("NO", (dialog, which) -> dialog.cancel())
                .show();
    }

    /**
     * Helper method to build the standard Name/Amount form for dialogs.
     */
    private android.widget.LinearLayout buildFormContainer(Context context) {
        android.widget.LinearLayout container = new android.widget.LinearLayout(context);
        container.setOrientation(android.widget.LinearLayout.VERTICAL);
        int paddingInPx = (int) (20 * getResources().getDisplayMetrics().density);
        container.setPadding(paddingInPx, paddingInPx, paddingInPx, 0);

        android.widget.LinearLayout.LayoutParams layoutParams = new android.widget.LinearLayout.LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT
        );
        layoutParams.setMargins(0, 0, 0, paddingInPx / 2);

        final EditText editNombre = new EditText(context);
        editNombre.setHint("Nombre");
        container.addView(editNombre, layoutParams);

        final EditText editCantidad = new EditText(context);
        editCantidad.setHint("Cantidad ($)");
        editCantidad.setInputType(android.text.InputType.TYPE_CLASS_NUMBER | android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL);
        container.addView(editCantidad, layoutParams);

        return container;
    }


    // ----------------------------------------------------------------------
    // 6. IMPLEMENTACIÓN DE INTERFAZ (OnTransaccionActionListener)
    // ----------------------------------------------------------------------

    @Override
    public void onTransaccionEdited(Transaccion transaccion) {
        // Find the index and notify the adapter of the change
        int index = -1;
        if (transaccion.getTipo().equals("GASTO")) {
            index = gastosList.indexOf(transaccion);
            if (index != -1) {
                gastosAdapter.notifyItemChanged(index);
            }
        }
        // updateTotals(); // Añadir si se implementa un cálculo total.
    }

    @Override
    public void onTransaccionDeleted(Transaccion transaccion) {
        if (transaccion.getTipo().equals("GASTO")) {
            int index = gastosList.indexOf(transaccion);
            if (index != -1) {
                gastosList.remove(index);
                gastosAdapter.notifyItemRemoved(index);
            }
        }
        // updateTotals(); // Añadir si se implementa un cálculo total.
    }
}