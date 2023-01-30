package at.ac.tuwien.sepm.groupphase.backend.repository;

import at.ac.tuwien.sepm.groupphase.backend.entity.ContainerTopic;

import javax.transaction.Transactional;

@Transactional
public interface ContainerTopicRepository extends ContainerRepository<ContainerTopic> {

}
