package com.fitnesscenter.payment;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.stripe.model.checkout.Session;

@RestController
@RequestMapping("/api/stripe")
public class StripeController {

  private final StripeCheckoutService stripeCheckoutService;
  private final StripeConfirmService stripeConfirmService;

  public StripeController(StripeCheckoutService stripeCheckoutService,
                          StripeConfirmService stripeConfirmService) {
    this.stripeCheckoutService = stripeCheckoutService;
    this.stripeConfirmService = stripeConfirmService;
  }

  @PreAuthorize("hasAnyRole('USER','SELLER','ADMIN')")
  @PostMapping("/checkout-session")
  public ResponseEntity<?> createCheckout(@RequestBody CreateCheckoutSessionRequest req) throws Exception {

    if (req.getAppointmentId() == null) {
      return ResponseEntity.badRequest().body(Map.of("error", "appointmentId is required"));
    }

    // zadržavam tvoju logiku (fallback)
    String userEmail = "test@example.com";
    int memberId = (req.getMemberId() != null ? req.getMemberId() : 1);

    Session session = stripeCheckoutService.createCheckoutSession(req.getAppointmentId(), userEmail, memberId);
    return ResponseEntity.ok(Map.of("url", session.getUrl()));
  }

  // ključni endpoint da radi bez webhooka:
  // FE ga zove na /stripe-success sa session_id iz URL-a
  @PreAuthorize("hasAnyRole('USER','SELLER','ADMIN')")
  @PostMapping("/confirm")
  public ResponseEntity<?> confirm(@RequestBody ConfirmCheckoutSessionRequest req) throws Exception {

    if (req.getSessionId() == null || req.getSessionId().isBlank()) {
      return ResponseEntity.badRequest().body(Map.of("error", "sessionId is required"));
    }

   
    int memberId = (req.getMemberId() != null ? req.getMemberId() : 1);

    stripeConfirmService.confirmPaidSession(req.getSessionId(), memberId);
    return ResponseEntity.ok(Map.of("ok", true));
  }
}
