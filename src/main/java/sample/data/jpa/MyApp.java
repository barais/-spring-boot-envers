package sample.data.jpa;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;
import org.hibernate.envers.query.AuditEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import sample.data.jpa.domain.City;
import sample.data.jpa.service.CityRepository;

@Component
public class MyApp {

	@Transactional
	public void populateDataBase() {
		City c = new City("Paris", "France");
		c.setMap("toto");
		c.setState("toto");
		this.cityRepo.save(c);

	}

	@Transactional
	public void updateCity() {
		City c = this.cityRepo.findOne(new Long(1));
		c.setState("titi");
		this.cityRepo.save(c);

	}

	@Transactional
	public void updateCity1() {
		City c = this.cityRepo.findOne(new Long(1));
		c.setState("tutu");
		this.cityRepo.save(c);
	}

	@Transactional
	public void listCityRevisions(Long id) {

		AuditReader auditReader = AuditReaderFactory.get(entityManager);
		// City cityObject = cityRepo.findOne(id);
		List<Number> revisions = auditReader.getRevisions(City.class, id);
		for (Number revision : revisions) {
			City cityRevision = auditReader.find(City.class, id, revision);
			System.err.println(cityRevision.getState());
			Date revisionDate = auditReader.getRevisionDate(revision);
			System.err.println(revisionDate);
		}
	}

	@Transactional
	public void queryRevision() {
		Date d1 = new Date();
		d1.setHours(12);
		d1.setMinutes(2);
		d1.setSeconds(0);
		System.err.println(d1);
		Date d2 = new Date();
		d2.setHours(12);
		d2.setMinutes(3);
		d2.setSeconds(0);
		System.err.println(d2);

		AuditReader auditReader = AuditReaderFactory.get(entityManager);
		List<Object[]> revisions = (List<Object[]>) auditReader.createQuery()
				.forRevisionsOfEntity(City.class, false, true)
				.add(AuditEntity.revisionProperty("timestamp").gt(d1.getTime()))
				.add(AuditEntity.revisionProperty("timestamp").lt(d2.getTime())).getResultList();
		System.err.println(revisions.size());
	}

	@Autowired
	private CityRepository cityRepo;

	@PersistenceContext
	private EntityManager entityManager;
}
