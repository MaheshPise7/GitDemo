package com.example.amol.ecom;

/**
 * Created by Amol on 21-Mar-16.
 */
public class ProfileEntity {

    private int CustId;
    private String CustContact;
    private String CustName;
    private String CustEmail;
    private String CustAddress;
    private String CustPassword;
    private String CustOtp;

    public ProfileEntity() {
        this.CustId = 0;
        this.CustContact = "";
        this.CustName = "";
        this.CustAddress = "";
        this.CustPassword = "";
        this.CustOtp = "";
    }

    public int getCustId() {
        return CustId;
    }

    public void setCustId(int custId) {
        CustId = custId;
    }

    public String getCustContact() {
        return CustContact;
    }

    public void setCustContact(String custContact) {
        CustContact = custContact;
    }

    public String getCustName() {
        return CustName;
    }

    public void setCustName(String custName) {
        CustName = custName;
    }

    public String getCustEmail() {
        return CustEmail;
    }

    public void setCustEmail(String custEmail) {
        CustEmail = custEmail;
    }

    public String getCustAddress() {
        return CustAddress;
    }

    public void setCustAddress(String custAddress) {
        CustAddress = custAddress;
    }

    public String getCustPassword() {
        return CustPassword;
    }

    public void setCustPassword(String custPassword) {
        CustPassword = custPassword;
    }

    public String getCustOtp() {
        return CustOtp;
    }

    public void setCustOtp(String custOtp) {
        CustOtp = custOtp;
    }
}

