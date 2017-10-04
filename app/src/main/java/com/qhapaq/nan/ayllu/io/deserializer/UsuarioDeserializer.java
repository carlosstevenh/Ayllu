package com.qhapaq.nan.ayllu.io.deserializer;

import com.qhapaq.nan.ayllu.domain.usuario.Usuario;
import com.qhapaq.nan.ayllu.io.model.JsonKeys;
import com.qhapaq.nan.ayllu.io.model.UsuarioResponse;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class UsuarioDeserializer implements JsonDeserializer<UsuarioResponse> {
    @Override
    public UsuarioResponse deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        Gson gson = new Gson();
        UsuarioResponse response = gson.fromJson(json, UsuarioResponse.class);

        //obtener el objeto Usuario
        JsonObject usuarioResponseData = json.getAsJsonObject().getAsJsonObject(JsonKeys.USUARIOS_RESULTS);

        //obtener el array Usuarios
        JsonArray usuarioArray = usuarioResponseData.getAsJsonArray(JsonKeys.USUARIOS_ARRAY);

        //convertir el json array a objetos de la clase Usuario
        response.setCategorias(extractUsersFromJsonArray(usuarioArray));
        return response;
    }

    private ArrayList<Usuario> extractUsersFromJsonArray(JsonArray array) {
        ArrayList<Usuario> usuarios = new ArrayList<>();

        for (int i = 0; i<array.size(); i++){
            JsonObject usuarioData = array.get(i).getAsJsonObject();
            Usuario usuario = new Usuario(
                    usuarioData.get(JsonKeys.COD_USU).getAsString(),
                    usuarioData.get(JsonKeys.ID_USU).getAsString(),
                    usuarioData.get(JsonKeys.NOM_USU).getAsString(),
                    usuarioData.get(JsonKeys.APE_USU).getAsString(),
                    usuarioData.get(JsonKeys.TIPO_USU).getAsString(),
                    usuarioData.get(JsonKeys.ESTADO_USU).getAsString(),
                    usuarioData.get(JsonKeys.API_USU).getAsString(),
                    usuarioData.get(JsonKeys.PAIS_USU).getAsString()
            );
            usuarios.add(usuario);
        }
        return usuarios;
    }
}
