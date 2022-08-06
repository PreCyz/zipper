package pawgit.zipper;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import jakarta.persistence.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Entity
@Table(name = "company")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Company implements Serializable {

    private static final long serialVersionUID = -6783504532088859179L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    @Column
    private String name;
    @Column(name = "start_date")
    @JsonSerialize(using = JsonDateSerializer.class)
    @JsonDeserialize(using = JsonDateDeserializer.class)
    private LocalDateTime startDateTime;
    @Column
    private int boardMembers;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDateTime getStartDateTime() {
        return startDateTime;
    }

    public void setStartDateTime(LocalDateTime startDateTime) {
        this.startDateTime = startDateTime;
    }

    public int getBoardMembers() {
        return boardMembers;
    }

    public void setBoardMembers(int boardMembers) {
        this.boardMembers = boardMembers;
    }

    @Override
    public String toString() {
        return "Company{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", startDateTime=" + startDateTime.format(DateTimeFormatter.ISO_DATE_TIME) +
                ", boardMembers=" + boardMembers +
                '}';
    }
}
