package com.example.capstone.arkadia.libris.controller.purchase;

import com.example.capstone.arkadia.libris.dto.request.purchase.CreateIntentDto;
import com.example.capstone.arkadia.libris.service.purchase.OrderService;
import com.example.capstone.arkadia.libris.service.purchase.PaymentService;
import com.stripe.exception.StripeException;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.PaymentIntent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/payments")
public class PaymentController {

    @Autowired private PaymentService paymentService;
    @Autowired private OrderService orderService;

    @PostMapping("/create-intent")
    public ResponseEntity<Map<String,String>> createIntent(@RequestBody CreateIntentDto dto) throws StripeException {
        String clientSecret = paymentService.createPaymentIntent(
                dto.getAmount(), dto.getCurrency(), dto.getOrderId()
        );
        return ResponseEntity.ok(Map.of("clientSecret", clientSecret));
    }

    @PostMapping("/webhook")
    public ResponseEntity<Void> handleWebhook(
            @RequestHeader("Stripe-Signature") String sigHeader,
            @RequestBody String payload
    ) throws SignatureVerificationException {
        Event event = paymentService.constructEvent(payload, sigHeader);
        if ("payment_intent.succeeded".equals(event.getType())) {
            PaymentIntent pi = (PaymentIntent) event.getDataObjectDeserializer()
                    .getObject().orElseThrow();
            Long orderId = Long.valueOf(pi.getMetadata().get("order_id"));
            orderService.markAsPaid(orderId);
        }
        return ResponseEntity.ok().build();
    }
}


