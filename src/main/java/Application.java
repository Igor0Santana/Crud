package main.java;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URI;
import java.net.URL;
import java.net.HttpURLConnection;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import main.java.domain.model.Post;


// https://mvnrepository.com/artifact/com.fasterxml.jackson.core/jackson-annotations/2.19.2
// https://mvnrepository.com/artifact/com.fasterxml.jackson.core/jackson-core/2.19.2
// https://mvnrepository.com/artifact/com.fasterxml.jackson.core/jackson-databind/2.19.2

public class Application {
    ObjectMapper mapper = new ObjectMapper();

    public String writeObjectAsStringJson(Object object) throws JsonProcessingException {
        return mapper.writeValueAsString(object);
    }

    public Post fetchPost(Integer id) throws IOException{
        URL url = URI.create("https://jsonplaceholder.typicode.com/posts/" + id).toURL();
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");

        try (var in = con.getInputStream()) {
            return mapper.readValue(in, Post.class);
        } finally {
            con.disconnect();
        }
    }

    public int createPost(Post post) throws IOException {
        URL url = URI.create("https://jsonplaceholder.typicode.com/posts").toURL();
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("POST");
        con.setDoOutput(true);
        con.setRequestProperty("Content-Type", "application/json; charset=UTF-8'");
        con.setConnectTimeout(5000);
        con.setReadTimeout(5000);

        try (OutputStream os = con.getOutputStream();
             BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"))) {
            writer.write(mapper.writeValueAsString(post));
            writer.flush();
        }

        con.connect();
        return con.getResponseCode();
    }

    public int updatePost(Integer id, Post post) throws IOException {
        URL url = URI.create("https://jsonplaceholder.typicode.com/posts/" + id).toURL();
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("PUT");
        con.setDoOutput(true);
        con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
        con.setConnectTimeout(5000);
        con.setReadTimeout(5000);

        try (OutputStream os = con.getOutputStream();
             BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"))) {
            writer.write(mapper.writeValueAsString(post));
            writer.flush();
        }

        con.connect();
        return con.getResponseCode();
    }

    public int deletePost(Integer id) throws IOException {
        URL url = URI.create("https://jsonplaceholder.typicode.com/posts/" + id).toURL();
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("DELETE");
        int responseCode = connection.getResponseCode();
        connection.disconnect();
        return responseCode;
    }


    public static void main(String[] args) throws IOException {
        Application app = new Application();
        Post post = app.fetchPost(10);
        System.out.println("Postagem Original: " + app.writeObjectAsStringJson(post));

        Post novoPost = new Post(101, 12345, "Teste title", "teste body");
        int statusCode = app.createPost(novoPost);
        System.out.println("Status da Criação: " + statusCode);

        post.setTitle("Título Alterado");
        post.setBody("Corpo do post alterado");
        int updateStatusCode = app.updatePost(10, post);
        System.out.println("Status da Atualização: " + updateStatusCode);
        System.out.println("Postagem Alterada: " + app.writeObjectAsStringJson(post));

        int deleteStatusCode = app.deletePost(10);
        System.out.println("Status do Delete: " + deleteStatusCode);
    }
}