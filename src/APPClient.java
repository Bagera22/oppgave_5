import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;

public class APPClient {
    public int savedSessionId;
    public String savedArgument;
    public int savedMultiplication;

    public static void main(String[] args) {
        APPClient app = new APPClient("datakomm.work", 80);
        app.postStudentInfo();
        app.requestTask1();
        app.preformTask1();
        app.requestTask2();
        app.preformTask2();
        app.requestTask3();
        app.preformTask3();
        //app.requestTask4();
        //app.preformTask4();
        app.recieveFeedback();
    }

    private String BASE_URL; // Base URL (address) of the server

    /**
     * Create an HTTP POST example
     *
     * @param host Will send request to this host: IP address or domain
     * @param port Will use this port
     */
    public APPClient(String host, int port) {
        BASE_URL = "http://" + host + ":" + port + "/";
    }

    /**
     * Post three random numbers to a specific path on the web server
     */
    public void postStudentInfo() {
        String a = "tommyaf@stud.ntnu.no";
        int b = 48264500;
        JSONObject json = new JSONObject();
        json.put("email", a);
        json.put("phone", b);
        System.out.println("Posting email + phone number to server");
        System.out.println(json.toString());
        sendPost("dkrest/auth", json);
    }

    public int sessionID() {
        return this.savedSessionId;
    }
    public String argument() {
        return this.savedArgument;
    }
    public int multiplication() {
        return this.savedMultiplication;
    }

    public void requestTask1() {
        sendGet("dkrest/gettask/1?sessionId=" + sessionID());
    }
    public void requestTask2() {
        sendGet("dkrest/gettask/2?sessionId=" + sessionID());
    }
    public void requestTask3() {
        sendGet("dkrest/gettask/3?sessionId=" + sessionID());
    }
    public void requestTask4() {
        sendGet("dkrest/gettask/4?sessionId=" + sessionID());
    }
    public void preformTask1() {
        String jsonObjectString = "{ \"sessionId\": Filler, \"msg\": \"Hello\" }";
        JSONObject object = new JSONObject(jsonObjectString);
        object.put("sessionId", sessionID());
        sendAnswer("dkrest/solve", object);
    }
    public void preformTask2() {
        String jsonObjectString = "{ \"sessionId\": Filler, \"msg\": \"Filler\" }";
        JSONObject object = new JSONObject(jsonObjectString);
        object.put("sessionId", sessionID());
        object.put("msg", argument());
        sendAnswer("dkrest/solve", object);
    }
    public void preformTask3() {
        String jsonObjectString = "{ \"sessionId\": Filler, \"result\": \"Filler\" }";
        JSONObject object = new JSONObject(jsonObjectString);
        object.put("sessionId", sessionID());
        object.put("result", multiplication());
        sendAnswer("dkrest/solve", object);
    }
    public void preformTask4() {
        String jsonObjectString = "{ \"sessionId\": Filler, \"pin\": \"Filler\" }";
        JSONObject object = new JSONObject(jsonObjectString);
        object.put("sessionId", sessionID());
        //object.put("pin", multiplication());
        sendAnswer("dkrest/solve", object);
    }
    public void recieveFeedback() {
        sendGet("dkrest/results/" + sessionID());
    }

