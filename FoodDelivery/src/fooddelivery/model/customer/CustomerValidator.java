package fooddelivery.model.customer;

import fooddelivery.utils.Validator;

public class CustomerValidator 
{

    public static String validateEgyptianMobile(String mobile) 
    {
        mobile = Validator.validateString(mobile, "Mobile number");

        return Validator.validate(
            mobile,
            value -> value.matches("01[0125]\\d{8}"),
            "Mobile number must be 11 digits and start with 010, 011, 012, or 015"
        );
    }
}