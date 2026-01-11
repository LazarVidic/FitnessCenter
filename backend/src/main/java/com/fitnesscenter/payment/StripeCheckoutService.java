package com.fitnesscenter.payment;

import com.fitnesscenter.models.Appointment;
import com.fitnesscenter.repositories.AppointmentRepository;
import com.fitnesscenter.services.ReservationService;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class StripeCheckoutService {

  private final AppointmentRepository appointmentRepository;
  private final ReservationService reservationService;

  @Value("${app.frontendBaseUrl:http://localhost:5173}")
  private String frontendBaseUrl;

  public StripeCheckoutService(AppointmentRepository appointmentRepository,
                               ReservationService reservationService) {
    this.appointmentRepository = appointmentRepository;
    this.reservationService = reservationService;
  }

  public Session createCheckoutSession(int appointmentId, String userEmail, int memberId) throws Exception {

    Appointment appt = appointmentRepository.findById(appointmentId).orElse(null);
    if (appt == null) throw new IllegalArgumentException("Termin ne postoji.");

    long remaining = reservationService.remainingForAppointment(appointmentId, appt.getMaxCapacity());
    if (remaining <= 0) throw new IllegalStateException("Termin je popunjen.");

    com.fitnesscenter.models.Service svc = appt.getService();
    if (svc == null) throw new IllegalStateException("Termin nema uslugu (service).");

    double priceEur = svc.getPrice();
    long amountCents = Math.round(priceEur * 100);

    String title = (svc.getNameService() != null ? svc.getNameService() : "Trening");
    String desc = "Rezervacija termina #" + appointmentId;

    SessionCreateParams params = SessionCreateParams.builder()
        .setMode(SessionCreateParams.Mode.PAYMENT)
        .setCustomerEmail(userEmail)
        .setSuccessUrl(frontendBaseUrl + "/stripe-success?session_id={CHECKOUT_SESSION_ID}")
        .setCancelUrl(frontendBaseUrl + "/stripe-cancel")
        .addLineItem(
            SessionCreateParams.LineItem.builder()
                .setQuantity(1L)
                .setPriceData(
                    SessionCreateParams.LineItem.PriceData.builder()
                        .setCurrency("eur")
                        .setUnitAmount(amountCents)
                        .setProductData(
                            SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                .setName(title)
                                .setDescription(desc)
                                .build()
                        )
                        .build()
                )
                .build()
        )
        .putMetadata("appointmentId", String.valueOf(appointmentId))
        .putMetadata("memberId", String.valueOf(memberId))
        .build();

    return Session.create(params);
  }
}
