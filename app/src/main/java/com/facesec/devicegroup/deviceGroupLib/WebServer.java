package com.facesec.devicegroup.deviceGroupLib;

import android.content.Context;
import android.util.Log;

import com.facesec.devicegroup.deviceGroupLib.util.ConfigUtils;
import com.facesec.devicegroup.deviceGroupLib.util.NetworkUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.HashMap;
import java.util.Map;

import fi.iki.elonen.NanoHTTPD;

/***
 * Created by Wang Tianyu
 * Web server class based on NanoHTTPD
 * Default port: 7034 set in ConfigUtils
 */

class WebServer extends NanoHTTPD {

    private static final String TAG = WebServer.class.getSimpleName();
    private Map<String, Boolean> clients;
    private ObjectMapper mapper;
    private ObjectNode memberData;
    private ObjectNode memberStatus;
    private Context context;
    private boolean status;
    private PeopleDataManager peopleDataManager;
    private String hostIp;

    /**
     * Initialize web server using default port 7034
     * @param context
     * @throws IOException
     * @throws KeyStoreException
     * @throws CertificateException
     * @throws NoSuchAlgorithmException
     * @throws UnrecoverableKeyException
     */

    public WebServer(Context context) throws IOException, KeyStoreException, CertificateException, NoSuchAlgorithmException, UnrecoverableKeyException {
        this(context, ConfigUtils.WEB_SERVER_PORT);
    }

    /**
     * Initialize web server using specialized port
     * @param context
     * @throws IOException
     * @throws KeyStoreException
     * @throws CertificateException
     * @throws NoSuchAlgorithmException
     * @throws UnrecoverableKeyException
     */

    public WebServer(Context context, int port) throws IOException, KeyStoreException, CertificateException, NoSuchAlgorithmException, UnrecoverableKeyException {
        super(port);
//      makeSecure(NanoHTTPD.makeSSLSocketFactory(
//                "/keystore.bks", "password".toCharArray()), null);
        this.context = context;
        peopleDataManager = PeopleDataManager.getPeopleDataManager();
//        KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
//        InputStream keyStoreStream = context.getAssets().open("keystore.bks");
//        keyStore.load(keyStoreStream, "password".toCharArray());
//        KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
//        keyManagerFactory.init(keyStore, "myCertificatePass".toCharArray());
//        makeSecure(NanoHTTPD.makeSSLSocketFactory(keyStore, keyManagerFactory), null);
    }


