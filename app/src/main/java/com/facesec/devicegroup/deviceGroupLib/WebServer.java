package com.facesec.devicegroup.deviceGroupLib;

import android.content.Context;

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

    public WebServer(Context context) throws IOException, KeyStoreException, CertificateException, NoSuchAlgorithmException, UnrecoverableKeyException {
        super(7034);
//        makeSecure(NanoHTTPD.makeSSLSocketFactory(
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

    private String executePostByMethod(String request){
        if (NetworkUtils.getMethodOfPost(request) == ConfigUtils.LEADER_START){
            LeaderManager.getLeaderManager().setContext(context).start();
            return NetworkUtils.getLocalIPAddress(context);
        }
        else if (NetworkUtils.getMethodOfPost(request) == ConfigUtils.MEMBER_AUTO_START){
            MemberManager.getMemberManager().setContext(context).autoStart();
        }
        else if (NetworkUtils.getMethodOfPost(request) == ConfigUtils.MEMBER_MANUAL_START) {
            MemberManager.getMemberManager().setContext(context).manualStart(NetworkUtils.getIpOfPost(request));
        }
        else if (NetworkUtils.getMethodOfPost(request) == ConfigUtils.LEADER_END) {
            MemberManager.getMemberManager().setContext(context).autoStart();
        }
        else if (NetworkUtils.getMethodOfPost(request) == ConfigUtils.MEMBER_AUTO_END) {
            MemberManager.getMemberManager().setContext(context).close();
        }
        else if (NetworkUtils.getMethodOfPost(request) == ConfigUtils.MEMBER_MANUAL_END) {
            MemberManager.getMemberManager().setContext(context).autoStart();
        }
        return "";
    }

//    private String executePostByMethod(String request){
//        switch (NetworkUtils.getMethodOfPost(request)){
//            case ConfigUtils.LEADER_START:
//                LeaderManager.getLeaderManager().setContext(context).start();
//                return NetworkUtils.getLocalIPAddress(context);
//            case ConfigUtils.MEMBER_AUTO_START:
//                MemberManager.getMemberManager().setContext(context).autoStart();
//                break;
//            case ConfigUtils.MEMBER_MANUAL_START:
//                MemberManager.getMemberManager().setContext(context).manualStart(NetworkUtils.getIpOfPost(request));
//                break;
//            case ConfigUtils.LEADER_END:
//                MemberManager.getMemberManager().setContext(context).autoStart();
//                break;
//            case ConfigUtils.MEMBER_AUTO_END:
//                MemberManager.getMemberManager().setContext(context).close();
//                break;
//            case ConfigUtils.MEMBER_MANUAL_END:
//                MemberManager.getMemberManager().setContext(context).autoStart();
//                break;
//        }
//        return "";
//    }

    private String executeGetByFilename(String filename){

        if (filename.equals("getMemberList")){
            clients = LeaderManager.getLeaderManager().setContext(context).getClients();
            mapper = new ObjectMapper();
            memberData = mapper.createObjectNode();
            for (Map.Entry<String, Boolean> client : clients.entrySet()){
                memberData.put(client.getKey(), client.getValue());
            }
            try {
                return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(memberData);
            } catch (Exception ex) {
                ex.printStackTrace();
                return "";
            }
        }
        else if (filename.equals("getMemberConnectingState")){
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
        }
        else if (filename.equals("getPeopleData")) {
            try {
                PeopleData data = peopleDataManager.getTotalPeopleData();
                ObjectMapper mapper= new ObjectMapper();
                ObjectNode peopleData = mapper.createObjectNode();
                peopleData.put("in", data.getIn());
                peopleData.put("out", data.getOut());
                peopleData.put("total", data.getTotal());
                return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(peopleData);
            } catch (Exception ex) {
                ex.printStackTrace();
                return "";
            }
        }
        else {
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

    public void setHostIp(String ip){
        this.hostIp = hostIp;
    }
//
//    public void memberSend(JSONObject data){
//        MemberManager.getMemberManager().sendMessage(data);
//    }


}
