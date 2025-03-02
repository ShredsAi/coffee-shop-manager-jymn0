package ai.shreds.domain.value_objects;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.Currency;

public final class DomainMoneyValue {
    private final BigDecimal amount;
    private final String currency;

    private DomainMoneyValue(BigDecimal amount, String currency) {
        validateAmount(amount);
        validateCurrency(currency);
        this.amount = amount;
        this.currency = currency;
    }

    public static DomainMoneyValue of(BigDecimal amount, String currency) {
        return new DomainMoneyValue(amount, currency);
    }

    public static DomainMoneyValue zero(String currency) {
        return new DomainMoneyValue(BigDecimal.ZERO, currency);
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getCurrency() {
        return currency;
    }

    public DomainMoneyValue add(DomainMoneyValue other) {
        validateSameCurrency(other);
        return new DomainMoneyValue(this.amount.add(other.amount), this.currency);
    }

    public DomainMoneyValue subtract(DomainMoneyValue other) {
        validateSameCurrency(other);
        return new DomainMoneyValue(this.amount.subtract(other.amount), this.currency);
    }

    private void validateAmount(BigDecimal amount) {
        if (amount == null) {
            throw new IllegalArgumentException("Amount cannot be null");
        }
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Amount cannot be negative");
        }
    }

    private void validateCurrency(String currency) {
        if (currency == null || currency.trim().isEmpty()) {
            throw new IllegalArgumentException("Currency cannot be null or empty");
        }
        try {
            Currency.getInstance(currency);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid currency code: " + currency);
        }
    }

    private void validateSameCurrency(DomainMoneyValue other) {
        if (!this.currency.equals(other.currency)) {
            throw new IllegalArgumentException("Cannot operate on different currencies");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DomainMoneyValue that = (DomainMoneyValue) o;
        return amount.compareTo(that.amount) == 0 && 
               currency.equals(that.currency);
    }

    @Override
    public int hashCode() {
        return Objects.hash(amount, currency);
    }

    @Override
    public String toString() {
        return amount.toString() + " " + currency;
    }
}
