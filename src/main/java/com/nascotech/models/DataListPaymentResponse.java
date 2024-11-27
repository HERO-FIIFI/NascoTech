package com.nascotech.models;
import com.nascotech.Utils.Link;

import java.util.ArrayList;
import java.util.List;

public class DataListPaymentResponse {

        private List<PaymentResponse> payments;
        private List<Link> links;

        public DataListPaymentResponse() {
            this.links = new ArrayList<>();
        }

        public DataListPaymentResponse(List<PaymentResponse> payments) {
            this.payments = payments;
            this.links = new ArrayList<>();
        }

        // Getters and Setters
        public List<PaymentResponse> getPayments() {
            return payments;
        }

        public void setPayments(Object payments) {
            this.payments = (List<PaymentResponse>) payments;
        }

        public List<Link> getLinks() {
            return links;
        }

        public void setLinks(List<Link> links) {
            this.links = links;
        }

        public void addLink(Link link) {
            this.links.add(link);
        }


}

