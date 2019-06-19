package ru.landar.spring.repository.solr;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.solr.repository.Query;
import org.springframework.data.solr.repository.SolrCrudRepository;

import ru.landar.spring.model.SearchContent;

public interface ObjSolrRepository extends SolrCrudRepository<SearchContent, String> {
	@Query("context:fd content:*?0*")
	public Page<SearchContent> find(String searchTerm, Pageable pageable);
}