    private void sendAnswer(String path, JSONObject object) {
        try {
            String url = BASE_URL + path;
            URL urlObj = new URL(url);
            System.out.println("Sending HTTP POST to " + url);
            HttpURLConnection con = (HttpURLConnection) urlObj.openConnection();

            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json");
            con.setDoOutput(true);

            OutputStream os = con.getOutputStream();
            os.write(object.toString().getBytes());
            os.flush();

            int responseCode = con.getResponseCode();
            if (responseCode == 200) {
                System.out.println("Server reached");

                // Response was OK, read the body (data)
                InputStream stream = con.getInputStream();
                String responseBody = convertStreamToString(stream);
                stream.close();
                System.out.println("Response from the server:");
                System.out.println(responseBody);
                try {

                    JSONObject response = new JSONObject(responseBody);
                    if (response.has("sessionId")) {
                        int sessionId = response.getInt("sessionId");
                        savedSessionId = sessionId;
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } else {
                String responseDescription = con.getResponseMessage();
                System.out.println("Request failed, response code: " + responseCode + " (" + responseDescription + ")");
            }
        } catch (ProtocolException e) {
            System.out.println("Protocol nto supported by the server");
        } catch (IOException e) {
            System.out.println("Something went wrong: " + e.getMessage());
            e.printStackTrace();
        }
    }


    /**
     * Send HTTP POST
     *
     * @param path     Relative path in the API.
     * @param jsonData The data in JSON format that will be posted to the server
     */
    private void sendPost(String path, JSONObject jsonData) {
        try {
            String url = BASE_URL + path;
            URL urlObj = new URL(url);
            System.out.println("Sending HTTP POST to " + url);
            HttpURLConnection con = (HttpURLConnection) urlObj.openConnection();

            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json");
            con.setDoOutput(true);

            OutputStream os = con.getOutputStream();
            os.write(jsonData.toString().getBytes());
            os.flush();

            int responseCode = con.getResponseCode();
            if (responseCode == 200) {
                System.out.println("Server reached");

                // Response was OK, read the body (data)
                InputStream stream = con.getInputStream();
                String responseBody = convertStreamToString(stream);
                stream.close();
                System.out.println("Response from the server:");
                System.out.println(responseBody);
                try {

                    JSONObject response = new JSONObject(responseBody);
                    if (response.has("sessionId")) {
                        int sessionId = response.getInt("sessionId");
                        savedSessionId = sessionId;
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } else {
                String responseDescription = con.getResponseMessage();
                System.out.println("Request failed, response code: " + responseCode + " (" + responseDescription + ")");
            }
        } catch (ProtocolException e) {
            System.out.println("Protocol nto supported by the server");
        } catch (IOException e) {
            System.out.println("Something went wrong: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Send HTTP GET
     *
     * @param path Relative path in the API.
     */
    private void sendGet(String path) {
        try {
            String url = BASE_URL + path;
            URL urlObj = new URL(url);
            System.out.println("Sending HTTP GET to " + url);
            HttpURLConnection con = (HttpURLConnection) urlObj.openConnection();

            con.setRequestMethod("GET");

            int responseCode = con.getResponseCode();
            if (responseCode == 200) {
                System.out.println("Server reached");
                // Response was OK, read the body (data)
                InputStream stream = con.getInputStream();
                String responseBody = convertStreamToString(stream);
                stream.close();
                System.out.println("Response from the server:");
                System.out.println(responseBody);
                try{
                    JSONObject response = new JSONObject(responseBody);

                    if (response.has("arguments")) {
                        JSONArray arg = response.getJSONArray("arguments");
                        savedArgument = arg.getString(0);
                        savedMultiplication= 1;
                        for (int i = 0; i < arg.length(); i++) {
                            savedMultiplication = savedMultiplication * arg.getInt(i);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                String responseDescription = con.getResponseMessage();
                System.out.println("Request failed, response code: " + responseCode + " (" + responseDescription + ")");
            }
        } catch (ProtocolException e) {
            System.out.println("Protocol not supported by the server");
        } catch (IOException e) {
            System.out.println("Something went wrong: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Read the whole content from an InputStream, return it as a string
     *
     * @param is Inputstream to read the body from
     * @return The whole body as a string
     */
    private String convertStreamToString(InputStream is) {
        BufferedReader in = new BufferedReader(new InputStreamReader(is));
        StringBuilder response = new StringBuilder();
        try {
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
                response.append('\n');
            }
        } catch (IOException ex) {
            System.out.println("Could not read the data from HTTP response: " + ex.getMessage());
        }
        return response.toString();
    }
}



