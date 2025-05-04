package il.cshaifasweng.OCSFMediatorExample.entities;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;


@Entity
@Table(name = "reserved_times")
public class ReservedTime implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "table_id", nullable = false)
    private HostingTable table;

    @Column(name = "reserved_time", nullable = false)
    private LocalDateTime reservedTime;

    @ManyToOne
    @JoinColumn(name = "reservation_id", nullable = false)
    private Reservation reservation;

    public ReservedTime() {}

    public ReservedTime(HostingTable table, LocalDateTime reservedTime, Reservation reservation) {
        this.table = table;
        this.reservedTime = reservedTime;
        this.reservation = reservation;
    }

    // Getters and setters
    public HostingTable getTable() { return table; }
    public void setTable(HostingTable table) { this.table = table; }

    public LocalDateTime getReservedTime() { return reservedTime; }
    public void setReservedTime(LocalDateTime reservedTime) { this.reservedTime = reservedTime; }

    public Reservation getReservation() { return reservation; }
    public void setReservation(Reservation reservation) { this.reservation = reservation; }
}


