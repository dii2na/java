package bank.accounts;

public interface InterestBearing
{
    default double getAnnualInterestRate(Account account)
    {
        double baseRate;
        double tierBonus;

        baseRate = account.getType().getAnnualInterestRate();
        tierBonus = account.getCustomer().getTier().getInterestBonus();
        return (baseRate + tierBonus);
    }

    default double applyAnnualInterest(Account account)
    {
        double interest;

        account.checkActive();
        interest = account.getBalance() * getAnnualInterestRate(account);
        account.increaseBalance(interest);
        return (interest);
    }
}
