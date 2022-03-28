package ru.respons;

public class RequestListOfRequest {

    private String LoanContractType;
    private String LoanContractID;
    private String BranchCode;
    private String Date;

    public RequestListOfRequest(String loanContractType, String loanContractID, String branchCode, String date) {
        LoanContractType = loanContractType;
        LoanContractID = loanContractID;
        BranchCode = branchCode;
        Date = date;
    }

    public String getLoanContractType() {
        return LoanContractType;
    }

    public String getLoanContractID() {
        return LoanContractID;
    }

    public String getBranchCode() {
        return BranchCode;
    }

    public String getDate() {
        return Date;
    }
}
