package gateway;

/**
 * Created by dean on 2017/3/17.
 */


import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

@RepositoryRestResource(collectionResourceRel = "nodes", path = "nodes")
public interface ZigbeeNodeRepository extends PagingAndSortingRepository<ZigbeeNode, Long> {

    List<ZigbeeNode> findByNodeName(@Param("nodeName") String nodeName);

}