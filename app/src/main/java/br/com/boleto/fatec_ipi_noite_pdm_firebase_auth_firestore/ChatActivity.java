package br.com.boleto.fatec_ipi_noite_pdm_firebase_auth_firestore;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {

    private RecyclerView mensagensRecyclerView;
    private ChatAdapter adapter;
    private List<Mensagem> mensagens;
    private FirebaseUser fireUser;
    private CollectionReference collMensagensReferences;

    private EditText mensagemEdittext;

    private double latitude;
    private double longitude;

    private LocationManager locationManager;
    private LocationListener locationListener;
    private static final int REQUEST_PERMISSION_COD_GPS = 1001;

    private void setupFirebase (){
        fireUser = FirebaseAuth.getInstance().getCurrentUser();

        collMensagensReferences = FirebaseFirestore.getInstance().collection("mensagens");

        collMensagensReferences.addSnapshotListener((result, e) -> {
            mensagens.clear();
            for (DocumentSnapshot doc : result.getDocuments()){
                Mensagem m = doc.toObject(Mensagem.class);
                mensagens.add(m);
            }
            Collections.sort(mensagens);
            adapter.notifyDataSetChanged();
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        setupFirebase();
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    1000,
                    10,
                    locationListener
            );
        }else{
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1001);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_activity);
        mensagemEdittext = findViewById(R.id.mensagemEditText);
        mensagensRecyclerView = findViewById(R.id.mensagensRecyclerView);
        mensagens = new ArrayList<>();
        adapter = new ChatAdapter(this,mensagens);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        mensagensRecyclerView.setAdapter(adapter);
        mensagensRecyclerView.setLayoutManager(llm);


        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                latitude = location.getLatitude();
                longitude = location.getLongitude();
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_PERMISSION_COD_GPS){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                if (ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) ==
                        PackageManager.PERMISSION_GRANTED) {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                            1000,
                            10,
                            locationListener);
                }
            }else {
                Toast.makeText(this,getString(R.string.no_gps_no_app),Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        locationManager.removeUpdates(locationListener);
    }

    public void enviarMensagem(View view) {
        String texto = mensagemEdittext.getText().toString();
        Mensagem m = new Mensagem(texto, new java.util.Date(),fireUser.getEmail(),false);
        collMensagensReferences.add(m);
        mensagemEdittext.setText("");
    }

    //location
    public void sendLocation(View view) {
        String localizacao = getString(R.string.lat_long,latitude,longitude);
        Mensagem m = new Mensagem(localizacao, new java.util.Date(),fireUser.getEmail(), true);
        collMensagensReferences.add(m);
        mensagemEdittext.setText("");
    }

}


class ChatViewHolder extends RecyclerView.ViewHolder {
    public TextView dataNomeTextView;
    public TextView mensagemTextView;
    public ImageView openMapButton;


    public ChatViewHolder(View raiz) {
        super(raiz);
        dataNomeTextView = raiz.findViewById(R.id.dataNomeTextView);
        mensagemTextView = raiz.findViewById(R.id.mensagemTextView);
        openMapButton = raiz.findViewById(R.id.openMapButton);

    }
}

class ChatAdapter extends RecyclerView.Adapter<ChatViewHolder>{

    private Context context;
    private List <Mensagem> mensagens;


    public ChatAdapter(Context context, List<Mensagem> mensagens) {
        this.context = context;
        this.mensagens = mensagens;
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //return null;
        LayoutInflater inflater = LayoutInflater.from(context);
        View raiz = inflater.inflate(R.layout.list_item,parent,false);
        return new ChatViewHolder(raiz);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        Mensagem m = mensagens.get(position);
        if (m.getvMsg()) {
            holder.dataNomeTextView.setText(
                    context.getString(R.string.data_nome,
                            DateHelper.format(m.getDate()),
                            m.getEmail()));
            holder.mensagemTextView.setText(m.getTexto());
            holder.openMapButton.setOnClickListener(v -> {
                Uri uri = Uri.parse(m.getTexto().replace("Lat:", "geo:").replace("Long:", ","));
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                intent.setPackage("com.google.android.apps.maps");
                context.startActivity(intent);
                holder.openMapButton.setVisibility(View.VISIBLE);
            });
        } else {
            holder.dataNomeTextView.setText(
                    context.getString(R.string.data_nome,
                            DateHelper.format(m.getDate()),
                            m.getEmail()));
            holder.mensagemTextView.setText(m.getTexto());
            holder.openMapButton.setVisibility(View.INVISIBLE);
        }


    }

    @Override
    public int getItemCount() {
        return mensagens.size();
    }
}





