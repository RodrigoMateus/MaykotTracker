package com.maykot.maykottracker.rest;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.maykot.maykottracker.dao.DataBaseOpenHelper;
import com.maykot.maykottracker.models.Sinal;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * @author persys
 * @since 2016-06-09
 */
public class SinalRest {

    public static final String TAG = "SinalRest";

    public interface SinalService {
        @POST("sinal")
        Call<Void> envia(
                @Body Sinal sinal
        );
    }

    public static int enviaSinais(Context context) {
        int qunantidadeSinais = 0;

        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
                .create();


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://107.170.47.53:9200/sinais/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        SinalService sinalService = retrofit.create(SinalService.class);

        final SQLiteDatabase database = DataBaseOpenHelper.getInstance(context).getDatabase();
        List<Sinal> sinals = Sinal.list(database);

        qunantidadeSinais = sinals.size();

        for (final Sinal sinal : sinals) {
            Log.i(TAG, "Enviando " + sinal.toString());

            sinalService.envia(sinal).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        Log.i(TAG, response.message());
                        sinal.apaga(database);
                    } else {
                        try {
                            Log.e(TAG, "Falhou " + response.code() + " " +
                                    response.errorBody().string());
                        } catch (IOException e) {

                        }
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Log.e(TAG, "Falha", t);
                }
            });
        }

        return qunantidadeSinais;
    }
}
