package com.embarcadero.interview.handson.service;

import javax.persistence.EntityManager;
import com.embarcadero.interview.handson.UserEntity;
import java.net.URI;
import java.util.List;
import javax.persistence.Query;
import javax.persistence.PersistenceContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import org.springframework.transaction.annotation.Transactional;

/**
 *  Rest web service to show users.
 */
@Path("com.embarcadero.interview.handson.userentity")
//@com.sun.jersey.spi.resource.Singleton
//@com.sun.jersey.api.spring.Autowire
public class UserEntityRESTFacade {
    @PersistenceContext(unitName = "com.tcontrol.prof_test-ambarcadero_war_1.0-SNAPSHOTPU")
   // @Error("Please fix your project manually, for instructions see http://wiki.netbeans.org/SpringWithAopalliance")
    protected EntityManager entityManager;

    public UserEntityRESTFacade() {
    }

    @POST
    @Consumes({"application/xml", "application/json"})
    @Transactional
    public Response create(UserEntity entity) {
        entityManager.persist(entity);
        return Response.created(URI.create(entity.getId().toString())).build();
    }

    @PUT
    @Consumes({"application/xml", "application/json"})
    @Transactional
    public void edit(UserEntity entity) {
        entityManager.merge(entity);
    }

    @DELETE
    @Path("{id}")
    @Transactional
    public void remove(@PathParam("id") Long id) {
        UserEntity entity = entityManager.getReference(UserEntity.class, id);
        entityManager.remove(entity);
    }

    @GET
    @Path("{id}")
    @Produces({"application/xml", "application/json"})
    @Transactional
    public UserEntity find(@PathParam("id") Long id) {
        return entityManager.find(UserEntity.class, id);
    }

    @GET
    @Produces({"application/xml", "application/json"})
    @Transactional
    public List<UserEntity> findAll() {
        return find(true, -1, -1);
    }

    @GET
    @Path("{max}/{first}")
    @Produces({"application/xml", "application/json"})
    @Transactional
    public List<UserEntity> findRange(@PathParam("max") Integer max, @PathParam("first") Integer first) {
        return find(false, max, first);
    }

    @GET
    @Path("count")
    @Produces("text/plain")
    @Transactional
    public String count() {
        try {
            Query query = entityManager.createQuery("SELECT count(o) FROM UserEntity AS o");
            return query.getSingleResult().toString();
        } finally {
            entityManager.close();
        }
    }

    private List<UserEntity> find(boolean all, int maxResults, int firstResult) {
        try {
            Query query = entityManager.createQuery("SELECT object(o) FROM UserEntity AS o");
            if (!all) {
                query.setMaxResults(maxResults);
                query.setFirstResult(firstResult);
            }
            return query.getResultList();
        } finally {
            entityManager.close();
        }
    }
    
}
