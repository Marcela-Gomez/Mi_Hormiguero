package sv.edu.mihormiguero;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.VideoView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;

public class MainActivity extends AppCompatActivity {
    private ImageView imagen;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        imagen = findViewById(R.id.ant);
        Glide.with(this)
                .asGif().load(R.drawable.hormiga).into(imagen);
        Animation animation = AnimationUtils.loadAnimation(MainActivity.this,R.anim.splash);
        imagen.setAnimation(animation);

        Handler manejador = new Handler();
        manejador.postDelayed(new Runnable() {
                                  @Override
                                  public void run() {
                                      Intent ventana = new Intent(MainActivity.this, inicio.class);
                                      startActivity(ventana);
                                      finish();
                                  }
                              }
                , 3000);
    }
}