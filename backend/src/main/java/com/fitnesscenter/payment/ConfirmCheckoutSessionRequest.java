package com.fitnesscenter.payment;

public class ConfirmCheckoutSessionRequest {

    private String sessionId;
    private Integer memberId;

    public ConfirmCheckoutSessionRequest() {}

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public Integer getMemberId() {
        return memberId;
    }

    public void setMemberId(Integer memberId) {
        this.memberId = memberId;
    }
}
