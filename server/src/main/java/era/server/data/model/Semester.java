package era.server.data.model;

import com.google.common.base.Objects;
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

    private final String uuid;
    private final Term term;
    private final Year year;

    public Semester(Term term, @Nullable Year year) {
        Preconditions.checkNotNull(term, "Term cannot be null!");
        this.uuid = null;
        this.term = term;
        // now is the only sensible default.
        this.year = year == null ? Year.now() : year;
    }

    public Semester(@Nullable String uuid, Term term, @Nullable Year year) {
        Preconditions.checkNotNull(term, "Term cannot be null!");
        this.uuid = uuid;
        this.term = term;
        // now is the only sensible default.
        this.year = year == null ? Year.now() : year;
    }

    @Override
    public Map<String, Object> toViewModel() {
        return ImmutableMap.of(
                "uuid", uuid,
                "term", term.name(),
                "year", year.getValue()
        );
    }

    @Nullable
    public String getUuid() {
        return uuid;
    }

    @Nonnull
    public Term getTerm() {
        return term;
    }


    @Nonnull
    public String getTermString() {
        return term.name();
    }

    @Nonnull
    public Year getYear() {
        return year;
    }

    public int getYearInt() {
        return year.getValue();
    }

    public Semester withDifferentUUID(String uuid) {
        if (!uuid.equals(this.uuid)) {
            return new Semester(uuid, this.term, this.year);
        } else {
            return this;
        }
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

    @Override
    public String toString() {
        return term.humanReadable() + " " + year.toString();
    }

}

