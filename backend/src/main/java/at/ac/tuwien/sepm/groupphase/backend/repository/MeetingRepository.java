package at.ac.tuwien.sepm.groupphase.backend.repository;


import at.ac.tuwien.sepm.groupphase.backend.entity.Meeting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MeetingRepository extends JpaRepository<Meeting, Long> {
    @Query("select m from Meeting m where m.endTime is null")
    List<Meeting> findOpenMeetings();

    @Query("select m from Meeting m where m.endTime is not null")
    List<Meeting> findFinishedMeetings();

    Meeting findMeetingByTitle(String title);

    List<Meeting> findAllBy();
}
