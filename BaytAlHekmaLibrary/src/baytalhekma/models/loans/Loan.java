package baytalhekma.models.loans;

import baytalhekma.models.items.LibraryItem;
import baytalhekma.models.members.Member;
import utils.Validator;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class Loan
{
    private final LibraryItem item;
    private final Member member;
    private final LocalDate borrowDate;
    private final LocalDate dueDate;
    private LocalDate returnDate;

    public Loan(LibraryItem item, Member member, LocalDate borrowDate)
    {
        this.item = Validator.validateNotNull(item, "Library item");
        this.member = Validator.validateNotNull(member, "Member");
        this.borrowDate = Validator.validateNotNull(borrowDate, "Borrow date");
        this.dueDate = borrowDate.plusDays(item.getLoanPeriod());
        this.returnDate = null;
    }

    public LibraryItem getItem()
    {
        return (item);
    }

    public Member getMember() {
        return (member);
    }

    public LocalDate getBorrowDate()
    {
        return (borrowDate);
    }

    public LocalDate getDueDate()
    {
        return (dueDate);
    }

    public LocalDate getReturnDate()
    {
        return (returnDate);
    }

    public boolean isReturned()
    {
        return (returnDate != null);
    }

    private void checkActive()
    {
        if (isReturned())
            throw new IllegalStateException("Loan has already been returned");
    }

    public void returnLoan()
    {
        checkActive();
        item.returnItem();
        member.recordReturn();
        returnDate = LocalDate.now();
    }

    public boolean isOverdue()
    {
        return (getOverdueDays() > 0);
    }

    public int getOverdueDays()
    {
        LocalDate endDate;

        endDate = isReturned()? returnDate : LocalDate.now();
        if (!endDate.isAfter(dueDate))
            return (0);
        return ((int) ChronoUnit.DAYS.between(dueDate, endDate));
    }

    public double calculateFine()
    {
        return (item.calculateFine(getOverdueDays()));
    }

}