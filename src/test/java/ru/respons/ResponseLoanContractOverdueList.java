package ru.respons;

public class ResponseLoanContractOverdueList {


    private static String LoanContractType;
    private static String LoanContractID;

    public ResponseLoanContractOverdueList(String loanContractType, String loanContractID) {
        LoanContractType = loanContractType;
        LoanContractID = loanContractID;
    }

    public String getLoanContractType() {
        return LoanContractType;
    }

    public static String getLoanContractID() {
        return LoanContractID;
    }
}
