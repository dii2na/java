package baytalhekma.enums;

public enum ItemCategory
{
    BOOK("Book"),
    MAGAZINE("Magazine"),
    DVD("DVD");

    private final String label;

    ItemCategory(String label)
    {
        this.label = label;
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