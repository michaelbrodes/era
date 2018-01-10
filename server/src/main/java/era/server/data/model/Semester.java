package era.server.data.model;

import com.google.common.base.Preconditions;
import com.google.common.collect.ComparisonChain;
import com.google.common.collect.ImmutableMap;
import era.server.data.Model;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.time.Year;
import java.util.Map;

/**
 * The semester that a {@link Course} belongs to. We represent this data as
 * a class (and also table in the database) so we can easily order it and
 * group by it.
 */
@ParametersAreNonnullByDefault
public class Semester implements Comparable<Semester>, Model {

    private long uniqueId;
    private Term term;
    private Year year;

    public Semester(Term term, Year year) {
        Preconditions.checkNotNull(term, "Term cannot be null!");
        Preconditions.checkNotNull(year, "Year cannot be null!");
        this.term = term;
        this.year = year;
    }

    public Semester(long uniqueId, String term, @Nullable Integer year) {
        Preconditions.checkArgument(uniqueId > 0, "A database id cannot be less than 1!");
        Preconditions.checkNotNull(term, "Term cannot be null!");
        this.uniqueId = uniqueId;
        this.term = Term.valueOf(term);
        // now is the only sensible default.
        this.year = year == null ? Year.now() : Year.of(year);
    }

    public static Semester of(Term term, Year year) {
        return new Semester(term, year);
    }

    @Override
    public Map<String, Object> toViewModel() {
        return ImmutableMap.of(
                "uniqueId", uniqueId,
                "term", term.name(),
                "year", year.getValue()
        );
    }

    public long getUniqueId() {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Semester semester = (Semester) o;

        return uniqueId == semester.uniqueId && term == semester.term && year.equals(semester.year);
    }

    @Override
    public int hashCode() {
        int result = term.hashCode();
        result = 31 * result + year.hashCode();
        return result;
    }

    @Override
    public int compareTo(Semester that) {
        return ComparisonChain.start()
                .compare(this.year, that.year)
                .compare(this.term.ordinal(), that.term.ordinal())
                .result();
    }

    @Override
    public String toString() {
        return term.humanReadable() + " " + year.toString();
    }

    public enum Term {
        FALL,
        SPRING;

        public static Term humanValueOf(String humanReadable) {
            return Term.valueOf(humanReadable.toUpperCase());
        }

        public final String humanReadable() {
            return this.name().substring(0, 1)
                    + this.name().substring(1).toLowerCase();
        }
    }
}

