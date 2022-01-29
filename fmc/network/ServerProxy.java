package cs240.fmc.network;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

import cs240.fmc.model.ClientObjects.DataCache;
import cs240.fmc.model.ServerObjects.Event;
import cs240.fmc.model.ServerObjects.Person;
import cs240.fmc.request.EventRequest;
import cs240.fmc.request.LoginRequest;
import cs240.fmc.request.PersonRequest;
import cs240.fmc.request.RegisterRequest;
import cs240.fmc.result.EventsResult;
import cs240.fmc.result.LoginResult;
import cs240.fmc.result.PersonsResult;
import cs240.fmc.result.RegisterResult;

public class ServerProxy {
    public static String serverHostName;
    public static int serverPortNumber;

    public LoginResult login(LoginRequest request) {
        LoginResult result = null;
        try {
            Gson gson = new Gson();
            String requestData = gson.toJson(request);

            URL url = new URL("http://" + serverHostName + ":" + serverPortNumber + "/user/login");
            HttpURLConnection http = (HttpURLConnection)url.openConnection();

            http.setRequestMethod("POST");
            http.setDoOutput(true);
            http.addRequestProperty("Accept", "application/json");
            http.connect();

            OutputStream requestBody = http.getOutputStream();
            writeString(requestData, requestBody);
            requestBody.close();

            if (http.getResponseCode() == HttpURLConnection.HTTP_OK) {
                InputStream respBody = http.getInputStream();
                String respData = readString(respBody);
                respBody.close();
                result = gson.fromJson(respData, LoginResult.class);
                return result;
            }
            else {
                System.out.println("ERROR: " + http.getResponseMessage());
                 result= new LoginResult(http.getResponseMessage());
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    public RegisterResult register(RegisterRequest request) {
        RegisterResult result = null;
        try {
            Gson gson = new Gson();
            String requestData = gson.toJson(request);

            URL url = new URL("http://" + serverHostName + ":" + serverPortNumber + "/user/register");
            HttpURLConnection http = (HttpURLConnection)url.openConnection();

            http.setRequestMethod("POST");
            http.setDoOutput(true);
            http.addRequestProperty("Accept", "application/json");
            http.connect();

            OutputStream requestBody = http.getOutputStream();
            writeString(requestData, requestBody);
            requestBody.close();

            if (http.getResponseCode() == HttpURLConnection.HTTP_OK) {
                InputStream respBody = http.getInputStream();
                String respData = readString(respBody);
                respBody.close();
                result = gson.fromJson(respData, RegisterResult.class);
                return result;
            }
            else {
                System.out.println("ERROR: " + http.getResponseMessage());
                result= new RegisterResult(http.getResponseMessage());
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    public PersonsResult person(PersonRequest request) {
        PersonsResult result = null;
        try {
            Gson gson = new Gson();
            String requestData = gson.toJson(request);

            URL url = new URL("http://" + serverHostName + ":" + serverPortNumber + "/person");
            HttpURLConnection http = (HttpURLConnection)url.openConnection();

            String authToken = request.getAuthToken();

            http.setRequestMethod("GET");
            http.addRequestProperty("Authorization", authToken);
            http.addRequestProperty("Accept", "application/json");
            http.connect();

            if (http.getResponseCode() == HttpURLConnection.HTTP_OK) {
                InputStream respBody = http.getInputStream();
                String respData = readString(respBody);
                respBody.close();
                result = gson.fromJson(respData, PersonsResult.class);
                return result;
            }
            else {
                System.out.println("ERROR: " + http.getResponseMessage());
                result= new PersonsResult(http.getResponseMessage());
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    public EventsResult event(EventRequest request) {
        EventsResult result = null;
        try {
            Gson gson = new Gson();
            String requestData = gson.toJson(request);

            URL url = new URL("http://" + serverHostName + ":" + serverPortNumber + "/event");
            HttpURLConnection http = (HttpURLConnection)url.openConnection();

            String authToken = request.getAuthToken();

            http.setRequestMethod("GET");
            http.addRequestProperty("Authorization", authToken);
            http.addRequestProperty("Accept", "application/json");
            http.connect();

            if (http.getResponseCode() == HttpURLConnection.HTTP_OK) {
                InputStream respBody = http.getInputStream();
                String respData = readString(respBody);
                respBody.close();
                result = gson.fromJson(respData, EventsResult.class);
                return result;
            }
            else {
                System.out.println("ERROR: " + http.getResponseMessage());
                result= new EventsResult(http.getResponseMessage());
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    private void writeString(String requestData, OutputStream requestBody) throws IOException {
        Writer writer = new OutputStreamWriter(requestBody);
        writer.write(requestData);
        writer.close();
    }

    public static void setServerHostName(String serverHostName) {
        ServerProxy.serverHostName = serverHostName;
    }

    public static void setServerPortNumber(int serverPortNumber) {
        ServerProxy.serverPortNumber = serverPortNumber;
    }

    private String readString(InputStream reqBody) {
        Scanner scanner = new Scanner(reqBody);
        StringBuilder reqBodyBuilder = new StringBuilder();
        while (scanner.hasNextLine()) {
            reqBodyBuilder.append(scanner.nextLine());
        }
        scanner.close();

        return reqBodyBuilder.toString();
    }

    public void retrieveFamilyData(LoginResult result) {
        String authTokenName = result.getAuthToken();

        PersonRequest personRequest = new PersonRequest(authTokenName);
        PersonsResult personsResult = this.person(personRequest);
        ArrayList<Person> people = personsResult.getData();

        EventRequest eventRequest = new EventRequest(authTokenName);
        EventsResult eventsResult = this.event(eventRequest);
        ArrayList<Event> events = eventsResult.getData();

        String userPersonID = result.getPersonID();
        DataCache.populateDataCache(people, events, userPersonID);
    }

    public void retrieveFamilyData(RegisterResult result) {
        String authTokenName = result.getAuthToken();

        PersonRequest personRequest = new PersonRequest(authTokenName);
        PersonsResult personsResult = this.person(personRequest);
        ArrayList<Person> people = personsResult.getData();

        EventRequest eventRequest = new EventRequest(authTokenName);
        EventsResult eventsResult = this.event(eventRequest);
        ArrayList<Event> events = eventsResult.getData();

        String userPersonID = result.getPersonID();
        DataCache.populateDataCache(people, events, userPersonID);
    }
}
