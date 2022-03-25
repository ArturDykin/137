package ru.utils;

public enum ContractNumber {

    CFT2RL("01-055", "07/20/2021 16:23:05"),
    BQ("623/0100-0005937", ""),
    PROFILE("625/0000-0423341", "");

    public String contractNumber;
    public String contractCreateDate;

    ContractNumber(String contractNumber, String contractCreateDate) {
        this.contractNumber = contractNumber;
        this.contractCreateDate = contractCreateDate;
    }

    public String getContractNumber() {
        return contractNumber;
    }

    public String getContractCreateDate() {
        return contractCreateDate;
    }

}
