package baytalhekma.enums;

public enum MembershipType
{
    REGULAR(0.00),
    STUDENT(0.50),
    SENIOR(0.25);

    private final double waiverRate;

    MembershipType(double waiverRate)
    {
        this.waiverRate =  Validator.validateInRange(
                waiverRate, 0.0, 1.0, "Waiver rate");
    }

    public double getWaiverRate()
    {
        return (waiverRate);
    }
}