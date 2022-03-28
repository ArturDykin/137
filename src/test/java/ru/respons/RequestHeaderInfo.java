package ru.respons;

public class RequestHeaderInfo {

    private String RqUID;
    private String RqType;
    private String RqTm;
    private String SystemFrom;
    private String SystemTo;
    private String ContextUserInfo;
    private String SessionId;

    public RequestHeaderInfo(String rqUID, String rqType, String rqTm, String systemFrom, String systemTo, String contextUserInfo, String sessionId) {
        RqUID = rqUID;
        RqType = rqType;
        RqTm = rqTm;
        SystemFrom = systemFrom;
        SystemTo = systemTo;
        ContextUserInfo = contextUserInfo;
        SessionId = sessionId;
    }

    public String getRqUID() {
        return RqUID;
    }

    public String getRqType() {
        return RqType;
    }

    public String getRqTm() {
        return RqTm;
    }

    public String getSystemFrom() {
        return SystemFrom;
    }

    public String getSystemTo() {
        return SystemTo;
    }

    public String getContextUserInfo() {
        return ContextUserInfo;
    }

    public String getSessionId() {
        return SessionId;
    }
}
