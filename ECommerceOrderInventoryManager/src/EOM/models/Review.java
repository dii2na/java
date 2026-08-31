package EOM.models;

import EOM.utils.Validator;

public class Review
{
    // Attributes

    private final int productId;
    private final String customerName;
    private final String comment;

    // Constructor

    public Review(int productId, String customerName, String comment)
    {
        this.productId = Validator.validatePositive(
            productId, "Product ID");
        this.customerName = Validator.validateString(
            customerName, "Customer name", false);
        this.comment = Validator.validateString(
            comment, "Comment", false);
    }

    // Getters

    public int getProductId()
    {
        return (productId);
    }

    public String getCustomerName()
    {
        return (customerName);
    }

    public String getComment()
    {
        return (comment);
    }

    // String Representation

    @Override
    public String toString()
    {
        return (formatTable(
            null,
            new Object[][]{
                {customerName, comment}
            }));
    }
}
