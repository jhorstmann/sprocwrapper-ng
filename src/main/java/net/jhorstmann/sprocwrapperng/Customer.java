package net.jhorstmann.sprocwrapperng;

import java.util.Date;

public class Customer {
    private String customerNumber;
    private Date created;
    private Address defaultBillingAddress;
    private Address defaultShippingAddress;

    public String getCustomerNumber() {
        return customerNumber;
    }

    public void setCustomerNumber(String customerNumber) {
        this.customerNumber = customerNumber;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Address getDefaultBillingAddress() {
        return defaultBillingAddress;
    }

    public void setDefaultBillingAddress(Address defaultBillingAddress) {
        this.defaultBillingAddress = defaultBillingAddress;
    }

    public Address getDefaultShippingAddress() {
        return defaultShippingAddress;
    }

    public void setDefaultShippingAddress(Address defaultShippingAddress) {
        this.defaultShippingAddress = defaultShippingAddress;
    }
}
