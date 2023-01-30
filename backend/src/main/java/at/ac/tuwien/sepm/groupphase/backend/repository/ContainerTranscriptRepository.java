package at.ac.tuwien.sepm.groupphase.backend.repository;

import at.ac.tuwien.sepm.groupphase.backend.entity.ContainerTranscript;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Transactional
public interface ContainerTranscriptRepository extends ContainerRepository<ContainerTranscript> {

    @Query("select t from ContainerTranscript t where t.timestamp <= ?1 and t.endTime >= ?2")
    List<ContainerTranscript> findAllByTimestampBeforeAndEndTimeAfter(LocalDateTime before, LocalDateTime after);

}
