package dw.ms.app.resources;


import dw.ms.app.core.Person;
import dw.ms.app.core.User;
import dw.ms.app.db.PersonDAO;
import io.dropwizard.auth.Auth;
import io.dropwizard.hibernate.UnitOfWork;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;

@RolesAllowed({ "ADMIN" })
@Path("/people")
@Produces(MediaType.APPLICATION_JSON)
public class PeopleResource {
    Logger log = LoggerFactory.getLogger(PeopleResource.class);

    private final PersonDAO peopleDAO;

    public PeopleResource(PersonDAO peopleDAO) {
        this.peopleDAO = peopleDAO;
    }

    @POST
    @UnitOfWork
    public Person createPerson(Person person,@Auth User user) {
        log.info("create person called ::: ",person.toString());
        return peopleDAO.create(person);
    }
    @GET
    @UnitOfWork
    public List<Person> listPeople() {
        return peopleDAO.findAll();
    }

}
