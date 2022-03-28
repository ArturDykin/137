package ru.respons;

public class ResponseHeaderInfo {

    private String RqUID;
    private String CorrelationUID;
    private String RqType;
    private String RqTm;
    private String SystemFrom;
    private String SystemTo;
    private String ErrorCode;

    public ResponseHeaderInfo(String rqUID, String correlationUID, String rqType, String rqTm, String systemFrom, String systemTo, String errorCode) {
        RqUID = rqUID;
        CorrelationUID = correlationUID;
        RqType = rqType;
        RqTm = rqTm;
        SystemFrom = systemFrom;
        SystemTo = systemTo;
        ErrorCode = errorCode;
    }

    public String getRqUID() {
        return RqUID;
    }

    public String getCorrelationUID() {
        return CorrelationUID;
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

    public String getErrorCode() {
        return ErrorCode;
    }
}
