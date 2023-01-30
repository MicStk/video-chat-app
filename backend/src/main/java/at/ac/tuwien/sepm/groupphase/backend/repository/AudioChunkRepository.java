package at.ac.tuwien.sepm.groupphase.backend.repository;

import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepm.groupphase.backend.entity.AudioChunk;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AudioChunkRepository extends JpaRepository<AudioChunk, Long> {
    List<AudioChunk> findAllByTimestampBetweenAndSavedToFileAtIsNullOrderByTimestampAsc(LocalDateTime start, LocalDateTime end);

    List<AudioChunk> findAllByTimestampBetweenAndSavedToFileAtIsNullAndAuthorOrderByTimestampAsc(LocalDateTime start, LocalDateTime end, ApplicationUser user);
}
