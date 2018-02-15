package era.uploader.data.model;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.collect.ComparisonChain;
import era.uploader.common.UUIDGenerator;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.time.Year;

/**
 * The semester that a {@link Course} belongs to. We represent this data as
 * a class (and also table in the database) so we can easily order it and
 * group by it.
 */
@ParametersAreNonnullByDefault
public class Semester implements Comparable<Semester> {

    private int uniqueId;
    private Term term;
    private Year year;
    private String uuid;

    private Semester(Term term, Year year) {
        Preconditions.checkNotNull(term, "Term cannot be null!");
        Preconditions.checkNotNull(year, "Year cannot be null!");
        this.term = term;
        this.year = year;
    }

    public Semester(String uuid, Term term, @Nullable Year year) {
        this.uuid = uuid;
        this.term = term;
        this.year = year;
    }

    public Semester(int uniqueId, String term, @Nullable Integer year, @Nonnull String uuid) {
        Preconditions.checkArgument(uniqueId > 0, "A database id cannot be less than 1!");
        Preconditions.checkNotNull(term, "Term cannot be null!");
        this.uniqueId = uniqueId;
        this.term = Term.valueOf(term);
        // now is the only sensible default.
        this.year = year == null ? Year.now() : Year.of(year);
        this.uuid = uuid;
    }

    public static Semester of(Term term, Year year) {
        return new Semester(term, year);
    }

    public static Semester of(String term, int year) {

        Term t = Term.valueOf(term);
        Year y = Year.of(year);

        return new Semester(t, y);
    }

    public int getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(int uniqueId) {
        Preconditions.checkArgument(uniqueId > 0, "A database id cannot be less than 1!");
        this.uniqueId = uniqueId;
    }

    @Nonnull
    public Term getTerm() {
        return term;
    }

    public void setTerm(Term term) {
        Preconditions.checkNotNull(term);
        this.term = term;
    }

    @Nonnull
    public String getTermString() {
        return term.name();
    }

    @Nonnull
    public Year getYear() {
        return year;
    }

    public void setYear(Year year) {
        Preconditions.checkNotNull(year);
        this.year = year;
    }

    public int getYearInt() {
        return year.getValue();
    }

    public String getUuid() {
        return uuid;
    }

    public void makeUnique() {
        this.uuid = UUIDGenerator.uuid();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Semester semester = (Semester) o;
        return getTerm() == semester.getTerm() &&
                Objects.equal(getYear(), semester.getYear());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getTerm(), getYear());
    }

    @Override
    public int compareTo(Semester that) {
        return ComparisonChain.start()
                .compare(this.year, that.year)
                .compare(this.term.ordinal(), that.term.ordinal())
                .result();
    }

    public String apiToString() {
        return term.name() + "-" + year.toString();
    }

    @Override
    public String toString() {
        return term.humanReadable() + " " + year.toString();
    }

}