    /**
     * Serve the request through given session
     * @param session Request
     * @return
     */
    @Override
    public Response serve(IHTTPSession session) {
        String uri = session.getUri();
        String filename = uri.substring(1);

        if (uri.equals("/"))
            filename = "index.html";

        String mimetype = "";
        if (filename.contains(".html") || filename.contains(".htm")) {
            mimetype = "text/html";
        } else if (filename.contains(".js")) {
            mimetype = "text/javascript";
        } else if (filename.contains(".css")) {
            mimetype = "text/css";
        } else if (filename.contains(".ico")){
            mimetype = "image/x-icon";
        } else if (!filename.contains(".")){

        } else {
            filename = "index.html";
            mimetype = "text/html";
        }

        String response = "";
        String line = "";
        BufferedReader reader = null;
        if (!mimetype.equals("")){
            try {
                reader = new BufferedReader(new InputStreamReader(context.getAssets().open(filename)));

                while ((line = reader.readLine()) != null) {

                    response += line;
                }

                reader.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (session.getMethod() == Method.GET){
            if (!filename.contains(".")) {
                response = executeGetByFilename(filename);
            }
            return newFixedLengthResponse(Response.Status.OK, mimetype, response);
        }
        else if (session.getMethod() == Method.POST){
            try {
                session.parseBody(new HashMap<String, String>());
                String requestBody = session.getQueryParameterString();
                return newFixedLengthResponse(Response.Status.OK, mimetype, executePostByMethod(requestBody));
            } catch (IOException | ResponseException e) {
                e.printStackTrace();
                // handle
            }
        }
        return newFixedLengthResponse(Response.Status.NOT_FOUND, MIME_PLAINTEXT,
                "The requested resource does not exist");
    }

    /**
     * Execute corresponding method by post request
     * @param request Post request
     * @return
     */

    private String executePostByMethod(String request){
        final int method = NetworkUtils.getMethodOfPost(request);
        Log.i("Webserver", "post method " + method + " received");
        switch (method){
            case ConfigUtils.LEADER_START:
                LeaderManager.getLeaderManager().setContext(context).start();
                return NetworkUtils.getLocalIPAddress(context);
            case ConfigUtils.LEADER_END:
                LeaderManager.getLeaderManager().setContext(context).stopConnectNewMembers();
                break;
            case ConfigUtils.MEMBER_AUTO_START:
                MemberManager.getMemberManager().setContext(context).autoStart();
                break;
            case ConfigUtils.MEMBER_MANUAL_START:
                MemberManager.getMemberManager().setContext(context).manualStart(NetworkUtils.getIpOfPost(request));
                break;
            case ConfigUtils.MEMBER_AUTO_END:
                MemberManager.getMemberManager().setContext(context).close();
                break;
            case ConfigUtils.MEMBER_MANUAL_END:
                MemberManager.getMemberManager().setContext(context).close();
                break;

        }
        return "";
    }

    /**
     * Execute corresponding method by get request
     * @param filename Getting file name
     * @return
     */

    private String executeGetByFilename(String filename){

        switch (filename) {
            case "getMemberList":
                clients = LeaderManager.getLeaderManager().setContext(context).getClients();
                mapper = new ObjectMapper();
                memberData = mapper.createObjectNode();
                for (Map.Entry<String, Boolean> client : clients.entrySet()) {
                    memberData.put(client.getKey(), client.getValue());
                }
                try {
                    return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(memberData);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    return "";
                }
            case "getMemberConnectingState":
                status = MemberManager.getMemberManager().setContext(context).getStatus();
                mapper = new ObjectMapper();
                memberStatus = mapper.createObjectNode();
                memberStatus.put("Status", status);
                try {
                    return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(memberStatus);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    return "";
                }
            case "getPeopleData":
                try {
                    PeopleData data = peopleDataManager.getTotalPeopleData();
                    ObjectMapper mapper = new ObjectMapper();
                    ObjectNode peopleData = mapper.createObjectNode();
                    peopleData.put("in", data.getIn());
                    peopleData.put("out", data.getOut());
                    peopleData.put("total", data.getTotal());
                    return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(peopleData);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    return "";
                }
            default:
                return "";
        }
    }

//    private String executeGetByFilename(String filename){
//        switch(filename){
//            case "getMemberList":
//                clients = LeaderManager.getLeaderManager().setContext(context).getClients();
//                mapper = new ObjectMapper();
//                memberData = mapper.createObjectNode();
//                for (Map.Entry<String, Boolean> client : clients.entrySet()){
//                    memberData.put(client.getKey(), client.getValue());
//                }
//                try {
//                    return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(memberData);
//                } catch (Exception ex) {
//                    ex.printStackTrace();
//                    return "";
//                }
//            case "getMemberConnectingState":
//                status = MemberManager.getMemberManager().setContext(context).getStatus();
//                mapper = new ObjectMapper();
//                memberStatus = mapper.createObjectNode();
//                memberStatus.put("Status", status);
//                try {
//                    return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(memberStatus);
//                } catch (Exception ex) {
//                    ex.printStackTrace();
//                    return "";
//                }
//            case "getPeopleData":
//                try {
//                    PeopleData data = peopleDataManager.getTotalPeopleData();
//                    ObjectMapper mapper= new ObjectMapper();
//                    ObjectNode peopleData = mapper.createObjectNode();
//                    peopleData.put("in", data.getIn());
//                    peopleData.put("out", data.getOut());
//                    peopleData.put("total", data.getTotal());
//                    return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(peopleData);
//                } catch (Exception ex) {
//                    ex.printStackTrace();
//                    return "";
//                }
//
//            default:
//                return "";
//        }
//    }

//
//    public void memberSend(JSONObject data){
//        MemberManager.getMemberManager().sendMessage(data);
//    }


}
