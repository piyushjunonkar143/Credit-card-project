package com.card91.creditCard.model;

import com.card91.creditCard.service.Views;
import com.fasterxml.jackson.annotation.JsonView;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.UniqueElements;

@Entity
@Getter
@Setter
public class Card {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int cardId;

    @NotNull(message = "refId is required")
    @Pattern(regexp = "^[0-9]+$", message = "reference id must be number only")
    @Size( max = 6, message = "refId length must be < 6 digits")
    private String refId;

    @JsonView(Views.MyResponseViews.class)
    @Column(length = 5)
    private String expiryDate;

    @Column(length = 5)
    private String validityDate;

    @NotEmpty(message = "year for expiry is required")
    private String expiryYears;

    @JsonView(Views.MyResponseViews.class)
    @Size(max = 25, message = "name1 size must be <25")
    @NotEmpty(message = "name1 is required")
    @Pattern(regexp = "^[^0-9]+$", message = "name1 does not contain any number")
    private String name1;

    @Size(max = 25, message = "name2 size must be <25")
    @Pattern(regexp = "^[^0-9]+$", message = "name2 does not contain any number")
    private String name2 = " ";

    @Size(max = 46, message = "address1 must be < 46")
    @NotEmpty(message = "address1 is required")
    private String address1;

    @Size(max = 46, message = "address2 must be < 46 character")
    @NotEmpty(message = "address2 is required")
    private String address2;

    @Size(max = 46, message = "address3 must be < 46 character")
    @NotEmpty(message = "address3 is required")
    private String address3;

    @Size(max = 46, message = "state must be < 46 character")
    @NotEmpty(message = "state is required")
    @Pattern(regexp = "^[^0-9]+$", message = "state does not contain any number")
    private String state;

    @Size(min = 6, max = 6, message = "city zip must be < 6 digits")
    @NotEmpty(message = "city zip is required")
    @Pattern(regexp = "^[0-9]+$", message = "city Zip does not contain any character")
    private String cityZip;

    @Size(max = 31, message = "country must be < 31 character")
    @NotEmpty(message = "country is required")
    @Pattern(regexp = "^[^0-9]+$", message = "country does not contain any number")
    private String country;

    @Size(min = 10, max = 10, message = "Phone Number must be of length 10")
    @NotEmpty(message = "Phone Number is required")
    @Pattern(regexp = "^[0-9]+$", message = "Phone number does not contain any character")
    private String phoneNum;

    @Pattern(regexp = "^\\d{6}(\\d{2})?$", message = "bin should contain 6 or 8 digits")
    @NotEmpty(message = "bin is required")
    private String bin;

    @Pattern(regexp = "^(16)$", message = "card length must be 16")
    private String cardLength = "16";

    @JsonView(Views.MyResponseViews.class)
    @Size(min= 16, max = 19)
    private String cardNo;

    @Size(min = 12, max = 12, message = "Adhaar Number must be of length 12")
    @Pattern(regexp = "^[0-9]+$", message = "Adhaar number does not contain any character")
    @NotEmpty(message = "Adhaar Number is required")
    private String adhaarNum;

    @Column(length = 10)
    private String descritionaryData;

    @JsonView(Views.MyResponseViews.class)
    @Column(length = 3)
    private String cvv;

    @Column(length = 3)
    private String cvv2;

    @Column(length = 3)
    private String icvd;

    @Column(length = 12)
    private String packId;

    private String hash;

    private Boolean printed = false;

}
