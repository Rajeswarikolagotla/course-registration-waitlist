package com.example.waitlist;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;

public final class Server {
    private static final CourseService service = new CourseService(2);
    public static void main(String[] args) throws Exception {
        int port = args.length == 0 ? 8080 : Integer.parseInt(args[0]);
        HttpServer server = HttpServer.create(new InetSocketAddress("127.0.0.1", port), 0);
        server.createContext("/api/state", exchange -> {
            if (!exchange.getRequestMethod().equals("GET")) { send(exchange,405,"{\"error\":\"Method not allowed\"}"); return; }
            CourseService.Snapshot s = service.snapshot();
            send(exchange,200,"{\"capacity\":"+s.capacity()+",\"enrolled\":"+array(s.enrolled())+",\"waitlisted\":"+array(s.waitlisted())+"}");
        });
        server.createContext("/api/", exchange -> {
            if (!exchange.getRequestMethod().equals("POST")) { send(exchange,405,"{\"error\":\"Method not allowed\"}"); return; }
            String action = exchange.getRequestURI().getPath().substring("/api/".length());
            try {
                String body = new String(exchange.getRequestBody().readNBytes(2048), StandardCharsets.UTF_8);
                Map<String,String> form = parseForm(body);
                String learner = form.get("learner");
                CourseService.Result result = switch(action) {
                    case "enroll" -> service.enroll(learner);
                    case "drop" -> service.drop(learner);
                    case "prerequisite" -> {
                        String completed = form.get("completed");
                        if (!"true".equals(completed) && !"false".equals(completed)) throw new IllegalArgumentException("completed must be true or false");
                        yield service.setPrerequisite(learner, Boolean.parseBoolean(completed));
                    }
                    default -> null;
                };
                if (result == null) { send(exchange,404,"{\"error\":\"Unknown action\"}"); return; }
                send(exchange,200,"{\"code\":"+quote(result.code())+",\"message\":"+quote(result.message())+",\"learner\":"+quote(result.learner())+",\"promoted\":"+quote(result.promoted())+"}");
            } catch (IllegalArgumentException e) { send(exchange,400,"{\"error\":"+quote(e.getMessage())+"}"); }
        });
        server.createContext("/", exchange -> {
            if (!exchange.getRequestMethod().equals("GET")) {send(exchange,405,"Method not allowed"); return;}
            String path = exchange.getRequestURI().getPath();
            String file = path.equals("/") ? "index.html" : path.substring(1);
            if (!Set.of("index.html", "app.js", "style.css").contains(file)) {send(exchange,404,"Not found");return;}
            byte[] bytes = Files.readAllBytes(Path.of("web",file));
            exchange.getResponseHeaders().set("Content-Type", file.endsWith(".js") ? "text/javascript; charset=utf-8" : file.endsWith(".css") ? "text/css; charset=utf-8" : "text/html; charset=utf-8");
            exchange.sendResponseHeaders(200,bytes.length);
            try(OutputStream out=exchange.getResponseBody()) {out.write(bytes);}
        });
        server.start();
        System.out.println("Open http://127.0.0.1:"+port);
    }
    private static Map<String,String> parseForm(String body) {
        Map<String,String> map = new HashMap<>();
        for (String pair : body.split("&")) {
            if (pair.isEmpty()) continue;
            String[] parts = pair.split("=",2);
            map.put(URLDecoder.decode(parts[0],StandardCharsets.UTF_8), URLDecoder.decode(parts.length>1?parts[1]:"",StandardCharsets.UTF_8));
        }
        return map;
    }
    private static String quote(String value) {
        if (value == null) return "null";
        return "\""+value.replace("\\","\\\\").replace("\"","\\\"").replace("\n","\\n").replace("\r","\\r")+"\"";
    }
    private static String array(List<String> values) {return "["+String.join(",",values.stream().map(Server::quote).toList())+"]";}
    private static void send(HttpExchange e,int status,String body) throws IOException {
        byte[] bytes=body.getBytes(StandardCharsets.UTF_8);
        e.getResponseHeaders().set("Content-Type","application/json; charset=utf-8");
        e.getResponseHeaders().set("Cache-Control","no-store");
        e.sendResponseHeaders(status,bytes.length);
        try(OutputStream out=e.getResponseBody()){out.write(bytes);}
    }
}
