package com.fitnesscenter.payment;

public class CreateCheckoutSessionRequest {

    private Integer appointmentId;
    private Integer memberId;

    public CreateCheckoutSessionRequest() {}

    public Integer getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(Integer appointmentId) {
        this.appointmentId = appointmentId;
    }

    public Integer getMemberId() {
        return memberId;
    }

    public void setMemberId(Integer memberId) {
        this.memberId = memberId;
    }
}
