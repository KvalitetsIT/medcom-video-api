package dk.medcom.video.api.dao.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "participant")
public class Participant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "meeting_id")
    private Meeting meeting;

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private ParticipantType type;

    @Column(name = "external_id")
    private String externalId;

    @Column(name = "organisation")
    private String organisation;

    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private ParticipantRole role;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Meeting getMeeting() { return meeting; }
    public void setMeeting(Meeting meeting) { this.meeting = meeting; }

    public ParticipantType getType() { return type; }
    public void setType(ParticipantType type) { this.type = type; }

    public String getExternalId() { return externalId; }
    public void setExternalId(String externalId) { this.externalId = externalId; }

    public String getOrganisation() { return organisation; }
    public void setOrganisation(String organisation) { this.organisation = organisation; }

    public ParticipantRole getRole() { return role; }
    public void setRole(ParticipantRole role) { this.role = role; }
}