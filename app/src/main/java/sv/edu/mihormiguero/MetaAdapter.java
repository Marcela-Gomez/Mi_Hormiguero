package sv.edu.mihormiguero;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MetaAdapter extends RecyclerView.Adapter<MetaAdapter.ViewHolder> {

    private ArrayList<metasC> lista;

    public MetaAdapter(ArrayList<metasC> lista) {
        this.lista = lista;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.meta_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        metasC meta = lista.get(position);

        holder.tvNombre.setText(meta.nombre);
        holder.tvCantidad.setText(meta.cantidad);
        holder.tvPorcentaje.setText(meta.porcentaje);
        holder.tvFecha.setText(meta.fecha);
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvNombre, tvCantidad, tvPorcentaje, tvFecha;

        public ViewHolder(View itemView) {
            super(itemView);

            tvNombre = itemView.findViewById(R.id.tvNombreMeta);
            tvCantidad = itemView.findViewById(R.id.tvCantidad);
            tvPorcentaje = itemView.findViewById(R.id.tvPorcentaje);
            tvFecha = itemView.findViewById(R.id.tvFecha);
        }
    }
}
