package com.fitnesscenter.payment;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fitnesscenter.models.Appointment;
import com.fitnesscenter.models.Member;
import com.fitnesscenter.models.Reservation;
import com.fitnesscenter.repositories.AppointmentRepository;
import com.fitnesscenter.repositories.MemberRepository;
import com.fitnesscenter.repositories.ReservationRepository;
import com.stripe.model.checkout.Session;

@Service
public class StripeConfirmService {

    private final AppointmentRepository appointmentRepository;
    private final MemberRepository memberRepository;
    private final ReservationRepository reservationRepository;

    public StripeConfirmService(AppointmentRepository appointmentRepository,
                                MemberRepository memberRepository,
                                ReservationRepository reservationRepository) {
        this.appointmentRepository = appointmentRepository;
        this.memberRepository = memberRepository;
        this.reservationRepository = reservationRepository;
    }

    @Transactional
    public void confirmPaidSession(String sessionId, int memberId) throws Exception {

        // idempotent
        if (reservationRepository.findByStripeSessionId(sessionId).isPresent()) return;

        Session session = Session.retrieve(sessionId);

        // 1) Stripe status provera
        if (!"paid".equalsIgnoreCase(session.getPaymentStatus())) {
            throw new IllegalStateException("Session nije plaćen. payment_status=" + session.getPaymentStatus());
        }

        // 2) Metadata provera
        String apptIdStr = session.getMetadata().get("appointmentId");
        if (apptIdStr == null) throw new IllegalArgumentException("Nema appointmentId u Stripe metadata.");

        String memberIdStr = session.getMetadata().get("memberId");
        if (memberIdStr == null) throw new IllegalArgumentException("Nema memberId u Stripe metadata.");

        int appointmentId = Integer.parseInt(apptIdStr);
        int memberIdFromStripe = Integer.parseInt(memberIdStr);

        // vrlo bitno (bez webhooka): confirm mora biti za istog member-a
        if (memberIdFromStripe != memberId) {
            throw new IllegalStateException("memberId mismatch (session metadata != request).");
        }

        // Appointment
        Optional<Appointment> optAppt = appointmentRepository.findById(appointmentId);
        Appointment appt = optAppt.orElse(null);
        if (appt == null) throw new IllegalArgumentException("Termin ne postoji.");

        // Member
        Optional<Member> optMember = memberRepository.findById(memberId);
        Member member = optMember.orElse(null);
        if (member == null) throw new IllegalArgumentException("Member ne postoji.");

        // 3) Amount/currency provera (da ne može da se potvrdi “jeftinija” sesija za skuplji termin)
        if (appt.getService() == null) throw new IllegalStateException("Termin nema uslugu (service).");

        long expectedAmountCents = Math.round(appt.getService().getPrice() * 100.0);
        Long paidAmount = session.getAmountTotal(); // u centima
        String currency = session.getCurrency();    // npr "eur"

        if (paidAmount == null || paidAmount.longValue() != expectedAmountCents) {
            throw new IllegalStateException("Amount mismatch. expected=" + expectedAmountCents + " got=" + paidAmount);
        }
        if (currency == null || !"eur".equalsIgnoreCase(currency)) {
            throw new IllegalStateException("Currency mismatch. expected=eur got=" + currency);
        }

        // capacity check (računaj samo PAID)
        long reservedPaid = reservationRepository.countPaidByAppointmentId(appointmentId);
        if (reservedPaid >= appt.getMaxCapacity()) {
            throw new IllegalStateException("Termin je popunjen.");
        }

        // upis rezervacije
        Reservation r = new Reservation();
        r.setMember(member);
        r.setAppointment(appt);
        r.setStripeSessionId(sessionId);
        r.setStatus("PAID");

        reservationRepository.save(r);
    }
}
