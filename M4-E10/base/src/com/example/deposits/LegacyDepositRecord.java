package com.example.deposits;

import java.math.BigDecimal;
import java.util.Date;

/**
 * A deposit as the DepositsPlus API returns it. Generated from the vendor's WSDL; do not edit.
 * DepositsPlus holds a value date as a java.util.Date at midnight UTC of that date, and a
 * timestamp as a java.util.Date of that instant.
 */
public class LegacyDepositRecord {

    private String dealRef;
    private String currencyCode;
    private BigDecimal principal;
    private Date startDate;
    private Date maturityDate;
    private Date bookedTimestamp;

    public String getDealRef() { return dealRef; }
    public void setDealRef(String dealRef) { this.dealRef = dealRef; }

    public String getCurrencyCode() { return currencyCode; }
    public void setCurrencyCode(String currencyCode) { this.currencyCode = currencyCode; }

    public BigDecimal getPrincipal() { return principal; }
    public void setPrincipal(BigDecimal principal) { this.principal = principal; }

    public Date getStartDate() { return startDate; }
    public void setStartDate(Date startDate) { this.startDate = startDate; }

    public Date getMaturityDate() { return maturityDate; }
    public void setMaturityDate(Date maturityDate) { this.maturityDate = maturityDate; }

    public Date getBookedTimestamp() { return bookedTimestamp; }
    public void setBookedTimestamp(Date bookedTimestamp) { this.bookedTimestamp = bookedTimestamp; }
}
