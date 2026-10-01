package fooddelivery.model.customer;

import fooddelivery.utils.Validator;

public class Address
{
    private final String district;
    private final String detail;

    public Address(String district, String detail)
    {
        this.district = Validator.validateString(district, "District");
        this.detail = Validator.validateString(detail, "Address detail");
    }

    public String getDistrict()
    {
        return (district);
    }

    public String getDetail()
    {
        return (detail);
    }

    // Textual representation

    @Override
    public String toString()
    {
        return ("Address{district=%s, detail=%s}"
            .formatted(district, detail));
    }

    // Identity

    @Override
    public boolean equals(Object object)
    {
        if (this == object)
            return (true);

        if (!(object instanceof Address other))
            return (false);

        return (district.equals(other.district)
            && detail.equals(other.detail));
    }

    @Override
    public int hashCode()
    {
        return (district.hashCode() * 31 + detail.hashCode());
    }
}
