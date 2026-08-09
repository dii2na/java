package baytalhekma.enums;

import utils.Validator;

public enum ItemCategory
{
    BOOK("Book"),
    MAGAZINE("Magazine"),
    DVD("DVD");

    private final String label;

    ItemCategory(String label)
    {
        this.label = Validator.validateString(label, "Category label", false);
    }

    public String getLabel()
    {
        return (label);
    }

    @Override
    public String toString()
    {
        return (label);
    }
}